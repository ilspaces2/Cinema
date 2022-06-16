package ru.job4j.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * В веб приложениях проверка доступ реализуется за счет фильтров.
 * Фильтры - это по сути обертки над сервлетами. Тот же самый шаблон декоратор.
 * Чтобы этого добиться в Java используется javax.servlet.Filter интерфейс.
 * По той же аналогии работает Spring Security.
 * <p>
 * Интерфейс Filter содержит метод doFilter. Через этот метод будут проходить запросы к сервлетам.
 * Если запрос идет к адресам loginPage или login, то мы их пропускаем сразу.
 * <p>
 * Если запросы идут к другим адресам, то проверяем наличие пользователя в HttpSession
 * Если его нет, то мы переходим на страницу авторизации.
 */

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (uri.endsWith("index") || uri.endsWith("loginPage") || uri.endsWith("login")
                || uri.endsWith("formRegistration") || uri.endsWith("userRegistration")) {
            chain.doFilter(req, res);
            return;
        }
        if (req.getSession().getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/loginPage");
            return;
        }
        chain.doFilter(req, res);
    }
}