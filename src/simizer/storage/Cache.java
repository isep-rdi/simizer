// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi

package simizer.storage;

import java.util.LinkedList;

/**
 * Represents a memory-based cache (speeds up file access times).
 *
 * @author sathiya
 */
public class Cache extends StorageElement {

  private static final double MB_DELAY = 0.019D;

  /**
   * Stores the access order of the resources.
   * <p>
   * This allows the cache to implement a kind of LRU (Least Recently Used)
   * policy for evicting data.  The data will be evicted when the space is
   * needed for a new {@code Resource}.
   */
  private final LinkedList<Integer> cachedResources = new LinkedList<>();

  public Cache(long capacity, long accessDelay) {
    super(capacity, accessDelay);

    setPerMBReadDelay(MB_DELAY);
  }

  @Override
  public boolean write(Resource resource) {
    if (resource.size() <= getCapacity()) {
      while (resource.size() > getFreeSpace()) {
        delete(read(cachedResources.removeFirst()));
      }

      if (write(resource)) {
        cachedResources.addLast(resource.getId());
        return true;
      }
    }

    // either there wasn't enough space for it to ever fit,
    // or there was some sort of a problem writing it to the storage
    return false;
  }

  public void updateCache(Integer rId) {
    this.cachedResources.remove(rId);
    this.cachedResources.addLast(rId);
  }

}
