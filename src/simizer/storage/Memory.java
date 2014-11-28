package simizer.storage;

import java.util.LinkedList;

/**
 * This class implements a memory controller. places the resources in memory,
 * accounts for free, available and used memory.
 *
 * @author slefebvr
 */
public class Memory extends StorageElement {

  private static double MB_DELAY = 1.0;
  private long cacheSz = 0;
  private LinkedList<Resource> cachedResource = new LinkedList<>();

  public Memory(long capacity, long accessDelay) {
    super(capacity, accessDelay);

    setPerMBReadDelay(MB_DELAY);
  }

  public long getFreeMemory() {
    return this.capacity - used;
  }

  public long getAvailableMemory() {
    return getFreeMemory() + cacheSz;
  }

  /**
   * This method puts the specified resource in memory. throws an exception if
   * there is not enough space.
   *
   * @param r
   * @throws NotEnoughSpaceException
   */
  public void loadResource(Resource r) throws NotEnoughSpaceException {
    if (r.size() > getAvailableMemory()) {
      throw new NotEnoughSpaceException(this);
    }

    if (cachedResource.contains(r)) {
      cacheSz -= r.size();
      this.write(r);
      return;
    }

    if (r.size() < getFreeMemory()) {
      this.write(r);
      return;
    }

    while (r.size() > getFreeMemory()) {
      Resource tmp = cachedResource.remove();
      cacheSz -= tmp.size();
    }
  }

  public void unref(Integer resourceId) {
    Resource r = read(resourceId);
    cachedResource.addLast(r);
    cacheSz += r.size();
    this.delete(r);
  }

  void checkAndRelease(Integer resId) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
