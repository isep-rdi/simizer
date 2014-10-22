/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.laws;

/**
 *
 * @author isep
 */
public class UniLaw extends Law {
    double param;
    @Override
    public void setParam(double par) {
        
    }

    @Override
    public int nextParam() {
        return (int) param;
    }
    public UniLaw(int p) {
        super(p);
        this.param = (double) p;
    }
}
