package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.incoming.LightCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.incoming.LightCertificateMapper;
import ch.admin.bag.covidcertificate.api.response.LightCertificateResponseDto;
import ch.admin.bag.covidcertificate.service.domain.LightCertificate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.Barcode;

import static ch.admin.bag.covidcertificate.api.Constants.MAPPING_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class LightCertificateGenerationService {
    private final LightCertificateMapper lightCertificateMapper;
    private final BarcodeService barcodeService;
    private final ObjectMapper objectMapper;

    public LightCertificateResponseDto createLightCertificate(LightCertificateCreateDto lightCertificateCreateDto) {
        try {
            LightCertificate lightCertificate = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);
            String contents = objectMapper.writer().writeValueAsString(lightCertificate);
            Barcode code = barcodeService.createBarcode(contents, lightCertificate.getExpirationInstant());
            return new LightCertificateResponseDto(code.getPayload(), code.getImage());
        } catch (JsonProcessingException exception) {
            throw new CreateCertificateException(MAPPING_ERROR);
        }
    }

}
