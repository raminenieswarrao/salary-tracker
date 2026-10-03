package com.eswar.salarytracker.controller;

import com.eswar.salarytracker.dto.ClientForm;
import com.eswar.salarytracker.entity.Client;
import com.eswar.salarytracker.service.ClientService;
import com.eswar.salarytracker.service.MonthlyRecordService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final MonthlyRecordService recordService;

    public ClientController(ClientService clientService, MonthlyRecordService recordService) {
        this.clientService = clientService;
        this.recordService = recordService;
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        Client client = clientService.getRequired(id);
        model.addAttribute("client", client);
        model.addAttribute("summary", clientService.calculateSummary(id));
        model.addAttribute("records", recordService.forClient(id));
        return "clients/profile";
    }

    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("clientForm", new ClientForm());
        model.addAttribute("pageTitle", "Add Client");
        model.addAttribute("editMode", false);
        return "clients/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("clientForm") ClientForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add Client");
            model.addAttribute("editMode", false);
            return "clients/form";
        }
        try {
            Client client = clientService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Client added successfully.");
            return "redirect:/clients/" + client.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("formError", ex.getMessage());
            model.addAttribute("pageTitle", "Add Client");
            model.addAttribute("editMode", false);
            return "clients/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editClient(@PathVariable Long id, Model model) {
        Client client = clientService.getRequired(id);
        model.addAttribute("client", client);
        model.addAttribute("clientForm", clientService.toForm(client));
        model.addAttribute("pageTitle", "Edit Client");
        model.addAttribute("editMode", true);
        return "clients/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("clientForm") ClientForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("client", clientService.getRequired(id));
            model.addAttribute("pageTitle", "Edit Client");
            model.addAttribute("editMode", true);
            return "clients/form";
        }
        try {
            clientService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Client updated successfully.");
            return "redirect:/clients/" + id;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("client", clientService.getRequired(id));
            model.addAttribute("formError", ex.getMessage());
            model.addAttribute("pageTitle", "Edit Client");
            model.addAttribute("editMode", true);
            return "clients/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Client client = clientService.getRequired(id);
        String name = client.getClientName();
        clientService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", name + " and all monthly records were deleted.");
        return "redirect:/";
    }
}
