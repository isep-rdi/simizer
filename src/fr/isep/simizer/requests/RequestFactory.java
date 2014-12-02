package fr.isep.simizer.requests;

import fr.isep.simizer.app.Application;
import fr.isep.simizer.storage.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds {@code Request} objects from pre-defined templates.
 * <p>
 * Typically, the templates will be loaded from a CSV file.  See {@link
 * #loadTemplates(java.lang.String)} for an explanation of the format of this
 * file.
 * <p>
 * Once loaded, {@link Request} objects can be created from templates by calling
 * the {@link #getRequest(long, java.lang.Integer)} method.
 */
public class RequestFactory {

  /** The templates used by this {@code RequestFactory}. */
  private final Map<Integer, Request> templates;

  /**
   * Initializes a new instance of the class.
   * <p>
   * To load templates from a file, use {@link
   * #loadTemplates(java.lang.String)}.
   */
  public RequestFactory() {
    this.templates = new HashMap<>();
  }

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
   * @throws IOException if a problem occurs opening or reading the file
   */
  public void loadTemplates(String path) throws IOException {
    BufferedReader reader = null;
    
    try {
      reader = new BufferedReader(new FileReader(new File(path)));

      String line;
      while ((line = reader.readLine()) != null) {
        String[] desc = line.split(";");
        Request request = new Request(
            Integer.parseInt(desc[1]),  // application ID
            desc[2],  // action
            desc[4],  // parameters
            true);  // this is a template
        addTemplate(Integer.parseInt(desc[0]), request);
      }

    } catch (FileNotFoundException ex) {
      Logger.getLogger(RequestFactory.class.getName()).log(
          Level.SEVERE, null, ex);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        Logger.getLogger(RequestFactory.class.getName()).log(
            Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Adds a template for the specified {@code Request} to this factory.
   *
   * @param templateId the ID for the template that is added
   * @param request the {@link Request} to use as the template
   */
  public void addTemplate(Integer templateId, Request request) {
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
