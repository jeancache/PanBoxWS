package com.panbox.beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="stock")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stock implements Serializable {
    @XmlElement(required=true)
    private int stckid;
    @XmlElement(required=true)
    private String stckname;
    @XmlElement(required=true)
    private double qty;
    @XmlElement(required=true)
    private String kitchenunit;
    @XmlElement
    private String deliveryunit;
    @XmlElement(required=true)
    private double equivalent;
    @XmlElement(required=true)
    private String type;
    @XmlElement
    private int supid;
    @XmlElement
    private String supname;
    @XmlElement
    private int reorderpt;
    @XmlElement
    private int reorderqty;
        
    public Stock() {
    }
    
    public Stock(String stckname, double qty, String kitchenunit, String deliveryunit, double equivalent, String type, int reorderpt, int reorderqty) {
        this.stckname = stckname;
        this.qty = qty;
        this.kitchenunit = kitchenunit;
        this.deliveryunit = deliveryunit;
        this.equivalent = equivalent;
        this.type = type;
        this.reorderpt = reorderpt;
        this.reorderqty = reorderqty;
    }
    
    public int getSupid() {
        return supid;
    }

    public void setSupid(int supid) {
        this.supid = supid;
    }

    public String getSupname() {
        return supname;
    }

    public void setSupname(String supname) {
        this.supname = supname;
    }
    
    public int getStckid() {
        return stckid;
    }

    public void setStckid(int stckid) {
        this.stckid = stckid;
    }

    public String getStckname() {
        return stckname;
    }

    public void setStckname(String stckname) {
        this.stckname = stckname;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getKitchenunit() {
        return kitchenunit;
    }

    public void setKitchenunit(String kitchenunit) {
        this.kitchenunit = kitchenunit;
    }

    public String getDeliveryunit() {
        return deliveryunit;
    }

    public void setDeliveryunit(String deliveryunit) {
        this.deliveryunit = deliveryunit;
    }

    public double getEquivalent() {
        return equivalent;
    }

    public void setEquivalent(double equivalent) {
        this.equivalent = equivalent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getReorderpt() {
        return reorderpt;
    }

    public void setReorderpt(int reorderpt) {
        this.reorderpt = reorderpt;
    }

    public int getReorderqty() {
        return reorderqty;
    }

    public void setReorderqty(int reorderqty) {
        this.reorderqty = reorderqty;
    }
    
    }