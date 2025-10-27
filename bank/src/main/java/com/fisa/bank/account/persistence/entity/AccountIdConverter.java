package com.fisa.bank.account.persistence.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountIdConverter implements AttributeConverter<AccountId, Long> {

    @Override
    public Long convertToDatabaseColumn(AccountId attribute) {
        return (attribute != null) ? attribute.getValue() : null;
    }

    @Override
    public AccountId convertToEntityAttribute(Long dbData) {
        return AccountId.of(dbData);
    }
}
