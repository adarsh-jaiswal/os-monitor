package com.monitoring.repository;

import com.monitoring.model.MetricDto;

import java.util.List;

public interface MetricsDtoRepository {

    int save(MetricDto metricDto);

    List<MetricDto> getByTimeInterval(long start, long end);

}
