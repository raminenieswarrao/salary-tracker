package com.eswar.salarytracker.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false, length = 150)
    private String clientName;

    @Column(name = "actual_start_date", nullable = false)
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "client_pay_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal clientPayPerHour;

    @Column(name = "vendor_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal vendorPercentage = BigDecimal.ZERO;

    @Column(name = "pay_start_month", nullable = false)
    private LocalDate payStartMonth;

    @Column(name = "pay_end_month")
    private LocalDate payEndMonth;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getEffectivePayPerHour() {
        if (clientPayPerHour == null || vendorPercentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal retainedPercentage = new BigDecimal("100").subtract(vendorPercentage);
        return clientPayPerHour.multiply(retainedPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDate getPayStartMonth() { return payStartMonth; }
    public void setPayStartMonth(LocalDate payStartMonth) { this.payStartMonth = payStartMonth; }
    public LocalDate getPayEndMonth() { return payEndMonth; }
    public void setPayEndMonth(LocalDate payEndMonth) { this.payEndMonth = payEndMonth; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
