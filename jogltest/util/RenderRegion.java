/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest.util;

/**
 *
 * @author Den
 */
public class RenderRegion {
    public int x;
    public int z;
    public int width;
    public int depth;

    public int step;

    public RenderRegion(int x, int y, int width, int depth, int level) {
        this.x = x;
        this.z = y;
        this.width = width;
        this.depth = depth;
        this.step = level;
    }
}
