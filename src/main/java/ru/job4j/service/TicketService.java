package ru.job4j.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.model.Ticket;
import ru.job4j.store.TicketDBStore;

import java.util.List;
import java.util.Optional;

@ThreadSafe
@Service
public class TicketService {

    private final TicketDBStore store;

    public TicketService(TicketDBStore store) {
        this.store = store;
    }

    public Optional<Ticket> add(Ticket ticket) {
        return store.add(ticket);
    }

    public Optional<Ticket> findById(int id) {
        return store.findById(id);
    }

    public boolean deleteAll() {
        return store.deleteAll();
    }

    public List<String> findTicketsByIdUser(int id) {
        return store.findTicketsByIdUser(id);
    }

    public boolean deleteTicketByIdFilm(int id) {
        return store.deleteTicketByIdFilm(id);
    }
}
