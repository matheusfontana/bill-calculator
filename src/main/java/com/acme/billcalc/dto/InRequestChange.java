package com.acme.billcalc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Map;

@Data
public class InRequestChange {

    @NotNull
    private Map<Integer, Integer> billAmount;

    @Nullable
    private boolean mostAmountRequested = false;
}
