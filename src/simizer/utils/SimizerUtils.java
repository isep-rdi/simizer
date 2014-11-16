/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.utils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simizer.Node;
import simizer.processor.Processor;
import simizer.storage.ResourceFactory;
import simizer.storage.StorageElement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import simizer.ServerNode;

/**
 *
 * @author isep
 */
public class SimizerUtils {
    static final JSONParser parser=new JSONParser();
    static  SimizerUtils sim;
    
    public static List<ServerNode> decodeNodes(String nodesJson) {
         List<ServerNode> nodes = new ArrayList<>();
         int id= 0;
        
        
         try {
            JSONArray array=(JSONArray)parser.parse(nodesJson);
            
            for(Object o: array) {
                JSONObject n = (JSONObject) o;
                
                Number nbNode = (Number) n.get("nb");
                Number cpu = (Number) n.get("cpuSlots");
                Number mem = (Number) n.get("memorySize");
                //System.out.println(mem.toString());
                Number queueSz = (Number) n.get("queueSize");
                Number cost = (Number) n.get("cost");
                Number diskSize = (Number) n.get("diskSize");
                Number nbMips = (Number) n.get("nbMips");
                String processorNom = (String) n.get("ProcessorName");
                Processor processor=null;
                System.out.println(processorNom);
                
                for(int i= 0; i< nbNode.intValue();i++) {
                    StorageElement disk = new StorageElement(diskSize.intValue(),4);
                    disk.setPerMBReadDelay(2.0);
                    
                    processor = createProcessor((int )cpu.longValue(), nbMips.doubleValue(), processorNom);
                    ServerNode current =new ServerNode(id,
                        mem.longValue()*1000,
                        queueSz.intValue(),
                        cost.doubleValue(), 
                        disk,
                        processor);
                    
                    processor.setNodeInstance(current);
                            
                    nodes.add(current);
                    id++;
                }                
            }
     
  
        } catch (ParseException ex) {
            Logger.getLogger(SimizerUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nodes;
    }
    public static Processor createProcessor(int core, double nbMips, String pName) {
        Processor processor = null;
        try{
                    
                Constructor pConst = Class.forName(pName).getConstructor(int.class, double.class);
                processor =  (Processor) pConst.newInstance(core,nbMips);
                //System.out.println("Creating Class for :" + pName);
                }
                catch(Exception e){
                    System.out.println("Exception in creating processor instance"
                            + " at LBSimUtis class :"+e.getMessage());
                }
        return processor;
    }
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
