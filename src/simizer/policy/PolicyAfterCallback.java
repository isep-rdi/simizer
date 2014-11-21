package simizer.policy;

import simizer.Node;
import simizer.requests.Request;

public interface PolicyAfterCallback {

  public void receivedRequest(Node node, Request request);

}
