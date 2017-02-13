/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cofmat.cofmatsvr;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="order")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order implements Serializable {
    @XmlElement(required=true)
    private int id;
    @XmlElement
    private ArrayList<Product> prodlist;
    @XmlElement
    private double total;
    
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

    public ArrayList<Product> getProdlist() {
        return prodlist;
    }

    public void setProdlist(ArrayList<Product> prodlist) {
        this.prodlist = prodlist;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    
}
