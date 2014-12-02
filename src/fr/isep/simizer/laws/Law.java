package fr.isep.simizer.laws;

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

  private Integer upperBound = null;

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

    // find the constructor for the specified number of arguments
    for (Constructor cs : lawClass.getConstructors()) {
      int argumentCount = args.length - 1;  // skip the class, but use others
      Class[] parameters = cs.getParameterTypes();
      if (parameters.length == argumentCount) {
        Object[] values = new Object[parameters.length];

        int index = 0;
        for (Class cls : parameters) {
          if (cls.equals(Integer.class)) {
            values[index] = Integer.parseInt(args[index + 1]);
          } else if (cls.equals(Double.class)) {
            values[index] = Double.parseDouble(args[index + 1]);
          } else {
            System.exit(1);
          }
          index++;
        }

        lawInstance = (Law) cs.newInstance(values);
      }
    }

    // make sure that we found a valid constructor before setting the parameter
    if (lawInstance == null) {
      throw new NullPointerException("Unable to instantiate Law.");
    }

    return lawInstance;
  }

  /**
   * Initializes an instance of the {@code Law} class.
   */
  public Law() {}

  /**
   * Sets the upper bound for the {@code Law}.
   * <p>
   * Once set, this {@code Law} will only return values in the range {@code [0,
   * upperBound)}.  This can be useful if a {@code Law} is being used to select
   * values from some sort of a collection with a strict upper bound.
   * <p>
   * To remove an upper bound that has previously been set, use {@link
   * #removeUpperBound()}.
   *
   * @param upperBound the upper bound to use
   */
  public void setUpperBound(int upperBound) {
    this.upperBound = upperBound;
  }

  /**
   * Removes the upper bound restriction for this {@code Law} instance.
   */
  public void removeUpperBound() {
    this.upperBound = null;
  }

  /**
   * Generates the next random value for this {@code Law}.
   * <p>
   * If there is an upper bound specified, then this method will return a value
   * in the range [0, upperBound).  Otherwise, this will return any valid
   * non-negative value from the Law's distribution.
   * <p>
   * If a generated value is outside of this range, new values will be generated
   * until one is within the acceptable range.
   *
   * @return a value from the distribution
   */
  public final int nextValue() {
    int result;
    do {
      result = generateNextValue();
    } while (result < 0
              || (this.upperBound != null && result >= this.upperBound));
    return result;
  }

  /**
   * Generates the next value from the distribution.
   * <p>
   * This should not enforce any restriction on the upper bound.  That logic is
   * handled internally within the Law class.
   *
   * @return a value from the distribution
   */
  protected abstract int generateNextValue();
}
