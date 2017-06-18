package com.panbox.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="prodstck")
@XmlAccessorType(XmlAccessType.FIELD)
public class Prodstck implements Serializable {
    @XmlElement(required=true)
    private int prdctid;
    @XmlElement(required=true)
    private int stckid;
    @XmlElement(required=true)
    private double qty;
    @XmlElement
    private String stckname;
    @XmlElement
    private String kitcheunit;
    
    public Prodstck() {
    }
    
    public Prodstck(int prdctid, int stckid, double qty) {
        this.prdctid = prdctid;
        this.stckid = stckid;
        this.qty = qty;
    }
    
    public String getStckname() {
        return stckname;
    }

    public void setStckname(String stckname) {
        this.stckname = stckname;
    }

    public String getKitcheunit() {
        return kitcheunit;
    }

    public void setKitcheunit(String kitcheunit) {
        this.kitcheunit = kitcheunit;
    }
    
    public int getPrdctid() {
        return prdctid;
    }

    public void setPrdctid(int prdctid) {
        this.prdctid = prdctid;
    }

    public int getStckid() {
        return stckid;
    }

    public void setStckid(int stckid) {
        this.stckid = stckid;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }
}