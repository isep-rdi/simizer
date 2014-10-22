package simizer.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import simizer.Node;
import simizer.VM;
import simizer.requests.Request;
import simizer.storage.Resource;

/**
 * Class Application represents a running process 
 * on given machine.
 * Implement the handle method for application semantics.
 * @see examples
 * 
 * @author Sylvain Lefebvre
 */
public abstract class Application {
    private final int id,memorySize;
    protected VM vm;
    protected Properties config=new Properties();
    private final Map<Long, Node> pending = new HashMap<>();
    
    public Application(int id,int memSize) {
        this.memorySize = memSize;
        this.id = id;
    }
    /**
     * Copy constructor for Application instances
     * copies everything except the VM.
     * @param appModel 
     */
    public Application(Application appModel) {
        this.id = appModel.id;
        this.memorySize = appModel.memorySize;
        this.config = appModel.config;
        this.vm = null;
    }
    
    public int getMemorySize() {
        return memorySize;
    }
   
    public void setVM(VM vm) {
        this.vm = vm;
    }

    public Integer getId() {
        return id;
    }
    public void setConfig(String name, String val) {
        config.setProperty(name, val);
    }
    
     protected int write(Resource res, int sz) {
       
        return vm.write(res, sz);
    }
    
    /**
     * Adds a MODIFY DiskTask to the current task Session.
     * @param resourceId
     * @param sz Size after modification
     * @return Version number of the resource, 0 if created, -1 if failure to write.
     */
    protected int modify(int resourceId, int sz) { 
       return vm.modify(resourceId, sz);
    }
    
    /**
     * Adds a processing task to the current task session;
     * @param nbInstructions
     * @param memSize
     * @param res
     * @return 0 if success
     */
    protected int execute(long nbInstructions, int memSize, List<Resource> res) {
        return vm.execute(nbInstructions, memSize, res);
    }
    protected Resource read(int resourceId) {
        return vm.read(resourceId);
    }
    protected Resource read(int resourceId, int sz) {
        return vm.read(resourceId, sz);
    }
    
    protected boolean sendRequest(Node dest, Request req) {
        pending.put(req.getId(), dest);
        return vm.sendRequest(dest, req);
    }
    
    protected boolean sendResponse(Node dest, Request req) {
        pending.remove(req.getId());
        return vm.sendResponse(req, dest);
    }
    
    protected boolean sendOneWay(Node n, Request request) {
        return sendRequest(n, request);
    }
    
    public abstract void init();
    public abstract void handle(Node orig,Request req);

    
}
