/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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
@XmlRootElement(name="prodstckList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProdstckList implements Serializable {
    @XmlElement(required=true)
    ArrayList<Prodstck> list;

    public ProdstckList(){
    }
    public ProdstckList(ArrayList<Prodstck> list){
        this.list = list;
    }
    public ArrayList<Prodstck> getList() {
        return list;
    }

    public void setList(ArrayList<Prodstck> list) {
        this.list = list;
    }
}