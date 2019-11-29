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

// Example_6_3
//
//    This example demonstrates using client-side vertex arrays
//    and a constant vertex attribute
//

package com.openglesbook.example6_3;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.ljs.android.oepg_ch6.R;
import com.openglesbook.common.Constants;
import com.openglesbook.common.ESShader;
import com.openglesbook.common.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Example6_3Renderer implements GLSurfaceView.Renderer {
    //对应vertex shader的layout参数值
    public static final int ATTRIBUTE_INDEX_COLOR = 0;
    //对应vertex shader的layout参数值
    public static final int ATTRIBUTE_INDEX_POS = 1;
    private final Context context;

    private final float[] verticesData = {
            0.0f, 0.5f, 0.0f, // v0
            -0.5f, -0.5f, 0.0f, // v1
            0.5f, -0.5f, 0.0f  // v2
    };
    // Handle to a program object
    private int program;
    // Additional member variables
    private int width;
    private int height;
    private FloatBuffer vertices;

    ///
    // Constructor
    //
    public Example6_3Renderer(Context context) {
        this.context = context;
        vertices = ByteBuffer
                .allocateDirect(verticesData.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertices.put(verticesData);
        vertices.position(0);
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vertexShaderSrc
                = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader_ch6_3);

        String fragmentShaderSrc
                = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader_ch6_3);

        // Load the shaders and get a linked program object
        program = ESShader.loadProgram(vertexShaderSrc, fragmentShaderSrc);

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

        // Set the vertex color to red
        GLES30.glVertexAttrib4f(ATTRIBUTE_INDEX_COLOR, 1.0f, 0.0f, 0.0f, 1.0f);

        // Load the vertex position
        vertices.position(0);
        GLES30.glVertexAttribPointer(ATTRIBUTE_INDEX_POS
                , Constants.POS_COMPONENT_SIZE
                , GLES30.GL_FLOAT
                , false
                , 0
                , vertices);

        GLES30.glEnableVertexAttribArray(ATTRIBUTE_INDEX_POS);

        int componentCnt = verticesData.length / Constants.POS_COMPONENT_SIZE;
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES
                , 0
                , componentCnt);

        GLES30.glDisableVertexAttribArray(ATTRIBUTE_INDEX_POS);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width = width;
        this.height = height;
    }
}
