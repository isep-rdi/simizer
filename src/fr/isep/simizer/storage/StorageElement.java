//Aruth Perum Jothi Aruth Perum Jothi Thani Perum Karunai Aruth Perum Jothi

package fr.isep.simizer.storage;

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

  /** The amount of space currently used. */
  protected long used = 0;

  /** The capacity of the storage. */
  protected long capacity;

  /** The constant-time delay (in milliseconds) when accessing data. */
  protected long delay;

  /** The amount time needed to read a megabyte from this storage. */
  protected double perMBReadDelay = 2.0;

  /** The amount of time needed to write a megabyte to this storage. */
  protected double perMBWriteDelay = 2.0;

  /** The resources contained in this {@code StorageElement}. */
  protected final Map<Integer, Resource> storage;

  public StorageElement(long capacity, long accessDelay) {
    this.capacity = capacity;
    this.delay = accessDelay;
    this.storage = new HashMap<>();
  }

  /**
   * Sets the time needed to read a megabyte from this {@code StorageElement}.
   * <p>
   * The value is multiplied by the number of megabytes being read to determine
   * the amount of time needed.
   *
   * @param perMBDelay the delay per megabyte
   */
  public void setPerMBReadDelay(double perMBDelay) {
    this.perMBReadDelay = perMBDelay;
  }

  /**
   * Sets the time needed to write a megabyte to this {@code StorageElement}.
   * <p>
   * The value is multiplied by the number of megabytes being written to
   * determine the amount of time needed.
   *
   * @param perMBDelay the delay per megabyte
   */
  public void setPerMBWriteDelay(double perMBDelay) {
    this.perMBWriteDelay = perMBDelay;
  }

  /**
   * Returns the amount of space currently available.
   * <p>
   * The internal implementation of this method returns the result of {@link
   * #getUsedSpace()} subtracted from the result of {@link #getCapacity()}.
   * Subclasses can continue using this implementation by only overriding those
   * two methods.
   *
   * @return the amount of space currently available
   */
  public long getFreeSpace() {
    return (getCapacity() - getUsedSpace());
  }

  /**
   * Returns the amount of space currently in use.
   *
   * @return the amount of space currently in use
   */
  public long getUsedSpace() {
    return this.used;
  }

  /**
   * Returns the total capacity.
   *
   * @return the total capacity
   */
  public long getCapacity() {
    return this.capacity;
  }

  /**
   * Immediately reads the resource with the specified ID.
   * <p>
   * There is no delay or simulation logic imposed by this method.  It will
   * immediately return the current version of the {@link Resource} for use.
   * <p>
   * Note that this does not return a copy of the {@link Resource}, but rather a
   * reference to the {@link Resource}.  If changes are made (such as saving a
   * modification), they will be reflected in the value previously returned by
   * this method.
   *
   * @param resourceId the ID of the {@link Resource} to read
   * @return the {@link Resource}, or null if this storage does not contain a
   *             {@link Resource} with that ID
   */
  public Resource read(int resourceId) {
    return storage.get(resourceId);
  }

  /**
   * Saves the {@code Resource}, modifying it or creating it if it doesn't exist.
   *
   * @param resource the {@link Resource} to save
   * @return true if the {@link Resource} could be saved, false if an error
   *         occurred (such as not having enough free space)
   */
  public boolean write(Resource resource) {
    // try to modify the file first, and if that fails, create it
    if (!modify(resource)) {
      if (resource.size() > getFreeSpace()) {
        return false;
      }

      storage.put(resource.getId(), resource);
      used += resource.size();
    }

    return true;  // getting this far means that we could change the resource
  }

  /**
   * Modifies the specified {@code Resource}.
   *
   * @param resource the {@link Resource} to modify
   * @return true if the {@link Resource} was successfully modified, false if
   *         there is no {@link Resource} with that ID
   */
  public boolean modify(Resource resource) {
    if (contains(resource.getId())) {
      read(resource.getId()).modify();
      return true;
    } else {
      return false;  // could not modify it because the file doesn't exist
    }
  }

  /**
   * Deletes the specified {@code Resource} from the storage.
   *
   * @param resource the {@link Resource} to delete
   * @return true if the {@link Resource} is deleted, false if it cannot be
   *             deleted (because it doesn't exist)
   */
  public boolean delete(Resource resource) {
    if (contains(resource.getId())) {
      storage.remove(resource.getId());
      used -= resource.size();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Determines if this storage contains the specified {@code Resource}.
   *
   * @param resourceId the ID of the {@link Resource} to find
   * @return true if this storage contains the {@link Resource}, false if it
   *             does not
   */
  public boolean contains(int resourceId) {
    return storage.containsKey(resourceId);
  }

  /**
   * Determines if this storage contains <b>all</b> of the {@code Resource}s.
   *
   * @param resources the IDs of the {@link Resource}s to check
   * @return true if this storage contains all of the {@link Resource}s with the
   *         specified IDs, false if it does not
   */
  public boolean contains(List<Integer> resources) {
    for (Integer resource : resources) {
      if (!contains(resource)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns an iterator of all the {@code Resource}s in this storage.
   *
   * @return an iterator of all the {@code Resource}s in this storage
   */
  public Iterable<Resource> getResources() {
    return this.storage.values();
  }

  private static long computeDelay(double mbDelay, long seekDelay, long size) {
    return Math.round((size * 1.0 / MEGABYTE) * mbDelay) + seekDelay;
  }

  public long getDelay(Integer ressourceId) {
    if (storage.containsKey(ressourceId)) {
      long rDelay = Math.round((storage.get(ressourceId).size() / MEGABYTE) * perMBReadDelay);
      return rDelay + delay; //disk_seek
    } else {
      return delay;
    }
  }

  public long getReadDelay(int id, long size) {
    if (storage.containsKey(id)) {
      return computeDelay(perMBReadDelay, delay, size);
    } else {
      return delay;
    }
  }

  public long getWriteDelay(int id, long size) {
    return computeDelay(perMBWriteDelay, delay, size);
  }

  /** @deprecated */
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

  /** @deprecated */
  public void Nwrite(List<Integer> ressources, long aliveTime) {
    for (Integer r : ressources) {
      Nwrite(r, aliveTime);
    }
  }

  /** @deprecated */
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

}
