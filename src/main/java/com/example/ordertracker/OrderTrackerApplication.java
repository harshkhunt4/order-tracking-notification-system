package com.example.ordertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OrderTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderTrackerApplication.class, args);

	}
}
