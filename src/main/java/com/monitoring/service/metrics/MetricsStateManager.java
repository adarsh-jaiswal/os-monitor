package com.monitoring.service.metrics;

import com.monitoring.model.MetricDto;
import com.monitoring.repository.MetricsDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;

/**
 * To maintain the metrics history cache. This class uses write-through cache technique.
 */
@Component
public class MetricsStateManager {

    private static final Logger LOGGER = Logger.getLogger(MetricsStateManager.class.getName());

    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock readLock = rwl.readLock();
    private static final Lock writeLock = rwl.writeLock();

    private static final Integer DURATION = 86_400; //24 hrs in seconds

    /**
     * metricsHistory is the cache which maintains a metrics queue of past 24hrs.
     */
    private Deque<MetricDto> metricsHistory;

    @Autowired
    private MetricsDtoRepository metricsDtoRepository;


    /**
     *  To be called every 30 sec from the scheduler.
     *  Adds a metric record in the cache and db. Also checks if the difference between the
     *  first metric recorded time in the queue minus the current time is more than the DURATION i.e 24hrs
     *  then discard the first metric from the queue.
     *
     * @param metric
     */
    public void updateMetrics(MetricDto metric) {
        if (!saveMetrics(metric)) return;

        writeLock.lock();
        try {
            long currTime = metric.getTime();
            if (!metricsHistory.isEmpty() && (currTime - metricsHistory.peekFirst().getTime()) >=  DURATION) {
                metricsHistory.pollFirst();
            }
            metricsHistory.offerLast(metric);
        } finally {
            writeLock.unlock();
        }

    }

    public List<MetricDto> getMetricsHistory() {
        return deepCopyMetricsHistory();
    }

    /**
     * To be called when the user wants all the metrics records of past 24hrs.
     * Creates a copy of the cache in thread safe manner to allow multiple reads only when no write is performed.
     * All reads will wait when a write operation is performed.
     *
     * @return
     */
    private List<MetricDto> deepCopyMetricsHistory() {
        readLock.lock();
        try {
            return this.metricsHistory.parallelStream().collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Called once from the constructor during bean initialization.
     * To populate the metrics cache with records lying between the current time and current time - 24hrs.
     *
     * @return
     */
    @PostConstruct
    void updateHistoryFromDisk() {
        long now = Instant.now().getEpochSecond();
        try {
            this.metricsHistory = new ArrayDeque<>(metricsDtoRepository.getByTimeInterval(now - DURATION, now));
        } catch (DataAccessException e) {
            LOGGER.log(SEVERE, "Error in retrieving metrics");
            LOGGER.log(SEVERE, e.getMessage());
            this.metricsHistory = new ArrayDeque<>();
        }
    }

    private boolean saveMetrics(MetricDto metric) {
        try {
            metricsDtoRepository.save(metric);
            return true;
        } catch (DataAccessException e) {
            LOGGER.log(SEVERE, "Error in saving metrics");
            LOGGER.log(SEVERE, e.getMessage());
            return false;
        }
    }

}
