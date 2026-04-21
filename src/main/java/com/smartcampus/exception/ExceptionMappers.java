/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LEGION
 */

//Room has sensors, so it cannot be deleted
@Provider
class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",      409);
        body.put("error",       "Conflict");
        body.put("message",     e.getMessage());
        body.put("roomId",      e.getRoomId());
        body.put("sensorCount", e.getSensorCount());
        body.put("resolution",  "DELETE all sensors in this room first, then retry.");
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}

@Provider
class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",     422);
        body.put("error",      "Unprocessable Entity");
        body.put("message",    e.getMessage());
        body.put("resolution", "Create the referenced room via POST /api/v1/rooms first.");
        return Response.status(422).type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}

// Sensor unavailable exception
@Provider
class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",     403);
        body.put("error",      "Forbidden");
        body.put("message",    e.getMessage());
        body.put("resolution", "Set sensor status to ACTIVE via PUT /api/v1/sensors/{id}.");
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}

// Resource Not Found
@Provider
class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
    @Override
    public Response toResponse(ResourceNotFoundException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",  404);
        body.put("error",   "Not Found");
        body.put("message", e.getMessage());
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}

@Provider
class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        LOGGER.log(Level.SEVERE, "Unhandled exception intercepted by GlobalExceptionMapper", e);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",  500);
        body.put("error",   "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please contact the system administrator.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON).entity(body).build();
    }
}
