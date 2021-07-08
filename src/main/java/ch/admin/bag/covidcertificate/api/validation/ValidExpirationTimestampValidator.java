package ch.admin.bag.covidcertificate.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

public class ValidExpirationTimestampValidator implements ConstraintValidator<ValidExpirationTimestamp, Long> {

    @Override
    public boolean isValid(Long expirationMillis, ConstraintValidatorContext context) {

        if (expirationMillis == null) {
            return false;
        }

        if (!(expirationMillis instanceof Long)) {
            throw new IllegalArgumentException("Illegal method signature, "
                    + "expected parameter of type Long.");
        }
        var expirationInstant = Instant.ofEpochMilli(expirationMillis);
        return expirationInstant.compareTo(Instant.now()) > 0;
    }
}