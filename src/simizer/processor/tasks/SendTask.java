package simizer.processor.tasks;

import simizer.Node;
import simizer.VM;
import simizer.requests.Request;

/**
 * This is default request sending task. It does not wait for an answer. There
 * are three ways of sending requests
 *
 *
 * @author Sylvain Lefebvre
 */
public class SendTask extends IOTask {

  private final Request r;
  private final Node dest;

  public SendTask(Request r, Node dest) {
    super(r.getSize());

    this.r = r;
    this.dest = dest;
  }

  public Request getRequest() {
    return this.r;
  }

  public Node getTgt() {
    return dest;
  }

  @Override
  public void startTask(VM vm, long timestamp) {
    if (r.getArTime() == -1) {
      r.setArtime(timestamp);
    } else {
      r.setFinishTime(timestamp);
    }

    TaskSession ts = super.getTaskSession();
    if (ts.isComplete()) {
      vm.endTaskSession(ts, timestamp);
    }
    vm.send(dest, r, timestamp);
  }
}
