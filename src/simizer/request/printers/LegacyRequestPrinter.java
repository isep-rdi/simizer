package simizer.request.printers;

import java.io.PrintStream;
import simizer.nodes.ClientNode;
import simizer.requests.Request;
import simizer.requests.RequestPrinter;

/**
 * Supports the original way of printing {@code ClientNode} output.
 * 
 * @author Max Radermacher
 */
public class LegacyRequestPrinter extends RequestPrinter {
  public LegacyRequestPrinter(PrintStream output) {
    super(output);
  }
  
  @Override
  public void print(ClientNode client, Request request) {
    long duration = request.getClientEndTimestamp()
            - request.getClientStartTimestamp();

    output.println(request.toString()
        + ";" + duration
        + ";" + client.getId());
  }
}
