package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.repository.UserRepository;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookStoreUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public BookStoreUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userFromDb = userRepository.findUserEntityByContacts_contact(username);
        if (userFromDb != null) {
            return new BookStoreUserDetails(userFromDb);
        } else {
            throw new UsernameNotFoundException(MessageFormatter.format("Пользователь с указанным контактом не найден: {}", username).getMessage());
        }
    }
}
