package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.KPI.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiLoggerService {
    private final KpiDataService kpiLogService;
    private final ServletJeapAuthorization jeapAuthorization;

    public void logKpi(String type, String transformationStatus) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null) {
            var kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {} {}",
                    kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_TIMESTAMP_FORMAT)),
                    kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_TRANSFORMATION_SERVICE),
                    kv(KPI_TYPE_KEY, type),
                    kv(KPI_GENERATION_STATUS_KEY, transformationStatus)
            );
            kpiLogService.log(new KpiData(kpiTimestamp, type, transformationStatus));
        }
    }
}
