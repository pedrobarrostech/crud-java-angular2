package com.sample.endpoints;

import com.sample.Application;
import com.sample.dao.UserDao;
import com.sample.domain.GenericErrorMessage;
import com.sample.domain.Token;
import com.sample.domain.User;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.server.validation.ValidationError;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.security.Key;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UserResourceTest extends JerseyTest {

    private static String AdminUserName = "bobMan";
    private static String AdminPassword = "aPassW0rd";
    private static User AdminUser = buildUser(AdminUserName, new String[]{"user", "admin"}, AdminPassword);

    private static String NormalUserName = "larry";
    private static String NormalPassword = "normalPassW0rd";
    private static User NormalUser = buildUser(NormalUserName, new String[]{"user"}, NormalPassword);

    private Token AdminToken;
    private Token NormalToken;

    private static final UserDao userDao = new UserDao();
    private static final Key key = MacProvider.generateKey();

    private static final User buildUser(String username, String[] roles, String password) {
        User user = new User();
        user.setUsername(username);
        user.setRoles(roles);
        user.setPassword(password);
        return user;
    }

    protected javax.ws.rs.core.Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new Application(userDao, key);
    }

    @BeforeClass
    public static void beforeClass() {
        AdminUser = userDao.addUser(AdminUser);
        NormalUser = userDao.addUser(NormalUser);
    }

    @Before
    public void getToken() {
        Response response;
        MultivaluedMap<String, String> formData;

        formData = new MultivaluedHashMap<String, String>();
        formData.add("username", AdminUserName);
        formData.add("password", AdminPassword);
        response = target("authentication").request().post(Entity.form(formData));
        assertEquals(200, response.getStatus());
        AdminToken = response.readEntity(Token.class);

        formData = new MultivaluedHashMap<String, String>();
        formData.add("username", NormalUserName);
        formData.add("password", NormalPassword);
        response = target("authentication").request().post(Entity.form(formData));
        assertEquals(200, response.getStatus());
        NormalToken = response.readEntity(Token.class);
    }

    @Test
    public void testAddUserAsAdmin() {

        String username = "BatMan";
        String[] roles = new String[]{"user"};
        String password = "password";

        User user = buildUser(username, roles, password);
        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("users").request().header("Authorization", "Bearer " + AdminToken.getAuthToken()).post(userEntity);
        assertEquals(200, response.getStatus());
        User responseUser = response.readEntity(User.class);

        assertEquals(username, responseUser.getUsername());
        assertArrayEquals(roles, responseUser.getRoles());
        assertEquals(password, responseUser.getPassword());
        assertNotNull(responseUser.getId());
    }

    @Test
    public void testAddUserAsAdminButWithBadValues() {

        String username = null;
        String[] roles = new String[]{"user"};
        String password = "password";

        User user = buildUser(username, roles, password);
        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("users").request().header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE).post(userEntity);

        List<ValidationError> validationErrorList = getValidationErrorList(response);
        assertEquals(400, response.getStatus());

        assertNotNull(validationErrorList);

        assertEquals(4, validationErrorList.size());
        System.out.println("validationErrorList = " + validationErrorList);
    }

    @Test
    public void testAddUserAsUser() {
        
        String username = "BatMan";
        String[] roles = new String[]{"user"};
        String password = "password";
        User user = buildUser(username, roles, password);
        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("users").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).post(userEntity);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testAddNullUser() {
        Response response = target("users").request().header("Authorization", "Bearer " + NormalToken.getAuthToken()).post(null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testFindUserThatDoesNotExist() {

        Response response = target("users")
                .path("999")
                .request()
                .header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        GenericErrorMessage errorMessage = response.readEntity(GenericErrorMessage.class);
        assertEquals(404, response.getStatus());
        assertEquals(404, errorMessage.getCode());
    }

    @Test
    public void testGetAllUsers() {

        Response response = target("users")
                .request()
                .header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<User> users = getUsers(response);

        assertEquals(200, response.getStatus());
        assertEquals(3, users.size());
    }

    @Test
    public void testUpdateUserAsAdmin() {

        User user = target("users")
                .path(String.valueOf(NormalUser.getId()))
                .request()
                .header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(User.class);

        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("users")
                .path(String.valueOf(NormalUser.getId()))
                .request()
                .header("Authorization", "Bearer " + AdminToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(userEntity);

        User responseUser = response.readEntity(User.class);

        assertEquals(200, response.getStatus());

        assertNotEquals(user.getVersion(), responseUser.getVersion());

    }

    @Test
    public void testUpdateUserAsTheSameUser() {

        User user = target("users")
                .path(String.valueOf(NormalUser.getId()))
                .request()
                .header("Authorization", "Bearer " + NormalToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(User.class);

        Entity<User> userEntity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("users")
                .path(String.valueOf(NormalUser.getId()))
                .request()
                .header("Authorization", "Bearer " + NormalToken.getAuthToken())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(userEntity);

        User responseUser = response.readEntity(User.class);

        assertEquals(200, response.getStatus());

        assertNotEquals(user.getVersion(), responseUser.getVersion());

    }

    private List<User> getUsers(final Response response) {
        return response.readEntity(new GenericType<List<User>>() {
        });
    }

    private List<ValidationError> getValidationErrorList(final Response response) {
        return response.readEntity(new GenericType<List<ValidationError>>() {
        });
    }

    private Set<String> getValidationMessageTemplates(final Response response) {
        return getValidationMessageTemplates(getValidationErrorList(response));
    }

    private Set<String> getValidationMessageTemplates(final List<ValidationError> errors) {
        final Set<String> templates = new HashSet<>();
        for (final ValidationError error : errors) {
            templates.add(error.getMessageTemplate());
        }
        return templates;
    }
}
