/**
 * Provides the {@code Application} abstraction for handling requests.
 * <p>
 * When using the framework, {@link fr.isep.simizer.app.Application} subclasses
 * can be created to provide the desired handling behavior for {@link
 * fr.isep.simizer.requests.Request}s that are received.
 * <p>
 * As an example, an {@link fr.isep.simizer.app.Application} could be used to
 * simulate serving static resources from a disk (or a cache), but it could also
 * be used to simulate dynamically generating pages (such as a CGI script) for
 * each {@link fr.isep.simizer.requests.Request}.
 */
package fr.isep.simizer.app;
