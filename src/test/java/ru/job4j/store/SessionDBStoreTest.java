package ru.job4j.store;

import org.junit.After;
import org.junit.Test;
import ru.job4j.Main;
import ru.job4j.model.Session;
import ru.job4j.model.Ticket;
import ru.job4j.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class SessionDBStoreTest {

    private static final TicketDBStore STORE_TICKET = new TicketDBStore(new Main().loadPool());

    private static final UserDBStore STORE_USER = new UserDBStore(new Main().loadPool());

    private static final SessionDBStore STORE_SESSION = new SessionDBStore(new Main().loadPool());

    @After
    public void clearSessionTable() {
        STORE_TICKET.deleteAll();
        STORE_USER.deleteAll();
        STORE_SESSION.deleteAll();
    }

    @Test
    public void whenAddSession() {
        Session session = new Session(0, "Film", 10, 10);
        STORE_SESSION.add(session);
        Session sessionDB = STORE_SESSION.findById(session.getId());
        assertThat(session.getName(), is(sessionDB.getName()));
        assertThat(session.getNumberOfRow(), is(sessionDB.getNumberOfRow()));
        assertThat(session.getNumberOfCell(), is(sessionDB.getNumberOfCell()));
    }

    @Test
    public void whenFindAll() {
        Session session1 = new Session(0, "Film1", 1, 1);
        Session session2 = new Session(0, "Film2", 2, 2);
        Session session3 = new Session(0, "Film3", 3, 3);
        STORE_SESSION.add(session1);
        STORE_SESSION.add(session2);
        STORE_SESSION.add(session3);
        assertThat(List.of(
                new Session(session1.getId(), "Film1", 1, 1),
                new Session(session2.getId(), "Film2", 2, 2),
                new Session(session3.getId(), "Film3", 3, 3)
        ), is(STORE_SESSION.findAll()));
    }

    @Test
    public void whenDeleteAll() {
        Session session1 = new Session(0, "Film1", 1, 1);
        Session session2 = new Session(0, "Film2", 2, 2);
        Session session3 = new Session(0, "Film3", 3, 3);
        STORE_SESSION.add(session1);
        STORE_SESSION.add(session2);
        STORE_SESSION.add(session3);
        assertTrue(STORE_SESSION.deleteAll());
        assertTrue(STORE_SESSION.findAll().isEmpty());
    }

    @Test
    public void whenDeleteByID() {
        Session session = new Session(0, "Film", 10, 10);
        STORE_SESSION.add(session);
        assertTrue(STORE_SESSION.deleteById(session.getId()));
        assertNull(STORE_SESSION.findById(session.getId()));
    }

    @Test
    public void whenUpdate() {
        Session session = new Session(0, "Film", 10, 10);
        STORE_SESSION.add(session);
        session.setName("FilmUpdate");
        session.setNumberOfRow(20);
        session.setNumberOfCell(20);
        STORE_SESSION.update(session);
        Session sessionDB = STORE_SESSION.findById(session.getId());
        assertThat(sessionDB.getName(), is(session.getName()));
        assertThat(sessionDB.getNumberOfRow(), is(session.getNumberOfRow()));
        assertThat(sessionDB.getNumberOfCell(), is(session.getNumberOfCell()));
    }

    @Test
    public void whenGetMatrix() {
        Session session = new Session(0, "Film", 5, 5);
        STORE_SESSION.add(session);
        User user = new User(0, "1", "2", "3");
        STORE_USER.add(user);
        Ticket ticket = new Ticket(0, session.getId(), 2, 3, user.getId());
        STORE_TICKET.add(ticket);
        boolean[][] matrix = new boolean[session.getNumberOfCell()][session.getNumberOfRow()];
        for (int row = 0; row < matrix.length; row++) {
            for (int cell = 0; cell < matrix[row].length; cell++) {
                if ((row == ticket.getPosRow() - 1) && (cell == ticket.getCell() - 1)) {
                    matrix[row][cell] = true;
                    break;
                }
                matrix[row][cell] = false;
            }
        }
        assertArrayEquals(matrix, STORE_SESSION.getMatrix(session));
    }
}