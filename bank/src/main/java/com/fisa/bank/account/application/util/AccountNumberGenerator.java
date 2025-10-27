package com.fisa.bank.account.application.util; /**
 * 계좌번호 생성 유틸
 *
 * 형식: XXXXYYYZZZZZZ (총 13자리 숫자)
 * 예시: 4821307591204
 */
import com.fisa.bank.account.persistence.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int MAX_RETRY_COUNT = 3; // 무한루프 방지

    private final AccountRepository accountRepository;

    @Autowired
    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * 중복되지 않는 계좌번호를 생성
     */
    public String generateUnique() {
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            String accountNumber = generate();
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
        throw new IllegalStateException("고유한 계좌번호를 생성할 수 없습니다. (재시도 초과)");
    }

    /*
        계좌번호 자동 생성기
    */
    public static String generate() {
        StringBuilder sb = new StringBuilder(13);

        for (int i = 0; i < 13; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }

        return sb.toString(); // 하이픈 없이 반환
    }
}
