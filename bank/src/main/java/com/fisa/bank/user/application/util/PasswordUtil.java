package com.fisa.bank.user.application.util;

import com.fisa.bank.user.application.exception.InvalidPasswordFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private final PasswordValidator validator;
    private final PasswordEncoder passwordEncoder;

    public String encrypt(String password){

        if(!validator.validate(password))
            throw InvalidPasswordFormatException.EXCEPTION;

        return passwordEncoder.encode(password);
    }

}
