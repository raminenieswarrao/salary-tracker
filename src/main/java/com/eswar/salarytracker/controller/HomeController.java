package com.eswar.salarytracker.controller;

import com.eswar.salarytracker.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClientService clientService;

    public HomeController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("summaries", clientService.summariesForAllClients());
        return "dashboard";
    }
}
