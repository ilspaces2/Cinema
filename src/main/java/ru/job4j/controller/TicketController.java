package ru.job4j.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;
import ru.job4j.service.TicketService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@ThreadSafe
@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/seat/{sessionId}/{sessionName}/{row}/{cell}")
    public String selectTicket(Model model,
                               @PathVariable("sessionId") int sessionId,
                               @PathVariable("sessionName") String sessionName,
                               @PathVariable("row") int row,
                               @PathVariable("cell") int cell,
                               HttpSession sessionUser) {
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("sessionName", sessionName);
        model.addAttribute("row", row);
        model.addAttribute("cell", cell);
        model.addAttribute("user", getUser(sessionUser));
        return "confirmTicket";
    }

    @PostMapping("/confirmTicket")
    public String confirmTicket(Model model, HttpSession sessionUser, @ModelAttribute Ticket ticket) {
        Optional<Ticket> ticketOptional = ticketService.add(ticket);
        if (ticketOptional.isEmpty()) {
            model.addAttribute("message", "Билет не куплен.");
            return "failConfirm";
        }
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("message", "Билет куплен.");
        return "successConfirm";
    }

    @GetMapping("/myTickets")
    public String showMyTickets(Model model, HttpSession sessionUser) {
        model.addAttribute("user", getUser(sessionUser));
        model.addAttribute("myTickets", ticketService.findTicketsByIdUser(getUser(sessionUser).getId()));
        return "myTickets";
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
