package fr.isep.simizer.processor.tasks;

/**
 * Represents the current status of a {@code Task}.
 */
public enum TaskStatus {
  /** A state for {@code Task}s that are currently being run. */
  RUNNING,

  /** A state for {@code Task}s that have finished running. */
  FINISHED,
}
