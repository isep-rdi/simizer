package fr.isep.simizer.request.printers;

import fr.isep.simizer.nodes.ClientNode;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestPrinter;
import java.io.PrintStream;

/**
 * Provides a slightly nicer-looking view of the output.
 * 
 * @author Max Radermacher
 */
public class PrettyRequestPrinter extends RequestPrinter {
  private static final String ENTRY_FORMAT
      = "%7d %6d %6d  %9d %9d %9d  %3d %s;%s%n";

  private boolean didPrintHeader = false;

  /**
   * Initializes the pretty printer.
   *
   * @param output where the {@link RequestPrinter} should send its output
   */
  public PrettyRequestPrinter(PrintStream output) {
    super(output);
  }

  /**
   * Prints the header row for the output.
   * <p>
   * This method is automatically called by the "print" method just before the
   * first line of output is produced.
   */
  protected void printHeader() {
    output.format(ENTRY_FORMAT.replace('d', 's'),
        "Request",
        "Client",
        "Errors",

        "Start",
        "Duration",
        "N Delay",

        "App",
        "Action",
        "Params");
  }

  /**
   * Prints the response details in a slightly nicer form.
   * <p>
   * This printer groups the output into three categories: information, timing,
   * and request.  The information category contains general information.  The
   * timing category contains various timing metrics.  And the request category
   * contains the specific details about the request.
   *
   * @param client the {@link ClientNode} that received the response
   * @param request the response that should be printed
   */
  @Override
  public void print(ClientNode client, Request request) {
    if (!didPrintHeader) {
      printHeader();
      didPrintHeader = true;
    }

    output.format(ENTRY_FORMAT,
        request.getId(),
        client.getId(),
        request.getErrorCount(),

        request.getClientStartTimestamp(),
        request.getClientEndTimestamp() - request.getClientStartTimestamp(),
        request.getNetworkDelay(),

        request.getApplicationId(),
        request.getAction(),
        request.getQuery());
  }
}
