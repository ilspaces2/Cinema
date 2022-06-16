package ru.job4j.controller;

import org.junit.Test;
import org.springframework.ui.Model;
import ru.job4j.model.Session;
import ru.job4j.service.SessionService;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class SessionControllerTest {

    @Test
    public void whenSession() {
        List<Session> sessionList = Arrays.asList(
                new Session(1, "Film1", 10, 10),
                new Session(2, "Film2", 5, 10),
                new Session(3, "Film3", 4, 2)
        );
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        SessionService sessionService = mock(SessionService.class);
        when(sessionService.findAll()).thenReturn(sessionList);
        SessionController sessionController = new SessionController(sessionService);
        String page = sessionController.sessions(model, session);
        verify(model).addAttribute("sessions", sessionList);
        assertThat(page, is("sessions"));
    }

    @Test
    public void whenSelectSession() {
        Session film = new Session(1, "Film1", 2, 2);
        boolean[][] matrix = new boolean[][]{{false, false}, {false, false}};
        Model model = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        SessionService sessionService = mock(SessionService.class);
        when(sessionService.findById(any(Integer.class))).thenReturn(film);
        when(sessionService.getMatrix(film)).thenReturn(matrix);
        SessionController sessionController = new SessionController(sessionService);
        String page = sessionController.selectSession(model, film.getId(), session);
        verify(model).addAttribute("sessionInfo", film);
        verify(model).addAttribute("matrix", matrix);
        assertThat(page, is("selectSeat"));
    }
}