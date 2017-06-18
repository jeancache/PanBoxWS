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
@XmlRootElement(name="stckcompound")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckCompound implements Serializable {
    @XmlElement(required=true)
    private int stckidbasic;
    @XmlElement(required=true)
    private int stckidcompound;
    @XmlElement(required=true)
    private double qty;
    @XmlElement
    private String stckname;
    @XmlElement
    private String kitchenunit;
    
    public StckCompound() {
    }
    
    public StckCompound(int stckidbasic, int stckidcompound, double qty) {
        this.stckidbasic = stckidbasic;
        this.stckidcompound = stckidcompound;
        this.qty = qty;
    }
    
    

    public String getStckname() {
        return stckname;
    }

    public void setStckname(String stckname) {
        this.stckname = stckname;
    }

    public String getKitchenunit() {
        return kitchenunit;
    }

    public void setKitchenunit(String kitcheunit) {
        this.kitchenunit = kitcheunit;
    }
    
    public int getStckidbasic() {
        return stckidbasic;
    }

    public void setStckidbasic(int stckidbasic) {
        this.stckidbasic = stckidbasic;
    }

    public int getStckidcompound() {
        return stckidcompound;
    }

    public void setStckidcompound(int stckidcompound) {
        this.stckidcompound = stckidcompound;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }
}