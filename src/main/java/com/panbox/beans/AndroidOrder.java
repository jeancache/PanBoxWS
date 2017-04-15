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
 * @author hp
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AndroidOrder implements Serializable {
    @XmlElement
    private ArrayList<Product> pl;

    public AndroidOrder() {
    }

    public ArrayList<Product> getPl() {
        return pl;
    }

    public void setPl(ArrayList<Product> pl) {
        this.pl = pl;
    }
    
    
}
