package fr.isep.simizer.requests;

import fr.isep.simizer.nodes.ClientNode;
import java.io.PrintStream;

/**
 * Allows the output of the simulation to be easily customized.
 * 
 * @author Max Radermacher
 */
public abstract class RequestPrinter {
  /** The {@code PrintStream} where the requests should be printed. */
  protected final PrintStream output;

  /**
   * Initializes a new instance.
   *
   * @param output the {@link PrintStream} where the requests should be printed
   */
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
