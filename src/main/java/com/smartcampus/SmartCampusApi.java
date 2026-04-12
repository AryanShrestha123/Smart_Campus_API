/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 *
 * @author LEGION
 */
public class SmartCampusApi {
    
    private static final Logger LOGGER = Logger.getLogger(SmartCampusApi.class.getName());
    public static final String BASE_URI = "http://0.0.0.0:8080/";
    
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages(
                        "com.smartcampus.resource",
                        "com.smartcampus.exception",
                        "com.smartcampus.filter"
                );
        
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        LOGGER.info("----------------------------------------");
        LOGGER.info("Smart Campus API started");
        LOGGER.info("URL: "+ BASE_URI);
        LOGGER.info("Press enter to stop the server..");
        LOGGER.info("----------------------------------------");
        System.in.read();
        server.stop();
        LOGGER.info("Server stopped.");
        
    }
}
