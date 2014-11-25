/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.utils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import simizer.storage.ResourceFactory;

/**
 *
 * @author isep
 */
public class SimizerUtils {
    static final JSONParser parser=new JSONParser();
    static  SimizerUtils sim;
    
    public static  String[] readRequestsFile(String fileName) {
        String tmp = readFile(fileName);
        return tmp.split("\\n");
        
    }
    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        String content=null;
        CharBuffer cbuf = CharBuffer.allocate(128);
        
        try {
            FileReader nfReader = new FileReader(filePath);
            int res = 0;
            do {
                res = nfReader.read(cbuf);
                if(res <= 0)
                    break;
                
                cbuf.rewind();
                //System.out.println("Read: " + res + " remaining:" + cbuf.remaining());
                sb.append(cbuf.subSequence(0,res));
                
                
              }while(res > -1);
            // Cleanup
            
            content = sb.toString();
            
            nfReader.close();
            
        } catch (IOException ex) {
            Logger.getLogger(SimizerUtils.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return content;
    }
    
    
    
     public static ResourceFactory getRessourceFactory(String resFile) {
         int nbInit=0, nbMax=0, size=0;
        try {
            JSONArray array=(JSONArray) parser.parse(readFile(resFile));
              for(Object o: array) {
                JSONObject n = (JSONObject) o;
                nbInit = ((Number) n.get("nbInit")).intValue();
                nbMax = ((Number) n.get("nbMax")).intValue();
                size = ((Number) n.get("size")).intValue();
              }
        } catch (ParseException ex) {
            Logger.getLogger(SimizerUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
         return new ResourceFactory(nbInit,nbMax,size);
     }
     
     private static int DEFAULT_RES_NB = 1024;
     private static int DEFAULT_RES_SZ = 8096;
     
    public static ResourceFactory getDefaultResourceFactory() {
        return new ResourceFactory(DEFAULT_RES_NB,DEFAULT_RES_NB, DEFAULT_RES_SZ);
    }
     
  
}
