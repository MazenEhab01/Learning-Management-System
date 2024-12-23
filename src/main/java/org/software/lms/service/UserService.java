package org.software.lms.service;

import org.software.lms.dto.ProfileUpdateDto;
import org.software.lms.dto.UserDto;
import org.software.lms.model.Role;
import org.software.lms.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getUsersByRole(Role role);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    UserDto viewProfile(Long userId);
    UserDto updateProfile(Long userId, ProfileUpdateDto profileUpdateDto);
}
