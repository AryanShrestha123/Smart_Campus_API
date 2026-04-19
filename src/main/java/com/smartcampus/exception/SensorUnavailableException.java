/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author LEGION
 */
public class SensorUnavailableException extends RuntimeException{
    public SensorUnavailableException(String sensorId, String status){
        super("Sensor '"+sensorId+"' is in '"+ status+"'state. "+
                "Only AACTIVE sensors accept new readings. Update statis via PUT /api/v1/sensors/"+sensorId);
    }
}
