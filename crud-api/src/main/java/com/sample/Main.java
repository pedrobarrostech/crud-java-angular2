/**
 * Created by Pedro Barros
 */
package com.sample;

import com.sample.dao.UserDao;
import com.sample.filters.CORSFilter;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.security.Key;
import java.util.logging.Logger;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/";
    final static Logger logger = Logger.getLogger(Main.class.getName());

    public static HttpServer startServer() {

        Key key = MacProvider.generateKey();

        final ResourceConfig rc = new Application(new UserDao(), key);
        rc.register(new CORSFilter());
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        logger.info(String.format("Jersey app started:", BASE_URI));
        System.in.read();
        server.stop();
    }
}

