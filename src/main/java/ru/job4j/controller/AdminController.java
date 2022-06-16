package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.model.Session;
import ru.job4j.model.User;
import ru.job4j.service.SessionService;
import ru.job4j.service.TicketService;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class AdminController {

    private final TicketService ticketService;
    private final SessionService sessionService;

    public AdminController(TicketService ticketService, SessionService sessionService) {
        this.ticketService = ticketService;
        this.sessionService = sessionService;
    }

    @GetMapping("/administration")
    public String adminControl(Model model,
                               HttpSession sessionUser,
                               @RequestParam(name = "fail", required = false) Boolean fail) {
        model.addAttribute("fail", fail != null);
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("sessions", sessionService.findAll());
        return checkUser(sessionUser) ? "adminPage" : "redirect:/sessions";
    }

    @GetMapping("formAdminAdd")
    public String formAddFilm(Model model, HttpSession sessionUser) {
        model.addAttribute("user", getUser(sessionUser));
        return checkUser(sessionUser) ? "adminAddSession" : "redirect:/sessions";
    }

    @PostMapping("/addAdminFilm")
    public String createFilm(Model model, @ModelAttribute Session session, HttpSession sessionUser) {
        sessionService.add(session);
        model.addAttribute("user", getUser(sessionUser));
        return checkUser(sessionUser) ? "redirect:/administration" : "redirect:/sessions";
    }

    @GetMapping("/formUpdateFilm/{sesId}")
    public String formUpdateFilm(Model model, @PathVariable("sesId") int sesId, HttpSession sessionUser) {
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("session", sessionService.findById(sesId));
        return checkUser(sessionUser) ? "adminUpdateSession" : "redirect:/sessions";
    }

    @PostMapping("/updateAdminFilm")
    public String updateFilm(Model model, @ModelAttribute Session session, HttpSession sessionUser) {
        sessionService.update(session);
        model.addAttribute("user", getUser(sessionUser));
        return checkUser(sessionUser) ? "redirect:/administration" : "redirect:/sessions";
    }

    @GetMapping("/adminDeleteFilm/{sesId}")
    public String adminDeleteFilm(@PathVariable("sesId") int sesId, HttpSession sessionUser) {
        if ((ticketService.deleteTicketByIdFilm(sesId)
                && sessionService.deleteById(sesId)) || sessionService.deleteById(sesId)) {
            return checkUser(sessionUser) ? "redirect:/administration?fail=true" : "redirect:/sessions";
        }
        return checkUser(sessionUser) ? "redirect:/administration" : "redirect:/sessions";

    }

    @GetMapping("/adminTickets/{sesId}")
    public String adminDeleteTickets(@PathVariable("sesId") int sesId, HttpSession sessionUser) {
        if (ticketService.deleteTicketByIdFilm(sesId)) {
            return checkUser(sessionUser) ? "redirect:/administration?fail=true" : "redirect:/sessions";
        }
        return checkUser(sessionUser) ? "redirect:/administration" : "redirect:/sessions";
    }

    private User getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setUsername("Гость");
        }
        return user;
    }

    private boolean checkUser(HttpSession session) {
        User user = getUser(session);
        return "Admin".equals(user.getEmail()) && "Admin".equals(user.getPhone());
    }
}
