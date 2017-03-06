/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@XmlRootElement(name="stock")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stock implements Serializable {
    @XmlElement(required=true)
    private int id;
    @XmlElement(required=true)
    private String stockName;

    public Stock() {
    }

    public Stock(int id, String stockName) {
        this.id = id;
        this.stockName = stockName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    
    
}