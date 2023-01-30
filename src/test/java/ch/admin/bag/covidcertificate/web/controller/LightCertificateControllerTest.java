package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.incoming.LightCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.LightCertificateResponseDto;
import ch.admin.bag.covidcertificate.service.KpiLoggerService;
import ch.admin.bag.covidcertificate.service.LightCertificateGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeLightCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.KPI.KPI_GENERATION_STATUS_REQUESTED;
import static ch.admin.bag.covidcertificate.api.Constants.KPI.KPI_GENERATION_STATUS_SUCCESSFUL;
import static ch.admin.bag.covidcertificate.api.Constants.KPI.KPI_TYPE_LIGHT_CERTIFICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class LightCertificateControllerTest {
    @InjectMocks
    private LightCertificateController controller;
    @Mock
    private LightCertificateGenerationService lightCertificateGenerationService;
    @Mock
    private KpiLoggerService kpiLoggerService;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    private static final String URL = "/api/v1/certificate-light/generate";

    private static final JFixture fixture = new JFixture();

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        customizeLightCertificateCreateDto(fixture);
    }

    @Nested
    class Generate {
        @Test
        void returnsCreatedStatus() throws Exception {
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        void generatesLightCertificate() throws Exception {
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isCreated());

            verify(lightCertificateGenerationService).createLightCertificate(lightCertificateCreateDto);
        }

        @Test
        void shouldReturnLightCertificate() throws Exception {
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            var lightCertificateResponseDto = fixture.create(LightCertificateResponseDto.class);
            when(lightCertificateGenerationService.createLightCertificate(any())).thenReturn(lightCertificateResponseDto);

            var response = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var actual = mapper.readValue(response.getResponse().getContentAsString(), LightCertificateResponseDto.class);
            assertEquals(lightCertificateResponseDto, actual);
        }

        @Test
        void logsKpiWithRequestedStatus() throws Exception {
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isCreated());

            verify(kpiLoggerService).logKpi(KPI_TYPE_LIGHT_CERTIFICATE, KPI_GENERATION_STATUS_REQUESTED);
        }

        @Test
        void logsKpiWithSuccessfulStatus() throws Exception {
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isCreated());

            verify(kpiLoggerService).logKpi(KPI_TYPE_LIGHT_CERTIFICATE, KPI_GENERATION_STATUS_SUCCESSFUL);
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.web.controller.LightCertificateControllerTest#invalidLightCertificateCreateDto")
        void validatesInputAndReturnsBadRequest_ifInputInvalid(LightCertificateCreateDto lightCertificateCreateDto) throws Exception {
            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(lightCertificateCreateDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<LightCertificateCreateDto> invalidLightCertificateCreateDto() {
        var nullName = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(nullName, "name", null);
        var nullExpiryDate = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(nullExpiryDate, "expiryDate", null);
        var expiryDateInThePast = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(expiryDateInThePast, "expiryDate", Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli());
        var expiryDateNow = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(expiryDateNow, "expiryDate", Instant.now().toEpochMilli());
        var nullFamilyName = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(nullFamilyName.getName(), "familyName", null);
        var emptyFamilyName = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(emptyFamilyName.getName(), "familyName", "");
        var nullGivenName = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(nullGivenName.getName(), "givenName", null);
        var emptyGivenName = fixture.create(LightCertificateCreateDto.class);
        ReflectionTestUtils.setField(emptyGivenName.getName(), "givenName", "");
        return Stream.of(nullName, nullExpiryDate, expiryDateInThePast, expiryDateNow, nullFamilyName, emptyFamilyName, nullGivenName, emptyGivenName);
    }
}
