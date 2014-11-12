package simizer.processor.tasks;

import simizer.VM;
import simizer.processor.TaskProcessor;
import simizer.processor.events.IOTaskEvent;
import simizer.storage.IOType;
import simizer.storage.Resource;

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

  /**
   * Initializes a new {@code DiskTask}.
   * <p>
   * When run, this task will simulate performing the specified action on the
   * specified resource, and it will send an {@link IOTaskEvent} upon
   * completion.
   *
   * @param size the size of the resource being modified
   * @param resource the actual resource being modified
   * @param type the type of action that should be taken with the resource
   */
  public DiskTask(int size, Resource resource, IOType type) {
    super(size);

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

  /**
   * {@inheritDoc}
   * <p>
   * When the task is complete, it triggers an event of type {@link
   * IOTaskEvent}.  (The event is given the {@link TaskProcessor} for the
   * specified {@link VM}.)
   *
   * @param vm the {@code VM} on which the task is run
   * @param timestamp the time when the task is run
   */
  @Override
  public void startTask(VM vm, long timestamp) {
    long timing = vm.getTaskLength(this);

    vm.registerEvent(
        new IOTaskEvent(timestamp + timing, this, vm.getProcessingUnit()));
  }
}
