package com.example.lab3;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;

import java.lang.Math;
public class CubeRenderer implements GLSurfaceView.Renderer {

    private Cube mCube;

    private float mAngle;
    private float mTransY;

    public CubeRenderer() {
        mCube = new Cube();
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        // translate up and down
        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -7.0f);
//        mTransY += 0.075f; // between -1 and +1
        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(mAngle, 1.0f, 0.0f, 0.0f);
        mAngle += 0.4;
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        mCube.draw(gl);
    }

    public void  onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        // 1 radian = 57.3 degrees
        float fieldOfView = 30.0f/57.3f;
        float aspectRatio = (float)width/(float)height;
        float zNear = 0.1f; //0.1f;
        float zFar = 1000; // 6;
        float size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
        gl.glFrustumf(-size, size, -size/aspectRatio, size/aspectRatio, zNear, zFar);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DITHER); // Enable/Disable
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(1,1,1,1); // change background color
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_FRONT); // assignment
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }
}
