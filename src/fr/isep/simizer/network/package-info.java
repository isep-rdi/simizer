/**
 * Allows messages to be sent to one another via a network.
 * <p>
 * The {@code network} package handles the interactions between nodes in the
 * system.  These nodes in the system typically communicate by sending {@link
 * fr.isep.simizer.network.Message}s containing {@link
 * fr.isep.simizer.requests.Request}s to one another.
 * <p>
 * The {@link fr.isep.simizer.network.Network} class handles the interactions
 * and message sending between various nodes, simulating network delays and
 * latencies by delaying the delivery of messages.  The amount by which messages
 * are delayed is fully configurable by specified the {@link
 * fr.isep.simizer.laws.Law} for the {@link fr.isep.simizer.network.Network}.
 */
package fr.isep.simizer.network;
