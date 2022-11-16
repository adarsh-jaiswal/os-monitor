package com.monitoring.repository;

import com.monitoring.model.MetricDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsDtoRepositoryImplTest {

    private static final String INSERT = "INSERT INTO metrics (time, cpu, memory) VALUES(?,?,?)";
    private static final String FETCH_METRICS = "SELECT * from metrics where time < ? and time >  ?  order by time asc";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MetricsDtoRepositoryImpl metricsDtoRepository;

    @Test
    void save() {
        MetricDto capturedMetric = new MetricDto.Builder()
                .time(452345624l)
                .cpuUsage(68.33f)
                .memoryUsage(78.43f)
                .build();
        int queryRes = 1;
        when(jdbcTemplate.update(eq(INSERT), eq(452345624l), eq(68.33f), eq(78.43f)))
                .thenReturn(queryRes);
        int saveRes = metricsDtoRepository.save(capturedMetric);
        assertThat(saveRes, is(1));
    }

    @Test
    void getByTimeInterval() {
        List<MetricDto> metrics = List.of(new MetricDto.Builder()
                .time(5l)
                .cpuUsage(48.33f)
                .memoryUsage(88.43f)
                .build(),
                new MetricDto.Builder()
                        .time(8l)
                        .cpuUsage(78.33f)
                        .memoryUsage(98.43f)
                        .build());
        when(jdbcTemplate.query(eq(FETCH_METRICS), any(BeanPropertyRowMapper.class),
                eq(10l), eq(3l)))
                .thenReturn(metrics);
        List<MetricDto> returnedMetrics = metricsDtoRepository.getByTimeInterval(3l, 10l);
        assertThat(returnedMetrics, Matchers.contains(metrics.toArray()));
    }
}