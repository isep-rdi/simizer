package simizer.laws;

/**
 *
 * @author isep
 */
public class ConstantLaw extends Law {

  private double param;

  public ConstantLaw(int p) {
    super(p);

    this.param = (double) p;
  }

  @Override
  public int nextValue() {
    return (int) param;
  }
}
