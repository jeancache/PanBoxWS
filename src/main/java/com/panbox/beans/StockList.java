
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
@XmlRootElement(name="stockList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StockList implements Serializable {
    @XmlElement(required=true)
    ArrayList<Stock> list;

    public StockList(){
    }
    public StockList(ArrayList<Stock> list){
        this.list = list;
    }
    public ArrayList<Stock> getList() {
        return list;
    }

    public void setList(ArrayList<Stock> list) {
        this.list = list;
    }
}
