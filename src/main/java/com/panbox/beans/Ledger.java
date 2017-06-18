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
@XmlRootElement(name="ledger")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ledger implements Serializable {
    @XmlElement(required=true)
    ArrayList<LedgerRecord> list;

    public Ledger(){
    }
    public Ledger(ArrayList<LedgerRecord> list){
        this.list = list;
    }
    public ArrayList<LedgerRecord> getList() {
        return list;
    }

    public void setList(ArrayList<LedgerRecord> list) {
        this.list = list;
    }
}