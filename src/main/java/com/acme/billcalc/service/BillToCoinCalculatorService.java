package com.acme.billcalc.service;

import com.acme.billcalc.configuration.BillsConfiguration;
import com.acme.billcalc.configuration.CoinsConfiguration;
import com.acme.billcalc.dto.OutRequestChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({CoinsConfiguration.class, BillsConfiguration.class})
public class BillToCoinCalculatorService {

    private final CoinsConfiguration coinsConfiguration;
    private final BillsConfiguration billsConfiguration;
    private final Map<Double, Integer> retrievedCoins = new LinkedHashMap<>();

    public OutRequestChange billToCoinCalculator(Map<Integer, Integer> bills, boolean mostAmount) {
        if (!validateBills(bills)) {
            throw new IllegalArgumentException("Invalid bill sent as parameter");
        }

        makeChange(bills, mostAmount);
        return OutRequestChange.builder()
            .billsGiven(bills)
            .totalAmountRequested(getTotalAmount(bills))
            .retrievedCoins(retrievedCoins)
            .availableCoins(getCoinInformation(mostAmount))
            .build();
    }

    protected void makeChange(Map<Integer, Integer> bills, boolean mostAmount) {
        HashMap<Double, Integer> coins = (HashMap<Double, Integer>) getCoinInformation(mostAmount);
        log.info("Available coins: {}", coins.toString());
        double totalBillAmount = getTotalAmount(bills);
        log.info("Total amount requested for change: {}", totalBillAmount);
        double totalCoinsAmount = getTotalAmount(coins);
        log.info("Total amount available for change: {}", totalCoinsAmount);

        if (totalBillAmount > totalCoinsAmount) {
            log.error("Not enough coins to make change");
            throw new IllegalArgumentException("Not enough coins to make change");
        } else {
            deductAmountOfCoinsNeeded(totalBillAmount, mostAmount);
        }
    }

    protected boolean validateBills(Map<Integer, Integer> bills) {
        return bills.entrySet().stream().allMatch(index -> billsConfiguration.getAllowedBills().contains(index.getKey()));
    }

    protected void deductAmountOfCoinsNeeded(double bill, boolean mostAmount){
        final AtomicReference<Double> amountExtracted = new AtomicReference<Double>(0.00);
        getCoinInformation(mostAmount)
            .entrySet()
            .stream()
            .forEach(doubleIntegerEntry -> {
                double currentCoin = doubleIntegerEntry.getKey();
                Integer currentValueAmount = doubleIntegerEntry.getValue();
                log.info("Bill total sum: {}", bill);
                log.info("Current amount of {} coins: {}", currentCoin, currentValueAmount);
                while(amountExtracted.get() < bill) {
                    DecimalFormat dec = new DecimalFormat("#0.00");
                    if (currentValueAmount == 0) {
                       break;
                    }
                    log.info("Deducting {} from storage", currentCoin);
                    deductAvailableCoins(currentCoin);
                    currentValueAmount--;
                    amountExtracted.set(Double.valueOf(dec.format(amountExtracted.get() + currentCoin)));
                    log.info("Adding retrieved coin into stats");
                    retrievedCoins.put(currentCoin, retrievedCoins.getOrDefault(currentCoin, 0) + 1);
                    log.info("Total amountExtracted: {}", amountExtracted.get());
                }
            });
        log.info("AmountExtracted: {}", amountExtracted.get());
        log.info("getCurrentCoinInformation getting the most amount={}: {}", mostAmount, getCoinInformation(mostAmount).toString());
    }

    protected void deductAvailableCoins(double key) {
        Integer coinsLeft = coinsConfiguration.getAvailableCoins().get(key);
        if (coinsLeft > 0) {
            coinsConfiguration.getAvailableCoins().put(key, coinsConfiguration.getAvailableCoins().get(key) - 1);
        } else {
            log.warn("No more coins of {} available", key);
        }
    }

    public Map<Double, Integer> getCoinInformation(boolean ascendingOrder){
        Comparator<Map.Entry<Double, Integer>> comparator = ascendingOrder ? Map.Entry.comparingByKey() : Map.Entry.<Double, Integer>comparingByKey().reversed();

        return coinsConfiguration.getAvailableCoins()
            .entrySet()
            .stream()
            .sorted(comparator)
            .collect(Collectors.
                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                    LinkedHashMap::new));
    }

    protected int getTotalAmount(Map<Integer, Integer> amount) {
        return amount.entrySet().stream().mapToInt(index -> index.getKey() * index.getValue()).sum();
    }

    protected double getTotalAmount(HashMap<Double, Integer> amount) {
        return amount.entrySet().stream().mapToInt(index ->  (int) (index.getKey() * index.getValue())).sum();
    }
}
