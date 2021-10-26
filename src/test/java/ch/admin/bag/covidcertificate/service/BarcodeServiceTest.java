package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.flextrade.jfixture.JFixture;
import com.upokecenter.cbor.CBORObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.service.DGCBarcodeEncoder;

import java.io.IOException;
import java.security.SignatureException;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceTest {
    @InjectMocks
    private BarcodeService barcodeService;
    @Mock
    private DGCBarcodeEncoder dgcBarcodeEncoder;

    private final JFixture fixture = new JFixture();

    @Test
    void shouldCreateBarcodeWithCorrectPayload() throws BarcodeException, IOException, SignatureException {
        var payload = fixture.create(String.class);
        var cborObject = fixture.create(CBORObject.class);

        try (MockedStatic<CBORObject> cborObjectMock = Mockito.mockStatic(CBORObject.class, Mockito.RETURNS_DEFAULTS)) {
            cborObjectMock.when(() -> CBORObject.FromJSONString(any())).thenReturn(cborObject);

            barcodeService.createBarcode(payload, fixture.create(Instant.class));
            verify(dgcBarcodeEncoder).encodeToBarcode(eq(cborObject.EncodeToBytes()), any(Instant.class));
        }
    }

    @Test
    void shouldCreateBarcodeWithCorrectExpiryInstant() throws BarcodeException, IOException, SignatureException {
        var expiryInstant = fixture.create(Instant.class);
        var cborObject = fixture.create(CBORObject.class);

        try (MockedStatic<CBORObject> cborObjectMock = Mockito.mockStatic(CBORObject.class, Mockito.RETURNS_DEFAULTS)) {
            cborObjectMock.when(() -> CBORObject.FromJSONString(any())).thenReturn(cborObject);

            barcodeService.createBarcode(fixture.create(String.class), expiryInstant);
            verify(dgcBarcodeEncoder).encodeToBarcode(any(byte[].class), eq(expiryInstant));
        }
    }

    @Test
    void shouldReturnCreatedBarcode() throws BarcodeException, IOException, SignatureException {
        var barcode = fixture.create(Barcode.class);
        var cborObject = fixture.create(CBORObject.class);
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any())).thenReturn(barcode);

        try (MockedStatic<CBORObject> cborObjectMock = Mockito.mockStatic(CBORObject.class, Mockito.RETURNS_DEFAULTS)) {
            cborObjectMock.when(() -> CBORObject.FromJSONString(any())).thenReturn(cborObject);

            var actual = barcodeService.createBarcode(fixture.create(String.class), fixture.create(Instant.class));

            assertEquals(barcode, actual);
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {BarcodeException.class, IOException.class, SignatureException.class})
    void shouldThrowCreateCertificateException_ifAnyCheckedExceptionIsThrown(Class<? extends Exception> exceptionClass) throws BarcodeException, IOException, SignatureException {
        var exception = fixture.create(exceptionClass);
        var cborObject = fixture.create(CBORObject.class);
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any())).thenThrow(exception);

        try (MockedStatic<CBORObject> cborObjectMock = Mockito.mockStatic(CBORObject.class, Mockito.RETURNS_DEFAULTS)) {
            cborObjectMock.when(() -> CBORObject.FromJSONString(any())).thenReturn(cborObject);

            var actual = assertThrows(CreateCertificateException.class, () ->
                    barcodeService.createBarcode(fixture.create(String.class), fixture.create(Instant.class))
            );
            assertEquals(CREATE_BARCODE_FAILED, actual.getError());
        }
    }

}
