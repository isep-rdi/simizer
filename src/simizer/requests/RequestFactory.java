package simizer.requests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import simizer.app.Application;
import simizer.storage.Resource;

/**
 * Builds {@code Request} objects from pre-defined templates.
 * <p>
 * Typically, the templates will be loaded from a CSV file.  See {@link
 * #loadRequests(java.lang.String)} for an explanation of the format of this
 * file.
 * <p>
 * Once loaded, {@link Request} objects can be created from templates by calling
 * the {@link #getRequest(long, java.lang.Integer)} method.
 */
public class RequestFactory {

  /**
   * Parses the specified file, loading the templates defined in the file.
   * <p>
   * The file should contain one template definition per line, where each
   * template definition follows the following format:
   * <p>
   * <em>Template ID;App ID;Type;Processing Time;Parameters;Instructions</em>
   * <p>
   * The meaning of each of these fields is as follows:
   * <blockquote><dl>
   *   <dt>Template ID
   *   <dd>Used to retrieve {@link Request} objects from this {@code
   *       RequestFactory} using the {@link #getRequest(long,
   *       java.lang.Integer)} method.
   *
   *   <dt>App ID
   *   <dd>This is the {@code App ID} of the {@link Application} that should
   *       handle {@code Request}s created from this template.
   *
   *   <dt>Type
   *   <dd>This is an application-specific field that can be used to send
   *       additional information with a {@link Request}.  For example, an
   *       application may use it to differentiate "read" requests from "write"
   *       requests so that it can change its behavior accordingly.
   *
   *   <dt>Processing Time
   *   <dd>This value is not currently used.
   *
   *   <dt>Params
   *   <dd>Used to specify the {@link Resource} objects involved in this {@link
   *       Request}.
   *
   *   <dt>Instructions
   *   <dd>The number of processor instructions needed to complete this {@link
   *       Request}.  This value is specified with units of "millions of
   *       instructions."  However, <b>this value is not currently used</b>.
   * </dl></blockquote>
   *
   * @param path the path of the file to load
   * @return a map containing {@link Request} objects accessible by their {@code
   *             Template ID}
   * @throws IOException if a problem occurs opening or reading the file
   */
  public static Map<Integer, Request> loadRequests(String path)
        throws IOException {

    BufferedReader reader = null;
    Map<Integer, Request> templateMap = new HashMap<>();
    try {
      reader = new BufferedReader(new FileReader(new File(path)));

      String line;
      while ((line = reader.readLine()) != null) {
        String[] desc = line.split(";");
        Request request = new Request(
            Integer.parseInt(desc[1]), // type/app id
            -1L, // arrival time
            desc[4], // parameters
            Long.parseLong(desc[3]), // processing time
            desc[2], // type
            Long.parseLong(desc[5]) * 1000000); // number of instructions
        request.setAppId(Integer.parseInt(desc[1]));
        templateMap.put(Integer.parseInt(desc[0]), request);
      }

    } catch (FileNotFoundException ex) {
      Logger.getLogger(RequestFactory.class.getName()).log(
          Level.SEVERE, null, ex);
      templateMap = null;
    } finally {
      try {
        reader.close();
      } catch (IOException ex) {
        Logger.getLogger(RequestFactory.class.getName()).log(
            Level.SEVERE, null, ex);
      }
    }
    return templateMap;
  }

  /** The templates used by this {@code RequestFactory}. */
  private final Map<Integer, Request> templates;

  /**
   * Initializes a new instance of the class with the specified templates.
   *
   * @param templates a {@link Map} containing request templates, where the keys
   *            are the ID of the template
   */
  public RequestFactory(Map<Integer, Request> templates) {
    this.templates = templates;
  }

  public void addRequest(Integer templateId, Request request) {
    templates.put(templateId, request);
  }

  /**
   * Builds a {@code Request} for the specified template.
   * <p>
   * Builds and returns a {@link Request} object based on the specified {@code
   * Template ID}.  The {@code Request ID} and {@code arrivalTime} are already
   * set when the object is returned.
   *
   * @param arrivalTime the timestamp when the {@link Request} is sent from the
   *            client machine
   * @param templateId the ID of the template to use
   * @return a {@link Request} created with the specified template
   */
  public Request getRequest(long arrivalTime, Integer templateId) {
    Request request = new Request(templates.get(templateId));
    request.setClientStartTimestamp(arrivalTime);
    return request;
  }

}
