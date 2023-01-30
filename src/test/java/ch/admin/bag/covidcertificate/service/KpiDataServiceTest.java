package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KpiDataServiceTest {
    @InjectMocks
    private KpiDataService kpiDataService;
    @Mock
    private KpiDataRepository logRepository;

    private final JFixture fixture = new JFixture();

    @Nested
    class Log{
        @Test
        void shouldSaveKpi(){
            var kpiLog = fixture.create(KpiData.class);
            kpiDataService.log(kpiLog);
            verify(logRepository).save(kpiLog);
        }
    }

}