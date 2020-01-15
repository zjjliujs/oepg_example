//
// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//

// MipMap2D
//
//    This is a simple example that demonstrates generating a mipmap chain
//    and rendering with it
//

package com.openglesbook.ch9_texture_cubmap;

import android.content.Context;
import android.opengl.GLES30;

import com.ljs.android.oepg_ch6.R;
import com.openglesbook.base.MyBaseRenderer;
import com.openglesbook.common.ESShader;
import com.openglesbook.common.ESShapes;
import com.openglesbook.common.TextResourceReader;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimpleTextureCubemapRenderer extends MyBaseRenderer {
    // Handle to a program object
    private int mProgramObject;
    // Attribute locations
    private int mPositionLoc;
    private int mNormalLoc;
    // Sampler location
    private int mSamplerLoc;
    // Texture ID
    private int mTextureId;
    // Vertex data
    private ESShapes mSphere = new ESShapes();
    // Additional member variables
    private int mWidth;
    private int mHeight;

    ///
    // Constructor
    //
    public SimpleTextureCubemapRenderer(Context context) {
        super(context);
    }

    ///
    // Create a simple cubemap with a 1x1 face with a different
    // color for each face
    private int createSimpleTextureCubemap() {
        int[] textureId = new int[1];

        // Face 0 - Red
        byte[] cubePixels0 = {127, 0, 0};
        // Face 1 - Green
        byte[] cubePixels1 = {0, 127, 0};
        // Face 2 - Blue
        byte[] cubePixels2 = {0, 0, 127};
        // Face 3 - Yellow
        byte[] cubePixels3 = {127, 127, 0};
        // Face 4 - Purple
        byte[] cubePixels4 = {127, 0, 127};
        // Face 5 - White
        byte[] cubePixels5 = {127, 127, 127};

        ByteBuffer cubePixels = ByteBuffer.allocateDirect(3);

        // Generate a texture object
        GLES30.glGenTextures(1, textureId, 0);

        // Bind the texture object
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, textureId[0]);

        // Load the cube face - Positive X
        cubePixels.put(cubePixels0).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Load the cube face - Negative X
        cubePixels.put(cubePixels1).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_X
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Load the cube face - Positive Y
        cubePixels.put(cubePixels2).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Y
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Load the cube face - Negative Y
        cubePixels.put(cubePixels3).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Load the cube face - Positive Z
        cubePixels.put(cubePixels4).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Z
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Load the cube face - Negative Z
        cubePixels.put(cubePixels5).position(0);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
                , 0
                , GLES30.GL_RGB
                , 1
                , 1
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , cubePixels);

        // Set the filtering mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        return textureId[0];
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader_ch9_texture_cubmap);

        String fShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader_ch9_texture_cubmap);

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Get the attribute locations
        mPositionLoc = GLES30.glGetAttribLocation(mProgramObject, "a_position");
        mNormalLoc = GLES30.glGetAttribLocation(mProgramObject, "a_normal");

        // Get the sampler location
        mSamplerLoc = GLES30.glGetUniformLocation(mProgramObject, "s_texture");

        // Load the texture
        mTextureId = createSimpleTextureCubemap();

        // Generate the vertex data
        mSphere.genSphere(20, 0.75f);

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    ///
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glEnable(GLES30.GL_CULL_FACE);

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Load the vertex position
        GLES30.glVertexAttribPointer(mPositionLoc, 3, GLES30.GL_FLOAT,
                false, 0, mSphere.getVertices());
        // Load the texture coordinate

        GLES30.glVertexAttribPointer(mNormalLoc, 3, GLES30.GL_FLOAT,
                false, 0, mSphere.getNormals());

        GLES30.glEnableVertexAttribArray(mPositionLoc);
        GLES30.glEnableVertexAttribArray(mNormalLoc);

        // Bind the texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, mTextureId);

        // Set the sampler texture unit to 0
        GLES30.glUniform1i(mSamplerLoc, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mSphere.getNumIndices(), GLES30.GL_UNSIGNED_SHORT, mSphere.getIndices());
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }
}
