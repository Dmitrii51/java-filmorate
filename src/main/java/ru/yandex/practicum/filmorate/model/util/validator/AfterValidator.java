package ru.yandex.practicum.filmorate.model.util.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterValidator implements ConstraintValidator<After, LocalDate> {

    private LocalDate date;

    @Override
    public void initialize(After annotation) {
        date = LocalDate.parse(annotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(date);
    }
}
