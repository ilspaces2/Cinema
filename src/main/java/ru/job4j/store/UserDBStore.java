package ru.job4j.store;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.job4j.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

@ThreadSafe
@Repository
public class UserDBStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDBStore.class);

    private final BasicDataSource pool;

    public UserDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    public Optional<User> add(User user) {
        Optional<User> userOptional = Optional.empty();
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("insert into users (username,email,phone) values (?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    userOptional = Optional.of(user);
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL add user: {}", e.getMessage());
        }
        return userOptional;
    }

    public Optional<User> findById(int id) {
        try (Connection connect = pool.getConnection();
             PreparedStatement ps = connect.prepareStatement("select * from users where id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("phone")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findById user: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public boolean deleteAll() {
        boolean rez = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("delete from users")
        ) {
            rez = ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("SQL deleteAll users: {}", e.getMessage());
        }
        return rez;
    }

    /**
     * Найти юзера по мэйлу и телефону
     */
    public Optional<User> findUserByEmailAndPhone(String email, String phone) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE email = ? and phone = ?")
        ) {
            ps.setString(1, email);
            ps.setString(2, phone);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(new User(it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone")
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("SQL findUserByEmailAndPhone user: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
