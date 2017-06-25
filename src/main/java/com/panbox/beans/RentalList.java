
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
@XmlRootElement(name="rentalList")
@XmlAccessorType(XmlAccessType.FIELD)
public class RentalList implements Serializable {
    @XmlElement(required=true)
    ArrayList<Rental> list;

    public RentalList(){
    }
    public RentalList(ArrayList<Rental> list){
        this.list = list;
    }
    public ArrayList<Rental> getList() {
        return list;
    }

    public void setList(ArrayList<Rental> list) {
        this.list = list;
    }
}
