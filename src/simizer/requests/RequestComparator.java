/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.requests;

import java.util.Comparator;

/**
 *
 * @author isep
 */
public class RequestComparator implements Comparator<Request> {

    @Override
    public int compare(Request t, Request t1) {
        if(t.equals(t1)) {
            return 0;
        }
        else if(t.artime < t1.artime) {
            return -1;
        }
        else if(t.artime == t1.artime) {
            return 0;
        }
        else {
            return 1;
        }
    }
    
}
