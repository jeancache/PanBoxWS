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
@XmlRootElement(name="stcksup")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckSup implements Serializable {
    @XmlElement(required=true)
    private int suppid;
    @XmlElement(required=true)
    private int stocksid;
    @XmlElement
    private String supname;   
    
    public StckSup() {
    }
    
    public StckSup(int suppid, int stocksid) {
        this.suppid = suppid;
        this.stocksid = stocksid;
    }

    public int getSuppid() {
        return suppid;
    }

    public void setSuppid(int suppid) {
        this.suppid = suppid;
    }

    public int getStocksid() {
        return stocksid;
    }

    public void setStocksid(int stocksid) {
        this.stocksid = stocksid;
    }
    
    public String getSupname() {
        return supname;
    }

    public void setSupname(String supname) {
        this.supname = supname;
    }
}