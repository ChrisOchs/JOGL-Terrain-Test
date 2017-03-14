/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jogltest.util;

public class Vector3d {

    public double x;
    public double y;
    public double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d normalize() {
        double norm = Math.sqrt(x * x + y * y + z * z);

        return new Vector3d(x / norm, y / norm, z / norm);
    }

    public Vector3d scalarMultiply(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    public String toString() {
        return String.format("<%f, %f, %f>", x, y, z);
    }

    public Vector3d add(Vector3d other) {
        return new Vector3d(x + other.x, y + other.y, z + other.z);
    }

    public Vector3d subtract(Vector3d other) {
        return new Vector3d(x - other.x, y - other.y, z - other.z);
    }

    public double dot(Vector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3d cross(Vector3d other) {
        return new Vector3d(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y - other.x));
    }
}
