package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.entity.enums.ContactType;
import com.example.MyBookShopApp.entity.user.UserContactEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.UserContactRepository;
import com.example.MyBookShopApp.repository.UserRepository;
import com.example.MyBookShopApp.security.BookStoreUserDetails;
import com.example.MyBookShopApp.security.BookStoreUserDetailsService;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserContactRepository userContactRepository;
    private final BookStoreUserDetailsService userDetailsService;

    public UserServiceImpl(JWTUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserContactRepository userContactRepository, BookStoreUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userContactRepository = userContactRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UserEntity getUserBySession(HttpSession session) {
        var user = userRepository.findUserEntityByHash(session.getId());
        if (user == null) {
            user = new UserEntity();
            user.setName("Empty User");
            user.setRegTime(Instant.ofEpochMilli(session.getCreationTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
            user.setHash(session.getId());
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public boolean isAuthorized(HttpSession httpSession) {
        return true; // TODO: 20.01.2022 Заглушка. Исправить позже
    }

    @Transactional
    @Override
    public void registerNewUser(RegistrationFormDto formDto, HttpSession httpSession) {
        if (!userContactRepository.existsAllByContactIn(List.of(formDto.getEmail(), formDto.getPhone()))) {
            UserEntity user = userRepository.findUserEntityByHash(httpSession.getId());
            user.setName(formDto.getName());
            user.setRegTime(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(formDto.getPassword()));
            userRepository.save(user);
            user.setContacts(saveContacts(formDto, user));
        }
    }

    @Override
    public Map<String, Object> login(ContactConfirmationRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getContact(), dto.getCode()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return Map.of("result", true);
    }

    @Override
    public ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getContact(), dto.getCode()));
        BookStoreUserDetails userDetails = (BookStoreUserDetails) userDetailsService.loadUserByUsername(dto.getContact());
        String token = jwtUtil.generateToken(userDetails);
        return new ContactConfirmationResponse(token);
    }

    @Override
    public Object getCurrentUser() {
        BookStoreUserDetails userDetails =
                (BookStoreUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findUserEntityByContacts_contact(userDetails.getUsername());
    }

    private List<UserContactEntity> saveContacts(RegistrationFormDto formDto, UserEntity user) {
        List<UserContactEntity> contactEntities = new ArrayList<>();
        if (formDto.getEmail() != null) {
            UserContactEntity emailContact = new UserContactEntity();
            emailContact.setApproved((short) 1);
            emailContact.setContact(formDto.getEmail());
            emailContact.setType(ContactType.EMAIL);
            emailContact.setUser(user);
            emailContact.setCode("111-111");
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
}
