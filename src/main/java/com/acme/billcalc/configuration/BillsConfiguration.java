package com.acme.billcalc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "billcalc.bills")
@Data
public class BillsConfiguration {
    List<Integer> allowedBills;
}
