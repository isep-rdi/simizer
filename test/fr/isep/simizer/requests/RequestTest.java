package fr.isep.simizer.requests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests the Request class.
 * <p>
 * This also includes tests for the "URL query string" parsing code.
 * 
 * @author Max Radermacher
 */
public class RequestTest {

  private Map<String, String> result = null;

  @Test
  public void testParseSimple() {
    result = Request.parseQuery("key=value");
    Assert.assertEquals("should have 1 key", 1, result.size());
    Assert.assertEquals("should have found 'key'", "value", result.get("key"));
  }

  @Test
  public void testParseTwoParameters() {
    result = Request.parseQuery("key=value&name=John");
    Assert.assertEquals("should have 2 keys", 2, result.size());
    Assert.assertEquals("should have found 'key'", "value", result.get("key"));
    Assert.assertEquals("should have found 'name'", "John", result.get("name"));
  }

  @Test
  public void testParseEncoding() {
    result = Request.parseQuery("m%20n=J%20Doe");
    Assert.assertEquals("should have 1 key", 1, result.size());
    Assert.assertEquals("should have found 'm n'", "J Doe", result.get("m n"));
  }

  @Test
  public void testParseEdge() {
    // %3D is =
    // %26 is &
    result = Request.parseQuery("%3D=%26&%26=%3DHi");
    Assert.assertEquals("should have 2 keys", 2, result.size());
    Assert.assertEquals("should have found '='", "&", result.get("="));
    Assert.assertEquals("should have found '&'", "=Hi", result.get("&"));
  }

  @Test
  public void testParseNull() {
    result = Request.parseQuery("name=");
    Assert.assertEquals("should have 1 key", 1, result.size());
    Assert.assertEquals("should have found 'name'", null, result.get("name"));
  }

  private String encodeResources(int[] resources) {
    String[] strings = new String[resources.length];
    for (int i = 0; i < resources.length; i++) {
      strings[i] = Integer.toString(resources[i]);
    }
    String toEncode = strings[0];
    for (int i = 1; i < strings.length; i++) {
      toEncode += "_" + strings[i];
    }
    try {
      return URLEncoder.encode(toEncode, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      System.err.println("Error during test: " + ex.getMessage());
      return null;
    }
  }

  public void testParseResources() {
    final int[] RESOURCES = new int[] { 5, 2, 3, 6, 1, 4, 7, 9 };
    String encoded = encodeResources(RESOURCES);
    
    Request request = new Request(0, "none", "resources=" + encoded);
    
    List<Integer> resources = request.getResources();
    Assert.assertEquals("incorrect resource count",
            RESOURCES.length, resources.size());
    
    int index = 0;
    for (Integer resource : resources) {
      Assert.assertEquals("incorrect resource " + index,
              RESOURCES[index], (int) resource);
    }
  }
}
