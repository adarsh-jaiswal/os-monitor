package com.monitoring.service.scheduler;

import com.monitoring.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CaptureMetricsScheduler {

    @Autowired
    private MetricsService metricsService;

    @Scheduled(fixedRateString = "${fixed.rate}", initialDelayString = "${fixed.initial.delay}")
    public void scheduledCaptureMetrics() {
        metricsService.saveCapturedMetrics();
    }
}
