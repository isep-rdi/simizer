/**
 * Provides the processor abstraction used when handling requests.
 * <p>
 * When a {@link fr.isep.simizer.app.Application} handles a request, it may need
 * to simulate various system operations, such as reading a file, sending a
 * message over a network, or executing some instructions on the processor.
 * <p>
 * These classes handle the sequential and parallel execution of these various
 * tasks.  The {@link fr.isep.simizer.processor.ProcessingUnit} class provides
 * an implementation of a {@link fr.isep.simizer.processor.TaskProcessor} that
 * simulates sharing the processor between multiple tasks and process switching.
 *
 * @see fr.isep.simizer.processor.tasks.Task
 */
package fr.isep.simizer.processor;
