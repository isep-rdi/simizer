/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simizer.app.Application;
import simizer.event.*;
import simizer.network.Message;
import simizer.network.Network;
import simizer.processor.ProcessingUnit;
import simizer.processor.tasks.*;
import simizer.requests.Request;
import simizer.storage.Cache;
import simizer.storage.IOType;
import simizer.storage.Resource;
import simizer.storage.StorageElement;


/**
 * This class models a virtual Machine. 
 * It can host several @see Application through the use of the Deploy method.
 * Provides interface for the Applications to read() write() and execute().
 * Handles TaskSession creations
 * 
 * @TODO: add a started state check
 * @see TaskSession
 * 
 * @author slefebvr
 */
public class VM extends ServerNode implements IEventProducer {
    public static double DEFAULT_COST=15.0; // 15 cents
    public static long DEFAULT_MEM_SZ = 1024*1024*1024; // 1 GB
    
    protected double hCost = 0.0;
    protected long memorySize,memoryUsed;
    protected ProcessingUnit proc;
   
    protected Map<Integer, Application> idToApp = new HashMap<>();
    private TaskSession currentTaskSession = null;
    private Cache memory;
    private boolean started=false;
    
    public VM(int id, Network n,ProcessingUnit p, long memSize, StorageElement disk, double hCost) {
        super(id, 0, 0,0,null,null);
        this.nw = n;
        this.proc = p;
        this.memorySize = memSize;
        this.memory = new Cache(memorySize,1);
        this.disk = disk;
        this.hCost = hCost;
        proc.setNodeInstance(this);
    }
    /**
     * Default constructor, with default processor and default cost.
     * @param id
     * @param n 
     */
    public VM(int id, Network n) {
        this(id, n, 
                new ProcessingUnit(1, 1000,null), 
                DEFAULT_MEM_SZ, 
                new StorageElement(1000000,10), 
                DEFAULT_COST);
        
    }
    /**
     * Deploys the given application instance on the VM
     * @param app 
     */
    public void deploy(Application app) {
        
        app.setVM(this);
        idToApp.put(app.getId(), app);
        memoryUsed+= app.getMemorySize();
        if(started)
            app.init();
    }
    /**
     * Starts the vm by calling the init() method of all deployed applications.
     * There is no guarantee on the applications starting order.
     * @see Application
     */
    public void startVM() {
       initTaskSession();
       for(Application app : idToApp.values())
           app.init();
       started=true;
       executeTaskSession();
    }
    /**
     * This method is called when a request is received by the VM.
     * It initiates a new TaskSession, Identifies the target application of the request and
     * executes the application handling, and starts task session executions.
     * @see TaskSession
     * @param orig
     * @param r 
     */
    @Override
    public void onRequestReceived(Node orig, Request r) {
        initTaskSession();
        Application app = idToApp.get(r.getAppId());
        
        if(app==null) {
            r.setError(-1);
            this.sendResponse(r, orig);
            executeTaskSession();
            return;
        }
            
        app.handle(orig, r);
        executeTaskSession();
    }
    /**
     * ServerNode method is to be removed,
     * while waiting for this we switch back to this model for compatibility
     * between VMs and ServerNode
     * @param timestamp
     * @param m 
     */
     @Override
    public void onRequestReception(long timestamp, Message m) {
        this.clock = timestamp;
        onRequestReceived(m.getOrigin(),m.getRequest());
        
     }
    /**
     * Adds READ task to the current task session.
     * 
     * @param resourceId
     * @param sz
     * @return the Resource if success, null if read fails.
     * 
     */
    public Resource read(int resourceId, int sz) {
        
        Resource res = disk.read(resourceId);
        
        if(res == null)
            return null;
        
        currentTaskSession.addTask(
                new DiskTask(sz, res,IOType.READ ));
        return res;
    }
    /**
     * @TODO DIRTY, TO REMOVE
     * @param resourceId
     * @return 
     */
    public Resource read(int resourceId) {
        Resource res= disk.read(resourceId);
        if(res==null) return null;
        currentTaskSession.addTask(
                new DiskTask((int) res.size(), res,IOType.READ));
        return res;
        
    }
    
    /**
     * Adds a WRITE task to the current session.
     * 
     * If the Resource already exists on  the local disk, it is modified.
     * @see Resource
     * @see IOType
     * @param res
     * @param sz
     * @return 0 if created, -1 if failure to write (eg. not enough space).
     */
    public int write(Resource res, int sz) {
       
        if(disk.getUsedSize()+sz > disk.getSize()) 
            return -1;
        
        currentTaskSession.addTask(
                new DiskTask(sz, res,IOType.WRITE));
        return 0;
    }
    
    /**
     * Adds a MODIFY DiskTask to the current task Session.
     * @param resourceId
     * @param sz Size after modification
     * @return Version number of the resource, 0 if created, -1 if failure to write.
     */
    public int modify(int resourceId, int sz) { 
        if(!disk.contains(resourceId))
            return -1;
        Resource res = disk.read(sz);
        if(disk.getUsedSize()+(sz-res.size()) > disk.getSize()) 
            return -1;
        
        currentTaskSession.addTask(
                new DiskTask(sz,res,IOType.MODIFY));
        return res.getVersion()+1;
    }
    
    /**
     * Adds a processing task to the current task session;
     * @param nbInstructions
     * @param memSize
     * @param res
     * @return 0 if success
     */
    public int execute(long nbInstructions, int memSize, List<Resource> res) { 
        currentTaskSession.addTask(new ProcTask(nbInstructions, memSize));
        return 0;
    }
    
    public void send(Node dest, Request req, long timestamp) {
       if(dest==this) {
           //RequestEndedEvent
           this.registerEvent(
                   new RequestReceivedEvent(
                   timestamp,
                   new Message(this, this, req),
                   this
                   ));
           return;
       } 
       
       nw.send(this, dest, req, timestamp);
       
    }
    
    private void initTaskSession() {
         if(currentTaskSession==null) {
             currentTaskSession = new TaskSession(0);
         } 
    }
    /**
     * Called at the end of  request handling
     * Starts task session processing.
     */
   private void executeTaskSession() {
      
        Task currentTask = currentTaskSession.getNextTask();
        if(currentTask !=null)
            currentTask.startTask(this, clock);
        currentTaskSession = null;
   }
   /**
    * Called when task Session has ended
    * @param ts
    * @param timestamp 
    */
    public void endTaskSession(TaskSession ts, long timestamp) {
       
        this.clock = timestamp;
    }
   
    /**
     * Starts a requesting session,
     * Adds a send task to current task session, 
     * this request waits for an answer 
     * before continuing to the next task in the session
     * Request handling should return after calling this method.
     * 
     * @return 
     * @see Application.sendRequest
     * @param dest
     * @param req 
     */
    public boolean sendRequest(Node dest, Request req) {
        //req.setArtime(clock);
        if(nw.getNode(dest.getId())!=null) {
            currentTaskSession.addTask(new SendTask(req,dest));
           // currentTaskSession.complete();
             return true;
        }
        return false;
    }
    
    /**
     * Sends back a response to a request.
     * Completes the current tasks session. 
     * Request handling should return after calling this method.
     * @return 
     * @see Application
     * @param dest
     * @param req 
     */
    public boolean sendResponse(Request req, Node dest) {
        
        if(nw.getNode(dest.getId())==null || req.getArTime()<0)
            return false;
        
        // target node exists and request is valid
        currentTaskSession.addTask(new SendTask(req,dest));
       
        return true;
       
    }
   
   
    /**
     * Computes data access time for the given disk access task
     * 
     * @see IOType
     * @see DiskTask
     * 
     * @param dt
     * @return data access time for the given disk access task
     */
    public long getTaskLength(DiskTask dt) {
        long accessTime = 0;
        int resId = dt.getResource().getId();
        switch (dt.getType()) {
            case READ:
                if (memory.contains(resId)) {
                    
                   return memory.getReadDelay(resId, dt.getSize());
                } else {
                   
                    return disk.getReadDelay(resId, dt.getSize());
                }
            
                case WRITE:
                case  MODIFY:
                    return disk.getWriteDelay(resId, dt.getSize());
                     
        }
        return accessTime;
    }

    public void handleWrite(DiskTask dt) {
        disk.write(dt.getResource());
        
    }
    
    private static EventProducer ep = new EventProducer(); // delegate for IEventProducer
    @Override
    public Channel getOutputChannel() {
        return ep.getOutputChannel();
    }

    @Override
    public void setChannel(Channel c) {
        ep.setChannel(c);
        proc.setChannel(c);
    }
    @Override
    public void registerEvent(Event evt) {
        ep.registerEvent(evt);

    }

    public void commitTask(IOTask t) {
        
        DiskTask dt = (DiskTask )t;
        Resource  res = dt.getResource();
        switch (dt.getType()) {
            case READ:
                memory.writeToCache(res);
                return;
            case WRITE:
                memory.writeToCache(res);
                disk.write(res);
                return;
            case  MODIFY:
                memory.modify(res);
                disk.modify(res);
        }
    }

    public ProcessingUnit getProcessingUnit() {
        return this.proc;
    }

    public long getClock() {
        return this.clock;
    }

   

}
