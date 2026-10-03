package com.eswar.salarytracker.entity;

public enum PaymentStatus {
    FULL_PAID("Full Paid"),
    PARTIALLY_PAID("Partially Paid"),
    NOT_PAID("Not Paid");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
