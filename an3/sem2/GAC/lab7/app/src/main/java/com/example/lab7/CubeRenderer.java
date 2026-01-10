package com.example.lab7;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLSurfaceView;

import java.io.*;

import java.lang.Math;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CubeRenderer implements GLSurfaceView.Renderer{

    private Cube mCube;
    private float mTransY;
    private float mAngle;

    public CubeRenderer() {
        mCube = new Cube();
    }

    public void onDrawFrame(GL10 gl) {
//        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        // translate up and down
//        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -7.0f);
//        mTransY += 0.075f; // between -1 and +1
//        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
//        gl.glRotatef(mAngle, 1.0f, 0.0f, 0.0f);
//        mAngle += 0.4;
//        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//        mCube.draw(gl);

        // added

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT |
                GL10.GL_STENCIL_BUFFER_BIT);
        gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
        renderToStencil(gl);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glPushMatrix();
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glDisable(GL10.GL_DEPTH_TEST);

        gl.glTranslatef(0.0f,((float)(Math.sin(-mTransY)/2.0f)-2.5f),-8.0f);
        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        gl.glScalef(1.0f, -1.0f, 1.0f);
        gl.glFrontFace(GL10.GL_CW);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);

        mCube.draw(gl);
        gl.glDisable(GL10.GL_BLEND);
        gl.glPopMatrix();
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_STENCIL_TEST);
        gl.glPushMatrix();

        gl.glScalef(1.0f, 1.0f, 1.0f);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glTranslatef(0.0f,(float)(1.5f*(Math.sin(mTransY)/2.0f)+2.0f),-8.0f);
        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        mCube.draw(gl);
        gl.glPopMatrix();
        mTransY+=0.075f;
        mAngle+=0.4f;
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
        //gl.glEnable(GL10.GL_CULL_FACE);
        //gl.glCullFace(GL10.GL_FRONT); // assignment
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
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
    public void renderToStencil(GL10 gl) {
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glStencilFunc(GL10.GL_ALWAYS,1, 0xFFFFFFFF);
        gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
        renderStage(gl);
        gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xFFFFFFFF);
        gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP,GL10.GL_KEEP);
    }

    public void renderStage(GL10 gl) {
        float[] flatSquareVertices =
                {
                        -1.0f, 0.0f, -1.0f,
                        1.0f, 0.0f, -1.0f,
                        -1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f
                };
        float[] flatSquareColors =
                {
                        1.0f, 0.0f, 0.0f, 0.5f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.5f, 0.0f, 0.0f, 0.5f
                };

        gl.glFrontFace(GL10.GL_CW);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f,-2.0f,-8.0f);
        gl.glScalef(2.5f,1.5f,2.0f);
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0,makeFloatBuffer(flatSquareVertices));
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL11.GL_FLOAT, 0,makeFloatBuffer(flatSquareColors));
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDepthMask(false);
        gl.glColorMask(true,false,false,true);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0, 4);

        gl.glColorMask(true,true,true,true);
        gl.glDepthMask(true);
        gl.glPopMatrix();
    }
}