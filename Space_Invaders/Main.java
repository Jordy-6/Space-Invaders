package Space_Invaders;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;

public class Main extends GLCanvas
        implements GLEventListener
{
    private ArrayList<Cube> objects3D;
    private Cube playerCube;
    private float angle;
    private String movementState = "RIGHT"; // Initialize movementState to "RIGHT" as an instance variable
    private float descentDistance = 0;// Initialize descentDistance to 0 as an instance variable

    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    public static void main(String[] args)
    {
        GLCanvas canvas = new Main();
        canvas.setPreferredSize(new Dimension(800, 600));
        final JFrame frame = new JFrame();
        frame.getContentPane().add(canvas);
        frame.setTitle("OpenGL #1");
        frame.pack();
        frame.setVisible(true);
        Animator animator = new Animator(canvas);
        animator.start();
    }

    public Main() {
        this.addGLEventListener(this);
        this.objects3D = new ArrayList<Cube>();
        this.angle = 0.0f;
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        playerCube.translate(-0.2f, 0, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        playerCube.translate(0.2f, 0, 0);
                        break;
                    case KeyEvent.VK_SPACE:
                        projectiles.add(new Projectile(playerCube.getPosX(), playerCube.getPosY() + 0.1f, playerCube.getPosZ(), 0.1f));
                        break;
                }
            }
        });
        this.setFocusable(true);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        // Draw objects ???
        gl.glPushMatrix();

            float dy = -0.008f;

            for (GraphicalObject obj: objects3D){
                obj.display(gl);
                obj.translate(0, dy, 0);
            }

            this.playerCube.display(gl);
            for (Projectile projectile : projectiles) {
                projectile.move();
                projectile.display(gl);
            }



            for (Projectile projectile : new ArrayList<>(projectiles)) { // Create a new list to avoid ConcurrentModificationException
                for (Cube cube : new ArrayList<>(objects3D)) { // Create a new list to avoid ConcurrentModificationException
                     if (isColliding(projectile, cube)) {
                        projectiles.remove(projectile);
                        objects3D.remove(cube);
                        break; // Break to avoid checking this projectile against other cubes
                    }

                }
            }

            for (Cube cube : new ArrayList<>(objects3D)) { // Create a new list to avoid ConcurrentModificationException
                if (!isPlayerColliding(cube)) {
                    System.out.println("Game Over");
                    System.exit(0);
                }
            }
        gl.glPopMatrix();

        //

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub

    }

    private boolean isColliding(GraphicalObject obj1, GraphicalObject obj2) {
        // Simple collision detection based on distance between objects
        // You may need to adjust this based on the size of your objects
        float distance = (float) Math.sqrt(Math.pow(obj1.getPosX() - obj2.getPosX(), 2) +
                Math.pow(obj1.getPosY() - obj2.getPosY(), 2) +
                Math.pow(obj1.getPosZ() - obj2.getPosZ(), 2));
        return distance < 0.6f; // Adjust this value as needed
    }

    private boolean isPlayerColliding(Cube enemyCube) {
        // Simple collision detection based on distance between objects
        // You may need to adjust this based on the size of your objects
        float distance = (float) Math.sqrt(Math.pow(playerCube.getPosX() - enemyCube.getPosX(), 2) +
                Math.pow(playerCube.getPosY() - enemyCube.getPosY(), 2) +
                Math.pow(playerCube.getPosZ() - enemyCube.getPosZ(), 2));
        return distance >  1.4f; // Adjust this value as needed
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Color background
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        // Initialize all graphical objects
        // Initialize all graphical objects
        int numCubesX = 6;
        int numCubesY = 4;
        float spacingX = 2.6f;
        for (int i = 0; i < numCubesX; i++) {
            for (int j = 0; j < numCubesY; j++) {
                float x = -spacingX * i + spacingX * (numCubesX - 1) / 2.0f;
                float y = 7.0f + -1.8f * j;
                this.objects3D.add(new Cube(x, y, -20, 0, 0, 0, 0.4f, 0, 0, 0));
            }
        }
        this.playerCube = new Cube(0f, -7f, -20f, 0, 0, 0, 0.7f, 0, 0, 0);
        //this.objects3D.add(new Square(0f, 0f, -40f, 0f, 0f, 0f, 4f, 1.0f, 0.0f, 0.0f));
        //this.objects3D.add(new Square(0f, 0f, -10f, 0f, 0f, 0f, 0.25f, 0.0f, 1.0f, 0.0f));
    }

    @Override
    public void reshape(GLAutoDrawable drawable,
                        int x, int y, int width, int height) {
        // TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
        // Set the view area
        gl.glViewport(0, 0, width, height);
        // Setup perspective projection
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU glu = new GLU();
        glu.gluPerspective(45.0, (float)width/height,
                0.1, 100.0);
        // Enable the model view
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }



}
