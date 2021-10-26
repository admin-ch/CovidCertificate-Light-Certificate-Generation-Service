package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class CreateCertificateException extends NestedRuntimeException {
    private final CreateCertificateError error;

    public CreateCertificateException(CreateCertificateError error) {
        super(error.getErrorMessage());
        this.error = error;
    }

    public CreateCertificateException(CreateCertificateError error, Throwable cause) {
        super(error.getErrorMessage(), cause);
        this.error = error;
    }
}
