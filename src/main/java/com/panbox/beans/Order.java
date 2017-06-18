/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order implements Serializable {
    //@XmlElement
    @XmlPath(".")
    @XmlJavaTypeAdapter(OrderAdapter.class)
    private Map<String, Integer> prodlist = new HashMap<String, Integer>();
    @XmlElement(required=true)
    //@XmlTransient
    private int id;
    @XmlElement
    //@XmlTransient
    private double total;
    
    @XmlElement
    private int tablenum;
    
    @XmlElement
    private String status;
    
    public Order() {
        
    }

    public Order(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Map<String, Integer> getProdlist() {
        return prodlist;
    }

    public void setProdlist(Map<String, Integer> prodlist) {
        this.prodlist = prodlist;
    }

    public int getTablenum() {
        return tablenum;
    }

    public void setTablenum(int tablenum) {
        this.tablenum = tablenum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
