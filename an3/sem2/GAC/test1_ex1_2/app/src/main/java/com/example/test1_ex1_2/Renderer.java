package com.example.test1_ex1_2;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;
public class Renderer implements GLSurfaceView.Renderer {

    private Circle circle_yellow;
    private Circle circle_blue1;
    private Circle circle_blue2;

    private float mTransY;

    public Renderer() {

        int nrSides = 360;

        float r = 2f;

        float vertices_yellow[] = new float[2 * nrSides];
        float vertices_blue1[] = new float[2 * nrSides];
        float vertices_blue2[] = new float[2 * nrSides];

        byte colors_yellow[] = new byte[4 * nrSides]; //(r,g,b,a)
        byte colors_blue[] = new byte[4 * nrSides];

        byte indices[] = new byte[39 * 360];

        for (int i = 0; i < (2 * nrSides); i+= 2) {

            //yellow
            float x1 = (r * (float) Math.cos(Math.toRadians(i)));
            float y1 = (r * (float) Math.sin(Math.toRadians(i)));

            //blue1
            float x2 = ((float) Math.cos(Math.toRadians(i)));
            float y2 = (3.5f + (float) Math.sin(Math.toRadians(i)));

            //blue2, x3 same as x2
            float y3 = (-3.5f + (float) Math.sin(Math.toRadians(i)));

            vertices_yellow[i] = x1;
            vertices_yellow[i + 1] = y1;

            vertices_blue1[i] = x2;
            vertices_blue1[i + 1] = y2;

            vertices_blue2[i] = x2;
            vertices_blue2[i + 1] = y3;
        }

        for (int i = 0; i < (4 * nrSides); i += 4) {
            colors_blue[i] = (byte) (0.49f * 255);
            colors_blue[i + 1] = (byte) (0.937f * 255);
            colors_blue[i + 2] = (byte) 255;
            colors_blue[i + 3] = (byte) 255;

            colors_yellow[i] = (byte) 255;
            colors_yellow[i + 1] = (byte) 255;
            colors_yellow[i + 2] = (byte) 0;
            colors_yellow[i + 3] = (byte) 255;
        }

        byte indicesA = 1;
        byte indicesB = 2;
        for (int i = 0; i < (39 * nrSides); i+=3) {
            indices[i] = (byte) 0;
            indices[i + 1] = indicesA++;
            indices[i + 2] = indicesB++;
        }

        circle_yellow = new Circle(vertices_yellow, colors_yellow, indices);
        circle_blue1 = new Circle(vertices_blue1, colors_blue, indices);
        circle_blue2 = new Circle(vertices_blue2, colors_blue, indices);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -5.0f);
        mTransY += 0.15f;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        circle_yellow.draw(gl);
        circle_blue1.draw(gl);
        circle_blue2.draw(gl);
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

        gl.glClearColor(0.894f,1,0.49f,1);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }
}
