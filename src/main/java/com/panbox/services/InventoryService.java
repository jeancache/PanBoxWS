package com.panbox.services;

import com.panbox.beans.Category;
import com.panbox.beans.Employee;
import com.panbox.beans.EmployeeList;
import com.panbox.beans.Ledger;
import com.panbox.beans.LedgerRecord;
import com.panbox.beans.Product;
import com.panbox.beans.ProdList;
import com.panbox.beans.Prodstck;
import com.panbox.beans.ProdstckList;
import com.panbox.beans.PurchaseOrder;
import com.panbox.beans.PurchaseOrderList;
import com.panbox.beans.StckCompound;
import com.panbox.beans.StckCompoundList;
import com.panbox.beans.StckPurOrd;
import com.panbox.beans.StckPurOrdList;
import com.panbox.beans.StckSup;
import com.panbox.beans.StckSupList;
import com.panbox.beans.Stock;
import com.panbox.beans.StockList;
import com.panbox.beans.Supplier;
import com.panbox.beans.SupplierList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
/**
 * REST Web Service
 *
 * @author gina PC
 */
@Path("inventory")
public class InventoryService {

    @Context
    private ServletContext context;
    //private Connection con;
    
    /**
     * Creates a new instance of JsontestResource
     */
    public InventoryService() {
    }

    /**
     * Retrieves representation of an instance of com.cofmat.cofmatsvr.InventoryService
     * @return an instance of com.cofmat.cofmatsvr.User
     */
    @GET
    @Path("/products")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList productList() {
        ArrayList<Product> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products ORDER BY prodid DESC");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product(rs.getString("prodname"), rs.getString("category"), rs.getDouble("price"));
                p.setStatus(rs.getString("status"));
                p.setId(rs.getInt("prodid"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProdList prodlist = new ProdList(list);
        return prodlist;
    }
    
    @GET
    @Path("/productlist/{order}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdList productslist(@PathParam("order") String order) {
        ArrayList<Product> list = new ArrayList<>();
        PreparedStatement ps;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            if(order.equals("DESC")){
                ps = conn.prepareStatement("SELECT * FROM products ORDER BY prodname DESC");
            }else{
                ps = conn.prepareStatement("SELECT * FROM products ORDER BY prodname ASC");
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Product p = new Product(rs.getString("prodname"), rs.getString("category"), rs.getDouble("price"));
                p.setStatus(rs.getString("status"));
                p.setId(rs.getInt("prodid"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProdList prodlist = new ProdList(list);
        return prodlist;
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
    
    @POST
    @Path("/updateprodstat")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateProductStatus(@FormParam("status") String status,
            @FormParam("id") int id) {
        String message = "Error";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE products SET status = ? WHERE prodid = ?");
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            message = "updated";
        }catch (Exception e) {
            e.printStackTrace();
            message = "error";
        }
        return message;
    }
            
    @POST
    @Path("/addAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Employee addAccount(@FormParam("name") String name,
            @FormParam("username") String username,
            @FormParam("password") String password) {
        Employee u = new Employee();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement userQuery = conn.prepareStatement("SELECT * FROM employees where name = ?");
            userQuery.setString(1, name);
            ResultSet rs = userQuery.executeQuery();
            if(rs.first()) {
                PreparedStatement accountQuery = conn.prepareStatement("UPDATE employees SET username = ?, password = ? WHERE name = ?");
                accountQuery.setString(1, username);
                accountQuery.setString(2, password);
                accountQuery.setString(3, name);
                accountQuery.executeUpdate();
                
                u.setName(name);
                u.setUsername(username);
            }
        } catch(Exception e) {
            System.out.println("Exception[addaccount]: " + e);
        }
        return u;
    }
    
    @POST
    @Path("/updateReturnSPO")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateReturnSPO(@FormParam("qtyremaining") int qtyremaining,
            @FormParam("poid") int poid,
            @FormParam("stksid") int stksid) {
        String status;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `stckpurord` SET `qtyremaining`=? WHERE `stksid` = ? AND `poid` = ?");
            ps.setInt(1, qtyremaining);
            ps.setInt(2, stksid);
            ps.setInt(3, poid);
            ps.executeUpdate();
            status = "updated";
        } catch(Exception e) {
            status = "error";
            System.out.println("Exception[addaccount]: " + e);
        }
        return status;
    }
    
    @POST
    @Path("/addEmployee")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public String addEmployee(Employee employee) {
        String message = "Error";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `employees`(`name`, `position`, `date hired`, `address`, `contacts`, `status`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getPosition());
            ps.setString(3, employee.getDatehired());
            ps.setString(4, employee.getAddress());
            ps.setString(5, employee.getContact());
            ps.setString(6, "active");
            ps.executeUpdate();
            message = "Okay";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
    
    //returns employee object if employee credentials are valid, else, return empty employee object and set id to -1 or -2
    @POST
    @Path("/adminlogin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Employee adminLogin(@FormParam("name") String name,
            @FormParam("password") String password,
            @Context HttpServletResponse servletResponse) {
        Employee e = new Employee();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM employees WHERE username = ? AND password = ? AND position = \"Owner\"");
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                e = new Employee(rs.getString("name"),rs.getString("address"), rs.getString("position"), rs.getString("contacts"), rs.getString("date hired"), rs.getString("status"));
                e.setId(rs.getInt("empid"));
            }else{
                e.setId(-1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.setId(-2);
        }
        return e;
    }
    
    //returns employee object if employee credentials are valid, else, return empty employee object and set id to -1 or -2
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
                e = new Employee(rs.getString("name"),rs.getString("address"), rs.getString("position"), rs.getString("contacts"), rs.getString("date hired"), rs.getString("status"));
                e.setId(rs.getInt("empid"));
            }else{
                e.setId(-1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            e.setId(-2);
        }
        return e;
    }
    
    @POST
    @Path("/adjuststckqty")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String adjustStckQty(@FormParam("qty") double qty, @FormParam("stckname") String stckname) {
        String status = "adjusted";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE stocks SET qty= ? WHERE stckname = ?");
            ps.setDouble(1, qty);
            ps.setString(2, stckname);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            status = "failed";
        }
        return status;
    }    
    
    @POST
    @Path("/suppliername")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Supplier getSupplier(@FormParam("name") String name) {
        Supplier s = new Supplier();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `supplier` WHERE `supname`= ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                s = new Supplier(rs.getString("supname"), rs.getString("address"), rs.getString("mobilenum"), rs.getString("telephonenum"));
                s.setId(rs.getInt("supid"));
            }
            ps.close();
            rs.close();
            return s;
        } catch   (Exception e) {
            System.out.println("Exception: " + e);
            return s;
        }
    }
    
    @GET
    @Path("/stockcategories")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getStockCategories(){
        ArrayList<Category> categorylist = new ArrayList<>();
        Category category = new Category();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT unit FROM stocks;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                category = new Category(rs.getString("unit"));
                categorylist.add(category);
            }
            ps.close();
            rs.close();
            return categorylist;
        } catch   (Exception e) {
            System.out.println("Exception: " + e);
            return categorylist;
        }
    }
    
    //return details of a particular purchse order to client
    @GET
    @Path("/purchaseOrder/{poid}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrder getPurchaseOrder(@PathParam("poid") int poid) {
            PurchaseOrder po = new PurchaseOrder();
        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `purchase order` WHERE poid = ?");
            ps.setInt(1, poid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                po.setPoid(rs.getInt("poid"));
            }
            ps.close();
            rs.close();
            return po;
        } catch   (Exception e) {
            System.out.println("Exception: " + e);
            return po;
        }
    }
    
    @GET
    @Path("/updatePurOrd/{poid}/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrder updatePurOrd( @PathParam("poid") int poid, @PathParam("status") String status) {
        PurchaseOrder purOrd = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `purchase order` SET `status` = ?  WHERE `purchase order`.`poid` = ?");
            ps.setString(1, status);
            ps.setInt(2, poid );
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            purOrd.setPoid(-2);
        }
        return purOrd;
    }
    
    @POST
    @Path("/addStckComp")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addStckComp(StckCompound sc) {
        String message = "Successfully added product";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `stckcompound`(`stckidbasic`, `stckidcompound`, `qty`) VALUES (?, ?, ?)");
            ps.setInt(1, sc.getStckidbasic());
            ps.setInt(2, sc.getStckidcompound());
            ps.setDouble(3, sc.getQty());
            ps.executeUpdate();
        } catch (Exception e) {
            message = "failed";
        }
        return message;
    }
    
    @POST
    @Path("/addProduct")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public String addproduct(Product p) {
        String message = "Successfully added product";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO products( prodname, category, price) VALUES (?, ?, ?)");
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.executeUpdate();
            p.setId(1);
        } catch (Exception e) {
            message = "failed";
        }
        return message;
    }
    
    @POST
    @Path("/addprodstck")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public String addprodstck(Prodstck p) {
        String message = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `prodstck`(`prdctid`, `stckid`, `qty`) VALUES (?, ?, ?)");
            System.out.println(p.getPrdctid());
            System.out.println(p.getStckid());
            System.out.println(p.getQty());
            ps.setInt(1, p.getPrdctid());
            ps.setInt(2, p.getStckid());
            ps.setDouble(3, p.getQty());
            ps.executeUpdate();
        } catch (Exception e) {
            message = "failed";
        }
        return message;
    }
    
    @POST
    @Path("/addsupplier")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public int addSupplier(Supplier s) {
        int ret = 1;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `supplier`(`supname`, `contactperson`, `address`, `mobilenum`, `telephonenum`) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, s.getName());
            ps.setString(2, s.getContactperson());
            ps.setString(3, s.getAddress());
            ps.setString(4, s.getMobilenum());
            ps.setString(5, s.getTelephonenum());
            ps.executeUpdate();
        } catch (Exception e) {
            ret = -1;
            e.printStackTrace();
        }
        return ret;
    }
    
    @POST
    @Path("/addpurchaseorder")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public PurchaseOrder addPurchaseOrder(PurchaseOrder purord) {
        PurchaseOrder po = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `purchase order`(`status`, `dateordered`, `empname`) VALUES (?, ?, ?)");
            ps.setString(1, purord.getStatus() );
            ps.setString(2, purord.getDateordered() );
            ps.setString(3, purord.getEmpname() );
            ps.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM `purchase order` ORDER BY poid DESC LIMIT 0, 1");
            ResultSet rs = ps2.executeQuery();
            while(rs.next()) {
                po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                po.setPoid(rs.getInt("poid"));
            }
        } catch (Exception e) {
        }
        return po;
    }
    
    //add entry to stckpurord
    @POST
    @Path("/addstckpurord")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public StckPurOrd addStckPurord(StckPurOrd sp) {
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `stckpurord`(`stksid`, `poid`, `supid`, `qtyordered`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, sp.getStksid());
            ps.setInt(2, sp.getPoid());
            ps.setInt(3, sp.getSuppid());
            ps.setInt(4, sp.getQtyordered());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sp;
    }
    
    //add entry to stock
    @POST
    @Path("/addstock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public Stock addStock(Stock s) {
        Stock stock = new Stock();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `stocks`(`stckname`, `qty`, `reorder point`, `reorder quantity`, `kitchenunit`, `deliveryunit`, `equivalent`, `type`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, s.getStckname());
            ps.setDouble(2, s.getQty());
            ps.setInt(3, s.getReorderpt());
            ps.setInt(4, s.getReorderqty());
            ps.setString(5, s.getKitchenunit());
            ps.setString(6, s.getDeliveryunit());
            ps.setDouble(7, s.getEquivalent());
            ps.setString(8, s.getType());
            ps.executeUpdate();
            s.setStckid(1);
        } catch (Exception e) {
            e.printStackTrace();
            s.setStckid(-2);
        }
        return s;
    }
    
    //add entry to stock
    @POST
    @Path("/addstcksup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public StckSup addStckSup(StckSup s) {
        StckSup ss = new StckSup();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `stcksup`(`suppid`, `stocksid`) VALUES (?, ?)");
            ps.setInt(1, s.getSuppid());
            ps.setInt(2, s.getStocksid());
            ps.executeUpdate();
            ss.setStocksid(1);
        } catch (Exception e) {
            e.printStackTrace();
            ss.setStocksid(-2);
        }
        return ss;
    }
    
    //delete stckcompounds based on id sent
    @POST
    @Path("/deletestckcomp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public StckCompound deleteStckCompound(@FormParam("stckid") int stckid) {
        StckCompound sc = new StckCompound();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `stckcompound` WHERE `stckidcompound` = ?");
            ps.setInt(1, stckid);
            ps.executeUpdate();
            sc.setStckidcompound(1);
        } catch (Exception e) {
            sc.setStckidcompound(-1);
            //message = "failed";
            e.printStackTrace();
        }
        return sc;
    }
    
    //delete prodstcks based on id sent
    @POST
    @Path("/deletestcksup")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public StckSup deleteStcksup(@FormParam("stocksid") int stocksid) {
        StckSup ss = new StckSup();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `stcksup` WHERE `stocksid` = ?");
            ps.setInt(1, stocksid);
            ps.executeUpdate();
            ss.setStocksid(1);
        } catch (Exception e) {
            ss.setStocksid(-1);
            //message = "failed";
            e.printStackTrace();
        }
        return ss;
    }
    
    //add entry to stock
    @POST
    @Path("/deleteprodstck")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Prodstck deleteProdStck(@FormParam("prodid") int prodid) {
        Prodstck prodstck = new Prodstck();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `prodstck` WHERE `prdctid` = ?");
            ps.setInt(1, prodid);
            ps.executeUpdate();
            prodstck.setPrdctid(1);
        } catch (Exception e) {
            prodstck.setPrdctid(-1);
            e.printStackTrace();
        }
        return prodstck;
    }
    
    //return an arraylist of stckpurord object fetched from the database based on the foreign key poid
    @GET
    @Path("/stckpurord/{poid}")
    @Produces(MediaType.APPLICATION_JSON)
    public StckPurOrdList getStckPurOrd(@PathParam("poid") int poid) {
        ArrayList<StckPurOrd> stckpurordlist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT a.stksid, a.poid, a.supid, a.datedelivered, a.qtyordered, a.qtydelivered,a.status, a.qtyremaining, b.stckname, b.equivalent, b.deliveryunit, c.empname, d.supname  FROM stckpurord a INNER JOIN stocks b ON a.stksid = b.stckid INNER JOIN `purchase order` c ON a.poid = c.poid INNER JOIN supplier d ON a.supid = d.supid WHERE a.poid = ?");
            ps.setInt(1, poid );
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                StckPurOrd spo = new StckPurOrd(rs.getInt("stksid"), rs.getInt("supid"), rs.getInt("poid"), rs.getInt("qtyordered"));
                spo.setDatedelivered(rs.getString("datedelivered"));
                spo.setQtydelivered( rs.getInt("qtydelivered"));
                spo.setStckname( rs.getString("stckname"));
                spo.setEmpname( rs.getString("empname"));
                spo.setSupname( rs.getString("supname"));
                spo.setEquivalent( rs.getDouble("equivalent"));
                spo.setDeliveryunit( rs.getString("deliveryunit"));
                spo.setStatus(rs.getString("status"));
                spo.setQtyremaining(rs.getInt("qtyremaining"));
                stckpurordlist.add(spo);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        StckPurOrdList spoList = new StckPurOrdList(stckpurordlist);
        return spoList;
    }
    
    @GET
    @Path("/ledgerrecord/{stckid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Ledger getLedger(@PathParam("stckid") int stckid) {
        ArrayList<LedgerRecord> ledgerrecord = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `ledger` WHERE stckid = ?");
            ps.setInt(1, stckid );
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                LedgerRecord lr = new LedgerRecord( rs.getInt("stckid"), rs.getString("date"), rs.getDouble("qtybefore"), rs.getDouble("qtyafter"), rs.getString("reason") );
                lr.setQtyin(rs.getDouble("qtyin"));
                lr.setQtyout(rs.getDouble("qtyout"));
                lr.setOrderid(rs.getInt("orderid"));
                lr.setPoid(rs.getInt("poid"));
                ledgerrecord.add(lr);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Ledger ledger = new Ledger(ledgerrecord);
        return ledger;
    }
    
    @GET
    @Path("/stckcompounds/{stckid}")
    @Produces(MediaType.APPLICATION_JSON)
    public StckCompoundList getStckcompounds(@PathParam("stckid") int stckid) {
        ArrayList<StckCompound> sclist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT a.`stckidcompound`, a.`qty`, b.stckid, b.stckname, b.kitchenunit FROM stckcompound a JOIN stocks b ON a.stckidbasic = b.stckid WHERE a.`stckidcompound` = ?");
            ps.setInt(1, stckid );
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                StckCompound sc = new StckCompound(rs.getInt("stckid"), rs.getInt("stckidcompound"), rs.getDouble("qty"));
                sc.setStckname(rs.getString("stckname"));
                sc.setKitchenunit(rs.getString("kitchenunit"));
                sclist.add(sc);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        StckCompoundList stckclist = new StckCompoundList(sclist);
        return stckclist;
    }

    @POST
    @Path("/updateStckPurOrd")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String updateStckPurOrd(@FormParam("datedelivered") String datedelivered,
            @FormParam("qtydelivered") int qtydelivered,
            @FormParam("qtyremaining") int qtyremaining,
            @FormParam("poid") int poid,
            @FormParam("stksid") int stksid) {
        String message = "success";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `stckpurord` SET `datedelivered`= ?,`qtydelivered`= ?,`qtyremaining`= ?  WHERE `poid` = ? AND `stksid` = ?");
            ps.setString(1, datedelivered);
            ps.setInt(2, qtydelivered);
            ps.setInt(3, qtyremaining);
            ps.setInt(4, poid);
            ps.setInt(5, stksid);
            ps.executeUpdate();
        } catch (Exception e) {
            message = "failed";
            e.printStackTrace();
        }
        return message;
    }

    //insert purchase order row to ledgerrecord and return ledgerrecord object
    @POST
    @Path("/addledgerrecordpurchaseorder")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public String addLedgerPORecord(LedgerRecord lr) {
        String message = "added";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `ledger`(`date`, `qtyin`, `qtyout`, `qtybefore`, `qtyafter`, `reason`, `stckid`, `poid`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, lr.getDate());
            ps.setDouble(2, lr.getQtyin());
            ps.setDouble(3, lr.getQtyout());
            ps.setDouble(4, lr.getQtybefore());
            ps.setDouble(5, lr.getQtyafter());
            ps.setString(6, lr.getReason());
            ps.setDouble(7, lr.getStckid());
            ps.setInt(8, lr.getPoid());
            ps.executeUpdate();
        } catch (Exception e) {
            message = "error";
            e.printStackTrace();
        }
        return message;
    }
    
    //insert purchase order row to ledgerrecord and return ledgerrecord object
    @POST
    @Path("/addledgerrecordcreatecomp")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_XML)
    public String addLedgerCCRecord(LedgerRecord lr) {
        String message = "added";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `ledger`(`date`, `qtyin`, `qtyout`, `qtybefore`, `qtyafter`, `reason`, `stckid`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, lr.getDate());
            ps.setDouble(2, lr.getQtyin());
            ps.setDouble(3, lr.getQtyout());
            ps.setDouble(4, lr.getQtybefore());
            ps.setDouble(5, lr.getQtyafter());
            ps.setString(6, lr.getReason());
            ps.setDouble(7, lr.getStckid());
            ps.executeUpdate();
        } catch (Exception e) {
            message = "error";
            e.printStackTrace();
        }
        return message;
    }
    
    //insert purchase order row to ledgerrecord and return ledgerrecord object
    @POST
    @Path("/addledgerrecord")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addLedgerRecord(LedgerRecord lr) {
        String message = "added";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `ledger`(`date`, `qtyin`, `qtyout`, `qtybefore`, `qtyafter`, `reason`, `stckid`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, lr.getDate());
            ps.setDouble(2, lr.getQtyin());
            ps.setDouble(3, lr.getQtyout());
            ps.setDouble(4, lr.getQtybefore());
            ps.setDouble(5, lr.getQtyafter());
            ps.setString(6, lr.getReason());
            ps.setDouble(7, lr.getStckid());
            ps.executeUpdate();
        } catch (Exception e) {
            message = "error";
            e.printStackTrace();
        }
        return message;
    }
    
    //update stock details
    @POST
    @Path("/updatestck")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public Stock updateStck(Stock s) {
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `stocks` SET `stckname`= ?, `reorder point`= ?,`reorder quantity`= ?,`kitchenunit`= ?,`deliveryunit`= ?,`equivalent`= ?,`type`= ? WHERE `stckid` = ?");
            ps.setString(1, s.getStckname());
            ps.setInt(2, s.getReorderpt());
            ps.setInt(3, s.getReorderqty());
            ps.setString(4, s.getKitchenunit());
            ps.setString(5, s.getDeliveryunit());
            ps.setDouble(6, s.getEquivalent());
            ps.setString(7, s.getType());
            ps.setInt(8, s.getStckid());
            ps.executeUpdate();
            s.setStckid(1);
        } catch (Exception e) {
            s.setStckid(-2);
            e.printStackTrace();
        }
        return s;
    }
    
    @POST
    @Path("/updateproduct")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_XML)
    public Product updateProduct(Product p) {
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE `products` SET prodname=?,`category`=?,`price`=? WHERE `prodid` = ?");
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
            p.setId(1);
        } catch (Exception e) {
            p.setId(-2);
            e.printStackTrace();
        }
        return p;
    }
    
    //update qty of stock; add or subtract
    @POST
    @Path("/updateStckQty")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Stock updateStckQty(@FormParam("stckid") int stckid,
            @FormParam("qty") double qty,
            @FormParam("op") String op,
            @Context HttpServletResponse servletResponse){
        Stock stock = new Stock();
        double currentqty = 0;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = null;
            if(op.equals("in")){
                ps = conn.prepareStatement("UPDATE `stocks` SET `qty`=`qty` + ? WHERE stckid = ?");
                ps.setDouble(1, qty);
                ps.setInt(2, stckid);
                ps.executeUpdate();
            }else if(op.equals("out")){
                ps = conn.prepareStatement("UPDATE `stocks` SET `qty`=`qty` - ? WHERE stckid = ?");
                ps.setDouble(1, qty);
                ps.setInt(2, stckid);
                ps.executeUpdate();
            }
            PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM `stocks` WHERE stckid = ?");
                ps1.setInt(1, stckid);
                ResultSet rs = ps1.executeQuery();
                    while(rs.next()){
                        stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                        stock.setStckid(rs.getInt("stckid"));
                    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stock;
    }
/*
    @GET
    @Path("/billofmat/{prodname}")
    @Produces(MediaType.APPLICATION_JSON)
    public BillOfMat getBillOfMaterials(@PathParam("prodname") String prodname) {
        BillOfMat bm = new BillOfMat(prodname);
        HashMap<String, Integer> bill = bm.getMaterials();
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/panbox", "root", "root");
            PreparedStatement billQuery = con.prepareStatement("SELECT stocks.stckid, stocks.stckname, stocks.qty FROM stocks JOIN prodstck ON stocks.stckid = prodstck.stckid JOIN products ON products.prodid = prodstck.prdctid WHERE products.prodname = ?");
            billQuery.setString(1, prodname);
            ResultSet billResult = billQuery.executeQuery();
            while(billResult.next()) {
                Stock s = new Stock(billResult.getInt("stocks.stckid"), billResult.getString("stocks.stckname"));
                bill.put(billResult.getString("stocks.stckname"), billResult.getInt("stocks.qty"));
            }
            bm.setMaterials(bill);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return bm;
    }
*/
    //return an arraylist of all the supplier in the database
    @GET
    @Path("/suppliers")
    @Produces(MediaType.APPLICATION_JSON)
    public SupplierList supplierList() {
        ArrayList<Supplier> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM supplier ORDER BY supname ASC");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                
                Supplier s = new Supplier(rs.getString("supname"), rs.getString("address"), rs.getString("mobilenum"), rs.getString("telephonenum")) ;
                s.setId(rs.getInt("supid"));
                s.setStatus(rs.getString("status"));
                s.setContactperson(rs.getString("contactperson"));
                list.add(s);
            }
                }catch(Exception e){
            
        }
        SupplierList suppList = new SupplierList(list);
        return suppList;
    }

    @GET
    @Path("/purchaseOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrderList purchaseOrderList() {
        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
        PurchaseOrder po = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `purchase order` WHERE 1");
            ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                    po.setPoid(rs.getInt("poid"));
                    purchaseOrders.add(po);
                }
            }catch(Exception e){
            e.printStackTrace();
        }
        PurchaseOrderList poList = new PurchaseOrderList(purchaseOrders);
        return poList;
    }
    
    @GET
    @Path("/purchaseOrderList/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrderList getPurchaseOrderListFromStatus(@PathParam("status") String status) {
        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
        PurchaseOrder po = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `purchase order` WHERE status = ?");
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                    po.setPoid(rs.getInt("poid"));
                    purchaseOrders.add(po);
                }
            }catch(Exception e){
            e.printStackTrace();
        }
        PurchaseOrderList poList = new PurchaseOrderList(purchaseOrders);
        return poList;
    }
    
    @GET
    @Path("/stckpurords/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public StckPurOrdList stckPurOrdListFromDate(@PathParam("date") String date) {
        ArrayList<StckPurOrd> stckpurordlist = new ArrayList<>();
        StckPurOrd spo = new StckPurOrd();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`poid`, a.`dateordered`, b.datedelivered, a.`empname`, c.stckname, c.deliveryunit, b.qtyordered, b.qtydelivered, a.`status`, b.stksid, a.poid, d.supid, d.supname, c.equivalent FROM `purchase order` a INNER JOIN stckpurord b ON a.poid = b.poid INNER JOIN stocks c ON b.stksid = c.stckid INNER JOIN supplier d ON b.supid = d.supid WHERE DATE(a.dateordered)=?");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
                while(rs.next()){
                spo = new StckPurOrd(rs.getInt("stksid"), rs.getInt("supid"), rs.getInt("poid"), rs.getInt("qtyordered"));
                spo.setDateordered(rs.getString("dateordered"));
                spo.setDatedelivered(rs.getString("datedelivered"));
                spo.setQtydelivered( rs.getInt("qtydelivered"));
                spo.setStckname( rs.getString("stckname"));
                spo.setEmpname( rs.getString("empname"));
                spo.setSupname( rs.getString("supname"));
                spo.setEquivalent( rs.getDouble("equivalent"));
                spo.setDeliveryunit( rs.getString("deliveryunit"));
                spo.setStatus(rs.getString("status"));
                stckpurordlist.add(spo);
            }
            }catch(Exception e){
            e.printStackTrace();
        }
        StckPurOrdList spoList = new StckPurOrdList(stckpurordlist);
        return spoList;
    }
    
    @POST
    @Path("/stckpurordsquery")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public StckPurOrdList stckPurOrdListBetweenDate(@FormParam("datefrom") String datefrom, @FormParam("dateto") String dateto) {
        ArrayList<StckPurOrd> stckpurordlist = new ArrayList<>();
        StckPurOrd spo = new StckPurOrd();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`poid`, a.`dateordered`, b.datedelivered, a.`empname`, c.stckname, c.deliveryunit, b.qtyordered, b.qtydelivered, a.`status`, b.stksid, a.poid, d.supid, d.supname, c.equivalent FROM `purchase order` a INNER JOIN stckpurord b ON a.poid = b.poid INNER JOIN stocks c ON b.stksid = c.stckid INNER JOIN supplier d ON b.supid = d.supid WHERE DATE(a.dateordered) BETWEEN ? AND ?");
            ps.setString(1, datefrom);
            ps.setString(2, dateto);
            ResultSet rs = ps.executeQuery();
                while(rs.next()){
                spo = new StckPurOrd(rs.getInt("stksid"), rs.getInt("supid"), rs.getInt("poid"), rs.getInt("qtyordered"));
                spo.setDateordered(rs.getString("dateordered"));
                spo.setDatedelivered(rs.getString("datedelivered"));
                spo.setQtydelivered( rs.getInt("qtydelivered"));
                spo.setStckname( rs.getString("stckname"));
                spo.setEmpname( rs.getString("empname"));
                spo.setSupname( rs.getString("supname"));
                spo.setEquivalent( rs.getDouble("equivalent"));
                spo.setDeliveryunit( rs.getString("deliveryunit"));
                spo.setStatus(rs.getString("status"));
                stckpurordlist.add(spo);
            }
            }catch(Exception e){
            e.printStackTrace();
        }
        StckPurOrdList spoList = new StckPurOrdList(stckpurordlist);
        return spoList;
    }
    
    @POST
    @Path("/stckpurordsmonth")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public StckPurOrdList stckPurOrdListMonth(@FormParam("month") String month, @FormParam("year") String year) {
        ArrayList<StckPurOrd> stckpurordlist = new ArrayList<>();
        StckPurOrd spo = new StckPurOrd();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`poid`, a.`dateordered`, b.datedelivered, a.`empname`, c.stckname, c.deliveryunit, b.qtyordered, b.qtydelivered, a.`status`, b.stksid, a.poid, d.supid, d.supname, c.equivalent FROM `purchase order` a INNER JOIN stckpurord b ON a.poid = b.poid INNER JOIN stocks c ON b.stksid = c.stckid INNER JOIN supplier d ON b.supid = d.supid WHERE MONTH(a.dateordered) = ? AND YEAR(a.dateordered) = ?");
            ps.setString(1, month);
            ps.setString(2, year);
            ResultSet rs = ps.executeQuery();
                while(rs.next()){
                spo = new StckPurOrd(rs.getInt("stksid"), rs.getInt("supid"), rs.getInt("poid"), rs.getInt("qtyordered"));
                spo.setDateordered(rs.getString("dateordered"));
                spo.setDatedelivered(rs.getString("datedelivered"));
                spo.setQtydelivered( rs.getInt("qtydelivered"));
                spo.setStckname( rs.getString("stckname"));
                spo.setEmpname( rs.getString("empname"));
                spo.setSupname( rs.getString("supname"));
                spo.setEquivalent( rs.getDouble("equivalent"));
                spo.setDeliveryunit( rs.getString("deliveryunit"));
                spo.setStatus(rs.getString("status"));
                stckpurordlist.add(spo);
            }
            }catch(Exception e){
            e.printStackTrace();
        }
        StckPurOrdList spoList = new StckPurOrdList(stckpurordlist);
        return spoList;
    }
    
    @POST
    @Path("/stckpurordsyear")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public StckPurOrdList stckPurOrdListYear(@FormParam("year") String year) {
        ArrayList<StckPurOrd> stckpurordlist = new ArrayList<>();
        StckPurOrd spo = new StckPurOrd();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`poid`, a.`dateordered`, b.datedelivered, a.`empname`, c.stckname, c.deliveryunit, b.qtyordered, b.qtydelivered, a.`status`, b.stksid, a.poid, d.supid, d.supname, c.equivalent FROM `purchase order` a INNER JOIN stckpurord b ON a.poid = b.poid INNER JOIN stocks c ON b.stksid = c.stckid INNER JOIN supplier d ON b.supid = d.supid WHERE YEAR(a.dateordered) = ?");
            ps.setString(1, year);
            ResultSet rs = ps.executeQuery();
                while(rs.next()){
                spo = new StckPurOrd(rs.getInt("stksid"), rs.getInt("supid"), rs.getInt("poid"), rs.getInt("qtyordered"));
                spo.setDateordered(rs.getString("dateordered"));
                spo.setDatedelivered(rs.getString("datedelivered"));
                spo.setQtydelivered( rs.getInt("qtydelivered"));
                spo.setStckname( rs.getString("stckname"));
                spo.setEmpname( rs.getString("empname"));
                spo.setSupname( rs.getString("supname"));
                spo.setEquivalent( rs.getDouble("equivalent"));
                spo.setDeliveryunit( rs.getString("deliveryunit"));
                spo.setStatus(rs.getString("status"));
                stckpurordlist.add(spo);
            }
            }catch(Exception e){
            e.printStackTrace();
        }
        StckPurOrdList spoList = new StckPurOrdList(stckpurordlist);
        return spoList;
    }
    
    @GET
    @Path("/purchaseOrders/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrderList purchaseOrderListFromDate(@PathParam("date") String date) {
        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
        PurchaseOrder po = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `purchase order` WHERE DATE(dateordered) = ?");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                    po.setPoid(rs.getInt("poid"));
                    purchaseOrders.add(po);
                }
            }catch(Exception e){
            e.printStackTrace();
        }
        PurchaseOrderList poList = new PurchaseOrderList(purchaseOrders);
        return poList;
    }      
    
    @GET
    @Path("/purchaseOrdersret/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrderList purchaseOrderListRet(@PathParam("date") String date) {
        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
        PurchaseOrder po = new PurchaseOrder();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `purchase order` WHERE DATE(dateordered) = ? AND status NOT LIKE '%unreceived%'");
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    po = new PurchaseOrder(rs.getString("dateordered"), rs.getString("status"), rs.getString("empname"));
                    po.setPoid(rs.getInt("poid"));
                    purchaseOrders.add(po);
                }
            }catch(Exception e){
            e.printStackTrace();
        }
        PurchaseOrderList poList = new PurchaseOrderList(purchaseOrders);
        return poList;
    }
    
    @POST
    @Path("/updateempinfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateEmployeeInfo(Employee u) throws SQLException {
        System.out.println(u.getId() + " " + u.getName());
        String ret = "Info Updated";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement infoQuery = conn.prepareStatement("UPDATE employees SET name = ?, position = ?, `date hired` = ?, address = ?, contacts = ? WHERE empid = ?");
            infoQuery.setString(1, u.getName());
            infoQuery.setString(2, u.getPosition());
            infoQuery.setString(3, u.getDatehired());
            infoQuery.setString(4, u.getAddress());
            infoQuery.setString(5, u.getContact());
            infoQuery.setInt(6, u.getId());
            infoQuery.executeUpdate();
        } catch(Exception e) {
            System.out.println("Exception[updateinfo]: " + e);
            ret = e.toString();
        }
        return ret;
    }
    
    @GET
    @Path("/prodstcks/{prdctid}")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdstckList getProdstckListFromPrdctid(@PathParam("prdctid") int prdctid) {
        ArrayList<Prodstck> prodstcklist = new ArrayList<>();
        Prodstck pstck = new Prodstck();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`prdctid`, a.`stckid`, a.`qty`, b.stckname, b.kitchenunit FROM `prodstck` a JOIN `stocks` b ON a.stckid = b.stckid WHERE a.prdctid = ?");
            ps.setInt(1, prdctid);
            ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    pstck = new Prodstck(rs.getInt("prdctid"), rs.getInt("stckid"), rs.getDouble("qty"));
                    pstck.setKitcheunit(rs.getString("kitchenunit"));
                    pstck.setStckname(rs.getString("stckname"));
                    prodstcklist.add(pstck);
                }
            }catch(Exception e){
            e.printStackTrace();
        }
        ProdstckList list = new ProdstckList(prodstcklist);
        return list;
    }
    
    @POST
    @Path("/employeestatus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeEmpStatus(@FormParam("empid") int id,
            @FormParam("status") String status) throws SQLException {
        String s = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement empStatQuery = conn.prepareStatement("UPDATE employees SET status=? WHERE empid = ?");
            empStatQuery.setString(1, status);
            empStatQuery.setInt(2, id);
            empStatQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[empstatus]: " + e);
            s = "error";
        }
        return s;
    }
    
    @GET
    @Path("/prodstckList")
    @Produces(MediaType.APPLICATION_JSON)
    public ProdstckList prodstckList() {
        ArrayList<Prodstck> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM prodstck");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                
                Prodstck pStock = new Prodstck(rs.getInt("prdctid"), rs.getInt("stckid"), rs.getInt("qty")) ;
                list.add(pStock);
            }
                }catch(Exception e){
            e.printStackTrace();
        }
        ProdstckList pStockList = new ProdstckList(list);
        return pStockList;
    }
    
    //return stock details of a particular stock
    @GET
    @Path("/stock/{stckname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Stock getStck(@PathParam("stckname") String stckname) {
        Stock stock = new Stock();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.`stckid`, a.`stckname`, a.`qty`, a.`reorder point`, a.`reorder quantity`, a.`kitchenunit`, a.`equivalent`, a.`deliveryunit`, a.`type`, b.suppid, c.supname  FROM `stocks` a INNER JOIN stcksup b ON a.stckid = b.stocksid INNER JOIN supplier c ON c.supid = b.suppid WHERE a.stckname = ?");
            ps.setString(1, stckname);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stock.setSupname(rs.getString("supname"));
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return stock;
    }
    
    @GET
    @Path("/stcksups/{stckid}")
    @Produces(MediaType.APPLICATION_JSON)
    public StckSupList getStckSupList(@PathParam("stckid") int stckid) {
        StckSup stcksup;
        ArrayList<StckSup> sslist = new ArrayList<>();
        StckSupList list = new StckSupList();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT a.suppid, a.stocksid ,b.supname  FROM stcksup a INNER JOIN supplier b ON a.suppid = b.supid WHERE a.stocksid = ?");
            ps.setInt(1, stckid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                stcksup = new StckSup(rs.getInt("suppid"), rs.getInt("stocksid"));
                stcksup.setSupname(rs.getString("supname"));
                sslist.add(stcksup);
            }
            list = new StckSupList(sslist);
            }catch(Exception e){
                e.printStackTrace();
            }
        return list;
    }
    
    @GET
    @Path("/stockdetails/{stckname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Stock getStckDetails(@PathParam("stckname") String stckname) {
        Stock stock = new Stock();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM stocks WHERE stckname = ?");
            ps.setString(1, stckname);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return stock;
    }
    
    @GET
    @Path("/stocklist/{order}")
    @Produces(MediaType.APPLICATION_JSON)
    public StockList getStockList(@PathParam("order") String order){
        ArrayList<Stock> stocklist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        PreparedStatement ps;
        try{
            if(order.equals("DESC")){
                ps = conn.prepareStatement("SELECT * FROM `stocks` ORDER BY stckname DESC");
            }else{
                ps = conn.prepareStatement("SELECT * FROM `stocks` ORDER BY stckname ASC");
            }
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Stock stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stocklist.add(stock);
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        StockList slist = new StockList(stocklist);
        return slist;
    }
    
    @GET
    @Path("/compounds")
    @Produces(MediaType.APPLICATION_JSON)
    public StockList getCompounds(){
        ArrayList<Stock> stocklist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `stocks` WHERE type = \"compound\" ORDER BY stckname ASC");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Stock stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stocklist.add(stock);
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        StockList slist = new StockList(stocklist);
        return slist;
    }
    
    @GET
    @Path("/basic")
    @Produces(MediaType.APPLICATION_JSON)
    public StockList getBasics(){
        ArrayList<Stock> stocklist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `stocks` WHERE type = \"basic\" ORDER BY stckname ASC");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Stock stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stocklist.add(stock);
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        StockList slist = new StockList(stocklist);
        return slist;
    }
    
    @GET
    @Path("/product/{prodname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProductFromName(@PathParam("prodname") String prodname) {
        Product p = null;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE prodname = ?");
            ps.setString(1, prodname);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                p = new Product(rs.getString("prodname"), rs.getString("category"), rs.getDouble("price"));
                p.setStatus(rs.getString("status"));
                p.setId(rs.getInt("prodid"));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
    
    //return an arraylist of stock the supplier provides
    @GET
    @Path("/stocks/{supname}")
    @Produces(MediaType.APPLICATION_JSON)
    public StockList getStockListFromSupp(@PathParam("supname") String supname) {
        ArrayList<Stock> stocklist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT a.`stckid`, a.`stckname`, a.`qty`, a.`reorder point`, a.`reorder quantity`, a.`kitchenunit`, a.`equivalent`, a.`deliveryunit`, a.`type`, b.suppid, c.supname  FROM `stocks` a INNER JOIN stcksup b ON a.stckid = b.stocksid INNER JOIN supplier c ON c.supid = b.suppid WHERE c.supname = ?;");
            ps.setString(1, supname);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Stock stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stock.setSupname(rs.getString("supname"));
                stocklist.add(stock);
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        StockList slist = new StockList(stocklist);
        return slist;
    }
    
    @POST
    @Path("/suppstatus")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String changeSupStatus(@FormParam("supid") int id,
            @FormParam("status") String status) throws SQLException {
        String s = "OK";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement supStatQuery = conn.prepareStatement("UPDATE supplier SET status=? WHERE supid = ?");
            supStatQuery.setString(1, status);
            supStatQuery.setInt(2, id);
            supStatQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Exception[supstatus]: " + e);
            s = "error";
        }
        return s;
    }
    
    @POST
    @Path("/updatesuppinfo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateSuppInfo(Supplier s) {
        System.out.println(s.toString());
        String ret = "Info Updated";
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement infoQuery = conn.prepareStatement("UPDATE supplier SET supname= ?, contactperson = ?, address = ?, mobilenum = ?, telephonenum = ? WHERE supid = ?");
            infoQuery.setString(1, s.getName());
            infoQuery.setString(2, s.getContactperson());
            infoQuery.setString(3, s.getAddress());
            infoQuery.setString(4, s.getMobilenum());
            infoQuery.setString(5, s.getTelephonenum());
            infoQuery.setInt(6, s.getId());
            infoQuery.executeUpdate();
        } catch(Exception e) {
            System.out.println("Exception[updateinfo]: " + e);
            ret = e.toString();
        }
        return ret;
    }
    
    @GET
    @Path("/stocktype/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public StockList getStockListFromType(@PathParam("type") String type){
        ArrayList<Stock> stocklist = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `stocks` WHERE type = \"compound\"");
            ps.setString(1, type);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Stock stock = new Stock(rs.getString("stckname"), rs.getDouble("qty"), rs.getString("kitchenunit"), rs.getString("deliveryunit"), rs.getDouble("equivalent"), rs.getString("type"), rs.getInt("reorder point"), rs.getInt("reorder quantity"));
                stock.setStckid(rs.getInt("stckid"));
                stock.setSupname(rs.getString("supname"));
                stocklist.add(stock);
            }
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        StockList slist = new StockList(stocklist);
        return slist;
    }
    
    
    @GET
    @Path("/kitchenunits")
    @Produces(MediaType.APPLICATION_JSON)
    public String getKitchenUnits(){
        String units = "";
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT kitchenunit FROM stocks WHERE 1;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                units = units + rs.getString("kitchenunit") + ",";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return units;
    }
    
    @GET
    @Path("/deliveryunits")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDeliveryUnits(){
        String units = "";
        Connection conn = (Connection) context.getAttribute("conn");
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT deliveryunit FROM stocks WHERE 1;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                units = units + rs.getString("deliveryunit") + ",";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return units;
    }
    
    @GET
    @Path("/purchasehistory/{supname}")
    @Produces(MediaType.APPLICATION_JSON)
    public PurchaseOrderList getPurchaseHistory(@PathParam("supname") String supname) {
        PurchaseOrderList poList = new PurchaseOrderList();
        ArrayList<PurchaseOrder> list = new ArrayList<>();
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement poQuery = conn.prepareStatement("SELECT DISTINCT(a.`poid`), a.`dateordered`, a.`status`, a.`empname` FROM `purchase order` a INNER JOIN `stckpurord` b ON a.`poid` = b.`poid` INNER JOIN `supplier` c ON b.`supid` = c.`supid` WHERE c.`supname` = ?");
            poQuery.setString(1, supname);
            ResultSet poRes = poQuery.executeQuery();
            while(poRes.next()) {
                PurchaseOrder po = new PurchaseOrder();
                po.setPoid(poRes.getInt("poid"));
                po.setDateordered(poRes.getString("dateordered"));
                po.setStatus(poRes.getString("status"));
                po.setEmpname(poRes.getString("empname"));
                list.add(po);
            }
            poList.setList(list);
        } catch (Exception e) {
            System.out.println("Exception[pohistory]: " + e);
        }
        return poList;
    }
    
    @GET
    @Path("/employee/{empname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Employee getEmployee(@PathParam("empname") String empname) {
        Employee employee =  null;
        Connection conn = (Connection) context.getAttribute("conn");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `employees` WHERE name = ?");
            ps.setString(1, empname);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                employee = new Employee(rs.getString("name"), rs.getString("address"), rs.getString("position"), rs.getString("contacts"), rs.getString("date hired"), rs.getString("status")) ;
                employee.setId(rs.getInt("empid"));
                employee.setUsername(rs.getString("username"));
            }
                }catch(Exception e){
                    e.printStackTrace();
        }
        return employee;
    }
}