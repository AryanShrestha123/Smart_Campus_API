/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author LEGION
 */

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application{
    // JAX-RS runtime scans packages for @Path and @Provider classes automatically.
}
