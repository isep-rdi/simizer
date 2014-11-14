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

  protected static ResourceFactory resourceFact;
  static private long MEGABYTE = 1024 * 1024L;

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


  public long volumeFilled() {
    return volumeFilled;
  }

  public long storageCapacity() {
    return this.capacity;
  }

  public long getAvailableCapacity() {
    return (capacity - volumeFilled);
  }

  public long getUsedSize() {
    return this.volumeFilled;
  }

  public long getSize() {
    return this.capacity;
  }


  public Resource read(Integer resId) {
    if (storage.containsKey(resId)) {
      return storage.get(resId);
    } else {
      return null;
    }
  }

  public void write(Resource r) {
    //checks wether it is a modification rather than a new file
    if (storage.containsKey(r.getId())) {
      storage.get(r.getId()).modify();
    } else {
      if (r.size() + volumeFilled <= capacity) {
        storage.put(r.getId(), r);
        volumeFilled += r.size();
      }

    }
  }

  public void modify(Resource res) {
    throw new UnsupportedOperationException("Not yet implemented");
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

  public void delete(Resource r) {
    if (storage.containsKey(r.getId())) {
      storage.remove(r.getId());
      volumeFilled -= r.size();
    }
  }

  public Boolean contains(Integer resc_name) {
    return storage.containsKey(resc_name);
  }

  public boolean contains(List<Integer> ressources) {
    boolean res = true;
    for (Integer r : ressources) {
      res &= contains(r);
    }
    return res;
  }

  public boolean isUnlocked(List<Integer> resources) {
    for (Integer id : resources) {
      if (storage.containsKey(id)) {
        Resource resource = this.storage.get(id);
        if (!resource.isUnLocked()) {
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
