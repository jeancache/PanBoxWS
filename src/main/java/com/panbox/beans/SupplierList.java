/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

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
@XmlRootElement(name="supplierList")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupplierList implements Serializable {
    @XmlElement(required=true)
    ArrayList<Supplier> list;

    public SupplierList(){
    }
    public SupplierList(ArrayList<Supplier> list){
        this.list = list;
    }
    public ArrayList<Supplier> getList() {
        return list;
    }

    public void setList(ArrayList<Supplier> list) {
        this.list = list;
    }
}
