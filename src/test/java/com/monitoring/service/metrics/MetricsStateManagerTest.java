package com.monitoring.service.metrics;

import com.monitoring.model.MetricDto;
import com.monitoring.repository.MetricsDtoRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static com.monitoring.utility.FieldSetter.setFinalStatic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsStateManagerTest {

    @Mock
    private static Lock readLock;

    @Mock
    private static Lock writeLock;

    private static final Integer DURATION = 2; // 2sec

    @Mock
    private MetricsDtoRepositoryImpl metricsDtoRepository;

    @Captor
    private ArgumentCaptor<Long> endTime;

    @Captor
    private ArgumentCaptor<Long> startTime;

    @InjectMocks
    private MetricsStateManager metricsStateManager;

    public void setup() throws Exception {
        setFinalStatic(MetricsStateManager.class.getDeclaredField("readLock"), readLock);
        setFinalStatic(MetricsStateManager.class.getDeclaredField("writeLock"), writeLock);
        setFinalStatic(MetricsStateManager.class.getDeclaredField("DURATION"), DURATION);
    }

    @Test
    void updateMetricsWhenDbHasPreviousValues() throws Exception {
        setup();
        List<MetricDto> previousMetrics = List.of(new MetricDto.Builder()
                .time(Instant.now().getEpochSecond())
                .cpuUsage(0.5f)
                .memoryUsage(0.2f)
                .build());

        when(metricsDtoRepository.getByTimeInterval(anyLong(), anyLong()))
                .thenReturn(previousMetrics);
        metricsStateManager.updateHistoryFromDisk();
        metricsStateManager.updateMetrics(new MetricDto.Builder()
                .time(Instant.now().getEpochSecond())
                .build());

        List<MetricDto> metrics = metricsStateManager.getMetricsHistory();
        assertThat(metrics.size(), is(2));

    }

    @Test
    void updateMetricsWhenRecordIsNotSaved() throws Exception {
        setup();
        MetricDto metricToSave = new MetricDto.Builder()
                .time(Instant.now().getEpochSecond())
                .cpuUsage(0.5f)
                .memoryUsage(0.2f)
                .build();
        when(metricsDtoRepository.save(eq(metricToSave)))
                .thenThrow(new DataRetrievalFailureException("message"));
        metricsStateManager.updateMetrics(metricToSave);

        verify(writeLock, never()).lock();
        verify(writeLock, never()).unlock();
    }

    @Test
    void updateMetricsWhenDbHasPreviousValuesButIsOlderThanGivenDuration() throws Exception {
        setup();
        long curr = Instant.now().getEpochSecond();
        List<MetricDto> previousMetrics = List.of(new MetricDto.Builder()
                .time(curr)
                .cpuUsage(0.5f)
                .memoryUsage(0.2f)
                .build());

        InOrder writeOrder = inOrder(writeLock);
        InOrder readOrder = inOrder(readLock);
        when(metricsDtoRepository.getByTimeInterval(anyLong(), anyLong()))
                .thenReturn(previousMetrics);

        metricsStateManager.updateHistoryFromDisk();
        metricsStateManager.updateMetrics(new MetricDto.Builder()
                .time(curr + 3l)//add 3 seconds to current time so that the older record is out of date
                .build());

        List<MetricDto> metrics = metricsStateManager.getMetricsHistory();
        writeOrder.verify(writeLock).lock();
        writeOrder.verify(writeLock).unlock();
        readOrder.verify(readLock).lock();
        readOrder.verify(readLock).unlock();

        assertThat(metrics.size(), is(1));
        assertThat(metrics.get(0).getTime(), is(curr + 3l));
    }

    @Test
    void getMetricsHistory() throws Exception {
        setup();
        List<MetricDto> previousMetrics = List.of(new MetricDto.Builder()
                .time(Instant.now().getEpochSecond())
                .cpuUsage(0.5f)
                .memoryUsage(0.2f)
                .build());
        when(metricsDtoRepository.getByTimeInterval(anyLong(), anyLong()))
                .thenReturn(previousMetrics);
        InOrder readOrder = inOrder(readLock);

        metricsStateManager.updateHistoryFromDisk();
        List<MetricDto> metrics = metricsStateManager.getMetricsHistory();
        assertThat(metrics.size(), is(1));

        readOrder.verify(readLock).lock();
        readOrder.verify(readLock).unlock();
    }

    @Test
    void updateHistoryFromDisk() throws Exception {
        setFinalStatic(MetricsStateManager.class.getDeclaredField("DURATION"), DURATION);
        when(metricsDtoRepository.getByTimeInterval(startTime.capture(), endTime.capture()))
                .thenReturn(new ArrayList<>());

        metricsStateManager.updateHistoryFromDisk();
        long endT = endTime.getValue();
        long startT = startTime.getValue();
        assertThat(endT - startT, is((long)DURATION));
    }

    @Test
    void updateHistoryFromDiskWhenExceptionIsThrown() {
        when(metricsDtoRepository.getByTimeInterval(anyLong(), anyLong()))
                .thenThrow(new DataRetrievalFailureException("message"));
        metricsStateManager.updateHistoryFromDisk();
        List<MetricDto> metrics = metricsStateManager.getMetricsHistory();
        assertThat(metrics.size(), is(0));
    }
}