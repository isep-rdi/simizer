package simizer.event;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * http://www.ibm.com/developerworks/library/j-jtp0730/index.html
 * @author Sylvain Generic event dispatcher, waiting on the Channel
 */
public class EventDispatcher implements Runnable {

  private static int dispatcherCounter = 0;
  private static long WAIT_TIME = 100;
  private final Channel chan;
  private final int number;
  private boolean ended = false;
  private int evtCounter = 0;
  private long clock = 0;

  public EventDispatcher(final Channel chan) {
    this.number = dispatcherCounter++;
    this.chan = chan;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();

    while (!ended) {

      Event e = null;

      try {
        e = chan.poll(WAIT_TIME, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        Logger.getLogger(EventDispatcher.class.getName()).log(Level.SEVERE, null, ex);
      }

      if (e != null) { // in case we wakeup and chan is empty nonetheless
        // System.out.println(e.timestamp);
        if (e.timestamp < clock) {
          System.out.println("Causality error clock: " + clock + "ts : " + e.timestamp + " Dispatcher number: " + this.number);
        }
        clock = e.timestamp;
        e.dispatch();
        evtCounter++;
      }

      if (chan.size() == 0) {
        stop();
      }
    }

    System.out.println("Nothing to do here anymore..." + evtCounter + " events in " + (System.currentTimeMillis() - start) + "ms");
  }

  public synchronized void stop() {
    this.ended = true;
  }

  public Channel getChannel() {
    return this.chan;
  }

  public int getEvtCounter() {
    return evtCounter;
  }

  public long getClock() {
    return clock;
  }

}
