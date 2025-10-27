package com.fisa.bank.account.application.util; /**
 * 계좌번호 생성 유틸
 *
 * 형식: XXXXYYYZZZZZZ (총 13자리 숫자)
 * 예시: 4821307591204
 */
import java.security.SecureRandom;

public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder(13);

        for (int i = 0; i < 13; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }

        return sb.toString(); // 하이픈 없이 반환
    }
}
