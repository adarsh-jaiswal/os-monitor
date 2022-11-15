package com.monitoring.service.scheduler;

import com.monitoring.service.metrics.MetricsStateManager;
import com.monitoring.utility.MetricCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CaptureMetricsScheduler {

    @Autowired
    private MetricsStateManager metricsStateManager;

    @Scheduled(fixedRateString = "${fixed.rate}", initialDelayString = "${fixed.initial.delay}")
    public void scheduledCaptureMetrics() {
        metricsStateManager.updateMetrics(MetricCapture.capture());
    }
}
