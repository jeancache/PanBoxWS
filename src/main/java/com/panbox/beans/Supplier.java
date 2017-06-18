package com.panbox.beans;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="supplier")
@XmlAccessorType(XmlAccessType.FIELD)
public class Supplier {
    @XmlElement(required=true)
     private int id;
    @XmlElement(required=true)
     private String name;
    @XmlElement(required=true)
     private String contactperson;
    @XmlElement(required=true)
     private String mobilenum;
    @XmlElement(required=true)
     private String status;    
    @XmlElement(required=true)
     private String address;
    @XmlElement(required=true)
     private String telephonenum;
    
    
    public Supplier() {
    }

    public Supplier(String name, String address, String mobilenum, String telephonenum) {
        this.name = name;
        this.mobilenum = mobilenum;
        this.address = address;
        this.telephonenum = telephonenum;
    }
    
    
    public Supplier(String name,String contactperson, String mobilenum,  String address, String telephonenum) {
        this.name = name;
        this.address = address;
        this.contactperson = contactperson;
        this.mobilenum = mobilenum;
        this.telephonenum = telephonenum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mobilenum
     */
    public String getMobilenum() {
        return mobilenum;
    }

    /**
     * @param mobilenum the mobilenum to set
     */
    public void setMobilenum(String mobilenum) {
        this.mobilenum = mobilenum;
    }

    /**
     * @return the telephonenum
     */
    public String getTelephonenum() {
        return telephonenum;
    }

    /**
     * @param telephonenum the telephonenum to set
     */
    public void setTelephonenum(String telephonenum) {
        this.telephonenum = telephonenum;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the contactPerson
     */
    public String getContactperson() {
        return contactperson;
    }

    /**
     * @param contactPerson the contactPerson to set
     */
    public void setContactperson(String contactPerson) {
        this.contactperson = contactPerson;
    }
    
    @Override
    public String toString(){
        return (id+" "+name+" "+contactperson+" "+ mobilenum+" " +address+" "+ telephonenum);
    }
    

}
