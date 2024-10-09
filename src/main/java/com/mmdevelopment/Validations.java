package com.mmdevelopment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Set;

public class Validations {

    private static final Validations instance = new Validations();

    private Validations(){}

    public static Validations getInstance(){
        return instance;
    }

    public void validate(Object object, boolean... onlyOne) throws IllegalArgumentException{
        boolean onlyOneValue = (onlyOne.length > 0) ? onlyOne[0] : false;
        Validator validator = getValidator();

        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<Object> violation : violations) {
                errorMessage.append(violation.getMessage()).append("\n");
                if (onlyOneValue){
                    break;
                }
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    public static Validator getValidator() {
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

}
