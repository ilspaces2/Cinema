package ru.job4j.controller;

import org.junit.Test;
import org.springframework.ui.Model;
import ru.job4j.model.Ticket;
import ru.job4j.service.TicketService;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class TicketControllerTest {

    @Test
    public void whenSelectTicket() {
        int sessionId = 1;
        String sessionName = "Film";
        int row = 1;
        int cell = 1;
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        TicketService ticketService = mock(TicketService.class);
        TicketController ticketController = new TicketController(ticketService);
        String page = ticketController.selectTicket(
                model,
                sessionId,
                "Film",
                row,
                cell,
                session
        );
        verify(model).addAttribute("sessionId", sessionId);
        verify(model).addAttribute("sessionName", sessionName);
        verify(model).addAttribute("row", row);
        verify(model).addAttribute("cell", cell);
        assertThat(page, is("confirmTicket"));
    }

    @Test
    public void whenConfirmTicketThenSuccess() {
        Ticket ticket = new Ticket(1, 1, 4, 4, 1);
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        TicketService ticketService = mock(TicketService.class);
        when(ticketService.add(any(Ticket.class))).thenReturn(Optional.of(ticket));
        TicketController ticketController = new TicketController(ticketService);
        String page = ticketController.confirmTicket(model, session, ticket);
        verify(model).addAttribute("message", "Билет куплен.");
        assertThat(page, is("successConfirm"));
    }

    @Test
    public void whenConfirmTicketThenFail() {
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        TicketService ticketService = mock(TicketService.class);
        TicketController ticketController = new TicketController(ticketService);
        String page = ticketController.confirmTicket(model, session, null);
        verify(model).addAttribute("message", "Билет не куплен.");
        assertThat(page, is("failConfirm"));
    }

    @Test
    public void whenShowMyTickets() {
        List<String> listTickets = List.of("1", "2", "3");
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        TicketService ticketService = mock(TicketService.class);
        when(ticketService.findTicketsByIdUser(any(Integer.class))).thenReturn(listTickets);
        TicketController ticketController = new TicketController(ticketService);
        String page = ticketController.showMyTickets(model, session);
        verify(model).addAttribute("myTickets", listTickets);
        assertThat(page, is("myTickets"));
    }
}