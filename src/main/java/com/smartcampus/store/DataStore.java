/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

//Imorting the models
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

//Importing necessary utils
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author LEGION
 */
public class DataStore {
    private static final DataStore INSTANCE = new DataStore();
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();
    
    private DataStore(){
        
    }
    
    public static DataStore getInstance(){
        return INSTANCE;
    }
    
    //Room operation
    public Map<String, Room> getRooms(){
        return rooms;
    }
    
    public Room getRoom(String id){
        return rooms.get(id);
    }
    
    public void addRoom(Room room){
        rooms.put(room.getId(), room);
    }
    
    public boolean deleteRoom(String id){
        if(!rooms.containsKey(id)){
            return false;
        }
        rooms.remove(id);
        return true;
    }
    
    
    //Sensor operations
    public Map<String, Sensor> getSensors(){
        return sensors;
    }
    
    public Sensor getSensor(String id){
        return sensors.get(id);
    }
    
    public void addSensor(Sensor sensor){
        sensors.put(sensor.getId(), sensor);
        sensorReadings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }
    
    public boolean deleteSensor(String id){
        if(!sensors.containsKey(id)){
            return false;
        }
        sensors.remove(id);
        return true;
    }
    
    //SensorReading operations
    public List<SensorReading> getReadings(String sensorId){
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }
    
    public void addReading(String sensorId, SensorReading reading){
        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);     
    }
    
      
}
