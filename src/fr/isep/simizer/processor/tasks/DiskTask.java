package fr.isep.simizer.processor.tasks;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.nodes.VM;
import fr.isep.simizer.processor.TaskProcessor;
import fr.isep.simizer.storage.IOType;
import fr.isep.simizer.storage.Resource;

/**
 * A task for writing/reading data to/from a disk.
 * <p>
 * Simulates the latency present in reading/writing files from a disk.  Because
 * the processor is free to perform other tasks while the {@code StorageElement}
 * is retrieving data, this {@code Task} can run in parallel with
 * processor-intensive tasks.
 */
public class DiskTask extends IOTask {

  /** The resource to read or write. */
  private final Resource resource;

  /** The type of action to take with the resource. */
  private final IOType type;

  /** The {@code VM} that should be used for IO operations. */
  private final VM vm;

  /**
   * Initializes a new {@code DiskTask}.
   * <p>
   * When run, this task will simulate performing the specified action on the
   * specified resource, and it will send an {@code IOTaskEvent} upon
   * completion.
   *
   * @param vm the {@link VM} where the {@code DiskTask} is operating
   * @param size the size of the resource being modified
   * @param resource the actual resource being modified
   * @param type the type of action that should be taken with the resource
   */
  public DiskTask(VM vm, int size, Resource resource, IOType type) {
    super(size);

    this.vm = vm;
    this.resource = resource;
    this.type = type;
  }

  /**
   * Returns the type of operation that will be taken with the {@code Task}.
   *
   * @return the type of operation that will be taken with the {@code Task}
   */
  public IOType getType() {
    return type;
  }

  /**
   * Returns the resource that will be modified with the {@code Task}.
   *
   * @return the resource that will be modified with the {@code Task}
   */
  public Resource getResource() {
    return resource;
  }

  @Override
  public void run(TaskProcessor processor, long timestamp) {
    super.run(processor, timestamp);

    long delay = vm.getTaskLength(this);
    processor.registerEvent(
        new DiskTaskEvent(timestamp + delay, this, this));
  }

  /**
   * Called when a {@code DiskTask} has finished.
   *
   * @param timestamp the current timestamp of the simulation
   * @param task the {@link DiskTask} that has "finished" (i.e., read its data
   *            from the disk)
   */
  public void onDataReady(long timestamp, DiskTask task) {
    vm.commitDiskTask(task);
    finish(timestamp);
  }

}

/**
 * Signals the completion of a disk IO operation.
 */
class DiskTaskEvent extends Event<DiskTask, DiskTask> {
  public DiskTaskEvent(long timestamp, DiskTask data, DiskTask target) {
    super(timestamp, data, target);
  }

  @Override
  public void dispatch() {
    this.target.onDataReady(timestamp, data);
  }
}
