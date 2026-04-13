/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LEGION
 */
public class Room {
    private String id;      // Unique identifier
    private String name;    // Name
    private int capacity;   // Maximum occupancy
    private List<String> sensorIds = new ArrayList<>();    // Collection of IDs of sensors deployed in the room
    
    
    // Constructors
    public Room(){}
    
    public Room(String id, String name, int capacity){
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
    
    // Getters
    public String getId(){return id;}
    
    public String getName(){return name;}
    
    public int gatCapacity(){return capacity;}
    
    public List<String> getSensorIds(){return sensorIds;}
    
    
    //Setters
    public void setId(String id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    
    public void setSensorIds(List<String> sensorIds){
        this.sensorIds = sensorIds;
    }
}
