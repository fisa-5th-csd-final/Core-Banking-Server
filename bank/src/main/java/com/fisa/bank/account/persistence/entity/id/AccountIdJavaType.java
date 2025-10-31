package com.fisa.bank.account.persistence.entity.id;

import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;

/**
 * AccountIdJavaType
 *
 * <p>BaseIdJavaType을 상속받아 Long 타입 ID의 매핑 로직을 재사용합니다.
 */
public class AccountIdJavaType extends BaseIdJavaType<Long, AccountId> {

  public static final AccountIdJavaType INSTANCE = new AccountIdJavaType();

  public AccountIdJavaType() {
    super(
        AccountId.class, // 1.ID 클래스
        AccountId::of, // 2.ID 생성 팩토리 메서드
        LongJavaType.INSTANCE, // 3.Descriptor
        BigIntJdbcType.INSTANCE // 4.DB에서 사용할 JDBC 타입
        );
  }
}
