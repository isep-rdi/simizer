//Aruth Perum Jothi Aruth Perum Jothi Thani Perum Karunai Aruth Perum Jothi

package simizer.storage;

/**
 *
 * @author sathiya
 */
public class Resource {

  private Integer id;
  private long size;
  private int version;
  private long aliveTime = 0;
  private boolean lock = false;

  public Resource(Integer id, int size) {
    this.id = id;
    this.size = size;
    this.version = 0;
  }

  //Copy constructor

  public Resource(Resource r) {
    this.id = r.id;
    this.size = r.size;
    this.version = 0;
  }

  public Resource(Integer rId) {
    this.id = rId;
    this.size = 1024;
    this.version = 0;
  }

  public long size() {
    return size;
  }

  public Integer getId() {
    return this.id;
  }

  public void modify() {
    this.version++;
  }

  public void setAliveTime(long aliveTime) {
    this.aliveTime = aliveTime;
  }

  public boolean isUnLocked() {
    if (this.lock) {
      return false;
    } else {
      return true;
    }
  }

  public void setLock() {
    this.lock = true;
  }

  public void unLock() {
    this.lock = false;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(Integer val) {
    this.version = val;
  }

}
