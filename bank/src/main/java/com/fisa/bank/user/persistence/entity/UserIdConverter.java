package com.fisa.bank.user.persistence.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserIdConverter implements AttributeConverter<UserId, Long> {

    @Override
    public Long convertToDatabaseColumn(UserId attribute) {
        return attribute.getValue();
    }

    @Override
    public UserId convertToEntityAttribute(Long dbData) {
        return UserId.of(dbData);
    }
}
