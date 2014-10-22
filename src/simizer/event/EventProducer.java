/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.event;

/**
 *
 * @author isep
 */
public class EventProducer implements IEventProducer{
    protected Channel chan;
    
    
    @Override
    public Channel getOutputChannel() {
        return this.chan;
    }

    @Override
    public void registerEvent(Event evt) {
        chan.registerEvent(evt);
        
    }

    @Override
    public void setChannel(Channel c) {
        this.chan = c;
    }
    public boolean cancelEvent(Event e) {
        return chan.remove(e);
    }
   
}
