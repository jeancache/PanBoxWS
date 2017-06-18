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
@XmlRootElement(name="category")
@XmlAccessorType(XmlAccessType.FIELD)
public class Category implements Serializable {
    
    public Category(String categories) {
        this.categories = categories;
    }
    
    public Category() {
    }
    
    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
    @XmlElement(required = true)
    private String categories;
}