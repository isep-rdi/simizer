/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.requests;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author isep
 */
public class RequestFactory {
    
   
    
    public static Map<Integer,Request> loadRequests(String path) throws IOException {
        BufferedReader reader = null;
        Map<Integer,Request> tplMap = new HashMap<Integer,Request>();
        try {
            File requestFile = new File(path);
            reader = new BufferedReader(new FileReader(requestFile));
            String line = null;
            
            while((line = reader.readLine()) != null) {
            // Do something with the line
                String[] desc = line.split(";");
                Request r = new Request(-1, //id 
                        Integer.parseInt(desc[1]), //type/app id 
                        -1L, //node
                        desc[4], //cost
                        Long.parseLong(desc[3]), 
                        desc[2],
                        Long.parseLong(desc[5])*1000000);
                r.setAppId(Integer.parseInt(desc[1]));
                
                tplMap.put(Integer.parseInt(desc[0]),r );
                //System.out.println(Integer.parseInt(line.split(";")[0]));
            
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            tplMap=null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(RequestFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return tplMap;
     }
    private final Map<Integer, Request> templates;
    private  int counter;
    
    public RequestFactory(Map<Integer, Request> templates) {
        this.counter  =  0;
        this.templates = templates;
    }
    
    public int getRequestNumber() {
        return templates.size();
    }
    
    public Request getRequest(long artime, int tplId) {
         Request r = new Request(templates.get(tplId));
        r.setArtime(artime);
        r.setId(counter++);
        
//        Request r = new Request(counter++, 
//                tplId, 
//                artime, 
//                desc[4], 
//                Long.parseLong(desc[3]), 
//                desc[2],Long.parseLong(desc[5])*1000000);
      
        return r;
    }
}
