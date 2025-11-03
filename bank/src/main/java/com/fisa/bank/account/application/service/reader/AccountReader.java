package com.fisa.bank.account.application.service.reader;

import com.fisa.bank.account.application.exception.AccessDeniedException;
import com.fisa.bank.account.application.exception.AccountNotFoundException;
import com.fisa.bank.account.persistence.entity.Account;
import com.fisa.bank.account.persistence.entity.id.AccountId;
import com.fisa.bank.account.persistence.repository.AccountRepository;
import com.fisa.bank.common.application.util.RequesterInfo;
import com.fisa.bank.user.application.exception.UserNotFoundException;
import com.fisa.bank.user.application.service.UserService;
import com.fisa.bank.user.persistence.entity.User;
import com.fisa.bank.user.persistence.entity.id.UserId;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.RequestInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AccountReader {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final RequesterInfo requesterInfo;

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
    }

    // 계좌번호로 조회 (비관적 락)
    public Account getByAccountNumberWithLock(String accountNumber) {
        return accountRepository.findByAccountNumberWithLock(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
    }

    // 현재 로그인한 사용자가 계좌의 소유자인지 검증 후, 계좌 반환
    public Account getOwnedAccount(String accountNumber) {
        Account account = getByAccountNumber(accountNumber);
        Long accountOwnerId = account.getUser().getUserId().getValue();
        Long currentUserId = requesterInfo.getUserId().getValue();

        if (!accountOwnerId.equals(currentUserId)) {
            throw new AccessDeniedException();
        }
        System.out.println(account);
        return account;
    }

    // 현재 로그인한 사용자가 계좌의 소유자인지 검증 후, 계좌 반환 (비관적 락)
    public Account getOwnedAccountWithLock(String accountNumber) {
        Account account = getByAccountNumberWithLock(accountNumber);
        Long accountOwnerId = account.getUser().getUserId().getValue();
        Long currentUserId = requesterInfo.getUserId().getValue();

        if (!accountOwnerId.equals(currentUserId)) {
            throw new AccessDeniedException();
        }
        System.out.println(account);
        return account;
    }

    // 사용자 가져오기
    public User getUserById() {
        Long currentUserId = requesterInfo.getUserId().getValue();
        return userService.getUserById(UserId.of(currentUserId));
    }
}
