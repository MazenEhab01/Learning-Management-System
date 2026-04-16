package org.software.lms.service;

import org.software.lms.dto.AuthenticationRequest;
import org.software.lms.dto.AuthenticationResponse;
import org.software.lms.model.*;
import org.software.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(AuthenticationRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Create new user based on role
        User user;
        Role role = request.getRole() != null ? request.getRole() : Role.STUDENT;

        if (role == Role.STUDENT) {
            Student student = new Student();
            student.setStudentNumber(request.getStudentNumber());
            user = student;
        } else if (role == Role.INSTRUCTOR) {
            Instructor instructor = new Instructor();
            instructor.setEmployeeId(request.getEmployeeId());
            user = instructor;
        } else if (role == Role.ADMIN) {
            user = new Admin();
        } else {
            user = new User();
        }

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set default values if not provided
        user.setFirstName(request.getFirstName() != null ? request.getFirstName() : "User");
        user.setLastName(request.getLastName() != null ? request.getLastName() : "");
        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        // Create and return authentication response
        return new AuthenticationResponse(
                user.getId(),
                user.getEmail(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}