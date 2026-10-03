package com.eswar.salarytracker.repository;

import com.eswar.salarytracker.entity.MonthlyPaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyPaymentRecordRepository extends JpaRepository<MonthlyPaymentRecord, Long> {
    List<MonthlyPaymentRecord> findByClientIdOrderByWorkMonthDesc(Long clientId);
    List<MonthlyPaymentRecord> findAllByOrderByWorkMonthDesc();
    boolean existsByClientIdAndWorkMonth(Long clientId, LocalDate workMonth);
    boolean existsByClientIdAndWorkMonthAndIdNot(Long clientId, LocalDate workMonth, Long id);
    long countByClientId(Long clientId);
}
