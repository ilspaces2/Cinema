package ru.job4j.store;

import net.jcip.annotations.ThreadSafe;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ThreadSafe
@Repository
public class SessionDBStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDBStore.class);

    private final BasicDataSource pool;

    public SessionDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Session add(Session session) {
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("insert into sessions "
                     + "(name, number_of_row, number_of_cell) values (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, session.getName());
            ps.setInt(2, session.getNumberOfRow());
            ps.setInt(3, session.getNumberOfCell());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    session.setId(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL add session: {}", e.getMessage());
        }
        return session;
    }

    public Session findById(int id) {
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("select * from sessions where id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Session(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("number_of_row"),
                            rs.getInt("number_of_cell")
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findById session: {}", e.getMessage());
        }
        return null;
    }

    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("select * from sessions")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(new Session(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("number_of_row"),
                            rs.getInt("number_of_cell")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findAll sessions: {}", e.getMessage());
        }
        return sessions;
    }

    public boolean deleteAll() {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from sessions")
        ) {
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL deleteAll sessions: {}", e.getMessage());
        }
        return rez;
    }

    public boolean deleteById(int id) {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from sessions where id=? ")
        ) {
            ps.setInt(1, id);
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL deleteById session: {}", e.getMessage());
        }
        return rez;
    }

    public boolean update(Session session) {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE sessions set "
                     + "name=?, number_of_row=?, number_of_cell=? WHERE id = ?")) {
            ps.setString(1, session.getName());
            ps.setInt(2, session.getNumberOfRow());
            ps.setInt(3, session.getNumberOfCell());
            ps.setInt(4, session.getId());
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL update session: {}", e.getMessage());
        }
        return rez;
    }

    /**
     * Находим занятые места по билетам и выставляем их в двумерном массиве значением true (будет означать что место занято).
     * Минус один в координатах потому-что индексы массива начинаются с нуля а ряды и места у нас с единицы
     */
    public boolean[][] getMatrix(Session session) {
        boolean[][] matrix = initMatrix(session.getNumberOfRow(), session.getNumberOfCell());
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("select pos_row,cell from ticket where session_id=?")) {
            ps.setInt(1, session.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    matrix[rs.getInt("pos_row") - 1][rs.getInt("cell") - 1] = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL seats session: {}", e.getMessage());
        }
        return matrix;
    }

    /**
     * Создание двумерного массива по количеству рядов и мест.
     * Инициализация ячеек значением false (будет означать что место свободно)
     */
    private boolean[][] initMatrix(int rows, int cells) {
        boolean[][] matrix = new boolean[rows][cells];
        for (boolean[] booleans : matrix) {
            Arrays.fill(booleans, false);
        }
        return matrix;
    }
}
