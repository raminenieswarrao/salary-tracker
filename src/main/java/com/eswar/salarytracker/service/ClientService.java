package com.eswar.salarytracker.service;

import com.eswar.salarytracker.dto.ClientForm;
import com.eswar.salarytracker.dto.ClientSummary;
import com.eswar.salarytracker.entity.Client;
import com.eswar.salarytracker.entity.MonthlyPaymentRecord;
import com.eswar.salarytracker.entity.PaymentStatus;
import com.eswar.salarytracker.repository.ClientRepository;
import com.eswar.salarytracker.repository.MonthlyPaymentRecordRepository;
import com.eswar.salarytracker.util.MonthUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final MonthlyPaymentRecordRepository recordRepository;

    public ClientService(ClientRepository clientRepository,
                         MonthlyPaymentRecordRepository recordRepository) {
        this.clientRepository = clientRepository;
        this.recordRepository = recordRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAllByOrderByClientNameAsc();
    }

    public Client getRequired(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + id));
    }

    public Map<Long, ClientSummary> summariesForAllClients() {
        Map<Long, ClientSummary> summaries = new LinkedHashMap<>();
        for (Client client : findAll()) {
            summaries.put(client.getId(), calculateSummary(client.getId()));
        }
        return summaries;
    }

    public ClientSummary calculateSummary(Long clientId) {
        List<MonthlyPaymentRecord> records = recordRepository.findByClientIdOrderByWorkMonthDesc(clientId);
        if (records.isEmpty()) {
            return ClientSummary.empty();
        }

        BigDecimal totalWorkingHours = BigDecimal.ZERO;
        BigDecimal totalAmountReceived = BigDecimal.ZERO;
        BigDecimal totalFullPaidHours = BigDecimal.ZERO;
        BigDecimal totalNotPaidHours = BigDecimal.ZERO;
        BigDecimal totalPartialHours = BigDecimal.ZERO;
        BigDecimal totalPartialAmount = BigDecimal.ZERO;
        long fullMonths = 0;
        long partialMonths = 0;
        long unpaidMonths = 0;

        for (MonthlyPaymentRecord record : records) {
            BigDecimal hours = nz(record.getWorkingHours());
            BigDecimal amount = nz(record.getAmountCredited());
            totalWorkingHours = totalWorkingHours.add(hours);

            if (record.getPaymentStatus() == PaymentStatus.FULL_PAID) {
                totalFullPaidHours = totalFullPaidHours.add(hours);
                totalAmountReceived = totalAmountReceived.add(amount);
                fullMonths++;
            } else if (record.getPaymentStatus() == PaymentStatus.PARTIALLY_PAID) {
                totalNotPaidHours = totalNotPaidHours.add(hours);
                totalPartialHours = totalPartialHours.add(hours);
                totalPartialAmount = totalPartialAmount.add(amount);
                totalAmountReceived = totalAmountReceived.add(amount);
                partialMonths++;
            } else if (record.getPaymentStatus() == PaymentStatus.NOT_PAID) {
                totalNotPaidHours = totalNotPaidHours.add(hours);
                unpaidMonths++;
            }
        }

        Client client = getRequired(clientId);

        BigDecimal grossSalary = totalWorkingHours
                .multiply(nz(client.getEffectivePayPerHour()));

        return new ClientSummary(
                totalWorkingHours,
                grossSalary,
                totalAmountReceived,
                totalFullPaidHours,
                totalNotPaidHours,
                totalPartialHours,
                totalPartialAmount,
                fullMonths,
                partialMonths,
                unpaidMonths
        );
    }

    @Transactional
    public Client create(ClientForm form) {
        validateDates(form);
        Client client = new Client();
        applyForm(client, form);
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Long id, ClientForm form) {
        validateDates(form);
        Client client = getRequired(id);
        applyForm(client, form);
        return clientRepository.save(client);
    }

    @Transactional
    public void delete(Long id) {
        Client client = getRequired(id);
        clientRepository.delete(client);
    }

    public ClientForm toForm(Client client) {
        ClientForm form = new ClientForm();
        form.setClientName(client.getClientName());
        form.setActualStartDate(client.getActualStartDate());
        form.setActualEndDate(client.getActualEndDate());
        form.setClientPayPerHour(client.getClientPayPerHour());
        form.setVendorPercentage(client.getVendorPercentage());
        form.setPayEndMonth(MonthUtil.formatMonthInput(client.getPayEndMonth()));
        form.setNotes(client.getNotes());
        return form;
    }

    private void applyForm(Client client, ClientForm form) {
        client.setClientName(form.getClientName().trim());
        client.setActualStartDate(form.getActualStartDate());
        client.setActualEndDate(form.getActualEndDate());
        client.setClientPayPerHour(form.getClientPayPerHour());
        client.setVendorPercentage(form.getVendorPercentage());
        client.setPayStartMonth(form.getActualStartDate().withDayOfMonth(1));
        LocalDate payEndMonth = MonthUtil.parseMonth(form.getPayEndMonth());
        if (payEndMonth == null && form.getActualEndDate() != null) {
            payEndMonth = form.getActualEndDate().withDayOfMonth(1);
        }
        client.setPayEndMonth(payEndMonth);
        client.setNotes(blankToNull(form.getNotes()));
    }

    private void validateDates(ClientForm form) {
        LocalDate start = form.getActualStartDate();
        LocalDate end = form.getActualEndDate();
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("Actual end date cannot be before the actual start date.");
        }

        LocalDate payEnd = MonthUtil.parseMonth(form.getPayEndMonth());
        if (start != null && payEnd != null && payEnd.isBefore(start.withDayOfMonth(1))) {
            throw new IllegalArgumentException("Pay end month cannot be before the client start month.");
        }
    }

    private static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
