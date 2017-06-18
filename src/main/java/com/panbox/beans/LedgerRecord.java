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
@XmlRootElement(name="ledgerrecord")
@XmlAccessorType(XmlAccessType.FIELD)
    public class LedgerRecord implements Serializable {
        @XmlElement(required = true)
        private int stckid;
        @XmlElement(required = true)
        private int orderid;
        @XmlElement(required = true)
        private int poid;
        @XmlElement(required = true)
        private String date;
        @XmlElement
        private double qtyin;
        @XmlElement
        private double qtyout;
        @XmlElement
        private double qtybefore;
        @XmlElement
        private double qtyafter;
        @XmlElement
        private String reason;
    
    public LedgerRecord() {
    }    
        
    public LedgerRecord(int stckid, String date, double qtybefore, double qtyafter, String reason) {
        this.stckid = stckid;
        this.date = date;
        this.qtybefore = qtybefore;
        this.qtyafter = qtyafter;
        this.reason = reason;
    }
    
    public int getStckid() {
        return stckid;
    }

    public void setStckid(int stckid) {
        this.stckid = stckid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getPoid() {
        return poid;
    }

    public void setPoid(int poid) {
        this.poid = poid;
    }

    public double getQtyin() {
        return qtyin;
    }

    public void setQtyin(double qtyin) {
        this.qtyin = qtyin;
    }

    public double getQtyout() {
        return qtyout;
    }

    public void setQtyout(double qtyout) {
        this.qtyout = qtyout;
    }

    public double getQtybefore() {
        return qtybefore;
    }

    public void setQtybefore(double qtybefore) {
        this.qtybefore = qtybefore;
    }

    public double getQtyafter() {
        return qtyafter;
    }

    public void setQtyafter(double qtyafter) {
        this.qtyafter = qtyafter;
    }
}