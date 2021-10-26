package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.KpiData;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KpiLoggerServiceTest {
    @InjectMocks
    private KpiLoggerService kpiLoggerService;
    @Mock
    private KpiDataService kpiDataService;

    private final JFixture fixture = new JFixture();

    @Test
    void shouldLogKpi(){
        var kpiTimestamp = fixture.create(LocalDateTime.class);
        var type = fixture.create(String.class);
        var generationStatus = fixture.create(String.class);

        try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(kpiTimestamp);

            kpiLoggerService.logKpi(type, generationStatus);

            verify(kpiDataService).log(new KpiData(kpiTimestamp, type, generationStatus));
        }
    }

}