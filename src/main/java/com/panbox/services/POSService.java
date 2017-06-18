/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import com.panbox.beans.AndroidOrder;
import com.panbox.beans.BillOfMat;
import com.panbox.beans.EmployeeList;
import com.panbox.beans.Product;
import com.panbox.beans.Order;
import com.panbox.beans.OrderList;
import com.panbox.beans.ProdList;
import com.panbox.beans.User;
import com.panbox.beans.Sale;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * REST Web Service
 *
 * @author gina PC
 */
@Path("panbox")
public class POSService {

    @Context
    private ServletContext context;
    
    private Connection con;

    /**
     * Creates a new instance of JsontestResource
     */
    public POSService() {
    }

    /**
     * Retrieves representation of an instance of com.cofmat.cofmatsvr.PanBoxService
     * @return an instance of com.cofmat.cofmatsvr.User
     */
    @GET
    @Path("/products/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList productList(@PathParam("category") String category) {
        ProdList pl = new ProdList();
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE category = ?");
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setPrice(rs.getInt("price") + 0.0);
                p.setName(rs.getString("prodname"));
                p.setId(rs.getInt("prodid"));
                list.add(p);
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
        pl.setList(list);
        return pl;
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public User login(@FormParam("name") String name,
            @FormParam("password") String password,
            @Context HttpServletResponse servletResponse) {
        User u = new User(name, password);
        u.setPosition("unknown");
        Connection conn = (Connection) context.getAttribute("conn");
        HashMap<String, Object[]> emps = new HashMap<>(1);
        //HashMap<String, Integer> emid = new HashMap<>(1);
        try {
            //con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cofmat", "root", "root");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees");
            //ps.setString(1, name);
            //ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String empname = rs.getString("name").split(",")[0].trim();
                String emppass = rs.getString("password");
                int empid = rs.getInt("empid");
                String emppos = rs.getString("position");
                emps.put(empname, new Object[]{emppass, empid, emppos});
                //emid.put(empname, empid);
            }
            if(emps.containsKey(name)) {
                if(emps.get(name)[0].toString().equalsIgnoreCase(password)) {
                    u.setId((int)emps.get(name)[1]);
                    u.setPosition(emps.get(name)[2].toString());
                } else {
                    u.setId(-1);
                }
            } else {
                u.setId(-2);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            u.setId(-3);
            //return u;
        }
        return u;
    }
    
    @GET
    @Path("/orders/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderList orders(@PathParam("status") String stat) {
        ArrayList<Order> orderList = new ArrayList<>();
        OrderList ol = new OrderList();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT ordid, tablenum FROM orders WHERE DATE(date) = CURDATE() AND status = ?");
            ps.setString(1, stat);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Order o = new Order(rs.getInt("ordid"));
                o.setTablenum(rs.getInt("tablenum"));
                //ArrayList<Product> prodList = new ArrayList<>();
                Map<String, Integer> hm = o.getProdlist();
                PreparedStatement prodquery = conn.prepareStatement("SELECT a.prodid, a.prodname, a.price, b.qty FROM products a JOIN ordprod b ON a.prodid = b.prdid JOIN orders c ON c.ordid = b.orid WHERE c.ordid = ?");
                prodquery.setInt(1, rs.getInt("ordid"));
                ResultSet orderset = prodquery.executeQuery();
                while(orderset.next()) {
                    hm.put(orderset.getString("prodname"), orderset.getInt("qty"));
                    o.setTotal(o.getTotal() + (orderset.getInt("qty") * orderset.getDouble("price")));
                }
                o.setProdlist(hm);
                orderList.add(o);
                prodquery.close();
                orderset.close();
            }
            ps.close();
            rs.close();
            ol.setList(orderList);
            //return ol;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            //return ol;
        }
        return ol;
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
    
    @POST
    @Path("/sendorder")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Order acceptOrder(Order o) {
        
        if(o.getProdlist().size() != 0) {
            Map<String, Integer> hm = o.getProdlist();
            Iterator<String> itr = hm.keySet().iterator();
            Date currentDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String current = sdf.format(currentDate);
            Connection conn = (Connection) context.getAttribute("conn");
            
            //recompute total
            double reTotal = 0.0;
            for(Entry<String, Integer> entry : hm.entrySet()) {
                reTotal = reTotal + (Double.parseDouble(getProdPrice(entry.getKey())) * entry.getValue());
            }
            try {
                //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "root");
                //Insert Order
                PreparedStatement ordQuery = conn.prepareStatement("INSERT INTO orders (total, status, date, tablenum) VALUES (?,?,?,?)");
                ordQuery.setDouble(1, reTotal);
                ordQuery.setString(2, "unpaid");
                ordQuery.setString(3, current);
                ordQuery.setInt(4, o.getTablenum());
                ordQuery.executeUpdate();

                //get order id
                int orderId = getOrdId(reTotal, "unpaid", current);
                o.setId(orderId);

                while(itr.hasNext()) {
                    String currentProd = itr.next();
                    int itemId = getProdId(currentProd);
                    PreparedStatement itemQuery = conn.prepareStatement("INSERT INTO ordprod (prdid, orid, qty) VALUES (?,?,?)");
                    itemQuery.setInt(1, itemId);
                    itemQuery.setInt(2, orderId);
                    itemQuery.setInt(3, hm.get(currentProd));
                    itemQuery.executeUpdate();
                    
                    //subtract from ledger
                }
            } catch (Exception e) {
                System.out.println("Exception:[acceptOrder]" + e);
            }
        }
        return o;
    }
    
    @POST
    @Path("/checkout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkoutOrder(Sale s) {
        Connection conn = (Connection) context.getAttribute("conn");
        int orderId = s.getOrder().getId();
        
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = sdf.format(currentDate);
        try {
            PreparedStatement saleQuery = conn.prepareStatement("INSERT INTO sales (empid, ordid, date) VALUES (?,?,?)");
            saleQuery.setInt(1, 12);
            saleQuery.setInt(2, orderId);
            saleQuery.setString(3, current);
            saleQuery.executeUpdate();
            
            //get sale id
            int salesId = getSalesId(orderId);
            
            //insert payment
            PreparedStatement payQuery = conn.prepareStatement("INSERT INTO payment (salesid, amount, changeAmt, date) VALUES (?,?,?,?)");
            payQuery.setInt(1, salesId);
            payQuery.setDouble(2, s.getAmountTendered());
            payQuery.setDouble(3, s.getChange());
            payQuery.setString(4, current);
            payQuery.executeUpdate();
            
            //update order status
            PreparedStatement orderUpdate = conn.prepareStatement("UPDATE orders SET status = ?, tablenum = ? WHERE ordid = ?");
            orderUpdate.setString(1, "Paid");
            orderUpdate.setInt(2, s.getOrder().getTablenum());
            orderUpdate.setInt(3, orderId);
            orderUpdate.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[checkout]: " + e);
        }
        return Response.status(200).entity("Order Paid").build();
    }
    
    @POST
    @Path("/pendingstatus")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String changeStatus(@FormParam("orderid") int orderid) {
        String response = "Status Changed";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement statQuery = conn.prepareStatement("UPDATE orders SET status = ? WHERE ordid = ?");
            statQuery.setString(1, "Pending");
            statQuery.setInt(2, orderid);
            statQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[changeStat]: " + e);
            response = "Exception";
        }
        return response;
    }
    
    @POST
    @Path("/finishstatus")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String finishOrder(@FormParam("orderid") String orderid,
            @FormParam("baristaid") String baristaid,
            @FormParam("cookid") String cookid) {
        String resp = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            int ordid = Integer.parseInt(orderid);
            int bid = Integer.parseInt(baristaid);
            int cid = Integer.parseInt(cookid);
            PreparedStatement statQuery = conn.prepareStatement("UPDATE orders SET status = ? WHERE ordid = ?");
            statQuery.setString(1, "Finished");
            statQuery.setInt(2, ordid);
            statQuery.executeUpdate();
            
            //query all 'food' products from order and update ordprod
            PreparedStatement foodQuery = conn.prepareStatement("UPDATE ordprod JOIN products ON prodid = prdid SET empid = ? WHERE orid = ? AND category = ?");
            foodQuery.setInt(1, cid);
            foodQuery.setInt(2, ordid);
            foodQuery.setString(3, "Food");
            foodQuery.executeUpdate();
            //query all 'beverage' products from order and update ordprod
            PreparedStatement bevQuery = conn.prepareStatement("UPDATE ordprod JOIN products ON prodid = prdid SET empid = ? WHERE orid = ? AND category = ?");
            bevQuery.setInt(1, bid);
            bevQuery.setInt(2, ordid);
            bevQuery.setString(3, "Beverage");
            bevQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[finishorder]: " + e);
            resp = "Error";
        }
        return resp;
    }
    
    @POST
    @Path("/voiditem")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String voidItem(@FormParam("orderid") int orderid,
            @FormParam("itemname") String itemname) {
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement countQuery = conn.prepareStatement("SELECT COUNT(prdid) AS 'count' FROM ordprod WHERE orid = ?");
            countQuery.setInt(1, orderid);
            ResultSet rs = countQuery.executeQuery();
            if(rs.first()) {
                if(rs.getInt("count") > 1) {
                    PreparedStatement voidQuery = conn.prepareStatement("DELETE FROM ordprod WHERE orid = ? AND prdid = ?");
                    voidQuery.setInt(1, orderid);
                    voidQuery.setInt(2, getProdId(itemname));
                    voidQuery.executeUpdate();
                } else {
                    //delete order
                    PreparedStatement orderQuery = conn.prepareStatement("DELETE FROM orders WHERE ordid = ?");
                    orderQuery.setInt(1, orderid);
                    orderQuery.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception[void]: " + e);
        }
        return "item removed";
    }
    
    @POST
    @Path("/deleteorder")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteOrder(@FormParam("orderid") int orderid) {
        String ret = "Order Cancelled";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ordQuery = conn.prepareStatement("DELETE FROM orders WHERE ordid = ?");
            ordQuery.setInt(1, orderid);
            ordQuery.executeUpdate();
        } catch (Exception e) {
            ret = "Problem encountered when cancelling order";
            System.out.println("Exception[delorder]: " + e);
        }
        return ret;
    } 
    
    @GET
    @Path("/androidtest")
    @Produces(MediaType.APPLICATION_JSON)
    public AndroidOrder testAO() {
        ArrayList<Product> pl = new ArrayList<>();
        
        Product p = new Product();
        p.setName("Pie");
        p.setQty(2);
        pl.add(p);
        
        Product p2 = new Product();
        p2.setName("Sandwich");
        p2.setQty(5);
        pl.add(p2);
        
        AndroidOrder ao = new AndroidOrder();
        ao.setPl(pl);
        return ao;
    }
    
    @POST
    @Path("/testplaintext")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response printText(String s) {
        try {
            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put("eclipselink.media-type", "application/json");
            JAXBContext jc = JAXBContext.newInstance(new Class[] {AndroidOrder.class}, properties);
            
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader json = new StringReader(s);
            AndroidOrder ao = (AndroidOrder) unmarshaller.unmarshal(json);
            System.out.println(ao.getPl().get(0).getName());
        } catch (JAXBException ex) {
            Logger.getLogger(POSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(200).entity("Order accepted").build();
    }
    
    private int getProdId(String s) {
        int id = -1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement idQuery = conn.prepareStatement("SELECT prodid FROM products WHERE prodname = ?");
            idQuery.setString(1, s);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("prodid");
            }
            idQuery.close();
        } catch (Exception e) {
            System.out.println("Exception:[getProdId] " +e);
        }
        return id;
    }
    
    private int getOrdId(double total, String status, String date) {
        int id = -1;
        Connection conn = (Connection) context.getAttribute("conn");
        
        try {
            PreparedStatement idQuery = conn.prepareStatement("SELECT ordid FROM orders WHERE total = ? AND status = ? AND date = ?");
            idQuery.setDouble(1, total);
            idQuery.setString(2, status);
            idQuery.setString(3, date);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("ordid");
            }
            idQuery.close();
        } catch (Exception e) {
            System.out.println("Exception:[getOrdId] " +e);
        }
        return id;
    }
    
    private int getSalesId(int orderId) {
        int id = -1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement idQuery = conn.prepareStatement("SELECT salesid FROM sales WHERE ordid = ?");
            idQuery.setInt(1, orderId);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("salesid");
            }
        } catch (Exception e) {
            System.out.println("Exception:[getSalesId] " +e);
        }
        return id;
    }
    
    @POST
    @Path("/updateempinfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public int updateEmployeeInfo(User u) {
        int ret = 1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement infoQuery = conn.prepareStatement("UPDATE employees SET name = ?,"
                    + "position = ?, date hired = ?, address = ?, contacts = ? WHERE empid = ?");
            infoQuery.setString(1, u.getName());
            infoQuery.setString(2, u.getPosition());
            infoQuery.setString(3, u.getDateHired());
            infoQuery.setString(4, u.getAddress());
            infoQuery.setInt(5, u.getContacts());
            infoQuery.setInt(6, u.getId());
            infoQuery.executeUpdate();
        } catch(Exception e) {
            System.out.println("Exception[updateinfo]: " + e);
            ret = -1;
        }
        return ret;
    }
    
    @POST
    @Path("/employeestatus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeEmpStatus(@FormParam("empid") int id,
            @FormParam("status") String status) {
        String s = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement empStatQuery = conn.prepareStatement("UPDATE employees SET status = ? WHERE empid = ?");
            empStatQuery.setString(1, status);
            empStatQuery.setInt(1, id);
            empStatQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[empstatus]: " + e);
            s = "error";
        }
        return s;
    }
    
    @GET
    @Path("/prodprice/{prodname}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getProdPrice(@PathParam("prodname") String prodname) {
        String price = "0";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement priceQuery = conn.prepareStatement("SELECT price FROM products WHERE prodname = ?");
            priceQuery.setString(1, prodname);
            ResultSet priceSet = priceQuery.executeQuery();
            if(priceSet.first()) {
                price = priceSet.getDouble("price") + "";
            }
        } catch (Exception e) {
            price = "-1";
        }
        return price;
    }
    
    @GET
    @Path("/runningtotal")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRunningTotal() {
        String ret = "0";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement total = conn.prepareStatement("SELECT SUM(`total`) AS `runningtotal` FROM `orders` WHERE DATE(`date`) = CURDATE() AND (status = ? OR status = ?)");
            total.setString(1, "Finished");
            total.setString(2, "Pending");
            ResultSet rsTotal = total.executeQuery();
            if(rsTotal.first()) {
                ret = rsTotal.getDouble("runningtotal") + "";
            }
        } catch (Exception e) {
            ret = "-1";
            System.out.println("Exception[total]: " + e);
        }
        return ret;
    }
    
    @GET
    @Path("/transhistory/{transdate}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderList getTransaction(@PathParam("transdate") String transdate) {
        OrderList ol = new OrderList();
        ArrayList<Order> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT ordid, tablenum FROM orders WHERE DATE(date) = ? AND status = ?");
            ps.setString(1, transdate);
            ps.setString(2, "Finished");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Order o = new Order(rs.getInt("ordid"));
                o.setTablenum(rs.getInt("tablenum"));
                //ArrayList<Product> prodList = new ArrayList<>();
                Map<String, Integer> hm = o.getProdlist();
                PreparedStatement prodquery = conn.prepareStatement("SELECT a.prodid, a.prodname, a.price, b.qty FROM products a JOIN ordprod b ON a.prodid = b.prdid JOIN orders c ON c.ordid = b.orid WHERE c.ordid = ?");
                prodquery.setInt(1, rs.getInt("ordid"));
                ResultSet orderset = prodquery.executeQuery();
                while(orderset.next()) {
                    hm.put(orderset.getString("prodname"), orderset.getInt("qty"));
                    o.setTotal(o.getTotal() + (orderset.getInt("qty") * orderset.getDouble("price")));
                }
                o.setProdlist(hm);
                list.add(o);
                prodquery.close();
                orderset.close();
            }
            ps.close();
            rs.close();
            ol.setList(list);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return ol;
    }
    
    
}
