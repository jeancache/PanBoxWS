/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

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
@XmlRootElement(name="purchaseorder")
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseOrder implements Serializable {
    @XmlElement(required=true)
    private int poid;
    @XmlElement(required=true)
    private String dateordered;
    @XmlElement(required=true)
    private String empname;
    @XmlElement(required=true)
    private String stckname;
    @XmlElement
    private String status;
    @XmlElement
    private String supname;
    
    public PurchaseOrder(){
    }  
    
    public PurchaseOrder(String dateordered, String status, String empname) {
        this.dateordered = dateordered;
        this.status = status;
        this.empname = empname;
    }
    
    
    public String getSupname() {
        return supname;
    }

    public void setSupname(String supname) {
        this.supname = supname;
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
    
    public int getPoid() {
        return poid;
    }

    public void setPoid(int poid) {
        this.poid = poid;
    }

    public String getDateordered() {
        return dateordered;
    }

    public void setDateordered(String dateordered) {
        this.dateordered = dateordered;
    }
    
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
