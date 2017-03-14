package jogltest.util;

/**
 *
 * @author Den
 */
public class Triangle {
    public Vector3d point1;
    public Vector3d point2;
    public Vector3d point3;

    public Vector3d normal;

    public Triangle(Vector3d point1, Vector3d point2, Vector3d point3) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        
        Vector3d quadNormal = Utils.calculateNormal(point1, point2, point3);
        
        this.normal = quadNormal;
    }
}
