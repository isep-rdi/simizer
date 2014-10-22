/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.storage;

/**
 *  Holds common size units used in the project
 * @author isep
 */
public enum StorageUnits {
    KILOBYTE (1024),
    MEGABYTE (1024 * 1024),
    GIGABYTE (1024 * 1024 * 1024);
    private final int value;
    
    StorageUnits(int value) {
        this.value = value;
    }

}
