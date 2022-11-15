package com.monitoring.utility;

import com.monitoring.model.MetricDto;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.time.Instant;

public class MetricCapture {

    private static final OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();


    public static MetricDto capture() {
        MetricDto capturedMetric = new MetricDto.Builder()
                .time(Instant.now().getEpochSecond())
                .cpuUsage((float) bean.getSystemCpuLoad())
                .memoryUsage((float) (bean.getTotalPhysicalMemorySize() - bean.getFreePhysicalMemorySize())
                        /bean.getTotalPhysicalMemorySize())
                .build();

        return capturedMetric;
    }



}
