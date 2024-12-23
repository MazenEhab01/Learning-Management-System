package org.software.lms.controller;

import org.software.lms.dto.ProfileUpdateDto;
import org.software.lms.dto.UserDto;
import org.software.lms.exception.ResourceNotFoundException;
import org.software.lms.model.User;
import org.software.lms.repository.UserRepository;
import org.software.lms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Add this line to autowire UserRepository

    @GetMapping("/me")
    public ResponseEntity<UserDto> viewProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()) // Now use the autowired instance
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(userService.viewProfile(user.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateDto profileUpdateDto) {
        User user = userRepository.findByEmail(userDetails.getUsername()) // Now use the autowired instance
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(userService.updateProfile(user.getId(), profileUpdateDto));
    }
}