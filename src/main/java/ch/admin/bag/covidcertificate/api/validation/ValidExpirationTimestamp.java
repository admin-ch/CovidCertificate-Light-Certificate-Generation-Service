package ch.admin.bag.covidcertificate.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidExpirationTimestampValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
@Documented
public @interface ValidExpirationTimestamp {
    String message() default "Expiration date should be in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}