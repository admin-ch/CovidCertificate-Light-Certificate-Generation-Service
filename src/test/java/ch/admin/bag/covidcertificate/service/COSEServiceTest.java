package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import com.flextrade.jfixture.JFixture;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class COSEServiceTest {
    @InjectMocks
    private COSEService coseService;
    @Mock
    private CBORService cborService;
    @Mock
    private SigningClient signingClient;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void init() throws Exception {
        // given
        lenient().when(cborService.getProtectedHeader()).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getPayload(any(byte[].class), any())).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenReturn(fixture.create(byte[].class));
        lenient().when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenReturn(fixture.create(byte[].class));
        lenient().when(signingClient.create(any(byte[].class))).thenReturn(fixture.create(byte[].class));
    }

    @Test
    void shouldCall_CBORService_GetProtectedHeader() throws Exception {
        // when
        coseService.getCOSESign1(fixture.create(byte[].class), null);
        // then
        verify(cborService).getProtectedHeader();
    }

    @Test
    void shouldCall_CBORService_GetPayload_withCorrectCbor() {
        // when
        var dgcCBOR = fixture.create(byte[].class);
        coseService.getCOSESign1(dgcCBOR, fixture.create(Instant.class));
        // then
        verify(cborService).getPayload(eq(dgcCBOR), any());
    }

    @Test
    void shouldCall_CBORService_GetPayload_withCorrectExpiryDate() {
        // when
        var expiryDate = fixture.create(Instant.class);
        coseService.getCOSESign1(fixture.create(byte[].class), expiryDate);
        // then
        verify(cborService).getPayload(any(), eq(expiryDate));
    }

    @Test
    void shouldCall_CBORService_GetSignatureData_withCorrectProtectedBody() throws DecoderException {
        // when
        var bodyProtected = fixture.create(byte[].class);
        when(cborService.getProtectedHeader()).thenReturn(bodyProtected);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(cborService).getSignatureData(eq(bodyProtected), any());
    }

    @Test
    void shouldCall_CBORService_GetSignatureData_withCorrectPayload() throws DecoderException {
        // when
        var payload = fixture.create(byte[].class);
        when(cborService.getPayload(any(), any())).thenReturn(payload);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(cborService).getSignatureData(any(), eq(payload));
    }

    @Test
    void shouldCall_SigningClient_Create_withCorrectData() {
        // when
        var signatureData = fixture.create(byte[].class);
        when(cborService.getSignatureData(any(), any())).thenReturn(signatureData);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(signingClient).create(signatureData);
    }

    @Test
    void shouldCall_CBORService_GetCOSESign1_withCorrectProtectedHeader() throws DecoderException {
        // when
        var protectedHeader = fixture.create(byte[].class);
        when(cborService.getProtectedHeader()).thenReturn(protectedHeader);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(cborService).getCOSESign1(eq(protectedHeader), any(), any());
    }

    @Test
    void shouldCall_CBORService_GetCOSESign1_withCorrectPayload() throws DecoderException {
        // when
        var payload = fixture.create(byte[].class);
        when(cborService.getPayload(any(), any())).thenReturn(payload);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(cborService).getCOSESign1(any(), eq(payload), any());
    }

    @Test
    void shouldCall_CBORService_GetCOSESign1_withCorrectSignature() throws DecoderException {
        // when
        var signature = fixture.create(byte[].class);
        when(signingClient.create(any())).thenReturn(signature);
        coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        // then
        verify(cborService).getCOSESign1(any(), any(), eq(signature));
    }

    @Test
    void shouldReturnCoseSign1Object() {
        var expected = fixture.create(byte[].class);
        when(cborService.getCOSESign1(any(), any(), any())).thenReturn(expected);
        var result = coseService.getCOSESign1(fixture.create(byte[].class), fixture.create(Instant.class));
        assertEquals(expected, result);
    }

    @Test
    void givenExceptionInCBORServiceGetProtectedHeaderIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() throws Exception {
        // given
        var dgcCBOR = fixture.create(byte[].class);
        var expiryDate = fixture.create(Instant.class);
        when(cborService.getProtectedHeader()).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, expiryDate));
        assertEquals(CREATE_COSE_PROTECTED_HEADER_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetPayloadIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        var dgcCBOR = fixture.create(byte[].class);
        var expiryDate = fixture.create(Instant.class);
        when(cborService.getPayload(any(byte[].class), any())).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, expiryDate));
        assertEquals(CREATE_COSE_PAYLOAD_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetSignatureDataIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        var dgcCBOR = fixture.create(byte[].class);
        var expiryDate = fixture.create(Instant.class);
        when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, expiryDate));
        assertEquals(CREATE_COSE_SIGNATURE_DATA_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInSigningClientCreateIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        var dgcCBOR = fixture.create(byte[].class);
        var expiryDate = fixture.create(Instant.class);
        when(signingClient.create(any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, expiryDate));
        assertEquals(CREATE_SIGNATURE_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetCOSESign1IsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        var dgcCBOR = fixture.create(byte[].class);
        var expiryDate = fixture.create(Instant.class);
        when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, expiryDate));
        assertEquals(CREATE_COSE_SIGN1_FAILED, exception.getError());
    }
}
