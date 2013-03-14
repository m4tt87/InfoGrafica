package com.recsysclient.maps.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

class Triangle {
    public Triangle() {

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
        
        // A unit-sided equalateral triangle centered on the origin.
        float[] coords = {
                // X, Y, Z
                -0.5f, -0.25f, 0,
                 0.5f, -0.25f, 0,
                 0.0f,  0.559016994f, 0
        };

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 3; j++) {
                mFVertexBuffer.put(coords[i*3+j] * 2.0f);
            }
        }

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 2; j++) {
                mTexBuffer.put(coords[i*3+j] * 2.0f + 0.5f);
            }
        }

        for(int i = 0; i < VERTS; i++) {
            mIndexBuffer.put((short) i);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
    	//gl.glPushMatrix();
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //gl.glTranslatef(1, 1, 1);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS,
                GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        //gl.glPopMatrix();
    }
    
    public void translate2D( float[] vector){
    	    	
    	float[] coords = {
                // X, Y, Z
    			mFVertexBuffer.get(0)+vector[0], mFVertexBuffer.get(1)+vector[1], 0,
    			mFVertexBuffer.get(3)+vector[0], mFVertexBuffer.get(4)+vector[1], 0,
    			mFVertexBuffer.get(6)+vector[0],  mFVertexBuffer.get(7)+vector[1], 0
        };
    	
    	mFVertexBuffer.clear();
    	mTexBuffer.clear();
    	
        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 3; j++) {
                mFVertexBuffer.put(coords[i*3+j] * 2.0f);
            }
        }

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 2; j++) {
                mTexBuffer.put(coords[i*3+j] * 2.0f + 0.5f);
            }
        }
        
        //mFVertexBuffer.position(0);
        //mTexBuffer.position(0);
        //mIndexBuffer.position(0);
    }

    private final static int VERTS = 3;

    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;
}