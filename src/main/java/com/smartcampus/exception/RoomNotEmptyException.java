/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author LEGION
 */
public class RoomNotEmptyException extends RuntimeException{
    private final String roomId;
    private final int sensorCount;
    
    public RoomNotEmptyException(String roomId, int sensorCount){
        super("Room '" + roomId + "' cannot be deleted: it still has " + 
                sensorCount + " active sensor(s) assigned to it. Delete or reassign all sensors first.");
        this.roomId = roomId;
        this.sensorCount = sensorCount;
    }
    
    public String getRoomId(){
        return roomId;
    }
    
    public int getSensorCount(){
        return sensorCount;
    }
    
}
