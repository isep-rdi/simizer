// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.storage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author sathiya
 */
public class Cache extends StorageElement{
    
    private static int PAGE_SZ = 4096; // memory page sz.
    
    private static double MB_DELAY = 0.019D;
   //private int capacity;
   //private int accessDelay;
   //private int volumeFilled;
   //private Map<Integer, Resource> storage;
    private Map<Integer, Integer> counters = new HashMap<Integer,Integer>();
    
   private LinkedList<Integer> seqList= new LinkedList<Integer>();
      
    public Cache(long capacity, long accessDelay){
        super(capacity, accessDelay, MB_DELAY);
       // this.capacity = capacity;
       // this.accessDelay = accessDelay;
       // this.storage = new HashMap<Integer, Resource>();
       // this.seqList = new LinkedList<Integer>();
       
    }
    
  //  public boolean contains(Integer rscID)
  //  {
  //      return storage.containsKey(rscID);
  //  }
    
    public void writeToCache(Resource r){
        if(r.size() <= capacity) {
            while(r.size() + volumeFilled > capacity){
                
                this.delete(storage.get(seqList.removeFirst()));
             }
        
            this.seqList.addLast(r.getId());
            
            write(r);
            //(counters.get(r.getId()) ?) 
        }
        
           //updateCache(r.getId());
           //volumeFilled += r.size();
    }
    
    public void updateCache(Integer rId){
        
        this.seqList.remove(rId);
        
        this.seqList.addLast(rId);
          
    }
    
  /*  
    // This writeTocache is based on LRU
    public long getAccessTime(Integer rId){
        
        Resource r = se.read(rId);
        
        if(seqList.contains(rId)){
            seqList.remove(rId);
            seqList.add(rId);
           return getDelay(rId);
        }
        else{
           //Ressource r = se.read(rId);
            
           while(r.size() + volumeFilled > capacity){
                seqList.remove();
            }
            
           seqList.add(r.getId());
           volumeFilled += r.size();
           
           return se.getDelay(r.getId());
           }
        
   }
*/
 // public long getDelay(Integer rId) {
 //           return ((storage.get(rId).size() / 1000000) * accessDelay) + 4; //disk_seek
 //       }
}
