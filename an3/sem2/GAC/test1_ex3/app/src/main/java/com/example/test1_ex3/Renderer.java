package com.example.test1_ex3;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
public class Renderer implements GLSurfaceView.Renderer {

    private Tetrahedron tetrahedron;

    public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;

    private float mTransY;
    private float mAngle;
    public Renderer() {
        float vertices[] = {
                -1f, -1f, -1f,
                0f, 1f, -1f,
                1f, -1f, -1f,
                0f, 0f, 1f
        };

        byte colors[] = {
                (byte) 0.7f * 255, 0, 0, 0,
                (byte) 0.7f * 255, 0, 0, 0,
                (byte) 0.8f * 255, (byte) 0.4f * 255, (byte) 0.5 * 255, 0,
                (byte) 0.8f * 255, (byte) 0.4f * 255, (byte) 0.5 * 255, 0
        };

        byte indices[] = {
                0, 1, 2,
                0, 3, 1,
                1, 3, 2,
                0, 2, 3
        };

        float[] normals =
        {
                -1.0f/(float)Math.sqrt(3), -1.0f/(float)Math.sqrt(3),
                -1.0f/(float)Math.sqrt(3), 0.0f/(float)Math.sqrt(2),
                1.0f/(float)Math.sqrt(2), -1.0f/(float)Math.sqrt(2),
                1.0f/(float)Math.sqrt(3), -1.0f/(float)Math.sqrt(3),
                -1.0f/(float)Math.sqrt(3), 0.0f, 0.0f, 1.0f
        };

        tetrahedron = new Tetrahedron(vertices, colors, indices, normals);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, (float) Math.cos(mTransY), -7.0f);
        mTransY += 0.015f;

        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        mAngle += 0.7f;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        tetrahedron.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float fieldOfView = 30.0f/57.3f;
        float aspectRatio = (float)width/(float)height;
        float zNear = 0.1f;
        float zFar = 1500;
        float size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
        gl.glFrustumf(-size, size, -size/aspectRatio, size/aspectRatio, zNear, zFar);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDepthMask(false);
        initLighting(gl);

        gl.glDisable(GL10.GL_DITHER);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(1f,1f,1f,1f);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
//        gl.glEnable(GL10.GL_DEPTH_TEST);

    }

    public void initLighting(GL10 gl) {
        float lightPosition[] = {0.0f, 2.0f, 1.0f, 1.0f};
        float lightAmbientCol[] = {1.0f, 1.0f, 0.0f, 1.0f,};

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, makeFloatBuffer(lightPosition));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, makeFloatBuffer(lightAmbientCol));

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_SMOOTH);
    }

    protected static FloatBuffer makeFloatBuffer(float[] array)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }
}
