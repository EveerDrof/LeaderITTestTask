package com.example.LeaderITTestTask;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
public class Security {
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    public Security() throws NoSuchAlgorithmException {
        passwordEncoder = new BCryptPasswordEncoder();
        secureRandom = SecureRandom.getInstanceStrong();
    }

    public String nextKey() {
        byte[] bytes = new byte[512];
        secureRandom.nextBytes(bytes);
        return new String(bytes);
    }

    public String hash(String key) throws NoSuchAlgorithmException {
        return passwordEncoder.encode(key);
    }

}