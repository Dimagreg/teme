package com.example.test1_ex1;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;
import java.lang.Math;

public class Renderer implements GLSurfaceView.Renderer {

    private Square mSquare;

    private Hexagon mHexagon1;

    private Hexagon mHexagon2;

    private float mTransY;
    private float mTransY2;

    byte maxColor = (byte) 255;

    byte colors[] = {
            maxColor, maxColor, 0 ,(byte) 200,
            0, maxColor, maxColor, (byte) 200,
            0, 0, 0, (byte) 200,
            maxColor, 0, maxColor, (byte) 200
    };

    float vertices_square[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    byte indices_square[] = {
            0, 3, 1,
            0, 2, 3
    };

    float vertices_hexagon[] = {
            -0.5f, -0.86f,
            -1.0f, 0.0f,
            -0.5f, 0.86f,
            0.5f, 0.86f,
            1.0f, 0.0f,
            0.5f, -0.86f
    };

    byte indices_hexagon[] = {
            1, 2, 0,
            2, 0, 3,
            0, 3, 5,
            3, 5, 4
    };

    public Renderer() {
        mSquare = new Square(vertices_square, colors, indices_square);
        mHexagon1 = new Hexagon(vertices_hexagon, colors, indices_hexagon);
        mHexagon2 = new Hexagon(vertices_hexagon, colors, indices_hexagon);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDisable(GL10.GL_DEPTH_TEST);

        // Hexagon 1
        gl.glPushMatrix(); //save current transformation matrix
        gl.glTranslatef((float)(Math.sin(mTransY2)), 0.0f, -2.6f); //apply only for current object
        mHexagon1.draw(gl, indices_hexagon.length);
        gl.glPopMatrix(); // restore matrix

        // Hexagon 2
        gl.glPushMatrix(); //save current transformation matrix
        gl.glTranslatef((float)(Math.sin(mTransY)), 0.0f, -2.9f); //apply only for current object
        mHexagon2.draw(gl, indices_hexagon.length);
        gl.glPopMatrix(); // restore matrix

        // Square
        gl.glPushMatrix(); //save current transformation matrix
        gl.glTranslatef(0.0f, (float)(Math.sin(mTransY)), -3.0f); //apply only for current object
        mSquare.draw(gl, indices_square.length);
        gl.glPopMatrix(); // restore matrix

        gl.glEnable(GL10.GL_DEPTH_TEST);

        mTransY += 0.075f;
        mTransY2 += 0.1f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float ratio = (float) width / height;
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0,0,0,0);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // 🔽 ENABLE BLENDING (for transparency)
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }
}
