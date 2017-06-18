/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.services;

import com.panbox.beans.Employee;
import com.panbox.beans.EmployeeList;
import com.panbox.beans.Product;
import com.panbox.beans.Order;
import com.panbox.beans.OrderList;
import com.panbox.beans.ProdList;
import com.panbox.beans.Rental;
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
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE category = ? and status = 'active' ");
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setPrice(rs.getInt("price") + 0.0);
                p.setName(rs.getString("prodname"));
                p.setId(rs.getInt("prodid"));
                
                p.setAvailable(isAvailable(rs.getInt("prodid")));
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
    public Employee login(@FormParam("name") String name,
            @FormParam("password") String password,
            @Context HttpServletResponse servletResponse) {
        Employee e = new Employee();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE username = ? AND password = ?");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                e = new Employee(rs.getString("name"), rs.getString("address"), rs.getString("position"), rs.getString("contacts"), rs.getString("date hired"), rs.getString("status"));
                e.setId(rs.getInt("empid"));
                e.setUsername(rs.getString("username"));
                e.setPassword(rs.getString("password"));
            } else {
                e.setId(-1);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            e.setId(-2);
        }
        return e;
    }
    
    @GET
    @Path("/orders/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderList orders(@PathParam("status") String stat) {
        ArrayList<Order> orderList = new ArrayList<>();
        OrderList ol = new OrderList();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            String sql = "";
            if(stat.equalsIgnoreCase("Finished")) {
                sql = "SELECT ordid, tablenum FROM orders WHERE DATE(date) = CURDATE() AND status = ?";
            } else {
                sql = "SELECT ordid, tablenum FROM orders WHERE status = ?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);
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
                    //1 - 
                }
            } catch (Exception e) {
                System.out.println("Exception[acceptOrder]: " + e);
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
            PreparedStatement saleQuery = conn.prepareStatement("INSERT INTO sales (empid, ordid, date, discount) VALUES (?,?,?,?)");
            saleQuery.setInt(1, s.getEmpid());
            saleQuery.setInt(2, orderId);
            saleQuery.setString(3, current);
            saleQuery.setDouble(4, s.getDiscount());
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
    public String finishOrder(@FormParam("orderid") int ordid,
            @FormParam("baristaid") int baristaid,
            @FormParam("cookid") int cookid) {
        String resp = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = sdf.format(currentDate);
        try {
            //compare if status s already finished
            //int ordid = Integer.parseInt(orderid);
            //for future tracking
            //int bid = Integer.parseInt(baristaid);
            //int cid = Integer.parseInt(cookid);
            
            PreparedStatement checkStat = conn.prepareStatement("SELECT IF((`status` = 'Finished'), 'true', 'false') AS 'check' FROM `orders` WHERE `ordid` = ?");
            checkStat.setInt(1, ordid);
            ResultSet checkRes = checkStat.executeQuery();
            boolean statCheck = false;
            if(checkRes.first()) {
                statCheck = Boolean.parseBoolean(checkRes.getString("check"));
            }
            
            if(!statCheck) {
                PreparedStatement statQuery = conn.prepareStatement("UPDATE orders SET status = ? WHERE ordid = ?");
                statQuery.setString(1, "Finished");
                statQuery.setInt(2, ordid);
                statQuery.executeUpdate();

                //query all 'food' products from order and update ordprod
                PreparedStatement foodQuery = conn.prepareStatement("UPDATE ordprod JOIN products ON prodid = prdid SET empid = ? WHERE orid = ? AND category = ?");
                foodQuery.setInt(1, cookid);
                foodQuery.setInt(2, ordid);
                foodQuery.setString(3, "Food");
                foodQuery.executeUpdate();
                //query all 'beverage' products from order and update ordprod
                PreparedStatement bevQuery = conn.prepareStatement("UPDATE ordprod JOIN products ON prodid = prdid SET empid = ? WHERE orid = ? AND category = ?");
                bevQuery.setInt(1, baristaid);
                bevQuery.setInt(2, ordid);
                bevQuery.setString(3, "Beverage");
                bevQuery.executeUpdate();

                //remove reservations
                PreparedStatement resvrQuery = conn.prepareStatement("UPDATE ordprod a JOIN products b ON a.prdid = b.prodid SET b.reservedqty = b.reservedqty - a.qty WHERE orid = ?");
                resvrQuery.setInt(1, ordid);
                resvrQuery.executeUpdate();
                //subtract from stocks
                PreparedStatement stocksQuery = conn.prepareStatement("SELECT d.`stckid`, c.`prodname`, ROUND(SUM(d.`qty` * b.`qty`), 4) AS 'subqty', e.`qty` AS 'qtybefore', (e.`qty` - ROUND(SUM(d.`qty` * b.`qty`), 4)) AS 'qtyafter'FROM `orders` a JOIN `ordprod` b ON `ordid` = `orid` JOIN `products` c ON `prodid` = `prdid` JOIN `prodstck` d ON `prdctid` = `prodid` JOIN `stocks` e ON d.`stckid` = e.`stckid` WHERE `ordid` = ? GROUP BY 1");
                stocksQuery.setInt(1, ordid);
                ResultSet stocksRes = stocksQuery.executeQuery();
                while(stocksRes.next()) {
                    //add ledger entries
                    PreparedStatement ledgerQuery = conn.prepareStatement("INSERT INTO `ledger` (`stckid`, `orderid`, `date`, `qtyout`, `qtybefore`, `qtyafter`, `reason`) VALUES (?,?,?,?,?,?,?)");
                    ledgerQuery.setInt(1, stocksRes.getInt("stckid"));
                    ledgerQuery.setInt(2, ordid);
                    ledgerQuery.setString(3, current);
                    ledgerQuery.setDouble(4, stocksRes.getDouble("subqty"));
                    ledgerQuery.setDouble(5, stocksRes.getDouble("qtybefore"));
                    ledgerQuery.setDouble(6, stocksRes.getDouble("qtyafter"));
                    ledgerQuery.setString(7, "used to create finished product");
                    ledgerQuery.executeUpdate();

                    //update stock qty
                    PreparedStatement stckupQuery = conn.prepareStatement("UPDATE stocks SET qty = ? WHERE stckid = ?");
                    stckupQuery.setDouble(1, stocksRes.getDouble("qtyafter"));
                    stckupQuery.setInt(2, stocksRes.getInt("stckid"));
                    stckupQuery.executeUpdate();
                }
            } else {
                resp = "Order status is already set to finished";
            }
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
                    
                    //recompute total
                    PreparedStatement totalQuery = conn.prepareStatement("SELECT SUM(`qty` * `price`) AS 'total' FROM `orders` JOIN `ordprod` ON `ordid` = `orid` JOIN `products` ON `prdid` = `prodid` WHERE `ordid` = ?");
                    totalQuery.setInt(1, orderid);
                    ResultSet totalRes = totalQuery.executeQuery();
                    if(totalRes.first()) {
                        PreparedStatement updateTotal = conn.prepareStatement("UPDATE orders SET total = ? WHERE ordid = ?");
                        updateTotal.setDouble(1, totalRes.getDouble("total"));
                        updateTotal.setInt(2, orderid);
                        updateTotal.executeUpdate();
                    }
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
    
    private int getSalesIdByRent(int rentId) {
        int id = -1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement idQuery = conn.prepareStatement("SELECT salesid FROM sales WHERE rentid = ?");
            idQuery.setInt(1, rentId);
            ResultSet idRes = idQuery.executeQuery();
            if(idRes.first()) {
                id = idRes.getInt("salesid");
            }
        } catch (Exception e) {
            System.out.println("Exception:[getSalesIdByRent] " +e);
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
            PreparedStatement total = conn.prepareStatement("SELECT SUM(b.`amount` - b.`changeamt`) AS 'runningtotal' FROM `sales` a JOIN `payment` b USING (`salesid`) WHERE a.`date` = CURDATE()");
            ResultSet rsTotal = total.executeQuery();
            if(rsTotal.first()) {
                ret = new String().valueOf(rsTotal.getDouble("runningtotal"));
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
            PreparedStatement ps = conn.prepareStatement("SELECT ordid, tablenum FROM orders WHERE DATE(date) = ? AND status IN ('Finished', 'Paid', 'Pending')");
            ps.setString(1, transdate);
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
    
    @GET
    @Path("/isavailable/{pid}")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isAvailable(@PathParam("pid") int pid) {
        boolean ret = false;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement availQuery = conn.prepareStatement("SELECT a.`stckid` AS 'cs', IF((a.`qty` <= (b.`qty` - (SELECT SUM(`qty` * `reservedqty`) FROM `prodstck` JOIN `products` ON `prdctid` = `prodid` WHERE `stckid` = `cs`))), 'true', 'false') AS 'isAvailable' FROM `prodstck` a JOIN `stocks` b ON a.`stckid` = b.`stckid` WHERE `prdctid` = ? GROUP BY 2");
            availQuery.setInt(1, pid);
            ResultSet availRes = availQuery.executeQuery();
            int rowcount = 0;
            while(availRes.next()) {
                rowcount++;
            }
            if(rowcount == 1) {
                availRes.first();
                ret = Boolean.parseBoolean(availRes.getString("isAvailable"));
            }
        } catch (Exception e) {
            System.out.println("Exception[isavailable]: " + e);
        }
        return ret;
    }
    
    @GET
    @Path("/isavailablebyname/{pname}")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean isAvailableByName(@PathParam("pname") String pname) {
        boolean ret = false;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement availQuery = conn.prepareStatement("SELECT a.`stckid` AS 'cs', IF((a.`qty` <= (b.`qty` - (SELECT SUM(`qty` * `reservedqty`) FROM `prodstck` JOIN `products` ON `prdctid` = `prodid` WHERE `stckid` = `cs`))), 'true', 'false') AS 'isAvailable' FROM `prodstck` a JOIN `stocks` b ON a.`stckid` = b.`stckid` JOIN `products` c ON a.`prdctid` = c.`prodid` WHERE c.`prodname` = ? GROUP BY 2");
            availQuery.setString(1, pname);
            ResultSet availRes = availQuery.executeQuery();
            int rowcount = 0;
            while(availRes.next()) {
                rowcount++;
            }
            if(rowcount == 1) {
                availRes.first();
                ret = Boolean.parseBoolean(availRes.getString("isAvailable"));
            }
        } catch (Exception e) {
            System.out.println("Exception[isavailable]: " + e);
        }
        return ret;
    }
    
    @POST
    @Path("/reserveproduct")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String reserveProduct(@FormParam("pid") int pid) {
        String resp = "Product Reserved";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            if(isAvailable(pid)) {
                PreparedStatement resQuery = conn.prepareStatement("UPDATE products SET reservedqty = reservedqty + 1 WHERE prodid = ?");
                resQuery.setInt(1, pid);
                resQuery.executeUpdate();
            } else {
                resp = "Product Unavailable";
            }
        } catch (Exception e) {
            System.out.println("Exception[reserve]: " + e);
            resp = "Error";
        }
        return resp;
    }
    
    @POST
    @Path("/reserveproductbyname")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String reservedProductByName(@FormParam("pname") String pname) {
        String resp = "Product Reserved";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            if(isAvailableByName(pname)) {
                PreparedStatement resQuery = conn.prepareStatement("UPDATE products SET reservedqty = reservedqty + 1 WHERE prodname = ?");
                resQuery.setString(1, pname);
                resQuery.executeUpdate();
            } else {
                resp = "Product Unavailable";
            }
        } catch (Exception e) {
            System.out.println("Exception[reserve]: " + e);
            resp = "Error";
        }
        return resp;
    }
    
    @POST
    @Path("/decreservation")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String decReservation(@FormParam("pname") String pname) {
        String resp = "Reservation Decreased";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement decQuery = conn.prepareStatement("UPDATE products SET reservedqty = reservedqty - 1 WHERE prodname = ?");
            decQuery.setString(1, pname);
            decQuery.executeUpdate();
        } catch (Exception e) {
            resp = "Error";
            System.out.println("Exception[decRes]: " + e);
        }
        return resp;
    }
    
    @POST
    @Path("/clearres")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String clearReservation(@FormParam("pname") String pname,
            @FormParam("qty") int qty) {
        String resp = "Resvation Removed";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement decQuery = conn.prepareStatement("UPDATE products SET reservedqty = reservedqty - ? WHERE prodname = ?");
            decQuery.setString(2, pname);
            decQuery.setInt(1, qty);
            decQuery.executeUpdate();
        } catch (Exception e) {
            resp = "Error";
            System.out.println("Exception[decRes]: " + e);
        }
        return resp;
    }
    
    @GET
    @Path("/employees")
    @Produces(MediaType.APPLICATION_JSON)
    public EmployeeList userList() {
        ArrayList<Employee> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Employee e = new Employee(rs.getString("name"), rs.getString("address"), rs.getString("position"), rs.getString("contacts"), rs.getString("date hired"), rs.getString("status")) ;
                e.setId(rs.getInt("empid"));
                e.setUsername(rs.getString("username"));
                e.setPassword(rs.getString("password"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }
                }catch(Exception e){
                    e.printStackTrace();
        }
        EmployeeList elist = new EmployeeList(list);
        return elist;
    }
    
    @GET
    @Path("/getpaidandpending")
    @Produces(MediaType.APPLICATION_JSON)
    public OrderList getPaidPending() {
        OrderList ol = new OrderList();
        ArrayList<Order> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT ordid, tablenum, status FROM orders WHERE status = ? OR status = ?");
            ps.setString(1, "Paid");
            ps.setString(2, "Pending");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Order o = new Order(rs.getInt("ordid"));
                o.setTablenum(rs.getInt("tablenum"));
                o.setStatus(rs.getString("status"));
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
            System.out.println("Exception[getpaidpend]: " + e);
        }
        return ol;
    }
    
    @POST
    @Path("/archeryrental")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addRental(Sale s) {
        String resp = "Rental Information Recorded";
        Connection conn = (Connection) context.getAttribute("conn");
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = sdf.format(currentDate);
        try {
            Rental r = s.getRental();
            //insert into rental
            //get rental id
            //insert sale
            //get sale id
            //insert payment
            PreparedStatement rentalQuery = conn.prepareStatement("INSERT INTO rental (custname, timein, timeout, date) VALUES (?,?,?,?)");
            rentalQuery.setString(1, r.getCustName());
            rentalQuery.setString(2, r.getTimeIn());
            rentalQuery.setString(3, r.getTimeOut());
            rentalQuery.setString(4, r.getDate());
            rentalQuery.executeUpdate();
            
            //get rental id
            int rentid = getRentalId(r.getTimeIn(), r.getTimeOut(), r.getDate());
            
            //insert Sale
            PreparedStatement saleQuery = conn.prepareStatement("INSERT INTO sales (empid, rentid, date) VALUES (?,?,?)");
            saleQuery.setInt(1, s.getEmpid());
            saleQuery.setInt(2, rentid);
            saleQuery.setString(3, current);
            saleQuery.executeUpdate();
            
            //getSaleid
            int saleid = getSalesIdByRent(rentid);
            
            //insert payment
            PreparedStatement payQuery = conn.prepareStatement("INSERT INTO payment (salesid, amount, changeAmt, date) VALUES (?,?,?,?)");
            payQuery.setInt(1, saleid);
            payQuery.setDouble(2, s.getAmountTendered());
            payQuery.setDouble(3, s.getChange());
            payQuery.setString(4, current);
            payQuery.executeUpdate();
            
        } catch (Exception e) {
            resp = "Error";
            System.out.println("Exception[addrental]: " + e);
        }
        return resp;
    }
    
    @GET
    @Path("/allproducts")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList getAllProducts() {
        ProdList pl = new ProdList();
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE and status = 'active' ");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setPrice(rs.getInt("price") + 0.0);
                p.setName(rs.getString("prodname"));
                p.setId(rs.getInt("prodid"));
                
                p.setAvailable(isAvailable(rs.getInt("prodid")));
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
    
    
    private int getRentalId(String timeIn, String timeOut, String date) {
        int ret = -1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement rentQuery = conn.prepareStatement("SELECT rentid FROM rental WHERE timein = ? AND timeout = ? AND date = ?");
            rentQuery.setString(1, timeIn);
            rentQuery.setString(2, timeOut);
            rentQuery.setString(3, date);
            ResultSet rentRes = rentQuery.executeQuery();
            if(rentRes.first()) {
                ret = rentRes.getInt("rentid");
            }
        } catch (Exception e) {
            System.out.println("Exception[addrental]: " + e);
        }
        return ret;
    }
    
    @GET
    @Path("/dailybestseller/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList getDailyBestSeller(@PathParam("date") String date) {
        ProdList pl = new ProdList();
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT `prodname`, SUM(b.`qty`) AS 'qty' "
                    + "FROM `orders` a "
                    + "JOIN `ordprod` b ON a.`ordid` = b.`orid` "
                    + "JOIN `products` c ON b.`prdid` = c.`prodid` "
                    + "WHERE DATE(a.`date`) = ? "
                    + "GROUP BY 1 "
                    + "ORDER BY 2 DESC "
                    + "LIMIT 10");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setName(rs.getString("prodname"));
                p.setQty(rs.getInt("qty"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Exceptionp[dailyBS]: " + e);
        }
        pl.setList(list);
        return pl;
    }
    
    @GET
    @Path("/dailytotal/{date}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getDailyTotal(@PathParam("date") String date) {
        String ret = "0";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement total = conn.prepareStatement("SELECT SUM(b.`amount` - b.`changeamt`) AS 'dailytotal' FROM `sales` a JOIN `payment` b USING (`salesid`) WHERE a.`date` = ?");
            total.setString(1, date);
            ResultSet rsTotal = total.executeQuery();
            if(rsTotal.first()) {
                ret = new String().valueOf(rsTotal.getDouble("dailytotal"));
            }
        } catch (Exception e) {
            ret = "-1";
            System.out.println("Exception[dailytotal]: " + e);
        }
        return ret;
    }
    
    @POST
    @Path("/monthlybestseller")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ProdList getMonthlyBestSeller(@FormParam("month") String month,
            @FormParam("year") String year) {
        ProdList pl = new ProdList();
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            int pmonth = Integer.parseInt(month);
            int pyear = Integer.parseInt(year);
            PreparedStatement ps = conn.prepareStatement("SELECT `prodname`, SUM(b.`qty`) AS 'qty' "
                    + "FROM `orders` a "
                    + "JOIN `ordprod` b ON a.`ordid` = b.`orid` "
                    + "JOIN `products` c ON b.`prdid` = c.`prodid` "
                    + "WHERE MONTH(a.`date`) = ? AND YEAR(a.`date`) = ? "
                    + "GROUP BY 1 "
                    + "ORDER BY 2 DESC "
                    + "LIMIT 10");
            ps.setInt(1, pmonth);
            ps.setInt(2, pyear);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setName(rs.getString("prodname"));
                p.setQty(rs.getInt("qty"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Exceptionp[monthlyBS]: " + e);
        }
        pl.setList(list);
        return pl;
    }
    
    @POST
    @Path("/monthlytotal")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String getMonthlyTotal(@FormParam("month") String month,
            @FormParam("year") String year) {
        String ret = "0";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            int pmonth = Integer.parseInt(month);
            int pyear = Integer.parseInt(year);
            PreparedStatement total = conn.prepareStatement("SELECT SUM(b.`amount` - b.`changeamt`) AS 'dailytotal' FROM `sales` a JOIN `payment` b USING (`salesid`) WHERE MONTH(a.`date`) = ? AND YEAR(a.`date`) = ?");
            total.setInt(1, pmonth);
            total.setInt(2, pyear);
            ResultSet rsTotal = total.executeQuery();
            if(rsTotal.first()) {
                ret = new String().valueOf(rsTotal.getDouble("dailytotal"));
            }
        } catch (Exception e) {
            ret = "-1";
            System.out.println("Exception[monthlytotal]: " + e);
        }
        return ret;
    }
    
    @GET
    @Path("/yearlybestseller/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList getYearlyBestSeller(@PathParam("year") String year) {
        ProdList pl = new ProdList();
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            int pyear = Integer.parseInt(year);
            PreparedStatement ps = conn.prepareStatement("SELECT `prodname`, SUM(b.`qty`) AS 'qty' "
                    + "FROM `orders` a "
                    + "JOIN `ordprod` b ON a.`ordid` = b.`orid` "
                    + "JOIN `products` c ON b.`prdid` = c.`prodid` "
                    + "WHERE YEAR(a.`date`) = ? "
                    + "GROUP BY 1 "
                    + "ORDER BY 2 DESC "
                    + "LIMIT 10");
            ps.setInt(1, pyear);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product();
                p.setName(rs.getString("prodname"));
                p.setQty(rs.getInt("qty"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Exceptionp[yearlyBS]: " + e);
        }
        pl.setList(list);
        return pl;
    }
    
    @GET
    @Path("/yearlytotal/{year}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getYearlyTotal(@PathParam("year") String year) {
        String ret = "0";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            int pyear = Integer.parseInt(year);
            PreparedStatement total = conn.prepareStatement("SELECT SUM(b.`amount` - b.`changeamt`) AS 'dailytotal' FROM `sales` a JOIN `payment` b USING (`salesid`) WHERE YEAR(a.`date`) = ?");
            total.setInt(1, pyear);
            ResultSet rsTotal = total.executeQuery();
            if(rsTotal.first()) {
                ret = new String().valueOf(rsTotal.getDouble("dailytotal"));
            }
        } catch (Exception e) {
            ret = "-1";
            System.out.println("Exception[dailytotal]: " + e);
        }
        return ret;
    }
}
