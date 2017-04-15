/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.panbox.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

/**
 *
 * @author hp
 */
public class OrderAdapter extends XmlAdapter<OrderAdapter.AdaptedMap, Map<String, Integer>>{

    @Override
    public Map<String, Integer> unmarshal(AdaptedMap v) throws Exception {
        Map<String, Integer> map = new HashMap<String, Integer>(v.list.size());
        for(AdaptedEntry adaptedEntry : v.list) {
            map.put(adaptedEntry.key, adaptedEntry.value);
        }
        return map;
    }

    @Override
    public AdaptedMap marshal(Map<String, Integer> v) throws Exception {
        AdaptedMap adaptedMap = new AdaptedMap();
        for(Entry<String, Integer> entry : v.entrySet()) {
            AdaptedEntry adaptedEntry = new AdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.value = entry.getValue();
            adaptedMap.list.add(adaptedEntry);
        }
        return adaptedMap;
    }
    
    public static class AdaptedMap {
        public List<AdaptedEntry> list = new ArrayList<AdaptedEntry>();
    }
    
    public static class AdaptedEntry {
        public String key;
        public Integer value;
    }
    
}
