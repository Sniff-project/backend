package com.sniff.utils.enums;

import com.sniff.utils.exception.InvalidEnumValueException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidEnumValue, String> {
    private Set<String> allowedValues;

    @Override
    public void initialize(ValidEnumValue validEnumValue) {
        Class<? extends Enum<?>> enumClass = validEnumValue.enumClass();
        allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && allowedValues.contains(value.toUpperCase())) {
            return true;
        } else {
            throw new InvalidEnumValueException("Invalid value: " + value);
        }
    }
}
