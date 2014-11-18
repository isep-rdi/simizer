package simizer.requests;

/**
 * A response is Request delagating to its parent request (the one it is
 * responding to)
 *
 * @author Sylvain Lefebvre
 */
public class Response extends Request {

  /**
   * Default constructor, creates a request with the same AppId as the parent
   * request
   *
   * @param type
   * @param parent
   */
  public Response(String type, Request parent) {
    this(type, parent, parent.typeId);
  }

  public Response(String type, Request parent, int appId) {
    super(type);
    this.type = type;
    this.typeId = appId;
  }

}
