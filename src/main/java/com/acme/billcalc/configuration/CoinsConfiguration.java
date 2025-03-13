package com.acme.billcalc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "billcalc.coins")
@Data
public class CoinsConfiguration {
    Map<Double, Integer> availableCoins;
}
