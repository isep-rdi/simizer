/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.event;

/**
 *
 * @author isep
 */
public interface IEventProducer {
    public Channel getOutputChannel();
    public void setChannel(final Channel c);
    public void registerEvent(Event evt);
}
