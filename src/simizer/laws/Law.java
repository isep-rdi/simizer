package simizer.laws;

import java.lang.reflect.Constructor;

/**
 * A class representing a probability distribution.
 * <p>
 * Probability distributions are used throughout the application to
 * mathematically define the randomness present in the simulation.  Custom
 * {@code Law}s can be defined to provide clients, servers, and the network with
 * non-standard or application-specific behavior.
 * <p>
 * A subclass should override the {@link #nextValue()} method to define its
 * custom behavior.
 */
public abstract class Law {

  protected int upperBound;

  /**
   * Instantiates a {@code Law} subclass from a string representation.
   * <p>
   * The string should be specified as
   * {@code <class>,<number of parameters>,<optional parameter>}.  The final
   * parameter is both optional and varies depending on the particular
   * distribution law.  See the concrete subclasses for its usage in each
   * situation.
   * <p>
   * This method will always return a valid {@code Law} subclass, unless an
   * error occurs, in which case an {@link Exception} will be thrown.
   * 
   * @param definition a string representing the law
   * @return a valid instance of a subclass of the Law class
   * @throws Exception if an error occurs while performing the instantiation
   */
  public static Law loadLaw(String definition) throws Exception {
    Law lawInstance = null;

    String[] args = definition.split(",");

    // find the name of the class to instantiate
    Class lawClass = Class.forName(args[0]);

    // use the constructor that takes a single argument to create an instance
    for (Constructor cs : lawClass.getConstructors()) {
      if (cs.getParameterTypes().length == 1) {
        lawInstance = (Law) cs.newInstance(Integer.parseInt(args[1]));
      }
    }

    // make sure that we found a valid constructor before setting the parameter
    if (lawInstance == null) {
      throw new NullPointerException("Unable to instantiate Law.");
    }

    if (args.length > 2) {
      lawInstance.setParam(Double.parseDouble(args[2]));
    }

    return lawInstance;
  }

  /**
   * Initializes an instance of the {@code Law} class.
   *
   * @param upperBound the number of parameters.  Values produced by the law
   *          will be in the range {@code [0, numberOfParameters)}.
   */
  public Law(int upperBound) {
    this.upperBound = upperBound;
  }

  /**
   * Changes the custom parameter associated with this {@code Law}.
   * <p>
   * This method is here to provide subclasses with a consistent interface for
   * defining an additional parameter.  For example, the {@link GaussianLaw}
   * uses it to allow the standard deviation of the distribution to be
   * customized.
   *
   * @param parameter change the custom parameter
   */
  public abstract void setParam(double parameter);

  /**
   * Generates the next random value for this {@code Law}.
   *
   * @return a value in the range {@code [0, upperBound)}
   */
  public abstract int nextValue();
}
