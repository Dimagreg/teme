package com.example.test2_ex2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.*;
import android.opengl.*;

public class Cube {

    private FloatBuffer mFVertexBuffer;
    private ByteBuffer mColorBuffer;
    private ByteBuffer mIndexBuffer;

    public FloatBuffer mTextureBuffer1;
    public FloatBuffer mTextureBuffer2;
    public FloatBuffer mTextureBuffer3;
    public FloatBuffer mTextureBuffer4;
    public FloatBuffer mTextureBuffer5;
    public FloatBuffer mTextureBuffer6;

    private int[] textures = new int[6];

    byte indices[];

    byte TFan1[] =
            {
                    0, 1, 3,
                    0, 3, 2,
            };

    byte TFan2[] =
            {
                    4, 7, 5,
                    4, 6, 7,
            };

    byte TFan3[] =
            {
                    8, 11, 9,
                    8, 10, 11,
            };

    byte TFan4[] =
            {
                    12, 15, 13,
                    12, 14, 15,
            };

    byte TFan5[] =
            {
                    16, 19, 17,
                    16, 18, 19,
            };

    byte TFan6[] =
            {
                    20, 23, 21,
                    20, 22, 23,
            };

    ByteBuffer mTFan1;
    ByteBuffer mTFan2;
    ByteBuffer mTFan3;
    ByteBuffer mTFan4;
    ByteBuffer mTFan5;
    ByteBuffer mTFan6;

    public Cube(float vertices[], byte colors[], byte indices[], float textureCoords[]) {

        this.indices = indices;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
        mColorBuffer = ByteBuffer.allocateDirect(colors.length);
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);


        mTFan1 = ByteBuffer.allocateDirect(TFan1.length);
        mTFan1.put(TFan1);
        mTFan1.position(0);
        mTFan2 = ByteBuffer.allocateDirect(TFan2.length);
        mTFan2.put(TFan2);
        mTFan2.position(0);
        mTFan3 = ByteBuffer.allocateDirect(TFan3.length);
        mTFan3.put(TFan3);
        mTFan3.position(0);
        mTFan4 = ByteBuffer.allocateDirect(TFan4.length);
        mTFan4.put(TFan4);
        mTFan4.position(0);
        mTFan5 = ByteBuffer.allocateDirect(TFan5.length);
        mTFan5.put(TFan5);
        mTFan5.position(0);
        mTFan6 = ByteBuffer.allocateDirect(TFan6.length);
        mTFan6.put(TFan6);
        mTFan6.position(0);

        mTextureBuffer1 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 0, 8));
        mTextureBuffer2 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 8, 16));
        mTextureBuffer3 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 16, 24));
        mTextureBuffer4 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 24, 32));
        mTextureBuffer5 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 32, 40));
        mTextureBuffer6 = makeFloatBuffer(Arrays.copyOfRange(textureCoords, 40, 48));
    }

    private FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public void draw(GL10 gl) {
        gl.glFrontFace(GL11.GL_CW);

        gl.glVertexPointer(3, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, mColorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Add this line

//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

//        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, indices.length, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

// Face 1
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer1);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan1);

// Face 2
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer2);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan2);

// Face 3
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer3);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan3);

// Face 4
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[3]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer4);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan4);

// Face 5
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[4]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer5);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan5);

// Face 6
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[5]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer6);
        gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mTFan6);

//        gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, mTextureBuffer);
//
//        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void createTexture(GL10 gl, Context context, int[] resource) {
        gl.glGenTextures(6, textures, 0); // 6 faces

        for (int i = 0; i < 6; i++) {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), resource[i]);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            image.recycle();
        }
    }
}
