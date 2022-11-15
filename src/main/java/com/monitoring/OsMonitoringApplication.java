package com.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OsMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsMonitoringApplication.class, args);
	}

}
