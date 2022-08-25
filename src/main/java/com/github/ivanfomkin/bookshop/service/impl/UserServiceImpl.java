package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.dto.user.UserDto;
import com.github.ivanfomkin.bookshop.dto.user.UserInfoElementDto;
import com.github.ivanfomkin.bookshop.dto.user.UserPageDto;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.enums.UserRole;
import com.github.ivanfomkin.bookshop.entity.user.ChangeUserDataEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserRoleEntity;
import com.github.ivanfomkin.bookshop.exception.ChangeUserDataException;
import com.github.ivanfomkin.bookshop.exception.InsufficientFundsException;
import com.github.ivanfomkin.bookshop.exception.PasswordsDidNotMatchException;
import com.github.ivanfomkin.bookshop.exception.SimplePasswordException;
import com.github.ivanfomkin.bookshop.repository.UserContactRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.repository.UserRoleRepository;
import com.github.ivanfomkin.bookshop.security.BookStorePhoneUserDetails;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetails;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetailsService;
import com.github.ivanfomkin.bookshop.security.jwt.JWTUtil;
import com.github.ivanfomkin.bookshop.service.ChangeUserDataService;
import com.github.ivanfomkin.bookshop.service.EmailMessageService;
import com.github.ivanfomkin.bookshop.service.UserService;
import com.github.ivanfomkin.bookshop.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JWTUtil jwtUtil;
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final EmailMessageService emailMessageService;
    private final AuthenticationManager authenticationManager;
    private final ChangeUserDataService changeUserDataService;
    private final UserContactRepository userContactRepository;
    private final BookStoreUserDetailsService userDetailsService;

    private static final String LOGIN_ERROR = "Неверное имя пользователя или пароль";

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserEntity registerNewUser(RegistrationFormDto formDto) {
        List<String> contactList = new ArrayList<>();
        if (formDto.getEmail() != null) {
            contactList.add(formDto.getEmail());
        }
        if (formDto.getPhone() != null) {
            contactList.add(CommonUtils.formatPhoneNumber(formDto.getPhone()));
        }
        if (userRepository.countAllByContacts_contactIn(contactList) < 1) {
            var user = new UserEntity();
            user.setHash(UUID.randomUUID().toString());
            user.setName(formDto.getName());
            user.setPassword(passwordEncoder.encode(formDto.getPassword()));
            userRepository.save(user);
            user.setContacts(saveContacts(formDto, user));
            createDefaultUserRole(user);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public UserEntity registerOAuthUser(Map<String, Object> attributes, String authorizedClientRegistrationId) {
        var user = switch (authorizedClientRegistrationId) {
            case "github" -> saveGitHubUser(attributes);
            case "google" -> saveGoogleUser(attributes);
            default ->
                    throw new IllegalArgumentException(MessageFormatter.format("Unsupported oauth2 provider: {}", authorizedClientRegistrationId).getMessage());
        };
        createDefaultUserRole(user);
        return user;
    }

    private UserEntity saveGoogleUser(Map<String, Object> attributes) {
        var login = attributes.get("name");
        var oAuth2Id = (String) attributes.get("sub");
        var email = attributes.get("email");
        UserEntity user = new UserEntity();
        user.setName(Objects.requireNonNull((String) login));
        user.setBalance(0.);
        user.setOauthId(oAuth2Id);
        user.setHash(UUID.randomUUID().toString());
        userRepository.save(user);
        UserContactEntity contact = createEmailContact((String) email, user, "google_oauth2");
        userContactRepository.save(contact);
        user.setContacts(List.of(contact));
        return user;
    }

    private void createDefaultUserRole(UserEntity user) {
        var userRole = new UserRoleEntity();
        userRole.setRole(UserRole.ROLE_USER);
        userRole.setUser(user);
        userRoleRepository.save(userRole);
    }

    private UserEntity saveGitHubUser(Map<String, Object> attributes) {
        var login = attributes.get("login");
        var oAuth2Id = (Integer) attributes.get("id");
        var email = (String) attributes.get("email");
        UserEntity user = new UserEntity();
        user.setName(Objects.requireNonNull((String) login));
        user.setBalance(0.);
        user.setOauthId(String.valueOf(oAuth2Id));
        user.setHash(UUID.randomUUID().toString());
        userRepository.save(user);
        if (email != null) {
            UserContactEntity emailContact = createEmailContact(email, user, "github_oauth2");
            user.setContacts(List.of(emailContact));
        }
        return user;
    }

    @Override
    public ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto) {
        return CommonUtils.isPhoneNumber(dto.getContact()) ? jwtPhoneLogin(dto) : jwtEmailLogin(dto);
    }

    public ContactConfirmationResponse jwtPhoneLogin(ContactConfirmationRequestDto dto) {
        var phone = CommonUtils.formatPhoneNumber(dto.getContact());
        try {
            var userDetails = (BookStorePhoneUserDetails) userDetailsService.loadUserByUsername(phone);
            if (userDetails.getPhoneConfirmationCode().equals(dto.getCode().replaceAll("\\D", ""))) {
                String token = jwtUtil.generateToken(userDetails);
                return new ContactConfirmationResponse(true, token, null);
            } else {
                return new ContactConfirmationResponse(false, null, LOGIN_ERROR);
            }
        } catch (Exception exception) {
            return new ContactConfirmationResponse(false, null, exception.getMessage());
        }
    }

    public ContactConfirmationResponse jwtEmailLogin(ContactConfirmationRequestDto dto) {
        try {
            var userDetails = (BookStoreUserDetails) userDetailsService.loadUserByUsername(dto.getContact());
            if (passwordEncoder.matches(dto.getCode(), userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getContact(), dto.getCode()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new ContactConfirmationResponse(true, token, null);
            } else {
                return new ContactConfirmationResponse(false, null, LOGIN_ERROR);
            }
        } catch (Exception exception) {
            return new ContactConfirmationResponse(false, null, exception.getMessage());
        }
    }

    @Transactional
    @Override
    public UserEntity getCurrentUser() {
        UserEntity user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof BookStoreUserDetails userDetails) {
            user = userRepository.findUserEntityByContacts_contact(userDetails.getUsername());
            return user;
        }
        if (authentication instanceof OAuth2AuthenticationToken oAuth2) {
            Map<String, Object> attributes = oAuth2.getPrincipal().getAttributes();
            switch (oAuth2.getAuthorizedClientRegistrationId()) {
                case "github" -> {
                    String email = (String) attributes.get("email");
                    Integer id = (Integer) attributes.get("id");
                    if (email != null) {
                        user = userRepository.findUserEntityByContacts_contact(email);
                    } else {
                        Objects.requireNonNull(id, "User oAuth2 id or user email must be not null");
                        user = userRepository.findUserEntityByOauthId(String.valueOf(id));
                    }
                    if (user == null) {
                        user = registerOAuthUser(attributes, oAuth2.getAuthorizedClientRegistrationId());
                    }
                }
                case "google" -> {
                    String email = (String) attributes.get("email");
                    user = userRepository.findUserEntityByContacts_contact(email);
                    if (user == null) {
                        user = registerOAuthUser(attributes, oAuth2.getAuthorizedClientRegistrationId());
                    }
                }
                default ->
                        throw new OAuth2AuthenticationException(MessageFormatter.format("Unsupported oauth2 provider: {}", oAuth2.getAuthorizedClientRegistrationId()).getMessage());
            }
        }
        return user;
    }

    private UserContactEntity createEmailContact(String email, UserEntity user, String code) {
        UserContactEntity emailContact = new UserContactEntity();
        emailContact.setApproved((short) 1);
        emailContact.setContact(email);
        emailContact.setType(ContactType.EMAIL);
        emailContact.setUser(user);
        emailContact.setCode(code);
        return emailContact;
    }

    private List<UserContactEntity> saveContacts(RegistrationFormDto formDto, UserEntity user) {
        List<UserContactEntity> contactEntities = new ArrayList<>();
        if (formDto.getEmail() != null) {
            UserContactEntity emailContact = userContactRepository.findByContact(formDto.getEmail());
            if (emailContact == null) {
                emailContact = createEmailContact(formDto.getEmail(), user, null);
            } else {
                emailContact.setApproved((short) 1);
                emailContact.setUser(user);
            }
            emailContact.setCodeTrials(0);
            emailContact.setCodeTime(null);
            contactEntities.add(emailContact);
        }
        if (formDto.getPhone() != null) {
            UserContactEntity phoneContact = userContactRepository.findByContact(CommonUtils.formatPhoneNumber(formDto.getPhone()));
            if (phoneContact == null) {
                phoneContact = new UserContactEntity();
                phoneContact.setContact(CommonUtils.formatPhoneNumber(formDto.getPhone()));
            }
            phoneContact.setApproved((short) 1);
            phoneContact.setUser(user);
            phoneContact.setCode(null);
            phoneContact.setCodeTrials(0);
            phoneContact.setCodeTime(null);
            contactEntities.add(phoneContact);
        }
        userContactRepository.saveAll(contactEntities);
        return contactEntities;
    }

    @Override
    public UserDto getCurrentUserInfo() {
        var currentUser = this.getCurrentUser();
        if (currentUser == null) {
            return new UserDto();
        }
        UserDto userDto = new UserDto();
        userDto.setName(currentUser.getName());
        userDto.setBalance(currentUser.getBalance());
        return userDto;
    }

    @Transactional
    @Override
    public void updateProfile(UpdateProfileDto updateProfileDto) {
        var currentUser = getCurrentUser();
        if (updateProfileDto.getPassword() != null && !updateProfileDto.getPassword().isBlank()) {
            if (!updateProfileDto.getPassword().equals(updateProfileDto.getPasswordReply())) {
                var message = messageSource.getMessage("profile.password.match.failed", new Object[]{}, LocaleContextHolder.getLocale());
                throw new PasswordsDidNotMatchException(message);
            } else {
                if (updateProfileDto.getPassword().length() < 7) {
                    var message = messageSource.getMessage("profile.password.simple", new Object[]{}, LocaleContextHolder.getLocale());
                    throw new SimplePasswordException(message);
                }
                updateProfileDto.setPassword(passwordEncoder.encode(updateProfileDto.getPassword()));
            }
        }

        var changeUserDataEntity = changeUserDataService.createChangeUserData(currentUser, updateProfileDto);
        emailMessageService.sendChangeDataMessage(currentUser.getEmail().getContact(), changeUserDataEntity.getToken());
    }

    @Override
    public void updateProfileConfirm(String token) {
        var changeDataEntity = changeUserDataService.findChangeUserDataByToken(token);
        if (changeDataEntity != null) {
            var user = changeDataEntity.getId().getUserEntity();
            if (changeDataEntity.getPassword() != null && !changeDataEntity.getPassword().isBlank()) {
                user.setPassword(changeDataEntity.getPassword());
            }
            if (changeDataEntity.getName() != null && !changeDataEntity.getName().isBlank() && !changeDataEntity.getName().equals(user.getName())) {
                user.setName(changeDataEntity.getName());
            }
            updatePhone(changeDataEntity, user);
            updateEmail(changeDataEntity, user);
            userRepository.save(user);
            changeUserDataService.deleteChangeUserData(changeDataEntity);
        } else {
            throw new ChangeUserDataException(messageSource.getMessage("profile.edit.exception", new Object[]{}, LocaleContextHolder.getLocale()));
        }
    }

    private void updateEmail(ChangeUserDataEntity changeUserDataEntity, UserEntity currentUser) {
        if (changeUserDataEntity.getMail() != null && !changeUserDataEntity.getMail().isBlank()) {
            var email = currentUser.getEmail();
            if (email != null) {
                if (!email.getContact().equals(changeUserDataEntity.getMail())) {
                    email.setContact(changeUserDataEntity.getMail());
                    userContactRepository.save(email);
                }
            } else {
                email = new UserContactEntity();
                email.setContact(changeUserDataEntity.getMail());
                email.setUser(currentUser);
                email.setCodeTrials(0);
                email.setType(ContactType.EMAIL);
                userContactRepository.save(email);
            }
        }
    }

    private void updatePhone(ChangeUserDataEntity changeUserDataEntity, UserEntity currentUser) {
        if (changeUserDataEntity.getPhone() != null && !changeUserDataEntity.getPhone().isBlank()) {
            var phone = currentUser.getPhone();
            var phoneFromDto = CommonUtils.formatPhoneNumber(changeUserDataEntity.getPhone());
            if (phone != null) {
                if (!phone.getContact().equals(phoneFromDto)) {
                    phone.setContact(phoneFromDto);
                    userContactRepository.save(phone);
                }
            } else {
                phone = new UserContactEntity();
                phone.setContact(phoneFromDto);
                phone.setUser(currentUser);
                phone.setCodeTrials(0);
                phone.setType(ContactType.PHONE);
                userContactRepository.save(phone);
            }
        }
    }

    @Override
    public UserPageDto getUserPageDto() {
        var user = this.getCurrentUser();
        var dto = new UserPageDto();
        var userPhoneEntity = user.getPhone();
        var userEmailEntity = user.getEmail();
        dto.setName(user.getName());
        dto.setEmail(userEmailEntity != null ? userEmailEntity.getContact() : "");
        dto.setPhone(userPhoneEntity != null ? String.format("+%s (%s) %s-%s-%s", userPhoneEntity.getContact().charAt(0), userPhoneEntity.getContact().substring(1, 4), userPhoneEntity.getContact().substring(4, 7), userPhoneEntity.getContact().substring(7, 9), userPhoneEntity.getContact().substring(9, 11)) : "");
        return dto;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateUserBalance(UserEntity user, Double amount, TransactionType transactionType) {
        Assert.isTrue(amount >= 0, MessageFormatter.format("Expected positive amount, but actual is {}", amount).getMessage());
        switch (transactionType) {
            case DEBIT -> {
                if (user.getBalance() >= amount) {
                    userRepository.debtFromUserBalance(user, amount);
                } else {
                    throw new InsufficientFundsException(MessageFormatter.arrayFormat("Insufficient funds for debt {} money point from userId {}. User balance is", new Object[]{amount, user.getId(), user.getBalance()}).getMessage());
                }
            }
            case DEPOSIT -> userRepository.addBalanceToUser(user, amount);
        }
    }

    @Override
    public CommonPageableDto<UserInfoElementDto> getPageableAllUsers(Pageable pageable, String searchQuery) {
        Page<UserEntity> userEntityPage;
        if (searchQuery == null || searchQuery.isBlank()) {
            userEntityPage = userRepository.findAll(pageable);
        } else {
            userEntityPage = userRepository.findUsersBySearchQuery(pageable, searchQuery);
        }
        var dto = new CommonPageableDto<UserInfoElementDto>();
        dto.setTotal(userEntityPage.getTotalElements());
        dto.setPerPage(pageable.getPageSize());
        dto.setPage(userEntityPage.getNumber());
        var data = userEntityPage.stream().map(u -> {
            var element = new UserInfoElementDto();
            element.setId(u.getId());
            element.setOauthUser(u.getOauthId() != null && !u.getOauthId().isBlank());
            element.setBalance(u.getBalance());
            element.setName(u.getName());
            element.setEmail(u.getEmail() == null ? "" : u.getEmail().getContact());
            element.setPhone(u.getPhone() == null ? "" : u.getPhone().getContact());
            element.setRegDate(u.getRegTime());
            return element;
        }).toList();
        dto.setData(data);
        return dto;
    }
}
