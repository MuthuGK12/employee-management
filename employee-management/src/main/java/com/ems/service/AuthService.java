package com.ems.service;

import com.ems.dto.request.AuthRequest;
import com.ems.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest.Login request);
    AuthResponse register(AuthRequest.Register request);
}
