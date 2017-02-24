/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import com.panbox.beans.Order;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author gina PC
 */
@Path("/cf")
public class TestOrders {

    @Context
    private Connection con;
    private UriInfo context;

    /**
     * Creates a new instance of TestOrders
     */
    public TestOrders() {
    }

    /**
     * Retrieves representation of an instance of com.cofmat.cofmatsvr.TestOrders
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/orders")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Order> orders() {
        ArrayList<Order> orderList = new ArrayList<>();
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT ordid FROM orders WHERE status = ?");
            ps.setString(1, "unpaid");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                orderList.add(new Order(rs.getInt("ordid")));/*
                Order o = new Order(rs.getInt("ordid"));
                ArrayList<Product> prodList = new ArrayList<>();
                PreparedStatement prodquery = con.prepareStatement("SELECT a.prodid, a.prodname, a.price, b.qty, empid FROM products a JOIN ordprod b ON a.prodid = b.productid JOIN orders c ON c.ordid = b.ordid WHERE c.ordid = ?");
                prodquery.setInt(1, rs.getInt("ordid"));
                ResultSet orderset = prodquery.executeQuery();
                while(orderset.next()) {
                    Product p = new Product();
                    p.setId(orderset.getInt("prodid"));
                    p.setName(orderset.getString("prodname"));
                    p.setPrice(orderset.getInt("price") + 0.0);
                    p.setQty(orderset.getInt("qty"));
                    p.setEmpid(orderset.getInt("empid"));
                    prodList.add(p);
                }
                o.setProdlist(prodList);
                //orderList.add(o);
                //prodquery.close();
                //orderset.close();*/
            }
            //ps.close();
            //rs.close();
            //return orderList;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            //return orderList;
        }
        return orderList;
    }
    
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Order> productList() {
        ArrayList<Order> list = new ArrayList<>();
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orders");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                //Product p = new Product(rs.getString("prodname"), rs.getString("proddesc"), rs.getInt("price") + 0.0);
                Order o = new Order(rs.getInt("ordid"));
                //p.setId(rs.getInt("prodid"));
                list.add(o);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return list;
    }

    /**
     * PUT method for updating or creating an instance of TestOrders
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
