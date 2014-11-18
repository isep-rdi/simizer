/**
 * Provides the necessary classes to simulate sending requests.
 * <p>
 * There are two main ways to create and send requests:
 * <ul><li>The default request does not require an answer from the destination
 *         node.  It can therefore be considered asynchronous.  See {@link
 *         Application#sendOneWay(Node,Request)}.
 * 
 *     <li>The second way requires a response from the destination node.  The
 *         responding node must therefore send the request back to the caller.
 *         (See {@link Application#sendResponse(Node,Request)}.</ul>
 * 
 * @since 0.4
 */
package simizer.requests;
