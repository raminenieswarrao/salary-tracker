package com.eswar.salarytracker.dto;

import com.eswar.salarytracker.entity.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class MonthlyRecordForm {

    @NotNull(message = "Client is required")
    private Long clientId;

    @NotBlank(message = "Work month is required")
    private String workMonth;

    @NotNull(message = "Working hours are required")
    @DecimalMin(value = "0.00", message = "Working hours cannot be negative")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal workingHours;

    @NotNull(message = "Amount credited is required")
    @DecimalMin(value = "0.00", message = "Amount credited cannot be negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amountCredited = BigDecimal.ZERO;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    private String paidMonth;

    @Size(max = 4000, message = "Note is too long")
    private String note;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getWorkMonth() { return workMonth; }
    public void setWorkMonth(String workMonth) { this.workMonth = workMonth; }
    public BigDecimal getWorkingHours() { return workingHours; }
    public void setWorkingHours(BigDecimal workingHours) { this.workingHours = workingHours; }
    public BigDecimal getAmountCredited() { return amountCredited; }
    public void setAmountCredited(BigDecimal amountCredited) { this.amountCredited = amountCredited; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaidMonth() { return paidMonth; }
    public void setPaidMonth(String paidMonth) { this.paidMonth = paidMonth; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
