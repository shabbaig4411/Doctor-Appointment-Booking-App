package com.auth_service.service;

import com.auth_service.dto.ApiResponse;
import com.auth_service.dto.RegistrationDto;
import com.auth_service.entity.User;
import com.auth_service.repositories.UserRepo;
import com.auth_service.util.ULIDGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<String> register(RegistrationDto userDto) {

        // duplicate check
        if (userRepo.existsByEmail(userDto.getEmail()) ||
                userRepo.existsByMobile(userDto.getMobile())) {

            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("Duplicate Entry");
            res.setCode(400);
            res.setData("Mobile or Email already registered");
            return res;
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        // make id as, private String id;
        user.setId(ULIDGenerator.generate());
        user.setRole(userDto.getRole().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Registration Successful");
        res.setCode(201);
        res.setData(" 🎉 Welcome to the Doctor Appointment Booking APP 🎉");
        return res;
    }

    public User getUserByMobile(String mobile) {
        return userRepo.findByMobile(mobile);
    }
}
