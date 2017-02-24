/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cofmat.cofmatsvr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author gina PC
 */
@Path("cofmat")
public class CofMatService {

    @Context
    private Configuration context;
    private Connection con;

    /**
     * Creates a new instance of JsontestResource
     */
    public CofMatService() {
    }

    /**
     * Retrieves representation of an instance of com.cofmat.cofmatsvr.CofMatService
     * @return an instance of com.cofmat.cofmatsvr.User
     */
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Product> productList() {
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getProperty("conn");
        try {
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product(rs.getString("prodname"), rs.getString("proddesc"), rs.getInt("price") + 0.0);
                p.setId(rs.getInt("prodid"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return list;
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User login(@FormParam("name") String name,
            @FormParam("password") String password,
            @Context HttpServletResponse servletResponse) {
        User u = new User(name, password);
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM employees WHERE name = ? AND password = ?");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                u.setId(rs.getInt("empid"));
                return u;
            } else {
                u.setId(-1);
                return u;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            u.setId(-2);
            return u;
        }
    }
    
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
                orderList.add(o);
                prodquery.close();
                orderset.close();
            }
            ps.close();
            rs.close();
            return orderList;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return orderList;
        }
    }
    
    @POST
    @Path("/adduser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User addUser(User u) {
        System.out.println(u.getName());
        System.out.println(u.getPassword());
        //return Response.status(200).entity("User accepted").build();
        u.setName(u.getName() + "ver");
        return u;
    }

    /**
     * PUT method for updating or creating an instance of CofMatService
     * @param content representation for the resource
     */
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public User putXml() {
        return new User("With", "root");
    }
}
