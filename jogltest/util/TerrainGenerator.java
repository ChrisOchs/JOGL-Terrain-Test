/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Den
 */
public class TerrainGenerator {

    private static int[][] heightMap;

    public static void setHeightMap(int [][] heightMap) {
        TerrainGenerator.heightMap = heightMap;
    }

    public static Vector3d[] generatePoints(int x, int z, int width, int depth) {

        final int STEP_SIZE = 64;

        Vector3d[] points = new Vector3d[(width + 1) * (depth + 1)];

        for(int r = 0; r <= depth; r++) {
            for(int c = 0; c <= width; c++) {

                int xIndex = c + x;
                int yIndex = r + z;

                points[r * (depth + 1) + c] = new Vector3d(xIndex * STEP_SIZE, getHeightAt(xIndex, yIndex), yIndex * STEP_SIZE);
            }
        }

        return points;
    }

    public static ArrayList<Vector3d> getNormalsFromPoints(Vector3d[] points) {
        ArrayList<Vector3d> normals = new ArrayList<Vector3d>();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {

                Vector3d point1 = points[i * 17 + j]; // Top Left
                Vector3d point2 = points[(i + 1) * 17 + j]; // Bottom Left
                Vector3d point3 = points[(i + 1) * 17 + j + 1]; // Bottom Right
                Vector3d point4 = points[i * 17 + j + 1]; // Top Right

                Vector3d t1 = point1.subtract(point2);
                Vector3d t2 = point2.subtract(point3);
                Vector3d t3 = point3.subtract(point4);
                Vector3d t4 = point4.subtract(point1);

                Vector3d normal1 = t1.cross(t2).normalize();
                Vector3d normal2 = t2.cross(t3).normalize();
                Vector3d normal3 = t3.cross(t4).normalize();
                Vector3d normal4 = t4.cross(t1).normalize();

                Vector3d normal = normal1.add(normal2).add(normal3).add(normal4).normalize();
                
                normals.add(normal);
            }
        }

        return normals;
    }

    public static ArrayList<Integer> getIndicesForLevel(Vector3d[] points, int step) {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        int stepSkip = (int) Math.pow(2, step);

        for (int i = 0; i < 16; i += stepSkip) {
            for (int j = 0; j < 16; j += stepSkip) {

                int point1 = i * 17 + j; // Top Left
                int point2 = (i + stepSkip) * 17 + j; // Bottom Left
                int point3 = (i + stepSkip) * 17 + j + stepSkip; // Bottom Right
                int point4 = i * 17 + j + stepSkip; // Top Right

                indices.add(point1);
                indices.add(point2);
                indices.add(point3);

                indices.add(point1);
                indices.add(point3);
                indices.add(point4);
            }
        }

        return indices;
    }

    public static int getHeightAt(int x, int z) {
        return heightMap[z][x];
    }
}
