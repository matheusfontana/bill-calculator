package com.acme.billcalc.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OutRequestChange {
    private Map<Integer, Integer> billsGiven;
    private double totalAmountRequested;
    private Map<Double, Integer> retrievedCoins;
    private Map<Double, Integer> availableCoins;
}
