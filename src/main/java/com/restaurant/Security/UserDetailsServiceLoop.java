package com.restaurant.Security;

import com.restaurant.Entity.UserEntity;
import com.restaurant.Repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;


@RequiredArgsConstructor
@Configuration
public class UserDetailsServiceLoop { //

    private final UserEntityRepository userEntityRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<UserEntity> userOptional = userEntityRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                String roleFromDatabase = user.getRole();
                GrantedAuthority authority = new SimpleGrantedAuthority(roleFromDatabase);

                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singletonList(authority)
                );
            } else {
                throw new UsernameNotFoundException("Username " + username + " was not found.");
            }
        };
    }
}
