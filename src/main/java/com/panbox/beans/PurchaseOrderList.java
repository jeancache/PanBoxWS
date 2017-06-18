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
@XmlRootElement(name="puchaseorderlist")
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseOrderList implements Serializable {
    @XmlElement(required=true)
    ArrayList<PurchaseOrder> list;

    public PurchaseOrderList(){
    }
    public PurchaseOrderList(ArrayList<PurchaseOrder> list){
        this.list = list;
    }
    public ArrayList<PurchaseOrder> getList() {
        return list;
    }

    public void setList(ArrayList<PurchaseOrder> list) {
        this.list = list;
    }
}
