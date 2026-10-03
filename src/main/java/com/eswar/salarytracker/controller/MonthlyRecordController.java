package com.eswar.salarytracker.controller;

import com.eswar.salarytracker.dto.MonthlyRecordForm;
import com.eswar.salarytracker.entity.MonthlyPaymentRecord;
import com.eswar.salarytracker.entity.PaymentStatus;
import com.eswar.salarytracker.service.ClientService;
import com.eswar.salarytracker.service.MonthlyRecordService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Month;
import java.util.Arrays;

@Controller
@RequestMapping("/records")
public class MonthlyRecordController {

    private final MonthlyRecordService recordService;
    private final ClientService clientService;

    public MonthlyRecordController(MonthlyRecordService recordService, ClientService clientService) {
        this.recordService = recordService;
        this.clientService = clientService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long clientId,
                       @RequestParam(required = false) Integer year,
                       @RequestParam(required = false) Integer month,
                       @RequestParam(required = false) Integer paidYear,
                       @RequestParam(required = false) Integer paidMonth,
                       @RequestParam(required = false) PaymentStatus status,
                       Model model) {

        var records = recordService.filter(
                clientId,
                year,
                month,
                paidYear,
                paidMonth,
                status
        );

        var totalWorkingHours = records.stream()
                .map(MonthlyPaymentRecord::getWorkingHours)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        var totalEarned = records.stream()
                .map(MonthlyPaymentRecord::getAmountCredited)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("records", records);
        model.addAttribute("clients", clientService.findAll());

        model.addAttribute("years", recordService.availableYears());
        model.addAttribute("paidYears", recordService.availablePaidYears());

        model.addAttribute("months", Arrays.asList(Month.values()));
        model.addAttribute("statuses", PaymentStatus.values());

        model.addAttribute("selectedClientId", clientId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedPaidYear", paidYear);
        model.addAttribute("selectedPaidMonth", paidMonth);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("totalWorkingHours", totalWorkingHours);
        model.addAttribute("totalEarned", totalEarned);

        return "records/list";
    }

    @GetMapping("/new")
    public String newRecord(@RequestParam(required = false) Long clientId, Model model) {
        MonthlyRecordForm form = new MonthlyRecordForm();
        form.setClientId(clientId);
        form.setAmountCredited(java.math.BigDecimal.ZERO);
        model.addAttribute("recordForm", form);
        populateFormModel(model, false, null);
        return "records/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("recordForm") MonthlyRecordForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model, false, null);
            return "records/form";
        }
        try {
            MonthlyPaymentRecord record = recordService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Monthly record added successfully.");
            return "redirect:/clients/" + record.getClient().getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("formError", ex.getMessage());
            populateFormModel(model, false, null);
            return "records/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editRecord(@PathVariable Long id, Model model) {
        MonthlyPaymentRecord record = recordService.getRequired(id);
        model.addAttribute("record", record);
        model.addAttribute("recordForm", recordService.toForm(record));
        populateFormModel(model, true, record);
        return "records/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("recordForm") MonthlyRecordForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        MonthlyPaymentRecord existing = recordService.getRequired(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("record", existing);
            populateFormModel(model, true, existing);
            return "records/form";
        }
        try {
            MonthlyPaymentRecord updated = recordService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Monthly record updated successfully.");
            return "redirect:/clients/" + updated.getClient().getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("record", existing);
            model.addAttribute("formError", ex.getMessage());
            populateFormModel(model, true, existing);
            return "records/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) String returnTo,
                         RedirectAttributes redirectAttributes) {
        MonthlyPaymentRecord record = recordService.getRequired(id);
        Long clientId = record.getClient().getId();
        recordService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Monthly record deleted.");
        if ("transactions".equals(returnTo)) {
            return "redirect:/records";
        }
        return "redirect:/clients/" + clientId;
    }

    private void populateFormModel(Model model, boolean editMode, MonthlyPaymentRecord record) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("editMode", editMode);
        model.addAttribute("pageTitle", editMode ? "Edit Monthly Record" : "Add Monthly Record");
        if (record != null) {
            model.addAttribute("record", record);
        }
    }
}
