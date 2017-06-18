
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
@XmlRootElement(name="stckpurordList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckPurOrdList implements Serializable {
    @XmlElement(required=true)
    ArrayList<StckPurOrd> list;

    public StckPurOrdList(){
    }
    public StckPurOrdList(ArrayList<StckPurOrd> list){
        this.list = list;
    }
    public ArrayList<StckPurOrd> getList() {
        return list;
    }

    public void setList(ArrayList<StckPurOrd> list) {
        this.list = list;
    }
}
