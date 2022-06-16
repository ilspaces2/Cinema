package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.model.Session;
import ru.job4j.model.User;
import ru.job4j.service.SessionService;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions")
    public String sessions(Model model, HttpSession sessionUser) {
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("sessions", sessionService.findAll());
        return "sessions";
    }

    @GetMapping("/selectSession/{sessionId}")
    public String selectSession(Model model, @PathVariable("sessionId") int sessionId, HttpSession sessionUser) {
        Session session = sessionService.findById(sessionId);
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("sessionInfo", session);
        model.addAttribute("matrix", sessionService.getMatrix(session));
        return "selectSeat";
    }

    private User getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setUsername("Гость");
        }
        return user;
    }
}
