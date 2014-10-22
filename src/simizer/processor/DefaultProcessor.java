
// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import simizer.Node;
import simizer.ServerNode;
import simizer.network.Message;
import simizer.requests.Request;
import simizer.storage.Cache;
import simizer.storage.Resource;
import simizer.storage.StorageElement;

/**
 *
 * @author sathiya
 */
public class DefaultProcessor implements Processor{
    
    private ServerNode nodeInstance = null;
    private Cache memory = null;
    private StorageElement disk = null;
   // private ConsPolicy conspol = null;
    
	/*
    public void setConsPolicy(ConsPolicy conspol){
        this.conspol = conspol;
    }
    */
    
    public DefaultProcessor(ServerNode node) {
		this.nodeInstance = node;
        this.memory = nodeInstance.getCache();
        this.disk = nodeInstance.getStorageElement();
        	
	}
    public Request executeRequest(Request r){
        
        //Request r = m.getRequest();
        Request processed = null;
        
       switch(r.getRequestType()) {
		case "read":
			processed = read(r);
			break;
		case "write":
			processed = write(r);
			break;
		case "modify":
			processed = modify(r);
			break;
			
	   }
	   
	/*	 if(r.getRequestType().equals("read"))
            processed = read(r);
        
        if(r.getRequestType().equals("write"))
            processed = write(r);
        
        if(r.getRequestType().equals("modify"))
            processed = modify(r);
      */ 
	   return processed;
        
    }
        
     public Request read(Request r)  {
            
       // 1. The node id will be registered in the request  
        
        //System.out.println("Node " + id + " received " + r);
        r.setNode(nodeInstance.getId());
        
        long accessTime=0;
        // 2. Check whether the resources exists in its cache
         for(Integer rId: r.getResources()) {
            if(memory.contains(rId)) {
                //if yes update the cache, to follow LRU conscept
                memory.updateCache(rId);
               // accessTime+= nodeInstance.getCache().getDelay(rId);
            } else if(disk.contains(rId)) {
                // if ressource is not in memory
                // check if memory available
                
                // Add the disk access time to retrieve the particular resource
                accessTime+= disk.getDelay(rId);
                //Retrieve the data from the disk and write it to the cache
                Resource res = disk.read(rId);
                
                memory.writeToCache(res);
            }
            else{
                r.setError(r.getError()+1);
            }
        }
        //2. Check locks ????
        
         r.setFinishTime(r.getArTime() + r.getDelay() + r.getProcTime() + accessTime);
        
        return r;
    }

        
    public Request write(Request r)
    {
        r.setNode(nodeInstance.getId());
        
        disk.write(r.getResources());
        
        //System.out.println(r.getId() +" is processed by :"+r.getNodeId());
      
       //Average write time of the resource is considered to be 50m.s 
        //Reference: http://www.tomshardware.com/charts/hdd-charts-2012/-04-Write-Throughput-Average-h2benchw-3.16,2904.html
       long writeTime = 50;
       
       // Write to cache
      // memory.writeToCache(r.getResources());
    
        r.setFinishTime(r.getArTime() + r.getDelay() + r.getProcTime() + writeTime);
        
        return r;
        
    }
    

    public Request modify(Request r)
    {
        r.setNode(nodeInstance.getId());
        
        disk.write(r.getResources());
        
        //System.out.println(r.getId() +" is processed by :"+r.getNodeId());
      
       //Average write time of the resource is considered to be 50m.s 
        //Reference: http://www.tomshardware.com/charts/hdd-charts-2012/-04-Write-Throughput-Average-h2benchw-3.16,2904.html
       long writeTime = 50;
       
       // Write to cache
      // memory.writeToCache(r.getResources());
       
        r.setFinishTime(r.getArTime() + r.getDelay() + r.getProcTime() + writeTime);
        
        return r;
    }
    public void onRequestReception(long timestamp, Message m, Node nodeInstance){};
    public void onRequestEnded(long timestamp, Request r, Node nodeInstance){};

    @Override
    public void onRequestReception(long timestamp, Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onRequestEnded(long timestamp, Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void executeNextRequest(long timestamp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNodeInstance(ServerNode n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNbCores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}