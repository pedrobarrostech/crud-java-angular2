package com.sample.dao;

import com.sample.dao.UserDao;
import com.sample.domain.EntityNotFoundException;
import com.sample.domain.User;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class UserDaoTest {

    private static final UserDao dao = new UserDao();
    private static User user1, user2;

    private static User buildUser( String username, String[] roles, String hashedPassword) {
        User user = new User();
        user.setUsername(username);
        user.setRoles(roles);
        user.setHashedPassword(hashedPassword);
        return user;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        user1 = dao.addUser(buildUser("A", new String[]{"A"}, "AAA"));
        user2 = dao.addUser(buildUser("C", new String[]{"C"}, "CCC"));
    }

    /**
     * Method: getUsers()
     */
    @Test
    public void testGetUsers() throws Exception {
        Collection<User> users = dao.getUsers();
        assertNotNull(users);
        assertNotEquals(0, users.size());
    }

    /**
     * Method: getUser(Long id)
     */
    @Test
    public void testGetUserId() throws Exception {
        User user = dao.getUser(user1.getId());
        assertNotNull(user);
        assertEquals("A", user.getUsername());
        assertEquals(1, user.getVersion());
    }

    /**
     * Method: getUser(Long id)
     */
    @Test
    public void testGetUserIdNext() throws Exception {
        User user = dao.getUser(user2.getId());
        assertNotNull(user);
        assertEquals("C", user.getUsername());
        assertEquals(1, user.getVersion());
    }

    /**
     * Method: getUser(String username)
     */
    @Test
    public void testGetUserUsername() throws Exception {
        User user = dao.getUser("A");
        assertNotNull(user);
        assertEquals("A", user.getUsername());
        assertEquals(1, user.getVersion());
    }

    /**
     * Method: addUser(User user)
     */
    @Test
    public void testAddUser() throws Exception {
        dao.addUser(buildUser("B", "B@gmail.com", new String[]{"B"}, "B"));
    }

    /**
     * Method: addUser(User user)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddUserTwice() throws Exception {
        dao.addUser(buildUser("B", "B@gmail.com", new String[]{"B"}, "B"));
    }

    /**
     * Method: addUser(User user)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithMissingID() throws Exception {
        User user = dao.addUser(buildUser("X", "XXXXXX@gmail.com", new String[]{"B"}, "B"));
        user.setId(null);
        dao.update(user);
    }
    /**
     * Method: addUser(User user)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithMissingName() throws Exception {
        User user = dao.addUser(buildUser("ZZ", "XXXXXX@gmail.com", new String[]{"B"}, "B"));
        user.setUsername(null);
        dao.update(user);
    }

    /**
     * Method: update(User user)
     */
    @Test
    public void testUpdate() throws Exception {
        User user = dao.getUser(user2.getId());
        int firstVersion = user.getVersion();
        User update = dao.update(user);
        System.out.println("update.getVersion() = " + update.getVersion());
    }

    /**
     * Method: User getUser(String username) throws EntityNotFoundException
     */
    @Test(expected = EntityNotFoundException.class)
    public void testGetUserByNameEntityNotFoundException () throws Exception {
        User user = dao.getUser(UUID.randomUUID().toString());
    }

    /**
     * Method: User getUser(Long id) throws EntityNotFoundException
     */
    @Test(expected = EntityNotFoundException.class)
    public void testGetUserByIdEntityNotFoundException() throws Exception {
        User user = dao.getUser(Long.MAX_VALUE);
    }

} 
