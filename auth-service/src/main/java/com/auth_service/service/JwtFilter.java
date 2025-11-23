package com.auth_service.service;

import com.auth_service.entity.User;
import com.auth_service.repositories.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final LoginUserDetailsService loginUserDetailsService;
    private final UserRepo  userRepo;

    public JwtFilter(JwtService jwtService,
                     LoginUserDetailsService loginUserDetailsService,
                     UserRepo  userRepo) {
        this.jwtService = jwtService;
        this.loginUserDetailsService = loginUserDetailsService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("path: " + path);
        if (path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/forgotpassword")||
                path.startsWith("/api/v1/auth/otp/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            String token = headerToken.substring(7);
            String userId = jwtService.validateTokenAndRetrieveSubject(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepo.findById(userId).orElse(null);
                if (user != null) {

                    var authority = new SimpleGrantedAuthority(user.getRole());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user.getMobile(), null, List.of(authority));

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }


        }
        filterChain.doFilter(request, response);
    }
}
