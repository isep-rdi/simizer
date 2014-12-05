package fr.isep.simizer.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles the automatic creation of a set of same-sized {@code Resource}s.
 * <p>
 * Using the {@link #ResourceFactory(int, int, int)} constructor, the factory
 * instance can be pre-populated with {@link Resource} objects.  After creation,
 * additional {@code Resource}s are added whenever they are accessed using the
 * {@link #getResource(java.lang.Integer)} method.
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

  /** The size of each created {@code Resource}. */
  private final long resourceSize;

  /**
   * Stores the {@code Resource} objects that have been created.
   * <p>
   * Maps {@code Resource} IDs to {@code Resource} objects.
   */
  private final Map<Integer, Resource> resources = new HashMap<>();

  /**
   * Initializes a new factory to create {@code Resource}s.
   * <p>
   * Using this constructor creates no resources by default.  To create {@link
   * Resource}s when creating the factory, use the {@link #ResourceFactory(int,
   * int)} constructor instead.
   *
   * @param resourceSize the size of each {@link Resource} created by this
   *            factory.
   */
  public ResourceFactory(long resourceSize) {
    this(resourceSize, 0);
  }

  /**
   * Initializes a new factory to create {@code Resource}s.
   *
   * @param resourceSize the size of each {@link Resource} created by this
   *            factory.
   * @param initialResourceCount the number of resources to initial load into
   *            the factory.  This can be useful when paired with {@link
   *            #getStartList()}, which will return the integer IDs of all the
   *            {@link Resource}s that have already been added.
   */
  public ResourceFactory(long resourceSize, int initialResourceCount) {
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

  /**
   * Returns the size of each {@code Resource} created by this factory.
   *
   * @return the size of each {@code Resource} created by this factory
   */
  public long getResourceSize() {
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
  public Resource getResource(Integer id) {
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
   * #getResource(java.lang.Integer)} method.
   * 
   * @return a list of {@code Resource} IDs stored in this factory
   */
  public List<Integer> getStartList() {
    return new ArrayList(resources.keySet());
  }

  /**
   * Returns up to {@code numberOfKeys} starting resources.
   * <p>
   * This method will return at most {@code numberOfResources} resources from
   * the {@code ResourceFactory}.  If there aren't {@code numberOfResources}
   * resources left in the factory, then a smaller number will be returned.
   * <p>
   * A call to this method is destructive, meaning that it "removes" elements
   * from future calls to this method and {@link #getStartList()}.  This makes
   * it useful for assigning a different set of resources to distinct storage
   * elements.
   * <p>
   * However, note that resources are recreated when they are specifically
   * requested by their ID.  Therefore, calling this method has no effect on the
   * behavior of the {@link #getResource(java.lang.Integer)} method.  Also note
   * that the return value of this method or the {@link #getStartList()} method
   * could be affected by calls that have been made to the {@link
   * #getResource(java.lang.Integer)} method.
   * 
   * @param numberOfResources the number of resources to return
   * @return a list containing the IDs for resources from this factory
   */
  public List<Integer> getStartList(int numberOfResources) {
    List<Integer> list = new ArrayList<>();

    Iterator<Integer> iterator = resources.keySet().iterator();
    while (iterator.hasNext() && numberOfResources > 0) {
      list.add(iterator.next());
      iterator.remove();
      numberOfResources--;
    }

    return list;
  }

}
