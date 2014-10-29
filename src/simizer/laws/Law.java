package simizer.laws;

import java.lang.reflect.Constructor;

/**
 * A class representing a probability distribution.
 */
public abstract class Law {

  protected int nbParams;

  public static Law loadLaw(String lawDef) throws Exception {
    Law lawInstance = null;

    String[] args = lawDef.split(",");

    Class lawClass = Class.forName(args[0]);
    for (Constructor cs : lawClass.getConstructors()) {
      if (cs.getParameterTypes().length == 1) {
        lawInstance = (Law) cs.newInstance(Integer.parseInt(args[1]));
      }
    }

    if (args.length > 2) {
      lawInstance.setParam(Double.parseDouble(args[2]));
    }

    return lawInstance;
  }

  public Law() {
    this(0);
  }

  public Law(int nbParams) {
    this.nbParams = nbParams;
  }

  public void setNbParams(int nbParams) {
    this.nbParams = nbParams;
  }

  public abstract void setParam(double par);

  public abstract int nextParam();

  public int nextParam(long time, double... params) {
    return nextParam();
  }
}
