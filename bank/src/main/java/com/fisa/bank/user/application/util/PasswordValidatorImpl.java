package com.fisa.bank.user.application.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidatorImpl implements PasswordValidator{

    private static final String REGEX = "";

    @Override
    public boolean validate(String origin) {
        return Pattern.matches(REGEX, origin);
    }
}
