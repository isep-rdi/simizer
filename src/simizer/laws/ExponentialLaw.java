/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.laws;

import java.util.Arrays;
import simizer.utils.StdRandom;

/**
 *
 * @author isep
 */
public class ExponentialLaw extends Law {
    protected double lambda = 1.0;
    protected double dNbParam,sum;
  
    public ExponentialLaw(int nbParams) {
        super(nbParams);
        this.dNbParam = (double) nbParams;
        
        //StdRandom.setSeed(System.currentTimeMillis());
    }
    public ExponentialLaw(int nbParams, double alpha) {
        this(nbParams);
        setParam(alpha);
    }
    
    @Override
    public int nextParam() {
       
      double tmpVal;
      do {
            tmpVal=StdRandom.uniform();
      } while(tmpVal >= sum);
        double cumul=0.0;
        int rank = -1;
        do {
            
            cumul += expDist(++rank,lambda);
        } while(cumul < tmpVal && rank < nbParams);
    
        return rank;
     
    }
    /**
     * Rank based generation
     * @return exponentially distributed integer between [0,nbParam] 
     */
    public int nextParam2() {
        double tmpVal  =StdRandom.uniform();
        double cumul=0.0;
        int rank = -1;
        do {
            
            cumul += expDist(++rank,lambda)/sum;
        } while(cumul < tmpVal && rank < nbParams);
    
        return rank;
    }
    @Override
    public void setParam(double par) {
        lambda = 1/par;
        sum = getSum(nbParams,lambda);
    }
    
    static public void main(String ... args) {
        double alpha = 15.0;
        int nbPar = 30;
        ExponentialLaw el = new ExponentialLaw(nbPar,alpha);
        for(int i=0;i<10000;i++) {
            System.out.println(el.nextParam()+";"+el.nextParam2());
        }        
    }
    
    static public double expDist(int x, double lambda)  
    {
        return(lambda*Math.exp(-lambda*x));
    }
    static public double[] generateDist(int nbPar, double lambda) {
        double [] result = new double[nbPar];
        double sum = 0.0;
        for(int i=0;i<nbPar;i++) {
           result[i] = expDist(i,lambda);
           sum += result[i];
        }
        for(int i=0; i< nbPar;i++) {
            result[i] /=sum;
        }
        return result;
    }    
    static public double getSum(int nbPar, double lambda) {
        double sum=0.0;
        for(int i=0;i<nbPar;i++) {
            sum +=  expDist(i,lambda);
        }
        return sum;
    }
}
