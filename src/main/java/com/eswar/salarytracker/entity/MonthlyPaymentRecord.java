package com.eswar.salarytracker.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_payment_records",
        uniqueConstraints = @UniqueConstraint(name = "uq_client_work_month", columnNames = {"client_id", "work_month"})
)
public class MonthlyPaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "work_month", nullable = false)
    private LocalDate workMonth;

    @Column(name = "working_hours", nullable = false, precision = 8, scale = 2)
    private BigDecimal workingHours;

    @Column(name = "amount_credited", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountCredited = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "paid_month")
    private LocalDate paidMonth;

    @Column(name = "note")
    private String note;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDate getWorkMonth() { return workMonth; }
    public void setWorkMonth(LocalDate workMonth) { this.workMonth = workMonth; }
    public BigDecimal getWorkingHours() { return workingHours; }
    public void setWorkingHours(BigDecimal workingHours) { this.workingHours = workingHours; }
    public BigDecimal getAmountCredited() { return amountCredited; }
    public void setAmountCredited(BigDecimal amountCredited) { this.amountCredited = amountCredited; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDate getPaidMonth() { return paidMonth; }
    public void setPaidMonth(LocalDate paidMonth) { this.paidMonth = paidMonth; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
