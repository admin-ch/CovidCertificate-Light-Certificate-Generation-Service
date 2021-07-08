package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.incoming.LightCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.LightCertificateResponseDto;
import ch.admin.bag.covidcertificate.service.KpiLoggerService;
import ch.admin.bag.covidcertificate.service.LightCertificateGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static ch.admin.bag.covidcertificate.api.Constants.KPI.*;


@RestController
@RequestMapping("/api/v1/certificate-light")
@RequiredArgsConstructor
@Slf4j
public class LightCertificateController {

    private final LightCertificateGenerationService generationService;
    private final KpiLoggerService kpiLoggerService;

    @PostMapping("/generate")
    public LightCertificateResponseDto generate(@Valid @RequestBody LightCertificateCreateDto lightCertificateCreateDto) {
        kpiLoggerService.logKpi(KPI_TYPE_LIGHT_CERTIFICATE, KPI_GENERATION_STATUS_REQUESTED);
        var lightCertificate = generationService.createLightCertificate(lightCertificateCreateDto);
        kpiLoggerService.logKpi(KPI_TYPE_LIGHT_CERTIFICATE, KPI_GENERATION_STATUS_SUCCESSFUL);
        return lightCertificate;
    }
}
