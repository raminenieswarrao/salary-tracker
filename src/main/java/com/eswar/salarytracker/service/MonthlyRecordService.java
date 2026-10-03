package com.eswar.salarytracker.service;

import com.eswar.salarytracker.dto.MonthlyRecordForm;
import com.eswar.salarytracker.entity.Client;
import com.eswar.salarytracker.entity.MonthlyPaymentRecord;
import com.eswar.salarytracker.entity.PaymentStatus;
import com.eswar.salarytracker.repository.MonthlyPaymentRecordRepository;
import com.eswar.salarytracker.util.MonthUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class MonthlyRecordService {

    private final MonthlyPaymentRecordRepository repository;
    private final ClientService clientService;

    public MonthlyRecordService(MonthlyPaymentRecordRepository repository,
                                ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    public MonthlyPaymentRecord getRequired(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly record not found: " + id));
    }

    public List<MonthlyPaymentRecord> forClient(Long clientId) {
        return repository.findByClientIdOrderByWorkMonthDesc(clientId);
    }

    public List<MonthlyPaymentRecord> filter(Long clientId,
                                             Integer year,
                                             Integer month,
                                             Integer paidYear,
                                             Integer paidMonth,
                                             PaymentStatus status) {

        return repository.findAllByOrderByWorkMonthDesc().stream()
                .filter(r -> clientId == null || Objects.equals(r.getClient().getId(), clientId))
                .filter(r -> year == null || r.getWorkMonth().getYear() == year)
                .filter(r -> month == null || r.getWorkMonth().getMonthValue() == month)
                .filter(r -> paidYear == null ||
                        (r.getPaidMonth() != null && r.getPaidMonth().getYear() == paidYear))
                .filter(r -> paidMonth == null ||
                        (r.getPaidMonth() != null && r.getPaidMonth().getMonthValue() == paidMonth))
                .filter(r -> status == null || r.getPaymentStatus() == status)
                .sorted(Comparator.comparing(MonthlyPaymentRecord::getWorkMonth).reversed()
                        .thenComparing(r -> r.getClient().getClientName()))
                .toList();
    }

    public List<Integer> availableYears() {
        return repository.findAllByOrderByWorkMonthDesc().stream()
                .map(r -> r.getWorkMonth().getYear())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public List<Integer> availablePaidYears() {
        return repository.findAllByOrderByWorkMonthDesc().stream()
                .map(MonthlyPaymentRecord::getPaidMonth)
                .filter(Objects::nonNull)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    @Transactional
    public MonthlyPaymentRecord create(MonthlyRecordForm form) {
        LocalDate workMonth = MonthUtil.parseMonth(form.getWorkMonth());
        validateForm(form, workMonth, null);

        if (repository.existsByClientIdAndWorkMonth(form.getClientId(), workMonth)) {
            throw new IllegalArgumentException("A record already exists for this client and work month.");
        }

        MonthlyPaymentRecord record = new MonthlyPaymentRecord();
        applyForm(record, form, workMonth);
        return repository.save(record);
    }

    @Transactional
    public MonthlyPaymentRecord update(Long id, MonthlyRecordForm form) {
        MonthlyPaymentRecord record = getRequired(id);
        LocalDate workMonth = MonthUtil.parseMonth(form.getWorkMonth());
        validateForm(form, workMonth, id);

        if (repository.existsByClientIdAndWorkMonthAndIdNot(form.getClientId(), workMonth, id)) {
            throw new IllegalArgumentException("A record already exists for this client and work month.");
        }

        applyForm(record, form, workMonth);
        return repository.save(record);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getRequired(id));
    }

    public MonthlyRecordForm toForm(MonthlyPaymentRecord record) {
        MonthlyRecordForm form = new MonthlyRecordForm();
        form.setClientId(record.getClient().getId());
        form.setWorkMonth(MonthUtil.formatMonthInput(record.getWorkMonth()));
        form.setWorkingHours(record.getWorkingHours());
        form.setAmountCredited(record.getAmountCredited());
        form.setPaymentStatus(record.getPaymentStatus());
        form.setPaidMonth(MonthUtil.formatMonthInput(record.getPaidMonth()));
        form.setNote(record.getNote());
        return form;
    }

    private void applyForm(MonthlyPaymentRecord record, MonthlyRecordForm form, LocalDate workMonth) {
        Client client = clientService.getRequired(form.getClientId());
        record.setClient(client);
        record.setWorkMonth(workMonth);
        record.setWorkingHours(form.getWorkingHours());
        record.setAmountCredited(form.getAmountCredited());
        record.setPaymentStatus(form.getPaymentStatus());
        record.setPaidMonth(form.getPaymentStatus() == PaymentStatus.NOT_PAID
                ? null : MonthUtil.parseMonth(form.getPaidMonth()));
        record.setNote(blankToNull(form.getNote()));
    }

    private void validateForm(MonthlyRecordForm form, LocalDate workMonth, Long recordId) {
        if (workMonth == null) {
            throw new IllegalArgumentException("Work month is required.");
        }

        Client client = clientService.getRequired(form.getClientId());
        LocalDate clientStart = client.getPayStartMonth();
        LocalDate clientEnd = client.getPayEndMonth();

        if (clientStart != null && workMonth.isBefore(clientStart)) {
            throw new IllegalArgumentException("Work month cannot be before the client's pay start month.");
        }
        if (clientEnd != null && workMonth.isAfter(clientEnd)) {
            throw new IllegalArgumentException("Work month cannot be after the client's pay end month.");
        }

        BigDecimal amount = form.getAmountCredited() == null ? BigDecimal.ZERO : form.getAmountCredited();
        PaymentStatus status = form.getPaymentStatus();
        LocalDate paidMonth = MonthUtil.parseMonth(form.getPaidMonth());

        if (status == PaymentStatus.NOT_PAID) {
            if (amount.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException("Not Paid records must have an amount credited of $0.00.");
            }
        } else {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(status.getLabel() + " records must have an amount credited greater than $0.00.");
            }
            if (paidMonth == null) {
                throw new IllegalArgumentException("Paid month is required for Full Paid and Partially Paid records.");
            }
            if (paidMonth.isBefore(workMonth)) {
                throw new IllegalArgumentException("Paid month cannot be before the work month.");
            }
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
