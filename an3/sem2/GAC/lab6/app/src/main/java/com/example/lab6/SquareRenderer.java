package com.example.lab6;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;

import java.io.*;

import java.lang.Math;
import android.content.Context;

public class SquareRenderer implements GLSurfaceView.Renderer{

    private Square mSquare;
    private float mTransY;
    private final Context context;

    public SquareRenderer(Context context){
        this.context=context;
        mSquare = new Square();
    }

    /*public void onDrawFrame(GL10 gl){
        // added
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //added
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColorMask(true, true, false, true); // modified
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);

        //added
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        //gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
        mSquare.draw(gl);

        gl.glLoadIdentity();
        gl.glTranslatef((float)(Math.sin(mTransY)/2.0f),0.0f, -2.9f);
        gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f); // modified colors
        //gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
        mSquare.draw2(gl);
        mTransY += .075f;
    }*/

    public void onDrawFrame(GL10 gl){
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        // translate up and down
        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);
        mTransY += 0.075f; // between -1 and +1
        //gl.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        mSquare.draw(gl);
    }

    public void  onSurfaceChanged(GL10 gl, int width, int height){
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float ratio = (float) width / height;
        // left, right, bottom, top, near far
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config){
        gl.glEnable(GL10.GL_DITHER); // Enable/Disable
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(1,1,1,1); // change background color
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // added
        int resid = R.drawable.hedly;
        mSquare.createTexture(gl, this.context, resid);
    }
}
