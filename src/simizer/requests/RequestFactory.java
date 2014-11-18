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

public class RequestFactory {

  public static Map<Integer, Request> loadRequests(String path)
        throws IOException {

    BufferedReader reader = null;
    Map<Integer, Request> tplMap = new HashMap<>();
    try {
      reader = new BufferedReader(new FileReader(new File(path)));

      String line;
      while ((line = reader.readLine()) != null) {
        String[] desc = line.split(";");
        Request r = new Request(-1, //id
            Integer.parseInt(desc[1]), //type/app id
            -1L, //node
            desc[4], //cost
            Long.parseLong(desc[3]),
            desc[2],
            Long.parseLong(desc[5]) * 1000000);
        r.setAppId(Integer.parseInt(desc[1]));
        tplMap.put(Integer.parseInt(desc[0]), r);
      }

    } catch (FileNotFoundException ex) {
      Logger.getLogger(RequestFactory.class.getName()).log(
          Level.SEVERE, null, ex);
      tplMap = null;
    } finally {
      try {
        reader.close();
      } catch (IOException ex) {
        Logger.getLogger(RequestFactory.class.getName()).log(
            Level.SEVERE, null, ex);
      }
    }
    return tplMap;
  }

  private final Map<Integer, Request> templates;
  private int counter;

  public RequestFactory(Map<Integer, Request> templates) {
    this.counter = 0;
    this.templates = templates;
  }

  public int getRequestNumber() {
    return templates.size();
  }

  public Request getRequest(long artime, int tplId) {
    Request r = new Request(templates.get(tplId));
    r.setArtime(artime);
    r.setId(counter++);
    return r;
  }

}
