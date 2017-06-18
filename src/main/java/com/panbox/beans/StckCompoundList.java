
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
@XmlRootElement(name="stckCompoundList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckCompoundList implements Serializable {
    @XmlElement(required=true)
    ArrayList<StckCompound> list;

    public StckCompoundList(){
    }
    public StckCompoundList(ArrayList<StckCompound> list){
        this.list = list;
    }
    public ArrayList<StckCompound> getList() {
        return list;
    }

    public void setList(ArrayList<StckCompound> list) {
        this.list = list;
    }
}
