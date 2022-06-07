package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.dto.user.UserDto;
import com.example.MyBookShopApp.entity.enums.ContactType;
import com.example.MyBookShopApp.entity.user.UserContactEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserDetailsService;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.service.UserService;
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

    private final String LOGIN_ERROR = "Неверное имя пользователя или пароль";

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
    public void registerNewUser(RegistrationFormDto formDto) {
        if (!userContactRepository.existsAllByContactIn(List.of(formDto.getEmail(), formDto.getPhone()))) {
            UserEntity user = new UserEntity();
            user.setHash(UUID.randomUUID().toString());
            user.setName(formDto.getName());
            user.setPassword(passwordEncoder.encode(formDto.getPassword()));
            userRepository.save(user);
            user.setContacts(saveContacts(formDto, user));
        }
    }

    @Transactional
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
    public Map<String, Object> login(ContactConfirmationRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getContact(), dto.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return Map.of("result", true);
    }

    @Override
    public ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto) {
        try {
            BookStoreUserDetails userDetails = (BookStoreUserDetails) userDetailsService.loadUserByUsername(dto.getContact());
            if (passwordEncoder.matches(dto.getCode(), userDetails.getPassword())) {
                String token = jwtUtil.generateToken(userDetails);
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getContact(), dto.getCode()));
                return new ContactConfirmationResponse(true, token, null);
            } else {
                return new ContactConfirmationResponse(false, null, LOGIN_ERROR);
            }
        } catch (Exception exception) {
            return new ContactConfirmationResponse(false, null, exception.getMessage());
        }
    }

    @Override
    public UserEntity getCurrentUser() {
        UserEntity user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof BookStoreUserDetails userDetails) {
            user = userRepository.findUserEntityByContacts_contact(userDetails.getUsername());
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
            UserContactEntity emailContact = createEmailContact(formDto.getEmail(), user, "111-111");
            contactEntities.add(emailContact);
        }
        if (formDto.getPhone() != null) {
            UserContactEntity phoneContact = new UserContactEntity();
            phoneContact.setApproved((short) 1);
            phoneContact.setContact(formDto.getPhone());
            phoneContact.setType(ContactType.EMAIL);
            phoneContact.setUser(user);
            phoneContact.setCode("111-111");
            contactEntities.add(phoneContact);
        }
        return userContactRepository.saveAll(contactEntities);
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
