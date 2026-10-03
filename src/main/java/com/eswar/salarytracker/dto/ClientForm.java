package com.eswar.salarytracker.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClientForm {

    @NotBlank(message = "Client name is required")
    @Size(max = 150, message = "Client name must be 150 characters or fewer")
    private String clientName;

    @NotNull(message = "Actual start date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate actualStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate actualEndDate;

    @NotNull(message = "Client hourly rate is required")
    @DecimalMin(value = "0.01", message = "Client hourly rate must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal clientPayPerHour;

    @NotNull(message = "Vendor percentage is required")
    @DecimalMin(value = "0.00", message = "Vendor percentage cannot be negative")
    @DecimalMax(value = "100.00", message = "Vendor percentage cannot exceed 100")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal vendorPercentage = BigDecimal.ZERO;

    private String payEndMonth;

    @Size(max = 4000, message = "Notes are too long")
    private String notes;

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public LocalDate getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }
    public BigDecimal getClientPayPerHour() { return clientPayPerHour; }
    public void setClientPayPerHour(BigDecimal clientPayPerHour) { this.clientPayPerHour = clientPayPerHour; }
    public BigDecimal getVendorPercentage() { return vendorPercentage; }
    public void setVendorPercentage(BigDecimal vendorPercentage) { this.vendorPercentage = vendorPercentage; }
    public String getPayEndMonth() { return payEndMonth; }
    public void setPayEndMonth(String payEndMonth) { this.payEndMonth = payEndMonth; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
