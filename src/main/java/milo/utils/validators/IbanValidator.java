package milo.utils.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<Iban, String> {

	public static final String IBAN_REGEXP = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}";

	public void initialize(Iban email) {
	}

	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && value.matches(IBAN_REGEXP);
	}
}