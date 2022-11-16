package com.monitoring.utility;

import com.monitoring.model.MetricDto;
import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.monitoring.utility.FieldSetter.setFinalStatic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricCaptureTest {

    @Mock
    private static OperatingSystemMXBean operatingSystemMXBean;

    @InjectMocks
    private MetricCapture metricCapture = new MetricCapture();

    @Test
    void capture() throws Exception {
        setFinalStatic(MetricCapture.class.getDeclaredField("bean"), operatingSystemMXBean);
        double cpuUsage = 0.89;
        when(operatingSystemMXBean.getSystemCpuLoad()).thenReturn(cpuUsage);
        when(operatingSystemMXBean.getTotalPhysicalMemorySize()).thenReturn(1000l);
        when(operatingSystemMXBean.getFreePhysicalMemorySize()).thenReturn(400l);

        MetricDto metric = metricCapture.capture();

        assertThat(metric.getTime(), is(not(0l)));
        assertThat(metric.getCpu(), is(0.89f));
        assertThat(metric.getMemory(), is(0.6f));
    }
}