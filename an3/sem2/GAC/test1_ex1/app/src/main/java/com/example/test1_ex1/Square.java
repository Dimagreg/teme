package com.example.test1_ex1;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
public class Square {

    private FloatBuffer mFVertexBuffer;
    private ByteBuffer mColorBuffer;
    private ByteBuffer mIndexBuffer;

    public Square(float vertices[], byte colors[], byte indices[]) {
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
    }

    public void draw(GL10 gl, int len) {
        gl.glFrontFace(GL11.GL_CW);
        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);

        gl.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, mColorBuffer);

        gl.glDrawElements(GL11.GL_TRIANGLES, len, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
}
