package com.panbox.beans;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.Serializable;
import java.sql.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="stckpurord")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckPurOrd implements Serializable {
    @XmlElement(required=true)
    private int stksid;
    @XmlElement(required=true)
    private int poid;
    @XmlElement(required=true)
    private int suppid;
    @XmlElement(required=true)
    private int qtyordered;
    @XmlElement
    private int qtydelivered;  
    @XmlElement
    private int qtyremaining;
    @XmlElement
    private double qtyequivalent;
    @XmlElement
    private String unit;
    @XmlElement
    private String dateordered;
    @XmlElement
    private String datedelivered;
    @XmlElement
    private String empname;
    @XmlElement
    private String supname;
    @XmlElement
    private String stckname;
    @XmlElement
    private double equivalent;
    @XmlElement
    private String deliveryunit;
    @XmlElement
    private String status;
    
    public StckPurOrd() {
    }
    
    public StckPurOrd(int stksid, int suppid, int poid, int qtyordered) {
        this.stksid = stksid;
        this.suppid = suppid;
        this.poid = poid;
        this.qtyordered = qtyordered;
    }
    
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public double getQtyequivalent() {
        return qtyequivalent;
    }

    public void setQtyequivalent(double qtyequivalent) {
        this.qtyequivalent = qtyequivalent;
    }
    
    public String getDateordered() {
        return dateordered;
    }

    public void setDateordered(String dateordered) {
        this.dateordered = dateordered;
    }
    
    public int getQtyremaining() {
        return qtyremaining;
    }

    public void setQtyremaining(int qtyremaining) {
        this.qtyremaining = qtyremaining;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getEquivalent() {
        return equivalent;
    }

    public void setEquivalent(double equivalent) {
        this.equivalent = equivalent;
    }

    public String getDeliveryunit() {
        return deliveryunit;
    }

    public void setDeliveryunit(String deliveryunit) {
        this.deliveryunit = deliveryunit;
    }
    
    public String getStckname() {
        return stckname;
    }

    public void setStckname(String stckname) {
        this.stckname = stckname;
    }
    
    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getSupname() {
        return supname;
    }

    public void setSupname(String supname) {
        this.supname = supname;
    }
    
    public int getQtyordered() {
        return qtyordered;
    }
    
    public void setQtyordered(int qtyordered) {
        this.qtyordered = qtyordered;
    }

    public int getQtydelivered() {
        return qtydelivered;
    }

    public void setQtydelivered(int qtydelivered) {
        this.qtydelivered = qtydelivered;
    }

    public String getDatedelivered() {
        return datedelivered;
    }

    public void setDatedelivered(String datedelivered) {
        this.datedelivered = datedelivered;
    }
       
    public int getStksid() {
        return stksid;
    }

    public void setStksid(int stksid) {
        this.stksid = stksid;
    }

    public int getPoid() {
        return poid;
    }

    public void setPoid(int poid) {
        this.poid = poid;
    }

    public int getSuppid() {
        return suppid;
    }

    public void setSuppid(int suppid) {
        this.suppid = suppid;
    }
}