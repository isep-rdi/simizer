package fr.isep.simizer.request.printers;

import fr.isep.simizer.nodes.ClientNode;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestPrinter;
import java.io.PrintStream;

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
