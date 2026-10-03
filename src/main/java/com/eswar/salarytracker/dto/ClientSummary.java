package com.eswar.salarytracker.dto;

import java.math.BigDecimal;

public record ClientSummary(
        BigDecimal totalWorkingHours,
        BigDecimal grossSalary,
        BigDecimal totalAmountReceived,
        BigDecimal totalFullPaidHours,
        BigDecimal totalNotPaidHours,
        BigDecimal totalPartialPaidMonthHours,
        BigDecimal totalPartialAmountReceived,
        long fullPaidMonths,
        long partiallyPaidMonths,
        long notPaidMonths
) {
    public static ClientSummary empty() {
        return new ClientSummary(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                0,
                0
        );
    }
}