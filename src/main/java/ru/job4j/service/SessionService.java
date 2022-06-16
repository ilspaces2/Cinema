package ru.job4j.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.model.Session;
import ru.job4j.store.SessionDBStore;

import java.util.List;

@ThreadSafe
@Service
public class SessionService {

    private final SessionDBStore store;

    public SessionService(SessionDBStore store) {
        this.store = store;
    }

    public Session add(Session session) {
        return store.add(session);
    }

    public Session findById(int id) {
        return store.findById(id);
    }

    public List<Session> findAll() {
        return store.findAll();
    }

    public boolean deleteAll() {
        return store.deleteAll();
    }

    public boolean deleteById(int id) {
        return store.deleteById(id);
    }

    public boolean[][] getMatrix(Session session) {
        return store.getMatrix(session);
    }

    public boolean update(Session session) {
        return store.update(session);
    }
}
