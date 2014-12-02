/**
 * Allows messages to be sent to one another via a network.
 * <p>
 * The {@code network} package handles the interactions between nodes in the
 * system.  These nodes in the system typically communicate by sending {@link
 * simizer.network.Message}s containing {@link simizer.requests.Request}s to one
 * another.
 * <p>
 * The {@link simizer.network.Network} class handles the interactions and
 * message sending between various nodes, simulating network delays and
 * latencies by delaying the delivery of messages.  The amount by which messages
 * are delayed is fully configurable by specified the {@link simizer.laws.Law}
 * for the {@link simizer.network.Network}.
 */
package fr.isep.simizer.network;
