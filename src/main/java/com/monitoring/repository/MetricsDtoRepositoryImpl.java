package com.monitoring.repository;

import com.monitoring.model.MetricDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MetricsDtoRepositoryImpl implements MetricsDtoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT = "INSERT INTO metrics (time, cpu, memory) VALUES(?,?,?)";
    private static final String FETCH_METRICS = "SELECT * from metrics where time < ? and time >  ?  order by time asc";

    @Override
    public int save(MetricDto metric) {
        return jdbcTemplate.update(INSERT,
                metric.getTime(), metric.getCpu(), metric.getMemory());
    }

    @Override
    public List<MetricDto> getByTimeInterval(long start, long end) {
        return jdbcTemplate.query(FETCH_METRICS, BeanPropertyRowMapper.newInstance(MetricDto.class),
                end, start);
    }
}