package fr.isep.simizer.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@code StorageElement} class.
 *
 * @author Max Radermacher
 */
public class StorageElementTest {

  private StorageElement storage;

  @Before
  public void setUp() {
    storage = new StorageElement(StorageElement.KILOBYTE, 10);
  }

  /**
   * Tests for the resource reading functionality.
   * <p>
   * The key here is that modifying the values returned by this function should
   * not modify those values on the disk.
   */
  @Test
  public void testRead() {
    Resource toStore = new Resource(1, StorageElement.KILOBYTE);
    assertTrue("should be able to write the Resource", storage.write(toStore));

    // read the Resource and make changes
    Resource read = storage.read(toStore.getId());
    assertTrue("should get a value for the Resource", read != null);
    read.setSize(StorageElement.MEGABYTE);
    read.modify();

    Resource secondRead = storage.read(toStore.getId());
    assertTrue("should get a value for the Resource", secondRead != null);
    assertEquals("the size should not have changed",
            StorageElement.KILOBYTE, secondRead.size());
    assertEquals("the version should not have changed",
            0, secondRead.getVersion());
  }

  /**
   * Tests for the resource writing functionality.
   * <p>
   * When writing a file, the old file should be replaced entirely with the one
   * that we specify, even if it has a different size or a different version.
   * <p>
   * Similarly, when we write a file, the disk should get its own copy.  Our
   * subsequent changes to the {@link Resource} should not be automatically
   * reflected.
   */
  @Test
  public void testWrite() {
    final long SIZE = StorageElement.KILOBYTE / 2;

    long freeSpace = storage.getFreeSpace();

    Resource myResource = new Resource(1, SIZE);

    assertFalse("should not contained the resource",
            storage.contains(myResource.getId()));

    storage.write(myResource);
    freeSpace -= SIZE;

    assertEquals("amount of free space should have decreased",
            freeSpace, storage.getFreeSpace());

    myResource.setSize(SIZE + 1);

    assertEquals("internal resource should not change size",
            SIZE, storage.read(myResource.getId()).size());

    long initialResourceVersion = myResource.getVersion();
    myResource.modify();

    assertEquals("internal resource should not change its version",
            initialResourceVersion,
            storage.read(myResource.getId()).getVersion());

    storage.write(myResource);
    freeSpace -= 1;
    assertEquals("amount of free space should have decreased by one",
            freeSpace, storage.getFreeSpace());
    assertEquals("resource sizes should match",
            myResource.size(), storage.read(myResource.getId()).size());
    assertEquals("resource versions should match",
            myResource.getVersion(),
            storage.read(myResource.getId()).getVersion());
  }
}
