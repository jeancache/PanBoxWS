
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
@XmlRootElement(name="stcksupList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StckSupList implements Serializable {
    @XmlElement(required=true)
    ArrayList<StckSup> list;

    public StckSupList(){
    }
    public StckSupList(ArrayList<StckSup> list){
        this.list = list;
    }
    public ArrayList<StckSup> getList() {
        return list;
    }

    public void setList(ArrayList<StckSup> list) {
        this.list = list;
    }
}
