package com.fisa.bank.user.persistence.entity.id;

import com.fisa.bank.common.persistence.id.BaseIdJavaType;
import org.hibernate.type.descriptor.java.LongJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;

public class UserIdJavaType extends BaseIdJavaType<Long, UserId> {

    public static final UserIdJavaType INSTANCE = new UserIdJavaType();

    // 생성자에서 부모 클래스로 필요한 데이터 전달
    public UserIdJavaType() {
        super(
                UserId.class,               // 1. ID 클래스
                UserId::of,                 // 2. ID 생성 팩토리 메서드
                LongJavaType.INSTANCE,      // 3. ID의 기본 타입 (Long)에 대한 Descriptor
                BigIntJdbcType.INSTANCE     // 4. DB에서 사용할 JDBC 타입
        );
    }
}