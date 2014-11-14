// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi

package simizer.storage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author sathiya
 */
public class Cache extends StorageElement {

  private static int PAGE_SZ = 4096; // memory page sz.

  private static double MB_DELAY = 0.019D;
  private Map<Integer, Integer> counters = new HashMap<Integer, Integer>();

  private LinkedList<Integer> seqList = new LinkedList<Integer>();

  public Cache(long capacity, long accessDelay) {
    super(capacity, accessDelay, MB_DELAY);
  }

  public void writeToCache(Resource r) {
    if (r.size() <= capacity) {
      while (r.size() + volumeFilled > capacity) {
        this.delete(storage.get(seqList.removeFirst()));
      }

      this.seqList.addLast(r.getId());
      write(r);
    }
  }

  public void updateCache(Integer rId) {
    this.seqList.remove(rId);
    this.seqList.addLast(rId);
  }

}
