package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.service.SessionService;

@ThreadSafe
@Controller
public class IndexController {

    private final SessionService sessionService;

    public IndexController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("sessions", sessionService.findAll());
        return "index";
    }
}
