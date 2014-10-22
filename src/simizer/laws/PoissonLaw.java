/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.laws;

import java.util.Random;

/**
 *
 * @author isep
 */
public class PoissonLaw extends Law {
    
    Random ran = new Random();
    double alpha =1.0;
    
    public PoissonLaw(int nbParams, double alpha) {
        super(nbParams);
        this.alpha = alpha;
        
    }
    
      public PoissonLaw(int nbParams) {
        super(nbParams);
    }

    @Override
    public int nextParam() {
       double l = Math.exp(-alpha), p = 1.0;
       
        int k = 0;
       do {
           k=0;
        do {
            k++;
            p = ran.nextDouble() * p;
            
            
        } while(p > l);
       } while(k >= nbParams);
        return k-1;
    }

    @Override
    public void setParam(double par) {
        this.alpha = par;
    }
   
    
}
