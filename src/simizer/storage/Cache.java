// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi

package simizer.storage;

import java.util.LinkedList;

/**
 * Represents a memory-based cache (speeds up file access times).
 * <p>
 * The implementation of this class is essentially the same as a {@link
 * StorageElement}, with two modifications to its behavior:
 * <ul><li>the speed of reading has been increased
 *     <li>{@link Resource}s are automatically removed to make room for newer
 *         {@link Resource}s that are written</ul>
 *
 * @author sathiya
 */
public class Cache extends StorageElement {

  private static final double PER_MB_DELAY = 0.019D;

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

    setPerMBReadDelay(PER_MB_DELAY);

    // TODO: I think that we should probably set the write delay here also.
    // Since the old code did not, I won't change it, but it's something to
    // consider for the future.
  }

  @Override
  public boolean write(Resource resource) {
    if (resource.size() <= getCapacity()) {
      while (resource.size() > getFreeSpace()) {
        delete(read(cachedResources.removeFirst()));
      }

      if (super.write(resource)) {
        cachedResources.addLast(resource.getId());
        return true;
      }
    }

    // either there wasn't enough space for it to ever fit,
    // or there was some sort of a problem writing it to the storage
    return false;
  }

  /**
   * Marks the resource with the specified ID as having been accessed.
   * <p>
   * When a value in the cache is used, we should re-add it to the back of the
   * LRU queue.
   * 
   * @param resourceId the ID of the resource that was accessed
   */
  public void updateCache(int resourceId) {
    // TODO: This should probably make sure that the specified resource is
    // actually contained in the cache.  If it is not, it could cause the
    // write() method above to throw a NullPointerException.  (It will try to
    // delete a null resource.)
    
    this.cachedResources.remove(resourceId);
    this.cachedResources.addLast(resourceId);
  }

}
