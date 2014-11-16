//Aruth Perum Jothi Aruth Perum Jothi Thani Perum Karunai Aruth Perum Jothi

package simizer.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sathiya
 */
public class StorageElement {

  /** The size of a kilobyte. */
  public static final long KILOBYTE = 1024;

  /** The size of a megabyte. */
  public static final long MEGABYTE = KILOBYTE * KILOBYTE;
  
  /** The size of a gigabyte. */
  public static final long GIGABYTE = KILOBYTE * MEGABYTE;

  /** The size of a terabyte. */
  public static final long TERABYTE = KILOBYTE * GIGABYTE;

  protected static ResourceFactory resourceFact;

  public static void setFactory(ResourceFactory rf) {
    resourceFact = rf;
  }

  public static ResourceFactory getFactory() {
    return resourceFact;
  }

  protected long volumeFilled = 0;
  protected long capacity;
  protected long delay;

  protected double mbReadDelay = 2.0;
  protected double mbWriteDelay = 2.0;

  protected final Map<Integer, Resource> storage;

  public StorageElement(long capacity, long accessDelay) {
    this.capacity = capacity;
    this.delay = accessDelay;
    this.storage = new HashMap<>();
  }

  public StorageElement(long capacity, long accessDelay, double mbReadDelay) {
    this(capacity, accessDelay);
    this.mbReadDelay = mbReadDelay;
  }

  public long getAvailableSpace() {
    return (capacity - volumeFilled);
  }

  public long getUsedSpace() {
    return this.volumeFilled;
  }

  public long getCapacity() {
    return this.capacity;
  }

  public Resource read(Integer resId) {
    return storage.get(resId);
  }

  public boolean write(Resource resource) {
    // try to modify the file first, and if that fails, create it
    if (!modify(resource)) {
      if (resource.size() > getAvailableSpace()) {
        return false;
      }

      storage.put(resource.getId(), resource);
      volumeFilled += resource.size();
    }

    return true;  // getting this far means that we could change the resource
  }

  public boolean modify(Resource resource) {
    if (contains(resource.getId())) {
      read(resource.getId()).modify();
      return true;
    } else {
      return false;  // could not modify it because the file doesn't exist
    }
  }

  public boolean delete(Resource resource) {
    if (contains(resource.getId())) {
      storage.remove(resource.getId());
      volumeFilled -= resource.size();
      return true;
    } else {
      return false;
    }
  }

  public void write(List<Integer> ressources) {
    for (Integer r : ressources) {
      write(resourceFact.getResource(r));
    }
  }

  public void Nwrite(List<Integer> ressources, long aliveTime) {
    for (Integer r : ressources) {
      Nwrite(r, aliveTime);
    }
  }

  public void Nwrite(Integer r, long aliveTime) {
    //checks wether it is a modification rather than a new file
    if (storage.containsKey(r)) {
      this.storage.get(r).modify();
      this.storage.get(r).setAliveTime(aliveTime);
    } else {
      Resource rTest = new Resource(r);
      rTest.setAliveTime(aliveTime);
      this.storage.put(r, rTest);
    }
  }

  public boolean contains(Integer resc_name) {
    return storage.containsKey(resc_name);
  }

  public boolean contains(List<Integer> resources) {
    for (Integer resource : resources) {
      if (!contains(resource)) {
        return false;
      }
    }
    return true;
  }

  public boolean isUnlocked(List<Integer> resources) {
    for (Integer id : resources) {
      if (contains(id)) {
        Resource resource = read(id);
        if (!resource.isUnlocked()) {
          return false;
        }
      }
    }
    return true;
  }

  public Iterable<Resource> getResourcesList() {
    return this.storage.values();
  }

  private static long computeDelay(double mbDelay, long seekDelay, int size) {
    return Math.round((size * 1.0 / MEGABYTE) * mbDelay) + seekDelay;
  }

  public long getDelay(Integer ressourceId) {
    if (storage.containsKey(ressourceId)) {
      long rDelay = Math.round((storage.get(ressourceId).size() / MEGABYTE) * mbReadDelay);
      return rDelay + delay; //disk_seek
    } else {
      return delay;
    }
  }

  public long getReadDelay(int id, int size) {
    if (storage.containsKey(id)) {
      return computeDelay(mbReadDelay, delay, size);
    } else {
      return delay;
    }
  }

  public long getWriteDelay(int id, int size) {
    return computeDelay(mbWriteDelay, delay, size);
  }

}
