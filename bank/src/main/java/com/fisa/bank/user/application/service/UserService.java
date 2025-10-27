package com.fisa.bank.user.application.service;

import com.fisa.bank.user.application.dto.UserCreateRequest;
import com.fisa.bank.user.application.util.PasswordUtil;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.repository.UserRepository;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    @Transactional
    public boolean create(UserCreateRequest request){
        String encryptedPassword = passwordUtil.encrypt(request.password());

        User user = User.create(
                request.name(),
                request.address(),
                request.birthday(),
                BigInteger.valueOf(request.salary()),
                request.loginId(),
                encryptedPassword);

        userRepository.save(user);

        return true;
    }

}
