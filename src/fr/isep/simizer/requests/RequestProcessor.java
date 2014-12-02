/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.requests;

/**
 *
 * @author isep
 */
public interface RequestProcessor {
    public void onDataReady(long timestamp, Request r);
    public void onQuantumEnded(long timestamp, Request r);
}