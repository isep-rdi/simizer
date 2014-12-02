 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.storage;

import fr.isep.simizer.event.EventProducer;
import fr.isep.simizer.processor.ProcessingUnit2;
import fr.isep.simizer.processor.tasks.DiskTask;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * An IO controller lists the IO (read and write) request (tasks).
 * It schedules the data ready events to pass to the Processing Unit, 
 * offloading disk and memory management from the VM and the processor.
 * @author isep
 */
public class IOController extends EventProducer {
    
    private ProcessingUnit2 pu;
    private StorageElement disk;
    private Memory memory;
    private long buffering;
    private long nextEvent;
    
    private final List<Resource> loading = new ArrayList<>();
    private final Queue<DiskTask> waitQueue = new LinkedList<>();
    
    
    public IOController(ProcessingUnit2 pu, StorageElement disk, Memory memory) {
        this.pu = pu;
        this.disk = disk;
        this.memory = memory;
    }
    
    
    public void ioRequest(DiskTask dt, long timestamp) {
        switch(dt.getType()) {
            case READ: scheduleRead(dt, timestamp); break;
            case WRITE: 
            case MODIFY: scheduleWrite(dt, timestamp); break;
        }
    }
    /**
     * Schedules the IOTaskEvent depending on the presence of data in the cache.
     * 0. Checks if data is not currently written, in that case,
     * put back the request in the queue.
     * 1. Checks whether the requested resource is in memory or currently loading.
     * 2a. if in memory increase reference counter, 
     * and schedules the read according to read delay.
     * 
     * 2b. if loading schedules the read for when loaded.
     * 
     * 2c. Not in memory, read from disk.
     * 
     * @param dt
     * @param timestamp 
     */
    private void scheduleRead(DiskTask dt,long timestamp) {
        if(!memory.contains(dt.getResource().getId())) {
            scheduleReadFromDisk(dt,timestamp);
        }
    }
    
    private void scheduleWrite(DiskTask dt, long timestamp) {
        
    }
    /**
     * PUBLIC FOR TESTING
     * Schedules the reading from disk of resource.
     * 1. Check if there is enough room to load the data in memory.
     * 2a. Schedules the TaskEndedEvent according to disk speed, 
     * and puts the resource in the loading list with the specified timestamp.
     * 2b. If no room is available, removes the cached data from memory, 
     * so that enough space is available
     * 2c. if cache size is not enough to hold data, data is not loaded but read from disk directly, 
     * added to read queue, not to loading queue.
     * @param dt
     * @param timestamp 
     */
    public void scheduleReadFromDisk(DiskTask dt, long timestamp) {
       Resource r = dt.getResource();
       if(nextEvent > timestamp)
           nextEvent += disk.getDelay(r.getId());

       // Max: Commented this out because of changes to TaskSession.
//       this.registerEvent(
//               new IOTaskEvent(nextEvent, dt,this.pu));
       
    }
    public void scheduleWriteToDisk(DiskTask dt, long timestamp) {
        
    }

    /**
     * Frees the specified resource from its write lock.
     * Triggers the processing of the io queue.
     * @param rId
     * @param timestamp 
     */
    public void writeDone(Integer rId, long timestamp) {
        
        processQueue(timestamp);
    }
    
    /**
     * Writes the specified resource in memory if needed.
     * Decreases the number of reference to the resource,
     * when the number reaches 0, the memory goes back to available,
     * and the resource is moved to the cached pool.
     * @param rId
     * @param timestamp
     * @throws Exception 
     */
    public void readDone(Integer rId, long timestamp) {
        if(!memory.contains(rId)) {
            memory.write(disk.read(rId));
        }
       processQueue(timestamp);
    }
    
    /**
     * Iterates over the request list.
     * @param timestamp 
     */
    public void processQueue(long timestamp) {
        int nbTasks = waitQueue.size();
        int i=0;
        while(i < nbTasks) {
            ioRequest(waitQueue.poll(),timestamp);
            i++;
        }
    }
    /**
     * Frees the resource form their respective locks
     * @param resList
     */
    public void releaseResource(List<Integer> resList) {
        for(Integer resId: resList) {
            memory.checkAndRelease(resId);
            
        }
    }
    

    
}
