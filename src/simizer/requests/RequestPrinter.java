package simizer.requests;

import java.io.PrintStream;
import simizer.nodes.ClientNode;

/**
 * Allows the output of the simulation to be easily customized.
 * 
 * @author Max Radermacher
 */
public abstract class RequestPrinter {
  protected final PrintStream output;

  public RequestPrinter(PrintStream output) {
    this.output = output;
  }

  /**
   * Called each time a response is received and needs to be printed.
   *
   * @param client the {@link ClientNode} that received the response
   * @param request the {@link Request} to print
   */
  public abstract void print(ClientNode client, Request request);
}
