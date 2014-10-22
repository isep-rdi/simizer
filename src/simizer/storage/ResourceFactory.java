/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author isep
 */
public class ResourceFactory {
    
    int  minRes, maxRes;
    int size, resourceCounter=0;
    private final Map<Integer, Resource> resources = new HashMap<>();
    
    public ResourceFactory(int minRes, int maxRes, int size) {
        this.minRes = minRes;
        this.maxRes = maxRes;
        this.size = size;
        init();
    }
    public int getMax() {
        return maxRes;
    }
    
    public int getSize() {
        return size;
    }
    /**
     * 
     * @return number of resources currently in factory 
     */
    public int getResourceNb() {
        return resources.size();
        
    }
    public Resource getResource(Integer id) {
       // System.out.println("called");
        if(!resources.containsKey(id)) {
            resources.put(id, new Resource(id, size));
        }
        Resource r = resources.get(id);
       
        return new Resource(resources.get(id));
    }

    private void init() {
        for(int i = 0; i < minRes; i++) {
            resources.put(i, new Resource(i, size));
        }
    }
    public List<Integer> getStartList() {
        return new ArrayList(resources.keySet());
    }
    
    public List<Integer> getStartList(int numberOfKeys) {
        ArrayList tempList = new ArrayList();
        Object[] tempArray = resources.keySet().toArray();
        
        for(int eachResource=(resourceCounter%tempArray.length);
                eachResource<((resourceCounter+numberOfKeys)%tempArray.length);
                eachResource++) {
            
            tempList.add(tempArray[eachResource]);
            resourceCounter++;
        }
        return tempList;
    }
   
}
