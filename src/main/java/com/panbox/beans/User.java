/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gina PC
 */
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable {
    @XmlElement(required=true)
    private int id;
    @XmlElement(required=true)
    private String name;
    @XmlElement(required=true)
    private String password;
    @XmlElement(required=true)
    private boolean admin;
    @XmlElement(required=true)
    private boolean barista;
    @XmlElement(required=true)
    private boolean cashier;
    @XmlElement(required=true)
    private boolean cook;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isBarista() {
        return barista;
    }

    public void setBarista(boolean barista) {
        this.barista = barista;
    }

    public boolean isCashier() {
        return cashier;
    }

    public void setCashier(boolean cashier) {
        this.cashier = cashier;
    }

    public boolean isCook() {
        return cook;
    }

    public void setCook(boolean cook) {
        this.cook = cook;
    }
    
    
}
