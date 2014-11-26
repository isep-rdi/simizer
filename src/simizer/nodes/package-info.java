/**
 * Provides implementations of machines (client, server) for the simulation.
 * <p>
 * The {@link simizer.nodes.Node} class provides a general abstraction for a
 * device in the simulation.  The concrete subclasses {@link simizer.nodes.VM}
 * and {@link simizer.nodes.ClientNode} provide specific implementations for
 * servers and clients in the simulation.
 * <p>
 * The {@link simzier.nodes.ClientNode} will send requests to the servers, and
 * then it will print out the responses that it receives.
 * <p>
 * The {@link simizer.nodes.VM} is a bit more generic, allowing customized
 * {@link simizer.app.Application} instances to be deployed on them.  These
 * applications can perform a variety of tasks, such as load balancing, simple
 * resource retrieval, as well as more complex behavior like simulating
 * processor-intensive tasks.
 *
 * @see simizer.app.Application
 */
package simizer.nodes;
