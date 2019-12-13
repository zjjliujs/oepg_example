// The MIT License (MIT)
//
// Copyright (c) 2013 Dan Ginsburg, Budirijanto Purnomo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

//
// Book:      OpenGL(R) ES 3.0 Programming Guide, 2nd Edition
// Authors:   Dan Ginsburg, Budirijanto Purnomo, Dave Shreiner, Aaftab Munshi
// ISBN-10:   0-321-93388-5
// ISBN-13:   978-0-321-93388-1
// Publisher: Addison-Wesley Professional
// URLs:      http://www.opengles-book.com
//            http://my.safaribooksonline.com/book/animation-and-3d/9780133440133
//

// Example_6_6
//
//    This example demonstrates drawing a primitive with
//    a separate VBO per attribute
//
//

package com.openglesbook.example6_6;

import android.content.Context;
import android.opengl.GLES30;

import com.ljs.android.oepg_ch6.R;
import com.openglesbook.base.MyBaseRenderer;
import com.openglesbook.common.Constants;
import com.openglesbook.common.ESShader;
import com.openglesbook.common.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Example6_6Renderer extends MyBaseRenderer {

    private static final int VERTEX_POS_SIZE = 3; // x, y and z
    private static final int VERTEX_COLOR_SIZE = 4; // r, g, b, and a
    private static final int VERTEX_POS_INDX = 0;
    private static final int VERTEX_COLOR_INDX = 1;
    // 3 vertices, with (x,y,z) ,(r, g, b, a) per-vertex
    private static final float[] verticesData = {
            0.0f, 0.5f, 0.0f, // v0
            -0.5f, -0.5f, 0.0f, // v1
            0.5f, -0.5f, 0.0f  // v2
    };
    private static final float[] colorData = {
            1.0f, 0.0f, 0.0f, 1.0f,   // c0
            0.0f, 1.0f, 0.0f, 1.0f,   // c1
            0.0f, 0.0f, 1.0f, 1.0f    // c2
    };
    private static final short[] indicesData = {
            0, 1, 2
    };
    private static final int[] vertexStrideData = {
            VERTEX_POS_SIZE * Constants.BYTES_PER_FLOAT,
            VERTEX_COLOR_SIZE * Constants.BYTES_PER_FLOAT
    };
    // Handle to a program object
    private int program;
    // Additional member variables
    private int width;
    private int height;
    private FloatBuffer vertices;
    private FloatBuffer colors;
    private ShortBuffer indices;
    // VertexBufferObject Ids
    private int[] vboIds = new int[3];
    private Context context;

    ///
    // Constructor
    //
    public Example6_6Renderer(Context context) {
        this.context = context;

        vertices = ByteBuffer
                .allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertices.put(verticesData).position(0);

        colors = ByteBuffer
                .allocateDirect(colorData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colors.put(colorData).position(0);

        indices = ByteBuffer
                .allocateDirect(indicesData.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indices.put(indicesData).position(0);
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader_ch6_6);

        String fShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader_ch6_6);

        // Load the shaders and get a linked program object
        program = ESShader.loadProgram(vShaderStr, fShaderStr);

        vboIds[0] = 0;
        vboIds[1] = 0;
        vboIds[2] = 0;

        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        GLES30.glViewport(0, 0, width, height);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES30.glUseProgram(program);

        drawPrimitiveWithVBOs();
    }

    private void drawPrimitiveWithVBOs() {
        int numVertices = verticesData.length / VERTEX_POS_SIZE;
        int numIndices = indicesData.length;

        // vboIds[0] - used to store vertex position
        // vboIds[1] - used to store vertex color
        // vboIds[2] - used to store element indices
        if (vboIds[0] == 0 && vboIds[1] == 0 && vboIds[2] == 0) {
            // Only allocate on the first draw
            GLES30.glGenBuffers(3, vboIds, 0);

            vertices.position(0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER
                    , vertexStrideData[0] * numVertices
                    , vertices
                    , GLES30.GL_STATIC_DRAW);

            colors.position(0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[1]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER
                    , vertexStrideData[1] * numVertices
                    , colors
                    , GLES30.GL_STATIC_DRAW);

            indices.position(0);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboIds[2]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER
                    , 2 * numIndices
                    , indices
                    , GLES30.GL_STATIC_DRAW);
        }

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glVertexAttribPointer(VERTEX_POS_INDX
                , VERTEX_POS_SIZE
                , GLES30.GL_FLOAT
                , false
                , vertexStrideData[0]
                , 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[1]);
        GLES30.glEnableVertexAttribArray(VERTEX_COLOR_INDX);
        GLES30.glVertexAttribPointer(VERTEX_COLOR_INDX
                , VERTEX_COLOR_SIZE
                , GLES30.GL_FLOAT
                , false
                , vertexStrideData[1]
                , 0);

        //draw by indices
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vboIds[2]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES
                , numIndices
                , GLES30.GL_UNSIGNED_SHORT
                , 0);

        GLES30.glDisableVertexAttribArray(VERTEX_POS_INDX);
        GLES30.glDisableVertexAttribArray(VERTEX_COLOR_INDX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
    }
}
