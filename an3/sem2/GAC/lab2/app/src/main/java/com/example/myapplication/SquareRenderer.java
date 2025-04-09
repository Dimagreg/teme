package com.example.myapplication;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;
import java.lang.Math;
public class SquareRenderer implements GLSurfaceView.Renderer {

    private Square redSquare;
    private Square greenSquare;

    private Square greenTriangle;

    private Square greenHexagon;
    private float mTransY;
    public SquareRenderer() {
        float red_vertices[] =
                {
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f
                };

        float green_vertices[] =
                {
                        -1.25f, -1.25f,
                        0.75f, -1.25f,
                        -1.25f, 0.75f,
                        0.75f, 0.75f
                };

        float vertices_triangle[] =
                {
                        -0.25f, -0.25f,
                        0.25f, -0.25f,
                        0f, 0.25f
                };

        float vertices_hexagon[] =
                {
                        0f, 1f, //0
                        0.25f, 1f, //1
                        0.45f, 0.75f, //2
                        0.25f, 0.5f, //3
                        0f, 0.5f, //4
                        -0.20f, 0.75f, //5
                };

        byte maxColor = (byte) 255;
        byte red_colors[] =
                {
                        maxColor, 0, 0, maxColor,
                        maxColor, 0, 0, maxColor,
                        maxColor, 0, 0, maxColor,
                        maxColor, 0, 0, maxColor,
                };

        byte green_colors[] =
                {
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                };
        byte indices[] =
                {
                        0, 3, 1,
                        0, 2, 3
                };

        byte indices_triangle[] =
                {
                        0, 2, 1
                };

        byte indices_hexagon[] =
                {
                        0, 1, 2,
                        1, 2, 3,
                        1, 3, 4,
                        2, 4, 5,
                        0, 2, 5
                };

        byte hexagon_colors[] =
                {
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                        0, maxColor, 0, maxColor,
                };

        redSquare = new Square(red_vertices, red_colors, indices);
        greenSquare = new Square(green_vertices, green_colors, indices);
        greenTriangle = new Square(vertices_triangle, green_colors, indices_triangle);
        greenHexagon = new Square(vertices_hexagon, hexagon_colors, indices_hexagon);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadIdentity();

        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);
//        mTransY += 0.075f;

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

//        greenTriangle.draw(gl, 3);
//        redSquare.draw(gl, 6);
//        greenSquare.draw(gl, 6);
        greenHexagon.draw(gl, 15);
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
    }
}
