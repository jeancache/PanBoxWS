/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import java.util.Set;
import javax.ws.rs.core.Application;

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
    
    /*
    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new java.util.HashSet<>();
        set.add(com.panbox.services.PanBoxObjectMapperProvider.class);
        set.add(JacksonFeature.class);
        return set;
    }*/
    
    

    /*@Override
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
    }*/
    
    
    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.panbox.services.InventoryService.class);
        resources.add(com.panbox.services.MoxyJsonConfigProvider.class);
        resources.add(com.panbox.services.POSService.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
    }
    
}
