package com.backend.Kata.services;

import com.backend.Kata.dto.JwtAuthenticationResponse;
import com.backend.Kata.dto.RefreshTokenRequest;
import com.backend.Kata.dto.SignInRequest;
import com.backend.Kata.dto.SignUpRequest;
import com.backend.Kata.entities.User;

public interface AuthenticationService {

    User signUp(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signIn(SignInRequest signInRequest);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
