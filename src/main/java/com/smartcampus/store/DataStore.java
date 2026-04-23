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
        seedData();  //Calling the seedData function to make dummy data
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
    
    
    //Pre-load the demo data
    private void seedData() {
        Room r1 = new Room("LIB-301", "Library Quiet Study",     50);
        Room r2 = new Room("LAB-101", "Computer Science Lab",    30);
        Room r3 = new Room("ENG-201", "Engineering Seminar Room", 25);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE",       22.5,  "LIB-301");
        Sensor s2 = new Sensor("CO2-001",  "CO2",         "ACTIVE",       412.0, "LIB-301");
        Sensor s3 = new Sensor("OCC-001",  "Occupancy",   "MAINTENANCE",    0.0, "LAB-101");
        Sensor s4 = new Sensor("TEMP-002", "Temperature", "ACTIVE",        19.8, "ENG-201");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
        sensors.put(s4.getId(), s4);

        r1.getSensorIds().add(s1.getId());
        r1.getSensorIds().add(s2.getId());
        r2.getSensorIds().add(s3.getId());
        r3.getSensorIds().add(s4.getId());

        sensorReadings.put(s1.getId(), new ArrayList<>());
        sensorReadings.put(s2.getId(), new ArrayList<>());
        sensorReadings.put(s3.getId(), new ArrayList<>());
        sensorReadings.put(s4.getId(), new ArrayList<>());

        sensorReadings.get(s1.getId()).add(new SensorReading("READ-001", System.currentTimeMillis() - 120000, 20.1));
        sensorReadings.get(s1.getId()).add(new SensorReading("READ-002", System.currentTimeMillis() - 60000,  21.4));
        sensorReadings.get(s1.getId()).add(new SensorReading("READ-003", System.currentTimeMillis(),          22.5));
        sensorReadings.get(s4.getId()).add(new SensorReading("READ-004", System.currentTimeMillis() - 30000,  19.8));
    }
      
}
