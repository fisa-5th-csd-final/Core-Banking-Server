package com.fisa.bank.account.persistence.entity.id;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

/**
 * AccountIdJavaType
 *
 * Hibernate가 AccountId를 DB BIGINT 컬럼과 매핑할 수 있도록 하는 JavaTypeDescriptor.
 */

public class AccountIdJavaType extends AbstractClassJavaType<AccountId> {

    public static final AccountIdJavaType INSTANCE = new AccountIdJavaType();

    public AccountIdJavaType() {
        super(AccountId.class);
    }

    @Override
    public AccountId fromString(CharSequence s) {
        return AccountId.of(Long.valueOf(s.toString()));
    }

    @Override
    public <X> X unwrap(AccountId value, Class<X> type, WrapperOptions options) {
        if (value == null) return null;
        Long raw = value.getValue();
        if (type.isAssignableFrom(Long.class))   return type.cast(raw);
        if (type.isAssignableFrom(String.class)) return type.cast(String.valueOf(raw));
        if (Number.class.isAssignableFrom(type)) return type.cast(raw);
        throw unknownUnwrap(type);
    }

    @Override
    public <X> AccountId wrap(X value, WrapperOptions options) {
        if (value == null) return null;
        if (value instanceof Long l)         return AccountId.of(l);
        if (value instanceof Number n)       return AccountId.of(n.longValue());
        if (value instanceof CharSequence s) return AccountId.of(Long.valueOf(s.toString()));
        throw unknownWrap(value.getClass());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return BigIntJdbcType.INSTANCE;
    }
}
