/**
 * Creates randomness by sampling a probability distribution.
 * <p>
 * The randomness during the simulation is introduced by a collection of {@link
 * Law} instances.  Different aspects of the simulation, such as the time
 * between requests, network delay, and generation of new clients is
 * configurable using these {@link Law}s.
 * <p>
 * Each {@link Law} is a probability distribution that can be sampled.  The
 * simulation can be very accurate when appropriate {@link Law}s are used to
 * simulate each aspect.  However, wherever a {@link Law} is used, any provided
 * subclass (which includes most of the typical distributions) or custom
 * subclass can be used to allow for fully customized behavior.
 */
package simizer.laws;
