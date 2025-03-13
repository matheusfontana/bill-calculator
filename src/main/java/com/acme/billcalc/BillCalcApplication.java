package com.acme.billcalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BillCalcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillCalcApplication.class, args);
	}
}
