package ru.job4j.store;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.job4j.Main;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class TicketDBStoreTest {

    private static final Session SESSION = new Session(0, "Film", 4, 4);
    private static final User USER = new User(0, "1", "2", "3");
    private static final TicketDBStore STORE_TICKET = new TicketDBStore(new Main().loadPool());
    private static final UserDBStore STORE_USER = new UserDBStore(new Main().loadPool());

    private static final SessionDBStore STORE_SESSION = new SessionDBStore(new Main().loadPool());

    @Before
    public void addTable() {
        STORE_SESSION.add(SESSION);
        STORE_USER.add(USER);
    }

    @After
    public void clearTable() {
        STORE_TICKET.deleteAll();
        STORE_USER.deleteAll();
        STORE_SESSION.deleteAll();
    }

    @Test
    public void whenAddTicket() {
        Ticket ticket = new Ticket(0, SESSION.getId(), 2, 3, USER.getId());
        STORE_TICKET.add(ticket);
        Optional<Ticket> ticketDB = STORE_TICKET.findById(ticket.getId());
        assertThat(ticket.getSessionId(), is(ticketDB.get().getSessionId()));
        assertThat(ticket.getPosRow(), is(ticketDB.get().getPosRow()));
        assertThat(ticket.getCell(), is(ticketDB.get().getCell()));
        assertThat(ticket.getUserId(), is(ticketDB.get().getUserId()));
    }

    @Test
    public void whenFindTicketsByIdUser() {
        Ticket ticket = new Ticket(0, SESSION.getId(), 2, 3, USER.getId());
        STORE_TICKET.add(ticket);
        List<String> tickets = STORE_TICKET.findTicketsByIdUser(USER.getId());
        String expected = String.format("№ билета: %d | Фильм: %s | Ряд: %d | Место: %d",
                ticket.getId(),
                SESSION.getName(),
                ticket.getPosRow(),
                ticket.getCell()
        );
        assertThat(tickets.get(0), is(expected));
    }

    @Test
    public void whenDeleteTicketByIdFilm() {
        Ticket ticket = new Ticket(0, SESSION.getId(), 2, 3, USER.getId());
        STORE_TICKET.add(ticket);
        assertTrue(STORE_TICKET.deleteTicketByIdFilm(SESSION.getId()));
        assertTrue(STORE_TICKET.findById(ticket.getId()).isEmpty());
    }
}