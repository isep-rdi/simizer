/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.tasks;

import simizer.VM;
import simizer.processor.events.IOTaskEvent;
import simizer.storage.IOType;
import simizer.storage.Resource;

/**
 *  Task for reading or writing data to a disk.
 * @author isep
 */
public class DiskTask extends IOTask {
    private final IOType type;
    private final Resource r;
    
    public DiskTask(int sz, Resource res, IOType type) {
        super(sz);
        
       this.r = res;
       this.type = type;
      
    }
    
    public IOType getType() {
        return type;
    }
    public Resource getResource() {
        return r;
    }
    /**
     * Registers an IOTaskEvent through the given virtual machine event
     * producer.
     * 
     * @param vm
     * @param timestamp 
     */
    @Override
    public void startTask(VM vm, long timestamp) {
       long timing = vm.getTaskLength(this);
      
        vm.registerEvent(new IOTaskEvent(timestamp+timing,this, vm.getProcessingUnit()));
    }
}
