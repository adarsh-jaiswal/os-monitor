package com.monitoring.controller;

import com.monitoring.model.MetricDto;
import com.monitoring.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping(path = "/metrics")
    public ResponseEntity<List<MetricDto>> getAllMetrics() {
        return new ResponseEntity<>(metricsService.getMetrics(), HttpStatus.OK);
    }


}
