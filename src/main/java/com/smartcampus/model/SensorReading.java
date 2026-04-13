/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

import java.util.UUID;

/**
 *
 * @author LEGION
 */
public class SensorReading {
    private String id;
    private long timestamp;
    private double value;
    
    //Constructors
    public SensorReading(){}
    
    public SensorReading(double value){
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }
    
    public SensorReading(String id, long timestamp, double value){
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }
    
    // Getters
    public String getId(){ return id; }
    public long getTimestamp(){ return timestamp; }
    public double getValue(){ return value; }
    
    // Setters
    public void setId(String id){
        this.id = id;
    }
    
    public void setTimestamp(long timestamp){ 
        this.timestamp = timestamp; 
    }
    
    public void setValue(double value){ 
        this.value = value; 
    }
    
}
