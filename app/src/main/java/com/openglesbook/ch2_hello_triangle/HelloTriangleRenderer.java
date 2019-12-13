//
// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//

// Hello_Triangle
//
//    This is a simple example that draws a single triangle with
//    a minimal vertex/fragment shader.  The purpose of this 
//    example is to demonstrate the basic concepts of 
//    OpenGL ES 2.0 rendering.

package com.openglesbook.ch2_hello_triangle;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.openglesbook.base.MyBaseRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HelloTriangleRenderer extends MyBaseRenderer {
    public static final int ATTRIBUTE_A_POSITION_LOCATION = 0;
    private static final int ATTRIBUTE_A_COLOR_LOCATION = 1;

    private final float[] mPositionVerticesData = {
            0.0f, 0.5f, -3f
            , -0.5f, -0.5f, -3f
            , 0.5f, -0.5f, -3f};
    private final float[] mColorVerticesData = {
            1.0f, 0.0f, 0.0f, 1.0f
            , 0.0f, 1.0f, 0.0f, 1.0f
            , 0.0f, 0.0f, 1.0f, 1.0f
    };
    // Member variables
    private int mProgramObject;
    /*
        private int mWidth;
        private int mHeight;
    */
    private FloatBuffer mPositionVertices;
    private FloatBuffer mColorVertices;
    private int muMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] mMMatrix = new float[16];

    /*
        private final float[] mPositionVerticesData = {
                -1.0f, -0.5f, 0,
                1.0f, -0.5f, 0,
                0.0f, 1.11803399f, 0
        };
    */
    ///
    // Constructor
    //
    public HelloTriangleRenderer(Context context) {
        mPositionVertices = ByteBuffer.allocateDirect(mPositionVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositionVertices.put(mPositionVerticesData).position(0);

        mColorVertices = ByteBuffer.allocateDirect(mColorVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorVertices.put(mColorVerticesData).position(0);
    }

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    private int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES20.glCreateShader(type);

        if (shader == 0)
            return 0;

        // Load the shader source
        GLES20.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES20.glCompileShader(shader);

        // Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            logError(GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = "uniform mat4 uMVPMatrix; \n"
                + "attribute vec4 aPosition;    \n"
                + "attribute vec4 aColor;       \n"
                + "varying vec4 vColor;         \n"
                + "void main()                  \n"
                + "{                            \n"
                + "     gl_Position = uMVPMatrix * aPosition;  \n"
                //+ "     vColor = vec4 (0.0, 1.0, 0.0, 1.0);"
                + "     vColor = aColor;"
                + "}                            \n";

        String fShaderStr = "precision mediump float;					  \n"
                + "varying vec4 vColor;                         \n"
                + "void main()                                  \n"
                + "{                                            \n"
                //+ "  gl_FragColor = vec4 ( 1.0, 1.0, 0.0, 1.0 );\n"
                + "     gl_FragColor = vColor;"
                + "}                                            \n";

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = LoadShader(GLES20.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = GLES20.glCreateProgram();

        if (programObject == 0)
            return;

        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);

        // Bind aPosition to attribute 0
        GLES20.glBindAttribLocation(programObject, ATTRIBUTE_A_POSITION_LOCATION, "aPosition");
        GLES20.glBindAttribLocation(programObject, ATTRIBUTE_A_COLOR_LOCATION, "aColor");

        // Link the program
        GLES20.glLinkProgram(programObject);

        // Check the link status
        GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            logError("Error linking program:");
            logError(GLES20.glGetProgramInfoLog(programObject));
            GLES20.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        mProgramObject = programObject;

        muMVPMatrixHandle = GLES20.glGetUniformLocation(programObject, "uMVPMatrix");

        GLES20.glClearColor(0.f, 0.f, 1.f, 1.f);

        //视线角度？
        Matrix.setLookAtM(viewMatrix, 0
                , 0, 0, 0f
                , 0f, 0f, -1f
                , 0f, 1.0f, 0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Clear the color buffer
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES20.glUseProgram(mProgramObject);

        // Load the vertex data
        GLES20.glVertexAttribPointer(ATTRIBUTE_A_POSITION_LOCATION, 3, GLES20.GL_FLOAT, false, 0, mPositionVertices);
        GLES20.glEnableVertexAttribArray(ATTRIBUTE_A_POSITION_LOCATION);

        GLES20.glVertexAttribPointer(ATTRIBUTE_A_COLOR_LOCATION, 3, GLES20.GL_FLOAT, false, 0, mColorVertices);
        GLES20.glEnableVertexAttribArray(ATTRIBUTE_A_COLOR_LOCATION);

        //生成围绕z轴旋转的矩阵
        Matrix.setRotateM(mMMatrix, 0,
                0,//rotate angle
                0, 0, 1.0f);//rotate axis
        Matrix.scaleM(mMMatrix, 0, 2, 2, 1);
        //视线矩阵 X 旋转矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        //视窗长宽比调整举证 X 视线矩阵 X 旋转矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    // /
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
/*
        mWidth = width;
        mHeight = height;
*/
        // Set the viewport
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //Matrix.setIdentityM(mMVPMatrix, 0);

        //查询支持的定点属性
        IntBuffer params = IntBuffer.allocate(10);
        GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, params);
        logDebug(String.format(Locale.CHINA
                , "position:%d, limit:%d, capacity:%d"
                , params.position(), params.limit(), params.capacity()));
        //params.flip();
        logDebug("max vertex attributes: " + params.get());
    }

}
