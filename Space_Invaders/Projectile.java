package Space_Invaders;

import com.jogamp.opengl.GL2;

public class Projectile extends GraphicalObject{
    private float speed;

    public Projectile(float pX, float pY, float pZ, float speed) {
        super(pX, pY, pZ, 0, 0, 0, 1, 1, 1, 1);
        this.speed = speed;
    }

    public void move() {
        this.translate(0, speed, 0);
    }

    @Override
    public void display_normalized(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.05f, 0.1f, 0f);
        gl.glVertex3f(0.05f, 0.1f, 0f);
        gl.glVertex3f(0.05f, -0.1f, 0f);
        gl.glVertex3f(-0.05f, -0.1f, 0f);
        gl.glEnd();
    }
}
