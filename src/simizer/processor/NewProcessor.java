//Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import java.util.LinkedList;
import java.util.Queue;
import simizer.ServerNode;
import simizer.processor.events.DataReadyEvent;
import simizer.processor.events.QuantumEndedEvent;
import simizer.processor.events.RequestEndedEvent;
import simizer.requests.Request;
import simizer.requests.RequestProcessor;

/**
 *
 * @author sathiya
 */
public class NewProcessor implements Processor, RequestProcessor{
    static private long DEF_QUANTUM = 200L;
    static private long MILLION = 1000 * 1000;
    private ServerNode nodeInstance = null;
    private long qtInst = 0;
    private Queue<Request> readyQueue = new LinkedList<Request>();
    private int nbProc, nbProcAv;
    private final double coreMips;
    
    
    private static long calculate_qtInst(double coreMips)
    {
        long qtInst = (long) Math.floor(DEF_QUANTUM  * ((coreMips * MILLION) /1000.0D));
        return qtInst;
    }
    
    public NewProcessor(int nbProc, double coreMips) {
       this(nbProc, coreMips,null);        
    }
    
    public NewProcessor(int nbProc, double coreMips, ServerNode n) {
         this.nbProc = nbProc;
         this.nbProcAv= nbProc;
         this.coreMips = coreMips;
         this.qtInst = calculate_qtInst(coreMips);
         this.nodeInstance = n;
    }
    
    public void setNodeInstance(ServerNode n) {
        this.nodeInstance = n;
    }
    
     /**
      * This method handles a new request. 
      * When data takes time to load, we put the request
      * in waiting queue. If data is ready and a core is available, 
      * the request is executed right away
      * otherwise it is queued.
      * @param timestamp Time at which reception occurs
      * @param r received Request
      */
    public void onRequestReception(long timestamp, Request r) {
         
       long accessTime = nodeInstance.getRequestAccessTime(r);
       //System.out.println("Access Time :" + r.getId() + ";"+ accessTime);
       if(accessTime > 0) {
           //r.setDelay(r.getDelay() + accessTime);
           nodeInstance.registerEvent(new DataReadyEvent(timestamp+accessTime, r,this));
           
       } else if (nbProcAv > 0) {
            executeRequest(r, timestamp);
            nbProcAv--;
       } else {
            readyQueue.add(r);
       }
    }
    
    @Override
    public void onRequestEnded(long timestamp,Request r) {
    //   System.out.println("Request ended" + r.getId());
//       r.setFinishTime(timestamp);
//       for(Integer rId: r.getResources()) {
//            if(nodeInstance.getCache().contains(rId)) {
//                nodeInstance.getCache().updateCache(rId);
//            } else {
//                // if ressource is not in memory
//                // check if memory available
//                // implement FIFO queue for memory eviction (subclass StorageElement);
//                Ressource res = nodeInstance.getStorageElement().read(rId);
//                //size check
//                if(res != null)
//                    nodeInstance.getCache().writeToCache(res);
//                else
//                    r.setError(r.getError()+1);
//            }
//        }
        
//       nodeInstance.getNetwork().send(nodeInstance, nodeInstance.getFrontendNode(), r, timestamp);
//       nodeInstance.setRequestCount(nodeInstance.getRequestCount()-1);
//       nodeInstance.setnbProcAv(nodeInstance.getnbProcAv()+1);
       
       // if some requests are waiting wer schedule it to be executed.
       System.out.println("CALLED");
    }
    
    
    @Override
    public void onDataReady(long timestamp, Request r) {
         if (nbProcAv > 0) {
            executeRequest(r, timestamp);
           //nodeInstance.setnbProcAv(nodeInstance.getnbProcAv()-1);
            nbProcAv--;
       } else {
            readyQueue.add(r);
       }
    }

    
    @Override
    public void onQuantumEnded(long timestamp, Request r) {
        readyQueue.add(r);
        executeRequest(readyQueue.poll(), timestamp);
    }
    
    private void  executeRequest(Request r, long timestamp)  {
        /*
         * two cases here:
         *  - the number of instructions left reaches 0 before the end of the Quanta
         *  means we plan a  @See RequestEndedEvent
        */ 
        if(timestamp > (r.getArTime()+r.getDelay())) {
            r.setDelay(timestamp  - r.getArTime());
        }
        if(qtInst >= r.getNbInst()) {
            double proportion = ((double) r.getNbInst()) / qtInst;
            r.setNbInst(0L);
            long next = (long) (timestamp + Math.ceil(proportion * DEF_QUANTUM));

            nodeInstance.registerEvent(new RequestEndedEvent(next, r, nodeInstance));
        } else {
            r.setNbInst(r.getNbInst() - qtInst);
            nodeInstance.registerEvent(new QuantumEndedEvent(timestamp + DEF_QUANTUM, r, this));  
        }
    }
    
    // Only called by the Node on RequestEndedEvent.
    // executes the next request in the ready queue.
     @Override
    public void executeNextRequest(long timestamp) {
        if(!readyQueue.isEmpty()) {
           Request rNew = readyQueue.poll();
           executeRequest(rNew,timestamp);
           // nodeInstance.setnbProcAv(nodeInstance.getnbProcAv()-1);
          // rNew.setDelay(rNew.getDelay() + (timestamp-rNew.getArTime()));
           //rNew = executeRequest(rNew);
           //registerEvent(new RequestEndedEvent(rNew.getFtime(), rNew, this) );
       } else if(nbProcAv < nbProc) {
           nbProcAv++;
       }
    }
    public Request read(Request r){ return null;}
    public Request write(Request r){ return null;}
    public Request modify(Request r){ return null;}
    public Request executeRequest(Request r){ return null;};

    @Override
    public int getNbCores() {
        return this.nbProc;
    }

   
}
