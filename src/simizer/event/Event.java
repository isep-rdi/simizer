/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.event;

/**
 *
 * @author isep
 */
public abstract class Event<D,T> implements Comparable<Event> {
    protected final D data;
    protected final long timestamp;
    protected final T target;
    
    public Event(long timestamp, D data, T target) {
        this.timestamp = timestamp;
        this.data = data;
        this.target = target;
    }
    
    public D getData() {
        return data;
    }
    public T getTarget() {
        return target;
    }
    public abstract void dispatch();

    @Override
    public int compareTo(final Event e) {
        
        if(data == e.data && timestamp == e.timestamp && target == e.target) {
            return 0;
        }
        
        return Long.compare(timestamp, e.timestamp);
    }

    long getTimeStamp() {
        return this.timestamp;
    }
    
}
