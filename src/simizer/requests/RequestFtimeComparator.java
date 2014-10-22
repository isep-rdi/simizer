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
public class RequestFtimeComparator implements Comparator<Request> {

    @Override
    public int compare(Request t, Request t1) {
        if(t.equals(t1)) {
            return 0;
        }
        return Long.compare(t.ftime, t1.ftime);
       
    }
}
