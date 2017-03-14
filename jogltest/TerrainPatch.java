/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest;

import com.jogamp.opengl.util.GLBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import jogltest.util.FrustumCuller;
import jogltest.util.RenderRegion;
import jogltest.util.TerrainGenerator;
import jogltest.util.Triangle;
import jogltest.util.Vector3d;

/**
 *
 * @author Den
 */
public class TerrainPatch {
    private RenderRegion region;

    private Vector3d midPoint;

    private int pointCount = 0;

    private int [] VBOVertices = new int[1];
    private int [] VBONormals = new int[1];
    private int [] VBOTextureCoordinates = new int[1];

    private static int [] indexBuffers = null;
    private static int [] bufferSizes = null;

    private static void initializeIndexBuffers(GL2 gl, Vector3d[] points) {
        if(indexBuffers != null) {
            return;
        }

        indexBuffers = new int[5];
        bufferSizes = new int[5];

        for (int i = 0; i < 5; i++) {
            ArrayList<Integer> indices = TerrainGenerator.getIndicesForLevel(points, i);

            IntBuffer indexBuffer = GLBuffers.newDirectIntBuffer(indices.size());

            for (int index : indices) {
                indexBuffer.put(index);
            }

            int [] indexBufferRef = new int[1];

            bufferSizes[i] = indices.size();

            indexBuffer.rewind();

            gl.glGenBuffers(1, indexBufferRef, 0);

            indexBuffers[i] = indexBufferRef[0];

            gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBufferRef[0]);
            gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferSizes[i] * GLBuffers.SIZEOF_INT, indexBuffer, GL2.GL_STATIC_DRAW);
        }
    }

    public TerrainPatch(GL2 gl, RenderRegion region) {
        this.region = region;

        Vector3d [] points = TerrainGenerator.generatePoints(region.x, region.z, region.width, region.depth);
        ArrayList<Vector3d> normals = TerrainGenerator.getNormalsFromPoints(points);
        
        TerrainPatch.initializeIndexBuffers(gl, points);
        
        midPoint = new Vector3d((region.x + region.width / 2) * 64, 0, (region.z + region.depth / 2) * 64);

        FloatBuffer vertexBuffer;
        FloatBuffer normalBuffer;
        FloatBuffer textureCoordinates;

        vertexBuffer = GLBuffers.newDirectFloatBuffer(points.length * 3);

        this.pointCount = points.length;

        for(Vector3d vertex : points) {
            vertexBuffer.put((float)vertex.x);
            vertexBuffer.put((float)vertex.y);
            vertexBuffer.put((float)vertex.z);
        }

        normalBuffer = GLBuffers.newDirectFloatBuffer(normals.size() * 3);

        for(Vector3d normal : normals) {
            normalBuffer.put((float)normal.x);
            normalBuffer.put((float)normal.y);
            normalBuffer.put((float)normal.z);
        }
        
        vertexBuffer.rewind();
        normalBuffer.rewind();

        gl.glGenBuffers(1, VBOVertices, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, pointCount * 3 * GLBuffers.SIZEOF_FLOAT, vertexBuffer, GL2.GL_STATIC_DRAW);

        gl.glGenBuffers(1, VBONormals, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBONormals[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, normals.size() * 3 * GLBuffers.SIZEOF_FLOAT, normalBuffer, GL2.GL_STATIC_DRAW);

        vertexBuffer = null;
        normalBuffer = null;
    }

    public double getDistanceTo(Vector3d point) {
        double deltaX = midPoint.x - point.x;
        double deltaZ = midPoint.z - point.z;

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        return distance;
    }

    private int [] drawDistances = {2048, 4096, 8192, 16384, 16384 * 3};

    public void render(GL2 gl, FrustumCuller culler, Vector3d cameraPosition) {

        int index = 0;

        double distance = getDistanceTo(cameraPosition);

        if(distance > drawDistances[drawDistances.length - 1]) {
            return;
        }

        for (index = 0; index < drawDistances.length; index++) {

            if (distance <= drawDistances[index]) {
                break;
            }

            if(index == drawDistances.length - 1) {
                return;
            }
        }

        switch(index){
            case 0:
                gl.glColor3f(1, 1, 1);
                break;
            case 1:
                gl.glColor3f(1, 0, 0);
                break;
            case 2:
                gl.glColor3f(0, 1, 0);
                break;
            case 3:
                gl.glColor3f(0, 0, 1);
                break;
            case 4:
                gl.glColor3f(1, 1, 0);
                break;
            case 5:
                gl.glColor3f(1, 0, 1);
                break;
        }

        GLU glu = new GLU();

        int buffer = TerrainPatch.indexBuffers[index];
        int bufferSize = TerrainPatch.bufferSizes[index];

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);

        gl.glBindBuffer(GL2.GL_NORMAL_ARRAY, VBONormals[0]);
        gl.glNormalPointer(GL2.GL_FLOAT, 0, 0);

        //gl.glDrawArrays(GL2.GL_TRIANGLES, 0, pointCount / 3);

        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, buffer);
        gl.glDrawElements(GL2.GL_TRIANGLES, bufferSize, GL2.GL_UNSIGNED_INT, 0);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);

        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
}
