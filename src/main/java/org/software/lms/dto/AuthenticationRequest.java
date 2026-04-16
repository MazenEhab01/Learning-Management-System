package org.software.lms.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.software.lms.model.Role;


public class AuthenticationRequest {
    private String email;
    private String password;
    private Role role;

    private String firstName;
    private String lastName;
    private String studentNumber;
    private String employeeId;
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public AuthenticationRequest(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public AuthenticationRequest() {
    }

}


