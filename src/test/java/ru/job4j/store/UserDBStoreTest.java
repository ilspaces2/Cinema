package ru.job4j.store;

import org.junit.After;
import org.junit.Test;
import ru.job4j.Main;
import ru.job4j.model.User;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class UserDBStoreTest {

    private static final UserDBStore STORE = new UserDBStore(new Main().loadPool());

    @After
    public void clearTable() {
        STORE.deleteAll();
    }

    @Test
    public void whenAddUser() {
        User user = new User(0, "Boris", "boris@yandex.ru", "1");
        STORE.add(user);
        Optional<User> userDB = STORE.findById(user.getId());
        assertThat(user.getEmail(), is(userDB.get().getEmail()));
        assertThat(user.getPhone(), is(userDB.get().getPhone()));
        assertThat(user.getUsername(), is(userDB.get().getUsername()));
    }


    @Test
    public void whenAddUsersWithEqualsEmail() {
        User user1 = new User(0, "Max", "Max@yandex.ru", "1");
        Optional<User> userOptional1 = STORE.add(user1);
        User user2 = new User(0, "Joe", "Max@yandex.ru", "1");
        Optional<User> userOptional2 = STORE.add(user2);
        assertFalse(userOptional1.isEmpty());
        assertTrue(userOptional2.isEmpty());
    }

    @Test
    public void whenFindByPasAndEmail() {
        User user = new User(0, "Max", "Max@yandex.ru1", "1");
        User user1 = new User(1, "Max1", "Max1@yandex.ru2", "2");
        User user2 = new User(2, "Max2", "Max2@yandex.ru3", "3");
        STORE.add(user);
        STORE.add(user1);
        STORE.add(user2);
        Optional<User> userOptional = STORE.findUserByEmailAndPhone(user.getEmail(), user.getPhone());
        assertFalse(userOptional.isEmpty());
        assertThat(userOptional.get().getPhone(), is(user.getPhone()));
        assertThat(userOptional.get().getEmail(), is(user.getEmail()));
    }
}