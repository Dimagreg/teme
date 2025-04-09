package com.example.test1_ex2;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;
public class Renderer implements GLSurfaceView.Renderer {

    private Pyramid pyramid;

    private Circle circle;

    float vertices_pyramid[] = {
            0f, 1f, 0f,
            -1f, -1f, 1f,
            1f, -1f, 1f,
            0f, 1f, 0f,
            1f, -1f, 1f,
            1f, -1f, -1f,
            0f, 1f, 0f,
            1f, -1f, -1f,
            -1f, -1f, -1f,
            0f, 1f, 0f,
            -1f, -1f, -1f,
            -1f, -1f, 1f,
            -1f, -1f, 1f,
            1f, -1f, 1f,
            -1f, -1f, -1f,
            -1f, -1f, -1f,
            1f, -1f, -1f,
            1f, -1f, 1f
    };

    byte colors_pyramid[] = {
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255,
            (byte) 255, 0, 0, (byte) 255
    };

    byte indices_pyramid[] = {
            0, 1, 2,
            3, 4, 5,
            6, 7, 8,
            9, 10, 11,
            12, 13, 14,
            15, 16, 17
    };

    int sides = 360;
    float r = 1.2f;
    float vertices_circle[] = new float[2 * sides];
    byte colors_circle[] = new byte[4 * sides];
    byte indices_circle[] = new byte[39 * sides];
    private float mTransY;
    private float mAngle;

    public Renderer() {
        for (int i = 0; i < (2 * sides); i += 2) {
            float x = r * (float) Math.cos(Math.toRadians(i));
            float y = 2.2f + r * (float) Math.sin(Math.toRadians(i));

            vertices_circle[i] = x;
            vertices_circle[i + 1] = y;
        }

        for (int i = 0; i < (4 * sides); i += 4) {
            colors_circle[i] = (byte) 255;
            colors_circle[i + 1] = (byte) 255;
            colors_circle[i + 2] = (byte) 0;
            colors_circle[i + 3] = (byte) 255;
        }

        byte A = 1;
        byte B = 2;
        for (int i = 0; i < (39 * sides); i += 3) {
            indices_circle[i] = 0;
            indices_circle[i + 1] = A++;
            indices_circle[i + 2] = B++;
        }

        pyramid = new Pyramid(vertices_pyramid, colors_pyramid, indices_pyramid);
        circle = new Circle(vertices_circle, colors_circle, indices_circle);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -7.0f);
        mTransY += 0.075f;

        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        mAngle += 0.4f;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        pyramid.draw(gl);
        circle.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float fieldOfView = 30.0f/57.3f;
        float aspectRatio = (float)width/(float)height;
        float zNear = 0.1f;
        float zFar = 1000;
        float size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
        gl.glFrustumf(-size, size, -size/aspectRatio, size/aspectRatio, zNear, zFar);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0.894f,1f,0.49f,1f);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }
}
