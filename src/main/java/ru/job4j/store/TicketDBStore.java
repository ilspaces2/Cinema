package ru.job4j.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Repository
public class TicketDBStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketDBStore.class);

    private final BasicDataSource pool;

    public TicketDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<Ticket> add(Ticket ticket) {
        Optional<Ticket> ticketOptional = Optional.empty();
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("insert into ticket "
                             + "(session_id, pos_row, cell, user_id) values (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getPosRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getUserId());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                    ticketOptional = Optional.of(ticket);
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL add ticket: {}", e.getMessage());
        }
        return ticketOptional;
    }

    public Optional<Ticket> findById(int id) {
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("select * from ticket where id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Ticket(
                            rs.getInt("id"),
                            rs.getInt("session_id"),
                            rs.getInt("pos_row"),
                            rs.getInt("cell"),
                            rs.getInt("user_id")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findById ticket: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public boolean deleteAll() {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from ticket")
        ) {
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL deleteAll tickets: {}", e.getMessage());
        }
        return rez;
    }

    /**
     * Ищем билеты юзера по его id
     *
     * @param id id юзера
     * @return вазвращаем все билеты юзера
     */
    public List<String> findTicketsByIdUser(int id) {
        List<String> tickets = new ArrayList<>();
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement(
                     "select ticket.id as num, name, pos_row, cell from ticket join sessions on user_id=? and sessions.id=ticket.session_id")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(String.format("№ билета: %d | Фильм: %s | Ряд: %d | Место: %d",
                            rs.getInt("num"),
                            rs.getString("name"),
                            rs.getInt("pos_row"),
                            rs.getInt("cell")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findTicketsByIdUser ticket: {}", e.getMessage());
        }
        return tickets;
    }

    /**
     * Удалить все билеты по id фильма
     *
     * @param id id фильма
     * @return boolean
     */
    public boolean deleteTicketByIdFilm(int id) {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from ticket where session_id =? ")
        ) {
            ps.setInt(1, id);
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL deleteTicketByIdFilm ticket: {}", e.getMessage());
        }
        return rez;
    }
}
