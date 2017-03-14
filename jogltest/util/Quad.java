package jogltest.util;

/**
 *
 * @author Den
 */
public class Quad {
    public Vector3d point1;
    public Vector3d point2;
    public Vector3d point3;
    public Vector3d point4;

    public Vector3d normal;

    public Quad(Vector3d point1, Vector3d point2, Vector3d point3, Vector3d point4) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        this.point4 = point4;

        Vector3d quadNormal = Utils.calculateNormal(point1, point2, point3);

        this.normal = quadNormal;
    }
}
