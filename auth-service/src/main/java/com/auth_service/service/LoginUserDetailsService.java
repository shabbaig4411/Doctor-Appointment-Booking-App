package com.auth_service.service;

import com.auth_service.entity.User;
import com.auth_service.repositories.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public LoginUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {

        User user = userRepo.findByMobile(mobile);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with mobile: " + mobile);
        }

        var authority =
                new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getMobile(),
                user.getPassword(),
                List.of(authority)
        );
    }

}