/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

//Importing necessary utilities
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author LEGION
 */

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    //Get method
    @GET
    public Response discover(){
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("apiName",     "Smart Campus Sensor & Room Management API");
        response.put("version",     "1.0.0");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors.");
        response.put("contact",     "admin@smartcampus.westminster.ac.uk");
        response.put("status",      "operational");
        response.put("builtWith",   "JAX-RS (Jersey 2.41) + Grizzly HTTP Server");
        
        Map<String, Object> links = new LinkedHashMap<>();
        links.put("self",    "/api/v1");
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("_links", links);
        
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms",    "Manage campus rooms. Supports GET, POST, PUT, DELETE.");
        resources.put("sensors",  "Manage IoT sensors. Supports GET, POST, PUT, DELETE and ?type= filtering.");
        resources.put("readings", "Access sensor history via /sensors/{sensorId}/readings");
        response.put("resources", resources);
        
        return Response.ok(response).build();
    }
}
