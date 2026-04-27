/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

//Imports
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author LEGION
 */

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final DataStore store = DataStore.getInstance();
    
    @Context
    private UriInfo uriInfo;
    
    //GET Method (to get all the rooms)
    @GET
    public Response getAllRooms(){
        List<Room> rooms = new ArrayList<>(store.getRooms().values());   //Storing all rooms in a list
        return Response.ok(rooms).build();  
    }
    
    //POST Method  (to create new room)
    @POST
    public Response createRoom(Room room){
        //Null body check
        if (room == null) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorBody(400, "Bad Request", "Request body is missing. Please provide a JSON body."))
                .build();
        }
        
        // Error response for null id
        if(room.getId() == null || room.getId().isBlank()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'id' is required.")).build();
        }
        
        // Error response for empty name
        if(room.getName() == null || room.getName().isBlank()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'name' is requird.")).build();
        }
        
        // Error response for negative capacity
        if (room.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'capacity' must be a positive integer.")).build();
        }
        
        // Error response if room id already exists
        if (store.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody(409, "Conflict", "Room '" + room.getId() + "' already exists.")).build();
        }
        
        // Persist the new room and return the resource location URI
        store.addRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }
    
    // GET medthod (for specific room)
    @GET    
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId){
        Room room = store.getRoom(roomId);
        if (room == null) throw new ResourceNotFoundException("Room '"+ roomId+"' was not found.");
        return Response.ok(room).build();
    }
    
    // PUT method to update the room
    @PUT
    @Path("/{roomId}")
    public Response updateRoom(@PathParam("roomId") String roomId, Room updated) {
        Room existing = store.getRoom(roomId);
        if (existing == null) throw new ResourceNotFoundException("Room '" + roomId + "' was not found.");
        if (updated.getName() != null && !updated.getName().isBlank()) existing.setName(updated.getName());
        if (updated.getCapacity() > 0) existing.setCapacity(updated.getCapacity());
        return Response.ok(existing).build();
    }

    //DELETE method to delete a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) throw new ResourceNotFoundException("Room '" + roomId + "' was not found.");
        if(!room.getSensorIds().isEmpty()) throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        store.deleteRoom(roomId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 200);
        body.put("message", "Room '" + roomId + "' successfully deleted.");
        body.put("deletedRoomId", roomId);
        return Response.ok(body).build();
    }
        
    
    // Helper method to build standardized JSON error responses
    private Map<String, Object> errorBody(int status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
