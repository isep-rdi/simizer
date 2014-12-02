package fr.isep.simizer.processor.tasks;

import fr.isep.simizer.nodes.Node;
import fr.isep.simizer.nodes.VM;
import fr.isep.simizer.processor.TaskProcessor;
import fr.isep.simizer.requests.Request;

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
  private final VM vm;

  /**
   * Initializes a new {@code Task} to send a request.
   * <p>
   * The specified {@link Request} will be sent from the {@link VM} executing
   * the request to the specified {@link Node} when the {@code Task} is
   * executed.
   *
   * @param request the request to send
   * @param destination the destination of the request
   * @param vm the {@link VM} from which the {@link Request} is sent
   */
  public SendTask(Request request, Node destination, VM vm) {
    super(request.getSize());

    this.request = request;
    this.destination = destination;
    this.vm = vm;
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

  @Override
  public void run(TaskProcessor processor, long timestamp) {
    super.run(processor, timestamp);
    
    if (request.getClientStartTimestamp() == -1) {
      request.setClientStartTimestamp(timestamp);
    } else {
      request.setServerFinishTimestamp(timestamp);
    }

    vm.send(destination, request, timestamp);
    vm.commitSendTask(this);

    finish(timestamp);  // ends the Task and starts the next one
  }
}
