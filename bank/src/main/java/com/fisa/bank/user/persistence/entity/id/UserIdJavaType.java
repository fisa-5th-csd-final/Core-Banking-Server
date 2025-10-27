package com.fisa.bank.user.persistence.entity.id;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class UserIdJavaType extends AbstractClassJavaType<UserId> {
    public static final UserIdJavaType INSTANCE = new UserIdJavaType();
    public UserIdJavaType(){ super(UserId.class); }

    @Override
    public UserId fromString(CharSequence s) {
        return UserId.of(Long.valueOf(s.toString()));
    }

    @Override
    public <X> X unwrap(UserId v, Class<X> type, WrapperOptions o) {
        if (v == null) return null;
        Long raw = v.getValue();
        if (type.isAssignableFrom(Long.class))   return type.cast(raw);
        if (type.isAssignableFrom(String.class)) return type.cast(String.valueOf(raw));
        if (Number.class.isAssignableFrom(type)) return type.cast(raw);
        throw unknownUnwrap(type);
    }

    @Override
    public <X> UserId wrap(X value, WrapperOptions o) {
        if (value == null) return null;
        if (value instanceof Long l)         return UserId.of(l);
        if (value instanceof Number n)       return UserId.of(n.longValue());
        if (value instanceof CharSequence s) return UserId.of(Long.valueOf(s.toString()));
        throw unknownWrap(value.getClass());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return BigIntJdbcType.INSTANCE;
    }
}
