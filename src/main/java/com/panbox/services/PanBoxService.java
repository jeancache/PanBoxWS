/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import com.panbox.beans.BillOfMat;
import com.panbox.beans.Product;
import com.panbox.beans.Order;
import com.panbox.beans.ProductList;
import com.panbox.beans.Stock;
import com.panbox.beans.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author gina PC
 */
@Path("panbox")
public class PanBoxService {

    @Context
    private ServletContext context;
    
    private Connection con;

    /**
     * Creates a new instance of JsontestResource
     */
    public PanBoxService() {
    }

    /**
     * Retrieves representation of an instance of com.cofmat.cofmatsvr.PanBoxService
     * @return an instance of com.cofmat.cofmatsvr.User
     */
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public ProductList productList() {
        ProductList pl = new ProductList();
        ArrayList<Product> list = new ArrayList<>();
        //Connection conn = (Connection) context.getProperty("conn");
        //Connection conn = (Connection) context.getAttribute("conn");
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "");
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                //Product p = new Product(rs.getString("prodname"), rs.getString("proddesc"), rs.getInt("price") + 0.0);
                Product p = new Product();
                p.setPrice(rs.getInt("price") + 0.0);
                p.setName(rs.getString("prodname"));
                p.setId(rs.getInt("prodid"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        pl.setList(list);
        return pl;
    }
    
    @GET
    @Path("/stocks")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Stock> stockList() {
        ArrayList<Stock> list = new ArrayList<>();
        //Connection conn = (Connection) context.getProperty("conn");
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM stocks");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                /*Product p = new Product(rs.getString("prodname"), rs.getString("proddesc"), rs.getInt("price") + 0.0);
                p.setId(rs.getInt("prodid"));
                list.add(p);*/
                list.add(new Stock(rs.getInt("stockid"), rs.getString("stockname")));
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
    @Path("/orders/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Order> orders(@PathParam("status") String stat) {
        ArrayList<Order> orderList = new ArrayList<>();
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "");
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT ordid FROM orders WHERE status = ?");
            ps.setString(1, stat);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Order o = new Order(rs.getInt("ordid"));
                //ArrayList<Product> prodList = new ArrayList<>();
                HashMap<String, Integer> hm = new HashMap<>();
                PreparedStatement prodquery = con.prepareStatement("SELECT a.prodid, a.prodname, a.price, b.qty FROM products a JOIN ordprod b ON a.prodid = b.prdid JOIN orders c ON c.ordid = b.orid WHERE c.ordid = ?");
                prodquery.setInt(1, rs.getInt("ordid"));
                ResultSet orderset = prodquery.executeQuery();
                while(orderset.next()) {
                    hm.put(orderset.getString("prodname"), orderset.getInt("qty"));
                    o.setTotal(o.getTotal() + (orderset.getInt("qty") * orderset.getInt("price")));
                }
                o.setProdlist(hm);
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
    
    @GET
    @Path("/billofmat/{prodid}")
    @Produces(MediaType.APPLICATION_JSON)
    public BillOfMat getBillOfMaterials(@PathParam("prodid") String prodid) {
        BillOfMat bm = new BillOfMat(Integer.parseInt(prodid));
        HashMap<String, Integer> bill = bm.getMaterials();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement billQuery = conn.prepareStatement("SELECT a.stockid, a.stockname, qty FROM stocks a JOIN prodstck b ON a.stockid = b.stockid JOIN products c ON c.prodid = b.prodid WHERE c.prodid = ?");
            billQuery.setInt(1, Integer.parseInt(prodid));
            ResultSet billResult = billQuery.executeQuery();
            while(billResult.next()) {
                Stock s = new Stock(billResult.getInt("stockid"), billResult.getString("stockname"));
                bill.put(billResult.getString("stockname"), billResult.getInt("qty"));
            }
            bm.setMaterials(bill);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return bm;
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
     * PUT method for updating or creating an instance of PanBoxService
     * @param content representation for the resource
     */
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public User[] putXml() {
        User[] uArr = new User[3];
        uArr[0] = new User("User", "one");
        uArr[1] = new User("User", "two");
        uArr[2] = new User("User", "three");
        return uArr;
    }
    
    @GET
    @Path("/test2")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User putXml2() {
        //HashMap<String, String> hm = new HashMap<>();
        //hm.put("Test", "2");
        return new User("test", "user");
        //return hm;
    }
    
    @GET
    @Path("/ordertest")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Order orderTest() {
        HashMap<String, Integer> hm = new HashMap<>();
        Order o = new Order();
        hm.put("Pie", 2);
        hm.put("Sandwich", 3);
        o.setProdlist(hm);
        return o;
    }
    
    @POST
    @Path("/sendorder")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response acceptOrder(Order o) {
        //Connection conn = (Connection) context.getAttribute("conn");
        HashMap<String, Integer> hm = o.getProdlist();
        Iterator<String> itr = hm.keySet().iterator();
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = sdf.format(currentDate);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "");
            //Insert Order
            PreparedStatement ordQuery = conn.prepareStatement("INSERT INTO orders (total, status, date) VALUES (?,?,?)");
            ordQuery.setDouble(1, o.getTotal());
            ordQuery.setString(2, "unpaid");
            ordQuery.setString(3, current);
            ordQuery.executeUpdate();
            
            //get order id
            int orderId = getOrdId(o.getTotal(), "unpaid", current);
            
            while(itr.hasNext()) {
                String currentProd = itr.next();
                int itemId = getProdId(currentProd);
                PreparedStatement itemQuery = conn.prepareStatement("INSERT INTO ordprod (prdid, orid, qty) VALUES (?,?,?)");
                itemQuery.setInt(1, itemId);
                itemQuery.setInt(2, orderId);
                itemQuery.setInt(3, hm.get(currentProd));
                itemQuery.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Exception:[acceptOrder]" + e);
        }
        return Response.status(200).entity("Order accepted").build();
    }
    
    private int getProdId(String s) {
        int id = -1;
        //Connection conn = (Connection) context.getAttribute("conn");
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "");
            PreparedStatement idQuery = conn.prepareStatement("SELECT prodid FROM products WHERE prodname = ?");
            idQuery.setString(1, s);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("prodid");
            }
        } catch (Exception e) {
            System.out.println("Exception:[getProdId] " +e);
        }
        return id;
    }
    
    private int getOrdId(double total, String status, String date) {
        int id = -1;
        //Connection conn = (Connection) context.getAttribute("conn");
        
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "");
            PreparedStatement idQuery = conn.prepareStatement("SELECT ordid FROM orders WHERE total = ? AND status = ? AND date = ?");
            idQuery.setDouble(1, total);
            idQuery.setString(2, status);
            idQuery.setString(3, date);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("ordid");
            }
        } catch (Exception e) {
            System.out.println("Exception:[getOrdId] " +e);
        }
        return id;
    }
}
