package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.incoming.LightCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.incoming.LightCertificateMapper;
import ch.admin.bag.covidcertificate.service.domain.LightCertificate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeLightCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.MAPPING_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LightCertificateGenerationServiceTest {
    @InjectMocks
    private LightCertificateGenerationService lightCertificateGenerationService;
    @Mock
    private LightCertificateMapper lightCertificateMapper;
    @Mock
    private BarcodeService barcodeService;
    @Mock
    private ObjectMapper objectMapper;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void init() throws JsonProcessingException {
        customizeLightCertificateCreateDto(fixture);

        lenient().when(lightCertificateMapper.toLightCertificate(any())).thenReturn(fixture.create(LightCertificate.class));
        lenient().when(barcodeService.createBarcode(any(), any())).thenReturn(fixture.create(Barcode.class));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        lenient().when(objectMapper.writer()).thenReturn(objectWriter);
        lenient().when(objectWriter.writeValueAsString(any())).thenReturn(fixture.create(String.class));
    }

    @Test
    void shouldMapDTOToCertificateLight() {
        var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
        lightCertificateGenerationService.createLightCertificate(lightCertificateCreateDto);
        verify(lightCertificateMapper).toLightCertificate(lightCertificateCreateDto);
    }

    @Test
    void shouldSerializeCertificateLightToJson() throws JsonProcessingException {
        var lightCertificate = fixture.create(LightCertificate.class);
        when(lightCertificateMapper.toLightCertificate(any())).thenReturn(lightCertificate);
        var objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writer()).thenReturn(objectWriter);

        lightCertificateGenerationService.createLightCertificate(fixture.create(LightCertificateCreateDto.class));

        verify(objectWriter).writeValueAsString(lightCertificate);

    }

    @Test
    void shouldCreateBarcode() throws JsonProcessingException {
        var lightCertificate = fixture.create(LightCertificate.class);
        when(lightCertificateMapper.toLightCertificate(any())).thenReturn(lightCertificate);
        var lightCertificateSerialized = fixture.create(String.class);
        var objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(lightCertificate)).thenReturn(lightCertificateSerialized);

        lightCertificateGenerationService.createLightCertificate(fixture.create(LightCertificateCreateDto.class));

        verify(barcodeService).createBarcode(lightCertificateSerialized, lightCertificate.getExpirationInstant());
    }

    @Test
    void shouldReturnBarcodeData() {
        var barcode = fixture.create(Barcode.class);
        when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

        var actual = lightCertificateGenerationService.createLightCertificate(fixture.create(LightCertificateCreateDto.class));

        assertEquals(barcode.getPayload(), actual.getPayload());
    }

    @Test
    void shouldReturnBarcodeImage() {
        var barcode = fixture.create(Barcode.class);
        when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

        var actual = lightCertificateGenerationService.createLightCertificate(fixture.create(LightCertificateCreateDto.class));

        assertEquals(barcode.getImage(), actual.getQrCode());
    }

    @Test
    void shouldThrowCreateCertificateException_ifSerializationToJsonFails() throws JsonProcessingException {
        var lightCertificateDto = fixture.create(LightCertificateCreateDto.class);
        var exception = fixture.create(JsonMappingException.class);
        var objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writer()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(any())).thenThrow(exception);

        var actual = assertThrows(CreateCertificateException.class, () ->
                lightCertificateGenerationService.createLightCertificate(lightCertificateDto)
        );

        assertEquals(MAPPING_ERROR, actual.getError());
    }
}