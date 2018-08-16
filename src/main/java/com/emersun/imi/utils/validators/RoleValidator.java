package com.emersun.imi.utils.validators;

import com.emersun.imi.configs.Constants;
import io.vavr.control.Option;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<Role,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.isEmpty())
            return true;
        if(value.equals(Constants.ADMIN_ROLE) || value.equals(Constants.OPERATOR_ROLE))
            return true;
        else return false;
    }
}
