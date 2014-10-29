package simizer.laws;

/**
 *
 * @author isep
 */
public class UniLaw extends Law {

  private double param;

  public UniLaw(int p) {
    super(p);

    this.param = (double) p;
  }

  @Override
  public void setParam(double par) {

  }

  @Override
  public int nextParam() {
    return (int) param;
  }
}
