/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.storage;

/**
 * This exception is raised when the system tries to load a 
 * resource that is too big for the current memory
 * @author slefebvr
 */
public class NotEnoughSpaceException extends Exception {
    private Memory mem;
    public NotEnoughSpaceException(Memory mem) {
        this.mem = mem;
    }
    public Memory getMemory() {
        return mem;
    }
}
