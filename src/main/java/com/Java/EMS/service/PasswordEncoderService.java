package com.Java.EMS.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderService {
    private static final PasswordEncoderService INSTANCE = new PasswordEncoderService();

    private final BCryptPasswordEncoder encoder;


    private PasswordEncoderService() {
        this.encoder = new BCryptPasswordEncoder(12);
    }


    public static PasswordEncoderService getInstance() {
        return INSTANCE;
    }

    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
