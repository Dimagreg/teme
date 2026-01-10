package com.example.lab6;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.*;
import android.opengl.*;

import android.content.Context;
public class Square {

    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mColorBuffer1, mColorBuffer2;
    private ByteBuffer mIndexBuffer, mColorBuffer;
    public float squareColorsRGBA[],squareColorsYMCA[];
    private int[] textures = new int[1]; // added
    private FloatBuffer mTextureBuffer;
    public float[] textureCoords=
            {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f
            };

    public Square() {

        float vertices[] =
                {
                        -1.0f, -1.0f,
                        1.0f, -1.0f,
                        -1.0f, 1.0f,
                        1.0f, 1.0f
                };

        float squareColorsYMCA[] =
                {
                        1.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 1.0f, 1.0f
                };
        float squareColorsRGBA[] =
                {
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 1.0f
                };

        byte maxColor=(byte)255;
        byte colors[] =
                {
                        maxColor, 0, 0, 0,
                        maxColor, 0, 0, 0,
                        maxColor, 0, 0, 0,
                        maxColor, 0, 0, 0,
                };
        byte indices[] =
                {
                        0, 3, 1,
                        0, 2, 3
                };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);

        mColorBuffer = ByteBuffer.allocateDirect(colors.length);
        mColorBuffer.put(indices);
        mColorBuffer.position(0);

        ByteBuffer vbb2 = ByteBuffer.allocateDirect(squareColorsYMCA.length * 4);
        vbb2.order(ByteOrder.nativeOrder());
        mColorBuffer1 = vbb2.asFloatBuffer();
        mColorBuffer1.put(squareColorsYMCA);
        mColorBuffer1.position(0);

        ByteBuffer vbb3 = ByteBuffer.allocateDirect(squareColorsRGBA.length * 4);
        vbb3.order(ByteOrder.nativeOrder());
        mColorBuffer2 = vbb3.asFloatBuffer();
        mColorBuffer2.put(squareColorsRGBA);
        mColorBuffer2.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }

    /*public void draw(GL10 gl){
        gl.glFrontFace(GL11.GL_CW);
        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer); // 2 - 2 coordinates (x,y)
        gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer1);
        //gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer2);
        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
    }

    public void draw2(GL10 gl){
        gl.glFrontFace(GL11.GL_CW);
        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer); // 2 - 2 coordinates (x,y)
        gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer2);
        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
    }*/

    public void draw(GL10 gl){
        gl.glFrontFace(GL11.GL_CW);
        //gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer); // 2 - 2 coordinates (x,y)
        //gl.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, mColorBuffer);
        //gl.glDrawElements(GL11.GL_TRIANGLE_FAN, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        // added
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);
        //gl.glDrawElements(GL11.GL_TRIANGLE_FAN, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

        //int timePassed=10;
        //gl.glBindTexture (GL10.GL_TEXTURE_2D, textures[timePassed % 10]);
        //timePassed += 10;

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, mTextureBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDrawElements(GL11.GL_TRIANGLE_FAN, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        // animated texture-------------------------------------------------------
        /*ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);

        textureCoords[0]+=texIncrease;
        textureCoords[1]+=texIncrease;
        textureCoords[2]+=texIncrease;
        textureCoords[3]+=texIncrease;
        textureCoords[4]+=texIncrease;
        textureCoords[5]+=texIncrease;
        textureCoords[6]+=texIncrease;
        textureCoords[7]+=texIncrease;*/
        //------------------------------------------------------------------------------

        //gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, mTextureBuffer);
        //gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void createTexture(GL10 gl, Context contextRegf, int resource){
        Bitmap image = BitmapFactory.decodeResource(contextRegf.getResources(), resource);
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // this
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        image.recycle();
    }
}
