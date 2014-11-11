package simizer.processor.tasks;

import simizer.Node;
import simizer.VM;
import simizer.requests.Request;

/**
 * A task to send a request through a {@code VM}.
 * <p>
 * When this {@code Task} is executed, it schedules its {@link Request} to be
 * sent by the {@link VM} running it.
 * <p>
 * After scheduling / sending the message (both happen in the same instant), the
 * next {@code Task} is started.  This {@code Task} does not wait for any sort
 * of a network response before moving on to the next {@code Task}.
 *
 * @author Sylvain Lefebvre
 */
public class SendTask extends IOTask {

  private final Request request;
  private final Node destination;

  /**
   * Initializes a new {@code Task} to send a request.
   * <p>
   * The specified {@link Request} will be sent from the {@link VM} executing
   * the request to the specified {@link Node} when the {@code Task} is
   * executed.
   *
   * @param request the request to send
   * @param destination the destination of the request
   */
  public SendTask(Request request, Node destination) {
    super(request.getSize());

    this.request = request;
    this.destination = destination;
  }

  /**
   * Returns the request that will be sent.
   *
   * @return the request that will be sent
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Returns the destination where the request will be sent.
   *
   * @return the destination of the request
   */

  public Node getDestination() {
    return destination;
  }

  /**
   * Returns the destination where the request will be sent.
   *
   * @deprecated Use {@link #getDestination()} instead.
   *
   * @return the destination of the request
   */
  @Deprecated
  public Node getTgt() {
    return destination;
  }

  /**
   * {@inheritDoc}
   * <p>
   * In this class, the behavior is to send a {@link Request} over the network.
   * This can be useful, for example, in order to schedule a network operation
   * to occur after some processing or disk operation has been completed.
   * 
   * @param vm the virtual machine executing the request
   * @param timestamp the time when the {@code Task} is started
   */
  @Override
  public void startTask(VM vm, long timestamp) {
    if (request.getArTime() == -1) {
      request.setArtime(timestamp);
    } else {
      request.setFinishTime(timestamp);
    }

    TaskSession ts = super.getTaskSession();
    if (ts.isComplete()) {
      vm.endTaskSession(ts, timestamp);
    }
    vm.send(destination, request, timestamp);
  }
}
