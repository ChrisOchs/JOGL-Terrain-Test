/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jogltest;

import com.jogamp.opengl.util.Animator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;
import jogltest.util.FrustumCuller;
import jogltest.util.RenderRegion;
import jogltest.util.TerrainGenerator;
import jogltest.util.Vector3d;

/**
 *
 * @author Den
 */
public class PlanetTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PlanetTest main = new PlanetTest();
    }

    private Animator animator;

    public PlanetTest() {
        JFrame frame = new JFrame("Planet Test");
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

                int CAMERA_SPEED = 8;

                if(e.isShiftDown()) {
                    CAMERA_SPEED = 256;
                }

                if(e.getKeyCode() == KeyEvent.VK_W) {
                    camera.x += (double)(CAMERA_SPEED * Math.sin(Math.toRadians(cameraTheta)));
                    camera.z += (double)(CAMERA_SPEED * Math.cos(Math.toRadians(cameraTheta)));
                } else if(e.getKeyCode() == KeyEvent.VK_S) {
                    camera.x -= (double)(CAMERA_SPEED * Math.sin(Math.toRadians(cameraTheta)));
                    camera.z -= (double)(CAMERA_SPEED * Math.cos(Math.toRadians(cameraTheta)));
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    cameraTheta += 3;
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    cameraTheta -= 3;
                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                    camera.y += CAMERA_SPEED;
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    camera.y -= CAMERA_SPEED;
                }
            }
        });

        initializePlanet();

        frame.setVisible(true);
    }

    private void initializePlanet() {
        TerrainGenerator.setHeightMap(loadImageToHeightMap("terrain.png"));
    }

    private int[][] loadImageToHeightMap(String fileName) {
        try {
            BufferedImage image = ImageIO.read(new File(fileName));

            int[][] height = new int[image.getHeight()][image.getWidth()];

            for (int r = 0; r < height.length; r++) {
                for (int c = 0; c < height[0].length; c++) {
                    int rgb = image.getRGB(c, r);

                    int red = ((rgb >> 16) & 0xff);
                    int green = ((rgb >> 8) & 0xff);
                    int blue = ((rgb) & 0xff);

                    height[r][c] = ((red + blue + green) / 3) * 64;
                }
            }

            return height;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int cameraTheta = 0;

    private Vector3d camera;

    private TerrainPatch[][] patches = new TerrainPatch[4][4];

    private class MyRenderer implements GLEventListener {
        private GLU glu = new GLU();

        private FrustumCuller culler = new FrustumCuller();

        public void display(GLAutoDrawable glDrawable) {
            final GL2 gl = glDrawable.getGL().getGL2();

            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(60.0, 1.0, 1.0, 16384 * 3);
            glu.gluLookAt(camera.x, camera.y, camera.z,
                    camera.x + (double)(128 * Math.sin(Math.toRadians(cameraTheta))),
                    camera.y,
                    camera.z + (double)(128 * Math.cos(Math.toRadians(cameraTheta))),
                    0, 1, 0);

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();

            //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

            float [] matAmbient = {0.5f, 0.5f, 0.5f, 1f};
            float [] matSpecular = {1f, 1f, 1f, 1f};

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecular, 0);

            culler.initializeFrustum(gl);

            for(int r = 0; r < patches.length; r++) {
                for(int c = 0; c < patches[r].length; c++) {
                    patches[r][c].render(gl, culler, camera);
                }
            }

            GLUquadric quadric = glu.gluNewQuadric();

            gl.glPushMatrix();

            gl.glTranslated(100, 2400, 100);

            glu.gluSphere(quadric, 100, 16, 16);

            gl.glPopMatrix();


            gl.glFlush();
        }

        public void init(GLAutoDrawable glDrawable) {
            GL2 gl = glDrawable.getGL().getGL2();

            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glShadeModel(GL2.GL_SMOOTH);
            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDepthFunc(GL2.GL_LEQUAL);
            gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

            gl.glCullFace(GL2.GL_BACK);
            gl.glEnable(GL2.GL_CULL_FACE);

            float [] globalAmbient = {.2f, .2f, .2f, 1f};

            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmbient, 0);

            float [] diffuse = {0.7f, 0.7f, 0.7f, 1};
            float [] ambient = {.2f, .2f, .2f, 1};
            float [] specular = {1, 1, 1, 1};

            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular, 0);

            gl.glEnable(GL2.GL_NORMALIZE);

            gl.glEnable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_LIGHTING);

            float [] lightPos = {2000, 2500, 2000, 0};
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);

            System.out.println("GENERATING TERRAIN");
            for (int r = 0; r < patches.length; r++) {
                for (int c = 0; c < patches[r].length; c++) {
                    RenderRegion region = new RenderRegion(c * 16, r * 16, 16, 16, 0);
                    patches[r][c] = new TerrainPatch(gl, region);
                }
            }

            int height = TerrainGenerator.getHeightAt(0, 0);
            camera = new Vector3d(0, height + 64, 0);
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
