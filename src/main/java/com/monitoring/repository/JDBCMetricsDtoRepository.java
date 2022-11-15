package com.monitoring.repository;

import com.monitoring.model.MetricDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JDBCMetricsDtoRepository implements MetricsDtoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(MetricDto metric) {
        return jdbcTemplate.update("INSERT INTO metrics (time, cpu, memory) VALUES(?,?,?)",
                new Object[] { metric.getTime(), metric.getCpu(), metric.getMemory() });
    }

    @Override
    public List<MetricDto> getByTimeInterval(long start, long end) {
        return jdbcTemplate.query("SELECT * from metrics where time < " + end +" and time > " + start + " order by time asc",
                BeanPropertyRowMapper.newInstance(MetricDto.class));
    }
}