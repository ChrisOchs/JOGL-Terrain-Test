/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest.util;

/**
 *
 * @author Den
 */
public class Utils {

    public static Vector3d calculateNormal(Vector3d point1, Vector3d point2, Vector3d point3) {
        double aX = point1.x - point2.x;
        double aY = point1.y - point2.y;
        double aZ = point1.z - point2.z;

        double bX = point2.x - point3.x;
        double bY = point2.y - point3.y;
        double bZ = point2.z - point3.z;

        Vector3d a = new Vector3d(aX, aY, aZ);
        Vector3d b = new Vector3d(bX, bY, bZ);

        return a.cross(b).normalize();
    }
}
