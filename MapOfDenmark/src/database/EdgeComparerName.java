/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.Comparator;

/**
 *
 * @author Alex
 */
public class EdgeComparerName implements Comparator<Edge>{

    @Override
    public int compare(Edge o1, Edge o2) {
        return o1.getRoadName().compareTo(o2.getRoadName());                
                
    }
}
