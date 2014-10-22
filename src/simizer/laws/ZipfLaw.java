/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.laws;

/**
 *
 * @author Hyunsik Choi 
 * http://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/
 */
import java.util.Random;

public class ZipfLaw extends Law {
 private Random rnd = new Random(System.currentTimeMillis());
 private double skew = 1.0;
 private double bottom = 0;
 private double[] rankPbb;

 public ZipfLaw(int nbParams, double skew) {
  super(nbParams);
  //this.size = size;
  this.setParam(skew);
  rankPbb = new double[nbParams];
  for(int i=0;i<rankPbb.length;i++) {
      rankPbb[i] = getProbability(i+1);
  }
 }
 
  public ZipfLaw(int nbParams) {
    this(nbParams,1.0);
  }
 

 // the next() method returns an rank id. 
 // The frequency of returned rank ids are followiong Zipf distribution.
    
 public int nextParam2() {
   int rank;
   double friquency = 0;
   double dice;
   
   rank = rnd.nextInt(nbParams);
   friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
   dice = rnd.nextDouble();

   while(!(dice < friquency)) {
     rank = rnd.nextInt(nbParams);
     friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
     dice = rnd.nextDouble();
   }
   
   return rank;
 }
    // iterate ranks until the appropriate one is reached
    // should be faster (less random than nextParam)
 @Override
 public int nextParam() {
     int rank = 1;
     double p = rnd.nextDouble();
     double blow = 0.0, bup = rankPbb[rank-1];
     
     while((p<blow || p>= bup) && rank <=nbParams) {
         rank++;
         blow=bup;
         bup+=rankPbb[rank-1];
     }
     return rank-1;
 }
 // This method returns a probability that the given rank occurs.
 public final double getProbability(int rank) {
   return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
 }

 public static void main(String[] args) {
    
   ZipfLaw zipf = new ZipfLaw(30,0.8);
   double  total=0.0;
//   for(int i = 1; i<=30; i++) {
//       double pbb = zipf.getProbability(i);
//       
//       System.out.println(i+ " has pbb of :" + pbb);
//       total+=pbb;
//   }
   
   long total1=0,total2=0;
   int p1,p2;
   long tmp;
   for(int i=0; i<10000;i++) {
       p1 = zipf.nextParam();
       System.out.println(i+";"+p1);
   }
   
   System.out.println("avg1 = " + (total1*1.0/1000));
   System.out.println("avg2 = " + (total2*1.0/1000));
 }

    @Override
    public final void setParam(double par) {
        this.skew = par;
        this.bottom = 0;
        for(int i=1;i <=nbParams; i++) 
            this.bottom += (1/Math.pow(i, this.skew));
    }

   
}
