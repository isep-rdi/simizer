/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.requests;

/**
 *
 * @author isep
 */

public class RequestDescription {
    
    static public RequestDescription rdFromCSV(String desc) {
        String [] descTab =desc.split(";");
    
        return new RequestDescription(Integer.parseInt(descTab[0]),
                Integer.parseInt(descTab[1]),
                Long.parseLong(descTab[3]),
                descTab[2],
                Long.parseLong(descTab[5])
                );
    }
    
    private final long nbInst;
    private int id,nbParams;
    private long processingTime;    
    private String rt;
    
    public RequestDescription(int id, int nbParams, long processingTime,String type, long nbInst) {
        this.id = id;
        this.nbParams = nbParams;
        this.processingTime = processingTime;
        this.rt = type; 
        this.nbInst = nbInst;
        
    }
    public int getId() {
        return this.id;
     }
    public int nbParams() {
        return nbParams;
    }
    public long getProcTime() {
        return processingTime;
    }
    public String getType() {
        return rt;
    }
}
