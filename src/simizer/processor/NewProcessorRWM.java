//Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import simizer.Node;
import simizer.ServerNode;
import simizer.processor.events.DataReadyEvent;
import simizer.processor.events.QuantumEndedEvent;
import simizer.processor.events.RequestEndedEvent;
import simizer.network.Message;
import simizer.requests.Request;
import simizer.requests.RequestProcessor;
import simizer.storage.Resource;
import simizer.storage.StorageElement;

/**
 * BROKEN VERSION DO NOT USE AS IT IS 
 * @see StorageElement
 * @author sathiya
 */
public class NewProcessorRWM implements Processor, RequestProcessor{
    static private long DEF_QUANTUM = 210L;
    static private long MILLION = 1000 * 1000;
    
    private ServerNode nodeInstance = null;
    private List<Node> availableNodes  = null;
    private long qtInst = 0;
    private Queue<Request> readyQueue = new LinkedList<Request>(); 
           // waitingQueue = new LinkedList<Request>();
    private int nbProc, nbProcAv;
    private final double coreMips;
    
    private static long calculate_qtInst(double coreMips)
    {
        long qtInst = (long) Math.floor(DEF_QUANTUM  * ((coreMips * MILLION) /1000.0D));
        return qtInst;
    }
    
    public NewProcessorRWM(int nbProc, double coreMips) {
       this(nbProc, coreMips,null);        
    }
    
    public NewProcessorRWM(int nbProc, double coreMips, ServerNode n) {
         this.nbProc = nbProc;
         this.nbProcAv= nbProc;
         this.coreMips = coreMips;
         this.qtInst = calculate_qtInst(coreMips);
         this.nodeInstance = n;
    }
    
    public void setNodeInstance(ServerNode n) {
        this.nodeInstance = n;
    }
    
    public void onRequestReception(long timestamp, Message m, ServerNode nodeInstance) {
       
        this.nodeInstance = nodeInstance;
        this.availableNodes = nodeInstance.getNetwork().getNodeList();
       Request r  = m.getRequest();
       r.setNode(nodeInstance.getId());
       
       long accessTime =0;
          switch(r.getRequestType()) {
                   
		case "read":
                        accessTime = nodeInstance.getRequestAccessTime(r);
			break;
		case "write":
			accessTime = 100;
			break;
		case "modify":
			accessTime = 100;
			break;
			
	   }
                      
      // long accessTime = nodeInstance.getRequestAccessTime(r);
       if(accessTime > 0) {
           nodeInstance.registerEvent(new DataReadyEvent(timestamp+accessTime, r,this));
           
       } else if (nbProcAv > 0) {
            executeRequest(r, timestamp);
            nbProcAv--;
       } else {
            readyQueue.add(r);
       }
       nodeInstance.setRequestCount(nodeInstance.getRequestCount()+1);
     
       
       
}
    
    public void onRequestEnded(long timestamp,Request r, ServerNode nodeInstance) {
    //   System.out.println("Request ended" + r.getId());
       r.setFinishTime(timestamp);
       for(Integer rId: r.getResources()) {
            if(nodeInstance.getCache().contains(rId)) {
                nodeInstance.getCache().updateCache(rId);
            } else {
                // if ressource is not in memory
                // check if memory available
                // implement FIFO queue for memory eviction (subclass StorageElement);
                Resource res = nodeInstance.getStorageElement().read(rId);
                //size check
                if(res != null)
                    nodeInstance.getCache().write(res);
                else
                    r.setError(r.getError()+1);
            }
        }
        
       nodeInstance.getNetwork().send(nodeInstance, nodeInstance.getFrontendNode(), r, timestamp);
       nodeInstance.setRequestCount(nodeInstance.getRequestCount()-1);
       nbProcAv++;
       
       // if some requests are waiting wer schedule it to be executed.
       if(!readyQueue.isEmpty()) {
           Request rNew = readyQueue.poll();
           
           executeRequest(rNew,timestamp);
           
          // rNew.setDelay(rNew.getDelay() + (timestamp-rNew.getArTime()));
           //rNew = executeRequest(rNew);
           //registerEvent(new RequestEndedEvent(rNew.getFtime(), rNew, this) );
           
           nbProcAv--;
       }
    }
    
    
    public void onDataReady(long timestamp, Request r) {
         if (nbProcAv > 0) {
            executeRequest(r, timestamp);
            nbProcAv--;
       } else {
            readyQueue.add(r);
       }
    }

    
    public void onQuantumEnded(long timestamp, Request r) {
        readyQueue.add(r);
        executeRequest(readyQueue.poll(), timestamp);
    }
    private long calculate_qtInst()
    {
        long qtInst = (long) Math.floor(DEF_QUANTUM  * ((coreMips * MILLION) /1000.0D));
        return qtInst;
    }
    public void  executeRequest(Request r, long timestamp)  {
        
               switch(r.getRequestType()) {
                   
		case "read":
                        read(r,timestamp);
			break;
		case "write":
			write(r,timestamp);
			break;
		case "modify":
			modify(r,timestamp);
			break;
			
	   }
   }
    
    public void read(Request r, long timestamp){
        
        // if data takes time to load, we put the request
       // in waiting queue. If data is ready and a core is available, 
       // the request is executed right away
        // otherwise it is queued.
       
//       long accessTime = nodeInstance.getRequestAccessTime(r);
//       if(accessTime > 0) {
//           nodeInstance.registerEvent(new DataReadyEvent(timestamp+accessTime, r,this));
//       }   
        if(qtInst == 0){
            qtInst=calculate_qtInst();
        }
        
        if(nodeInstance.getStorageElement().isUnlocked(r.getResources())) {
         
            if(qtInst >= r.getNbInst()) {
                    double proportion = ((double) r.getNbInst()) / qtInst;
                    r.setNbInst(0L);
                    long next = (long) (timestamp + Math.ceil(proportion * DEF_QUANTUM));
                    nodeInstance.registerEvent(new RequestEndedEvent(next, r, nodeInstance));
            } else {
                r.setNbInst(r.getNbInst() - qtInst);
                nodeInstance.registerEvent(new QuantumEndedEvent(timestamp + DEF_QUANTUM, r, this));  
            }
        } else {
            readyQueue.add(r);
        }
        
    }
    
    public void write(Request r, long timestamp){
        
        if(qtInst == 0){
            qtInst=calculate_qtInst();
        }
        
        if(qtInst >= r.getNbInst()) {
            double proportion = ((double) r.getNbInst()) / qtInst;
            r.setNbInst(0L);
            long next = (long) (timestamp + Math.ceil(proportion * DEF_QUANTUM));
            
//---------------------      
       List<Node> nodes = availableNodes; 
       long aliveTime = 0;
       long writeTime = 50;
    
    // And +50 is assuming writing time of each resources will be 50 milli seconds
       
       StorageElement se = nodeInstance.getStorageElement();
             
       se.Nwrite(r.getResources(),aliveTime+=next);
       //se.Nwrite(r.getResources());
       
// -------------------------

        nodeInstance.registerEvent(new RequestEndedEvent(next, r, nodeInstance));
        } else {
            r.setNbInst(r.getNbInst() - qtInst);
            nodeInstance.registerEvent(new QuantumEndedEvent(timestamp + DEF_QUANTUM, r, this));  
        }
    }
    
    public void modify(Request r,long timestamp){ }

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
    public Request executeRequest(Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Request read(Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Request write(Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Request modify(Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNbCores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}