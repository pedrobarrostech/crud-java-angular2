/**
 * Created by Pedro Barros
 */
package com.sample.endpoints;

import com.sample.dao.UserDao;
import com.sample.domain.EntityNotFoundException;
import com.sample.domain.Token;
import com.sample.domain.User;
import com.sample.util.TokenUtil;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

@PermitAll
@Path("/authentication")
public class AuthenticationResource {

    private final static Logger logger = Logger.getLogger(AuthenticationResource.class.getName());

    /**
     * HK2 Injection.
     */
    @Context
    UserDao dao;

    @Context
    Key key;

    @POST
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response authenticateUser(@FormParam("username") String username,
                                     @FormParam("password") String password) {

        Date expiry = getExpiryDate(15);
        User user = authenticate(username, password);

        String jwtString = TokenUtil.getJWTString(username, user.getRoles(), user.getVersion(), expiry, key);
        Token token = new com.sample.domain.Token();
        token.setAuthToken(jwtString);
        token.setExpires(expiry);

        return Response.ok(token).build();
    }

    /**
     * get Expire date in minutes.
     *
     * @param minutes the minutes in the future.
     * @return
     */
    private Date getExpiryDate(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    private User authenticate(String username, String password) throws NotAuthorizedException {
        User user = null;
        try {
            user = dao.getUser(username);
        } catch (EntityNotFoundException e) {
            logger.info("Invalid username '" + username + "' ");
            throw new NotAuthorizedException("Invalid username '" + username + "' ");
        }
        if (user.getHashedPassword().equals(password)) {
            logger.info("USER AUTHENTICATED");
        } else {
            logger.info("USER NOT AUTHENTICATED");
            throw new NotAuthorizedException("Invalid username or password");
        }
        return user;
    }


}
