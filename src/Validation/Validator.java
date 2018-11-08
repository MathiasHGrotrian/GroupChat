package Validation;

import Strategy.ValidationStrategy;

//super class of other validators
//part of strategy pattern
class Validator
{
    private ValidationStrategy validationStrategy;

    void setValidationStrategy(ValidationStrategy newValidationStrategy)
    {
        this.validationStrategy = newValidationStrategy;
    }

    boolean validate(String input)
    {
        return validationStrategy.validationStrategy(input);
    }
}
