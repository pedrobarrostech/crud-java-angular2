/**
 * Created by Pedro Barros
 */
package com.sample;

import com.sample.dao.UserDao;
import com.sample.domain.User;
import com.sample.filters.JWTSecurityFilter;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;
import java.security.Key;

@ApplicationPath("/")
public class Application extends ResourceConfig {

    private static UserDao userDao;
    private static Key key;

    public Application() {
        this(new UserDao(), MacProvider.generateKey());

        User user = new User();
        user.setUsername("ironman");
        user.setRoles(new String[]{"user", "admin"});
        user.setPassword("password");
        this.getUserDao().addUser(user);
    }

    public Application(final UserDao userDao, final Key key) {
        this.setUserDao(userDao);
        this.setKey(key);

        // Validation.
        // register(ValidationConfigurationContextResolver.class);
        // logging
        register(LoggingFilter.class);
        // roles security
        register(RolesAllowedDynamicFeature.class);
        // jwt filter
        register(JWTSecurityFilter.class);
        // turn on Jackson, Moxy isn't that good of a solution.
        register(JacksonFeature.class);

        packages("com.sample");

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getUserDao()).to(UserDao.class);
                bind(getKey()).to(Key.class);
            }
        });
        property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", "true");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

}
