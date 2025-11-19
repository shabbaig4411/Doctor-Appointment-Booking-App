package com.auth_service.rest;

import com.auth_service.dto.ApiResponse;
import com.auth_service.dto.ForgotPasswordDto;
import com.auth_service.dto.LoginDto;
import com.auth_service.dto.RegistrationDto;
import com.auth_service.entity.User;
import com.auth_service.exception.InvalidPasswordException;
import com.auth_service.repositories.UserRepo;
import com.auth_service.service.JwtService;
import com.auth_service.service.RegistrationService;
import com.auth_service.util.OtpEntry;
import com.auth_service.util.OtpUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final RegistrationService registrationService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpUtil otpUtil;

    private final Map<String, OtpEntry> otpStore = new HashMap<>();

    public UserController(RegistrationService registrationService,
                          UserRepo userRepo,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          OtpUtil otpUtil) {

        this.registrationService = registrationService;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpUtil = otpUtil;
    }

    // ---------------- REGISTER ---------------------
    //   http://localhost:5555/auth/api/v1/auth/register

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @RequestBody @Valid RegistrationDto userDto,
            BindingResult bindingResult) {
        // Validate Role
        if (!userDto.getRole().equalsIgnoreCase("doctor") &&
                !userDto.getRole().equalsIgnoreCase("patient")) {

            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("❌ Invalid Role");
            res.setCode(400);
            res.setData(userDto.getRole() + " is not allowed. Use doctor/patient.");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();

            ApiResponse<List<String>> res = new ApiResponse<>();
            res.setMessage("Validation Failed");
            res.setCode(400);
            res.setData(errors);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Register
        ApiResponse<String> response = registrationService.register(userDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }


    // ---------------- LOGIN -----------------------
    //   http://localhost:5555/auth/api/v1/auth/login

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @RequestBody @Valid LoginDto loginDto,
            BindingResult bindingResult) {

        // Validation errors
        if (bindingResult.hasErrors()) {

            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();

            ApiResponse<List<String>> res = new ApiResponse<>();
            res.setMessage("Validation Failed");
            res.setCode(400);
            res.setData(errors);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Check mobile exists
        if (!userRepo.existsByMobile(loginDto.getMobile())) {
            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("Not Registered");
            res.setCode(404);
            res.setData("User not found");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        loginDto.getMobile(),
                        loginDto.getPassword()
                );

        try {
            Authentication auth = authenticationManager.authenticate(token);

            if (auth.isAuthenticated()) {
                User user = registrationService.getUserByMobile(loginDto.getMobile());
                String jwt = jwtService.generateToken(user.getId(), user.getRole());

                ApiResponse<String> res = new ApiResponse<>();
                res.setMessage("Logged in successfully");
                res.setCode(200);
                res.setData("JWT Token:  " + jwt);

                return new ResponseEntity<>(res, HttpStatus.OK);
            }

        } catch (BadCredentialsException ex) {
            throw new InvalidPasswordException("Invalid Password!");
        }

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Login Failed");
        res.setCode(400);
        res.setData("Invalid Credentials");
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }


    // ---------------- SEND OTP ----------------------
    //   http://localhost:5555/auth/api/v1/auth/otp/1234567890

    @PostMapping("/otp/{mobile}")
    public ResponseEntity<ApiResponse<?>> sendOtp(@PathVariable String mobile) {

        if (!userRepo.existsByMobile(mobile)) {
            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("User Not Registered");
            res.setCode(403);
            res.setData("Mobile not found!");
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }

        String otp = otpUtil.generateOtp(mobile); //  Sent to mobile number.
        otpStore.put(mobile, new OtpEntry(mobile, otp, LocalDateTime.now().plusMinutes(5)));

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("OTP Sent Successfully to Mobile: " + mobile);
        res.setCode(200);
        res.setData("OTP valid for 5 minutes.");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    // ---------------- FORGOT PASSWORD ----------------------
    //   http://localhost:5555/auth/api/v1/auth/otp/forgotpassword

    @PostMapping("/forgotpassword")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @RequestBody @Valid ForgotPasswordDto forgotPasswordDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .toList();

            ApiResponse<List<String>> res = new ApiResponse<>();
            res.setMessage("Validation Failed");
            res.setCode(400);
            res.setData(errors);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        if (!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmNewPassword())) {
            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("Passwords does not match! Must Match both the Passwords. ");
            res.setCode(400);
            res.setData(forgotPasswordDto.getNewPassword() + " ❌ " + forgotPasswordDto.getConfirmNewPassword());
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Check mobile exists
        if (!userRepo.existsByMobile(forgotPasswordDto.getMobile())) {
            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("User Not Found");
            res.setCode(404);
            res.setData("Mobile not registered!");
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }

        // Validate OTP
        OtpEntry entry = otpStore.get(forgotPasswordDto.getMobile());

        if (entry == null ||
                !entry.otp().equals(forgotPasswordDto.getOtp()) ||
                LocalDateTime.now().isAfter(entry.expiry())) {

            ApiResponse<String> res = new ApiResponse<>();
            res.setMessage("Invalid OTP");
            res.setCode(400);
            res.setData("OTP expired or incorrect");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

        // Update password
        User user = userRepo.findByMobile(forgotPasswordDto.getMobile());
        user.setPassword(forgotPasswordDto.getNewPassword());

        userRepo.save(user);

        ApiResponse<String> res = new ApiResponse<>();
        res.setMessage("Password Updated Successfully");
        res.setCode(200);
        res.setData("You can now login.");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
