/**
 * Provides the classes necessary to create requests
 * and responses between nodes .
 * <p>
 * There are several ways of creating requests:
 * <ul>
 * <li> The default request does not imply an answer from the destination node.
 *      It is therefore asynchronous by default. (@see simizer.app.Application.sendOneWay)
 * </li>
 * 
 * <li> the second method demands a response from the destination node. 
 * the responding node must therefore create a Response object 
 * with the originating request as parent parameter, and call the sendResponse method
 * Upon reception a ResponseReceived event will be signaled to the VM and propagated to the Application,
 * which will the call the handle method of the corresponding Application.
 * 
 * @since 0.4
 * @see simizer.VM
 * @see  simizer.app
 */
package simizer.requests;
