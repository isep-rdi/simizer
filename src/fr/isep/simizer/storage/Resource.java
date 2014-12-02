//Aruth Perum Jothi Aruth Perum Jothi Thani Perum Karunai Aruth Perum Jothi

package fr.isep.simizer.storage;

/**
 * Represents an object in that can be stored in some form of memory.
 * <p>
 * A {@code Resource} can be thought of as a file.  They can be stored on disks
 * and in memory, they have a size, and they can be read, write, and modified.
 * 
 * @author sathiya
 */
public class Resource {

  /** The default size of {@code Resource}s. */
  public static final int DEFAULT_SIZE = 1024;

  /** The ID of the {@code Resource}. */
  private final Integer id;

  /** The current size of the {@code Resource}. */
  private long size;

  /**
   * The current revision (version) of the resource.
   * <p>
   * This value is useful in determining whether or not two copies on two
   * different storage mediums are no longer synchronized.
   */
  private int version = 0;

  /** Whether or not the file is locked. */
  private boolean lock = false;

  /**
   * Creates a {@code Resource} with the default size ({@value #DEFAULT_SIZE}).
   *
   * @param resourceId the ID to use for the {@code Resource}
   */
  public Resource(Integer resourceId) {
    this(resourceId, DEFAULT_SIZE);
  }

  /**
   * Creates a {@code Resource} with the specified ID and size.
   * 
   * @param resourceId the ID to use for the {@code Resource}
   * @param size the size of the {@code Resource}
   */
  public Resource(Integer resourceId, int size) {
    this.id = resourceId;
    this.size = size;
  }

  /**
   * Creates a {@code Resource} by copying the contents of another.
   * <p>
   * Please be aware that this constructor will still set the {@code version} of
   * the new resource to zero.  (This may or may not be the desired behavior,
   * but it is the way that it works.)
   *
   * @param resource the {@code Resource} to copy
   */
  public Resource(Resource resource) {
    this.id = resource.id;
    this.size = resource.size;
  }

  /**
   * Returns the ID of the {@code Resource}.
   *
   * @return the ID of the {@code Resource}
   */
  public Integer getId() {
    return this.id;
  }

  /**
   * Returns the size of the {@code Resource}.
   *
   * @return the size of the {@code Resource}
   */
  public long size() {
    return size;
  }

  /**
   * Returns the current version number of the {@code Resource}.
   * 
   * @return the current version number of the {@code Resource}
   */
  public int getVersion() {
    return this.version;
  }

  /**
   * Changes the current version number of the {@code Resource}.
   *
   * @param version the new version number
   */
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * "Modifies" the file, which increments the version number.
   * <p>
   * Since the {@code Resource} objects do not contain any actual data, we use a
   * number to represent revisions of files.  To determine if two particular
   * copies of a file (having the same resource ID) contain the same data,
   * compare the version numbers for equality.
   */
  public void modify() {
    this.version++;
  }

  /**
   * Returns whether or not the file is unlocked.
   *
   * @return true if the file is unlocked, false if it is locked
   */
  public boolean isUnlocked() {
    return !this.lock;
  }

  /**
   * Locks the {@code Resource}.
   */
  public void lock() {
    this.lock = true;
  }

  /**
   * Unlocks the {@code Resource}.
   */
  public void unlock() {
    this.lock = false;
  }

  /** @deprecated */
  public void setAliveTime(long aliveTime) {
    // this.aliveTime = aliveTime;
  }

}