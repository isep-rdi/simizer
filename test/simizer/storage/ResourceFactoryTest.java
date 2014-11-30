package simizer.storage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@code ResourceFactory} class.
 *
 * @author Max Radermacher
 */
public class ResourceFactoryTest {
  private ResourceFactory factory = null;

  @Before
  public void setUp() {
    factory = new ResourceFactory(10, 0, 32);
  }

  /**
   * Checks whether or not the {@link ResourceFactory#getResource(int)} method
   * performs as expected.
   */
  @Test
  public void testGetResource() {
    // we should be able to retrieve resources created during the initial phase
    Resource r1 = factory.getResource(5);
    Assert.assertNotNull("a resource should be returned", r1);
    Assert.assertEquals("resource should be 32 bytes", 32, r1.size());

    // we should be able to get resources that weren't created in the initial
    // phase
    Resource r2 = factory.getResource(15);
    Assert.assertNotNull("resource should be created when needed", r2);
    Assert.assertEquals("resource should be 32 bytes", 32, r2.size());


    // When we call getStartList(), it is a destructive operation.  Therefore,
    // we should make sure that we can still retrieve resources that have been
    // returned by this method.
    List<Integer> list = factory.getStartList(10);
    Assert.assertTrue("should get resource with ID 5", list.contains(5));
    Resource r3 = factory.getResource(5);
    Assert.assertNotNull("resource should be recreated", r3);
    Assert.assertEquals("resource should be 32 bytes", 32, r3.size());
  }

  /**
   * Checks whether or not {@link ResourceFactory#getStartList()} returns the
   * expected list of resources.
   */
  @Test
  public void testGetStartList() {
    List<Integer> resources = factory.getStartList();

    // there should be ten resources returned
    Assert.assertEquals("did not get all resources", 10, resources.size());
  }

  /**
   * Checks whether or not {@link ResourceFactory#getStartList(int)} returns the
   * resources in properly-size groups.
   */
  @Test
  public void testGetStartListPartial() {
    Set<Integer> all = new HashSet<>(9);

    for (int i = 0; i < 3; i++) {
      List<Integer> list = factory.getStartList(3);
      // makes sure that each of these gets a group of three items
      Assert.assertEquals("did not retrieve enough items (" + i + ")",
          3, list.size());
      all.addAll(list);
    }

    // Since we started with ten items, there should be one item left.  Make
    // sure that we only get one item (and no errors) when requesting the final
    // group.
    List<Integer> list = factory.getStartList(3);
    Assert.assertEquals("did not retrieve enough items (4)", 1, list.size());

    all.addAll(list);

    // Make sure that all of the resources returned throughout the process are
    // unique.  Do this by adding them to a Set and checking the size.
    Assert.assertEquals("should have 10 unique resources", 10, all.size());
  }
}
