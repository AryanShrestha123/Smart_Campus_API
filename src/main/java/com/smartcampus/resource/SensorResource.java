/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

//Imports
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author LEGION
 */

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final DataStore store = DataStore.getInstance();
    
    @Context
    private UriInfo uriInfo;
    
    //GET Method
    @GET
    public Response getAllSensors(@QueryParam("type") String type){
        List<Sensor> all = new ArrayList<>(store.getSensors().values());
        if(type != null && !type.isBlank()){
            List<Sensor> filtered = all.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type.trim()))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(all).build();
    }
    
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'id' is required.")).build();
        }
        
        if (sensor.getType() == null || sensor.getType().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'type' is required.")).build();
        }
        
        if (store.getSensor(sensor.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody(409, "Conflict", "Sensor '" + sensor.getId() + "' already exists.")).build();
        }
        
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new LinkedResourceNotFoundException("Field 'roomId' is required.");
        }
        if (store.getRoom(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException(
                "The roomId '" + sensor.getRoomId() + "' does not reference an existing room. " +
                "Create the room first via POST /api/v1/rooms.");
        }
        
        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) sensor.setStatus("ACTIVE");
        store.addSensor(sensor);
        Room room = store.getRoom(sensor.getRoomId());
        if (!room.getSensorIds().contains(sensor.getId())) room.getSensorIds().add(sensor.getId());
        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new ResourceNotFoundException("Sensor '" + sensorId + "' was not found.");
        return Response.ok(sensor).build();
    }

    @PUT
    @Path("/{sensorId}")
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updated) {
        Sensor existing = store.getSensor(sensorId);
        if (existing == null) throw new ResourceNotFoundException("Sensor '" + sensorId + "' was not found.");
        if (updated.getStatus() != null && !updated.getStatus().isBlank()) existing.setStatus(updated.getStatus());
        if (updated.getType() != null && !updated.getType().isBlank()) existing.setType(updated.getType());
        if (updated.getCurrentValue() != 0) existing.setCurrentValue(updated.getCurrentValue());
        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new ResourceNotFoundException("Sensor '" + sensorId + "' was not found.");
        Room room = store.getRoom(sensor.getRoomId());
        if (room != null) room.getSensorIds().remove(sensorId);
        store.deleteSensor(sensorId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 200);
        body.put("message", "Sensor '" + sensorId + "' successfully deleted.");
        body.put("deletedSensorId", sensorId);
        return Response.ok(body).build();
    }

    // Sub-Resource Locator - no HTTP verb annotation
    // JAX-RS delegates /readings path to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        if (store.getSensor(sensorId) == null)
            throw new ResourceNotFoundException("Sensor '" + sensorId + "' was not found.");
        return new SensorReadingResource(sensorId);
    }

    private Map<String, Object> errorBody(int status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
