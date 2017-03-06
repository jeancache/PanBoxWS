/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

import java.io.Serializable;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="bill")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillOfMat implements Serializable {
    @XmlElement
    private int id;
    @XmlElement
    private HashMap<String, Integer> materials;

    public BillOfMat() {
        materials = new HashMap<>();
    }

    public BillOfMat(int id) {
        this.id = id;
        materials = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<String, Integer> getMaterials() {
        return materials;
    }

    public void setMaterials(HashMap<String, Integer> materials) {
        this.materials = materials;
    }
    
    
}
