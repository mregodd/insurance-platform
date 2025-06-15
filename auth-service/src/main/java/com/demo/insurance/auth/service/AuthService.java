package com.demo.insurance.auth.service;

import com.demo.insurance.auth.dto.LoginRequest;
import com.demo.insurance.auth.dto.LoginResponse;
import com.demo.insurance.auth.dto.UserDto;

public interface AuthService {
    void register(UserDto userDto);
    LoginResponse authenticate(LoginRequest loginRequest);
}
