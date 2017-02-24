/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

/**
 *
 * @author gina PC
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new java.util.HashMap<>();
        addRestProperties(properties);
        return properties;
    }
    
    private void addRestProperties(Map<String, Object> properties) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            properties.put("conn", conn);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
    
    
    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        //resources.add(com.cofmat.cofmatsvr.JsonMoxyConfigurationContextResolver.class);
        resources.add(com.panbox.services.CofMatService.class);
        resources.add(com.panbox.services.JsonMoxyConfigurationContextResolver.class);
        resources.add(com.panbox.services.TestOrders.class);
    }
    
}
