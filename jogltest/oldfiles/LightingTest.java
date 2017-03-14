/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest.oldfiles;

import com.jogamp.opengl.util.Animator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import jogltest.util.Quad;
import jogltest.util.Vector3d;

/**
 *
 * @author Den
 */
public class LightingTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LightingTest main = new LightingTest();
    }

    private Animator animator;

    public LightingTest() {
        JFrame frame = new JFrame("Lighting Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glCapabilities = new GLCapabilities(profile);
        GLCanvas glCanvas = new GLCanvas(glCapabilities);

        glCanvas.addGLEventListener(new MyRenderer());

        frame.setSize(1024, 1024);

        animator = new Animator(glCanvas);

        new Thread(new Runnable() {
            public void run() {
                animator.start();
            }
        }).start();

        frame.add(glCanvas);

        glCanvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_W) {
                    camera.x += (double)(2 * Math.sin(Math.toRadians(thetaCamera)));
                    camera.z += (double)(2 * Math.cos(Math.toRadians(thetaCamera)));
                } else if(e.getKeyCode() == KeyEvent.VK_S) {
                    camera.x -= (double)(2 * Math.sin(Math.toRadians(thetaCamera)));
                    camera.z -= (double)(2 * Math.cos(Math.toRadians(thetaCamera)));
                } else if(e.getKeyCode() == KeyEvent.VK_A) {
                    thetaCamera += 3;
                } else if(e.getKeyCode() == KeyEvent.VK_D) {
                    thetaCamera -= 3;
                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                    camera.y += 2;
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    camera.y -= 2;
                }
            }
        });

        initializeTerrain(loadImageToHeightMap("terrain.png"));

        frame.setVisible(true);
    }

    private int [][] loadImageToHeightMap(String fileName) {
        try {
            BufferedImage image = ImageIO.read(new File(fileName));

            int [][] height = new int[image.getHeight()][image.getWidth()];

            for(int r = 0; r < height.length; r++) {
                for(int c = 0; c < height[0].length; c++) {
                    int rgb = image.getRGB(c, r);

                    int red = ((rgb >> 16) & 0xff);
                    int green = ((rgb >> 8) & 0xff);
                    int blue = ((rgb) & 0xff);

                    height[r][c] = (red + blue + green) / 64;
                }
            }

            return height;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<Quad> terrainQuads;

    private void initializeTerrain(int [][] heights) {

        terrainQuads = new ArrayList<Quad>();

        for (int x = 0; x < heights.length - 1; x++) {
            for (int z = 0; z < heights[0].length - 1; z++) {
                Vector3d point1 = new Vector3d(x * STEP_SIZE, heights[x][z], z * STEP_SIZE);
                Vector3d point2 = new Vector3d(x * STEP_SIZE, heights[x][z + 1], (z + 1) * STEP_SIZE);
                Vector3d point3 = new Vector3d((x + 1) * STEP_SIZE, heights[x + 1][z + 1], (z + 1) * STEP_SIZE);
                Vector3d point4 = new Vector3d((x + 1) * STEP_SIZE, heights[x + 1][z], z * STEP_SIZE);

                terrainQuads.add(new Quad(point1, point2, point3, point4));
            }
        }
    }

    private Vector3d camera = new Vector3d(0, 64, 0);

    private int thetaCamera = 0;

    private final double STEP_SIZE = 0.2;

    private class MyRenderer implements GLEventListener {
        private GLU glu = new GLU();

        public void display(GLAutoDrawable glDrawable) {
            final GL2 gl = glDrawable.getGL().getGL2();

            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0, 1.0, 1.0, 1024.0);
            glu.gluLookAt(camera.x, camera.y, camera.z, camera.x + (double)(128 * Math.sin(Math.toRadians(thetaCamera))), 0, camera.z + (double)(128 * Math.cos(Math.toRadians(thetaCamera))), 0, 1, 0);


            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();

            //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

            float [] matAmbient = {0.2f, 0.2f, 0.2f, 0.0f};
            float [] matDiffuse = {0.5f, 0.5f, 0.5f, 0.0f};
            float [] matSpecular = {0.2f, 0.2f, 0.2f, 0.0f};
            float [] matShininess = {0.5f};

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess, 0);

            gl.glColor3d(1, 1, 1);

            gl.glBegin(GL2.GL_QUADS);

            for (Quad quad : terrainQuads) {
                gl.glNormal3d(quad.normal.x, quad.normal.y, quad.normal.z);

                gl.glVertex3d(quad.point1.x, quad.point1.y, quad.point1.z);
                gl.glVertex3d(quad.point2.x, quad.point2.y, quad.point2.z);
                gl.glVertex3d(quad.point3.x, quad.point3.y, quad.point3.z);
                gl.glVertex3d(quad.point4.x, quad.point4.y, quad.point4.z);
            }

            gl.glEnd();


            gl.glFlush();
        }

        public void init(GLAutoDrawable glDrawable) {
            GL2 gl = glDrawable.getGL().getGL2();

            gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
            gl.glShadeModel(GL2.GL_FLAT);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDepthFunc(GL2.GL_LEQUAL);
            gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

            float[] lightPos = {100f, 0f, -10f, 1f};
            float[] lightAmbient = {0.1f, 0.1f, 0.1f, 0.1f};
            float[] lightDiffuse = {1f, 1f, 1f, 0f};
            float[] lightSpecular = {1f, 1f, 1f, 0f};

            gl.glLightfv(gl.GL_LIGHT0, GL2.GL_AMBIENT, new float[] {0.5f, 0.5f, 0.5f, 0f}, 0);

            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightSpecular, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);

            gl.glEnable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_LIGHT1);
            gl.glEnable(GL2.GL_LIGHTING);

        }

        public void displayChanged(GLAutoDrawable glDrawable,
                boolean modeChanged,
                boolean deviceChanged) {


        }

        public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
            final GL2 gl = glDrawable.getGL().getGL2();

            if(height <= 0) {
                height = 0;
            }

            float h = (float) width / (float) height;

            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(45.0f, h, 1.0, 20.0);

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

        public void dispose(GLAutoDrawable glDrawable) {

        }
    }
}
