package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UserDto;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.UserContactRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.security.BookStorePhoneUserDetails;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetails;
import com.github.ivanfomkin.bookshop.security.BookStoreUserDetailsService;
import com.github.ivanfomkin.bookshop.security.jwt.JWTUtil;
import com.github.ivanfomkin.bookshop.service.UserService;
import com.github.ivanfomkin.bookshop.util.CommonUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserContactRepository userContactRepository;
    private final BookStoreUserDetailsService userDetailsService;

    private static final String LOGIN_ERROR = "Неверное имя пользователя или пароль";

    public UserServiceImpl(JWTUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserContactRepository userContactRepository, BookStoreUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userContactRepository = userContactRepository;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
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
            UserEntity user = new UserEntity();
            user.setHash(UUID.randomUUID().toString());
            user.setName(formDto.getName());
            user.setPassword(passwordEncoder.encode(formDto.getPassword()));
            userRepository.save(user);
            user.setContacts(saveContacts(formDto, user));
            return user;
        } else {
            return null;
        }
    }

    @Override
    public UserEntity registerOAuthUser(Map<String, Object> attributes, String authorizedClientRegistrationId) {
        return switch (authorizedClientRegistrationId) {
            case "github" -> saveGitHubUser(attributes);
            case "google" -> saveGoogleUser(attributes);
            default ->
                    throw new IllegalArgumentException(MessageFormatter.format("Unsupported oauth2 provider: {}", authorizedClientRegistrationId).getMessage());
        };
    }

    private UserEntity saveGoogleUser(Map<String, Object> attributes) {
        var login = attributes.get("name");
        var oAuth2Id = (String) attributes.get("sub");
        var email = attributes.get("email");
        UserEntity user = new UserEntity();
        user.setName(Objects.requireNonNull((String) login));
        user.setBalance(0);
        user.setOauthId(oAuth2Id);
        user.setHash(UUID.randomUUID().toString());
        userRepository.save(user);
        UserContactEntity contact = createEmailContact((String) email, user, "google_oauth2");
        userContactRepository.save(contact);
        user.setContacts(List.of(contact));
        return user;
    }

    private UserEntity saveGitHubUser(Map<String, Object> attributes) {
        var login = attributes.get("login");
        var oAuth2Id = (Integer) attributes.get("id");
        var email = (String) attributes.get("email");
        UserEntity user = new UserEntity();
        user.setName(Objects.requireNonNull((String) login));
        user.setBalance(0);
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
}
