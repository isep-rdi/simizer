package fr.isep.simizer.storage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@code Cache} class.
 *
 * @author Max Radermacher
 */
public class CacheTest {
  private Cache cache = null;

  private static final long CAPACITY = 1024;
  private static final long ACCESS_TIME = 16;

  @Before
  public void setUp() {
    cache = new Cache(CAPACITY, ACCESS_TIME);
  }

  /**
   * Tests if the resources can be written to the {@code Cache}.
   */
  @Test
  public void testWrite() {
    Assert.assertFalse("Resource already present.", cache.contains(1));
    cache.write(new Resource(1, 512));
    Assert.assertTrue("Resource missing.", cache.contains(1));
  }

  /**
   * Tests if the {@code Cache} automatically removes resources to make room.
   */
  @Test
  public void testAutomaticDeletion() {
    // place initial resources
    for (int i = 0; i < 4; i++) {
      cache.write(new Resource(i, 256));
    }
    for (int i = 0; i < 4; i++) {
      Assert.assertTrue("Resource " + i + " is missing.", cache.contains(i));
    }

    // add a new Resource
    cache.write(new Resource(4, 600));  // should replace three resources

    for (int i = 0; i < 3; i++) {
      Assert.assertFalse("Resource " + i + " not deleted.", cache.contains(i));
    }
    Assert.assertTrue("Resource " + 3 + " is missing.", cache.contains(3));
    Assert.assertTrue("Resource " + 4 + " is missing.", cache.contains(4));
  }

  /**
   * Tests if the {@link Cache#updateCache(int)} method works as described.
   */
  @Test
  public void testUpdateCache() {
    // fill the cache
    cache.write(new Resource(1, 512));
    cache.write(new Resource(2, 512));

    // ensure that the cache contains the first resource
    Assert.assertTrue("Resource 1 is missing.", cache.contains(1));
    Assert.assertTrue("Resource 2 is missing.", cache.contains(2));

    // mark the first Resource as updated
    cache.updateCache(1);

    // add another Resource
    cache.write(new Resource(3, 512));

    // ensure that the cache contains the first and third resources
    Assert.assertTrue("Resource 1 is missing.", cache.contains(1));
    Assert.assertFalse("Resource 2 present.", cache.contains(2));
    Assert.assertTrue("Resource 3 is missing.", cache.contains(3));
  }

  /**
   * Tests if the handling of large resources is correct.
   * <p>
   * For resources larger than the size of the cache, the cache should neither
   * store them nor remove other resources to make room.
   */
  @Test
  public void testLargeResources() {
    cache.write(new Resource(1, 1024));
    cache.write(new Resource(2, 2048));

    Assert.assertTrue("Resource 1 missing.", cache.contains(1));
    Assert.assertFalse("Resource 2 present.", cache.contains(2));
  }
}
