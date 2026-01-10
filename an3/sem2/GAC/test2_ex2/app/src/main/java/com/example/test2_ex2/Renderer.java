package com.example.test2_ex2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
public class Renderer implements GLSurfaceView.Renderer {

    private Cube mCube;

    float vertices[] =
            {
                    -1.0f, -1.0f, 1.0f, //0
                    1.0f, -1.0f, 1.0f, //1
                    -1.0f, 1.0f, 1.0f, //2
                    1.0f, 1.0f, 1.0f, //3
                    1.0f, -1.0f, 1.0f, //4
                    1.0f, -1.0f, -1.0f,//5
                    1.0f, 1.0f, 1.0f,//6
                    1.0f, 1.0f, -1.0f,//7
                    1.0f, -1.0f, -1.0f,//8
                    -1.0f, -1.0f, -1.0f,//9
                    1.0f, 1.0f, -1.0f,//10
                    -1.0f, 1.0f, -1.0f,//11
                    -1.0f, -1.0f, -1.0f,//12
                    -1.0f, -1.0f, 1.0f,//13
                    -1.0f, 1.0f, -1.0f,//14
                    -1.0f, 1.0f, 1.0f,//15
                    -1.0f, -1.0f, -1.0f,//16
                    1.0f, -1.0f, -1.0f,//17
                    -1.0f, -1.0f, 1.0f,//18
                    1.0f, -1.0f, 1.0f,//19
                    -1.0f, 1.0f, 1.0f,//20
                    1.0f, 1.0f, 1.0f,//21
                    -1.0f, 1.0f, -1.0f,//22
                    1.0f, 1.0f, -1.0f,//23
            };

    byte maxColor=(byte)255;
    byte colors[] =
            {
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
                    maxColor, maxColor, maxColor, maxColor,
            };

    byte indices[] =
            {
                 0, 1, 3,
                 0, 3, 2,
                 4, 5, 7,
                 4, 7, 6,
                 8, 9, 11,
                 8, 11, 10,
                 12, 13, 15,
                 12, 15, 14,
                 16, 17, 19,
                 16, 19, 18,
                 20, 21, 23,
                 20, 22, 19,
            };

    float[] textureCoords = new float[] {
            // Front face (4 vertices)
            0.0f, 1.0f,  // Vertex 0: Bottom-left
            1.0f, 1.0f,  // Vertex 1: Bottom-right
            1.0f, 0.0f,  // Vertex 3: Top-right
            0.0f, 0.0f,  // Vertex 2: Top-left

            // Right face (4 vertices)
            0.0f, 1.0f,  // Vertex 4: Bottom-left
            1.0f, 1.0f,  // Vertex 5: Bottom-right
            1.0f, 0.0f,  // Vertex 7: Top-right
            0.0f, 0.0f,  // Vertex 6: Top-left

            // Back face (4 vertices)
            0.0f, 1.0f,  // Vertex 8: Bottom-left
            1.0f, 1.0f,  // Vertex 9: Bottom-right
            1.0f, 0.0f,  // Vertex 11: Top-right
            0.0f, 0.0f,  // Vertex 10: Top-left

            // Left face (4 vertices)
            0.0f, 1.0f,  // Vertex 12: Bottom-left
            1.0f, 1.0f,  // Vertex 13: Bottom-right
            1.0f, 0.0f,  // Vertex 15: Top-right
            0.0f, 0.0f,  // Vertex 14: Top-left

            // Bottom face (4 vertices)
            0.0f, 1.0f,  // Vertex 16: Bottom-left
            1.0f, 1.0f,  // Vertex 17: Bottom-right
            1.0f, 0.0f,  // Vertex 19: Top-right
            0.0f, 0.0f,  // Vertex 18: Top-left

            // Top face (4 vertices)
            0.0f, 1.0f,  // Vertex 20: Bottom-left
            1.0f, 1.0f,  // Vertex 21: Bottom-right
            1.0f, 0.0f,  // Vertex 23: Top-right
            0.0f, 0.0f   // Vertex 22: Top-left
    };

    private float mTransY;
    private float mAngle;

    private Context context;
    public Renderer(Context context) {
        this.context = context;
        mCube = new Cube(vertices, colors, indices, textureCoords);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -7.0f);
        mTransY += 0.075f;

        gl.glRotatef(mAngle, 1.0f, 0.0f, 0.0f);
        mAngle += 0.7f;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCube.draw(gl);
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

        gl.glClearColor(0,0,0,0);

//        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        mCube.createTexture(gl, context, new int[]{
                R.drawable.goldengate,
                R.drawable.goldengate,
                R.drawable.goldengate,
                R.drawable.goldengate,
                R.drawable.goldengate,
                R.drawable.goldengate
        });
    }
}
