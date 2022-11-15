package com.monitoring.service;

import com.monitoring.model.MetricDto;
import com.monitoring.service.metrics.MetricsStateManager;
import com.monitoring.utility.MetricCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricsService {

    @Autowired
    private MetricsStateManager metricsStateManager;

    public List<MetricDto> getMetrics() {
        List<MetricDto> metrics = metricsStateManager.getMetricsHistory();
        metrics.add(MetricCapture.capture());
        return metrics;
    }

    public void saveCapturedMetrics() {
        metricsStateManager.updateMetrics(MetricCapture.capture());
    }
}
