package simizer.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the automatic creation of a set of same-sized {@code Resource}s.
 * <p>
 * Using the {@link #ResourceFactory(int, int, int)} constructor, the factory
 * instance can be pre-populated with {@link Resource} objects.  After creation,
 * additional {@code Resource}s are added whenever they are accessed using the
 * {@link #getResource(int)} method.
 * <p>
 * When accessing {@link Resource} objects, this class returns copies rather
 * than the resources themselves.  This is in place to allow the same {@link
 * Resource} to be easily stored in multiple {@link StorageElement}s.  By having
 * a separate reference for each {@link StorageElement}, calling modify (to
 * change the version) on one system won't immediately change the {@code
 * Resource} version across all systems.  This allows the persistence policies
 * to be effectively tested.
 */
public class ResourceFactory {

  /** The maximum number of allowed resources.  Not enforced. */
  private final int maxRes;

  /** The size of each created {@code Resource}. */
  private final int resourceSize;

  /** Unclear. */
  private int resourceCounter = 0;

  /**
   * Stores the {@code Resource} objects that have been created.
   * <p>
   * Maps {@code Resource} IDs to {@code Resource} objects.
   */
  private final Map<Integer, Resource> resources = new HashMap<>();

  /**
   * Initializes a new factory to create {@code Resource}s.
   *
   * @param initialResourceCount the number of resources to initial load into
   *            the factory.  This can be useful when paired with {@link
   *            #getStartList()}, which will return the integer IDs of all the
   *            {@link Resource}s that have already been added.
   * @param maxRes meant to represent the maximum number of {@link Resource}s
   *            that can be created, but is currently not enforced.
   * @param resourceSize the size of each {@link Resource} created by this
   *            factory.
   */
  public ResourceFactory(int initialResourceCount, int maxRes,
        int resourceSize) {

    this.maxRes = maxRes;
    this.resourceSize = resourceSize;

    createInitialResources(initialResourceCount);
  }

  /**
   * Adds the first {@code count} number of resources to the factory.
   *
   * @param count the number of resources to add
   */
  private void createInitialResources(int count) {
    for (int i = 0; i < count; i++) {
      resources.put(i, new Resource(i, resourceSize));
    }
  }

  public int getMax() {
    return maxRes;
  }

  /**
   * Returns the size of each {@code Resource} created by this factory.
   *
   * @return the size of each {@code Resource} created by this factory
   */
  public int getResourceSize() {
    return resourceSize;
  }

  /**
   * Returns the number of resources that currently exist in the factory.
   *
   * @return the number of resources that currently exist in the factory
   */
  public int getResourceNb() {
    return resources.size();
  }

  /**
   * Returns a <b>new</b> resource of with an ID of {@code id}.
   * <p>
   * This method ensures that the factory contains an entry for a specified
   * {@link Resource} ID.  It then returns a fresh copy of the specified {@link
   * Resource} (i.e., a version of the {@link Resource} with its {@code version}
   * set to zero.  Calling this method multiple times with the same {@code id}
   * will give difference {@link Resource}s that aren't connected in any way.
   * (This is useful for storing the same {@link Resource} on multiple systems
   * where the underlying files aren't actually connected.  Instead, it is up
   * to the application (or persistence policy) to handle updating the files in
   * parallel.)
   * 
   * @param id the ID of the resource to return
   * @return the resource with the specified ID
   */
  public Resource getResource(int id) {
    if (!resources.containsKey(id)) {
      resources.put(id, new Resource(id, resourceSize));
    }
    return new Resource(resources.get(id));
  }

  /**
   * Returns a list of {@code Resource} IDs stored in this factory.
   * <p>
   * This is all of the resources that have already been created by the factory.
   * This includes the resources created using the initializer, and it also
   * includes any other resources that have been accessed with the {@link
   * #getResource(int)} method.
   * 
   * @return a list of {@code Resource} IDs stored in this factory
   */
  public List<Integer> getStartList() {
    return new ArrayList(resources.keySet());
  }

  /**
   * BROKEN
   * 
   * @param numberOfKeys
   * @return 
   */
  public List<Integer> getStartList(int numberOfKeys) {
    ArrayList tempList = new ArrayList();
    Object[] tempArray = resources.keySet().toArray();

    for (int eachResource = (resourceCounter % tempArray.length);
            eachResource < ((resourceCounter + numberOfKeys) % tempArray.length);
            eachResource++) {

      tempList.add(tempArray[eachResource]);
      resourceCounter++;
    }
    return tempList;
  }

}
