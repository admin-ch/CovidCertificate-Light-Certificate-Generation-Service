package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String CERTIFICATE_VERSION = "1.0.0";

    public static final String LIGHT_CERTIFICATE_HEADER = "LT1:";
    public static final int DAYS_UNTIL_RECOVERY_VALID = 10;
    public static final int RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS = 179;

    public static final CreateCertificateError MAPPING_ERROR = new CreateCertificateError(555, "Mapping the dto to the qr code failed.", HttpStatus.BAD_REQUEST);
    public static final CreateCertificateError CREATE_COSE_PROTECTED_HEADER_FAILED = new CreateCertificateError(550, "Creating COSE protected header failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_PAYLOAD_FAILED = new CreateCertificateError(551, "Creating COSE payload failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGNATURE_DATA_FAILED = new CreateCertificateError(552, "Creating COSE signature data failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_SIGNATURE_FAILED = new CreateCertificateError(553, "Creating signature failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_COSE_SIGN1_FAILED = new CreateCertificateError(554, "Creating COSE_Sign1 failed.", HttpStatus.INTERNAL_SERVER_ERROR);
    public static final CreateCertificateError CREATE_BARCODE_FAILED = new CreateCertificateError(555, "Creating barcode failed.", HttpStatus.INTERNAL_SERVER_ERROR);


    // KPI Logs constants
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class KPI {
        public static final String KPI_TYPE_LIGHT_CERTIFICATE = "lcc";
        public static final String KPI_TIMESTAMP_KEY = "ts";
        public static final String KPI_TYPE_KEY = "type";
        public static final String KPI_CREATE_CERTIFICATE_SYSTEM_KEY = "cc";
        public static final String KPI_TRANSFORMATION_SERVICE = "ltr";
        public static final String KPI_GENERATION_STATUS_KEY = "sts";
        public static final String KPI_GENERATION_STATUS_REQUESTED = "requested";
        public static final String KPI_GENERATION_STATUS_SUCCESSFUL = "successful";

        public static final DateTimeFormatter LOG_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    }
}
