//
// Book:      OpenGL(R) ES 2.0 Programming Guide
// Authors:   Aaftab Munshi, Dan Ginsburg, Dave Shreiner
// ISBN-10:   0321502795
// ISBN-13:   9780321502797
// Publisher: Addison-Wesley Professional
// URLs:      http://safari.informit.com/9780321563835
//            http://www.opengles-book.com
//

// Simple_Texture2D
//
//    This is a simple example that draws a quad with a 2D
//    texture image. The purpose of this example is to demonstrate 
//    the basics of 2D texturing
//

package com.openglesbook.ch9_texture2d;

import android.content.Context;
import android.opengl.GLES30;

import com.ljs.android.oepg_ch6.R;
import com.openglesbook.base.MyBaseRenderer;
import com.openglesbook.common.ESShader;
import com.openglesbook.common.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimpleTexture2DRenderer extends MyBaseRenderer {

    private static final int TEST_TEXTURE_RGB = 0;
    private static final int TEST_TEXTURE_SIMPLE_LUMINANCE = 1;
    private static final int TEST_TEXTURE_MAP_LUMINANCE = 2;

    private final float[] mVerticesData =
            {
                    -0.5f, 0.5f, 0.0f, // Position 0
                    0.0f, 0.0f, // TexCoord 0
                    -0.5f, -0.5f, 0.0f, // Position 1
                    0.0f, 1.0f, // TexCoord 1
                    0.5f, -0.5f, 0.0f, // Position 2
                    1.0f, 1.0f, // TexCoord 2
                    0.5f, 0.5f, 0.0f, // Position 3
                    1.0f, 0.0f // TexCoord 3
            };
    private final short[] mIndicesData =
            {
                    0, 1, 2, 0, 2, 3
            };
    // Handle to a program object
    private int mProgramObject;
    // Attribute locations
    private int mPositionLoc;
    private int mTexCoordLoc;
    // Sampler location
    private int mSamplerLoc;
    // Texture handle
    private int mTextureId;
    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private int testTextureFormat = TEST_TEXTURE_MAP_LUMINANCE;

    ///
    // Constructor
    //
    public SimpleTexture2DRenderer(Context context) {
        super(context);
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    //
    // Create a simple 2x2 texture image with four different colors
    //
    private int createSimpleTexture2D() {
        // Texture object handle
        int[] textureId = new int[1];

        // Use tightly packed data
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1);

        //  Generate a texture object
        GLES30.glGenTextures(1, textureId, 0);

        // Bind the texture object
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);

        // 2x2 Image, 3 bytes per pixel (R, G, B)
        byte[] pixels;
        int width, height;
        ByteBuffer pixelBuffer;
        int internalFormat;
        int capacity;
        int type = GLES30.GL_UNSIGNED_BYTE;
        if (testTextureFormat == TEST_TEXTURE_RGB) {
            pixels = new byte[]{
                    (byte) 0xff, 0, 0, // Red
                    0, (byte) 0xff, 0, // Green
                    0, 0, (byte) 0xff, // Blue
                    (byte) 0xff, (byte) 0xff, 0  // Yellow
            };
            width = 2;
            height = 2;
            capacity = width * height * 3;
            internalFormat = GLES30.GL_RGB;
        } else if (testTextureFormat == TEST_TEXTURE_MAP_LUMINANCE) {
            int[] data = context.getResources().getIntArray(R.array.map_data1);
            pixels = new byte[data.length];
            for (int i = 0; i < data.length; ++i)
                pixels[i] = (byte) data[i];
            width = 353;
            height = 282;
            internalFormat = GLES30.GL_LUMINANCE;
            capacity = width * height;
        } else {
            pixels = new byte[]{
                    (byte) 0xff
                    , (byte) 0xaa
                    , (byte) 0x77
                    , (byte) 0x44,
            };
            width = 2;
            height = 2;
            internalFormat = GLES30.GL_LUMINANCE;
            capacity = width * height;
        }
        pixelBuffer = ByteBuffer.allocateDirect(capacity);
        pixelBuffer.put(pixels).position(0);
        //  Load the texture
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D
                , 0
                , internalFormat
                , width
                , height
                , 0
                , internalFormat
                , type
                , pixelBuffer);

        // Set the filtering mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        return textureId[0];
    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader_ch9_simple_texture2d);

        String fShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader_ch9_simple_texture2d);

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Get the attribute locations
        mPositionLoc = GLES30.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES30.glGetAttribLocation(mProgramObject, "a_texCoord");

        // Get the sampler location
        mSamplerLoc = GLES30.glGetUniformLocation(mProgramObject, "s_texture");

        // Load the texture
        mTextureId = createSimpleTexture2D();

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused) {
        // Set the viewport
        GLES30.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Load the vertex position
        mVertices.position(0);
        GLES30.glVertexAttribPointer(mPositionLoc, 3, GLES30.GL_FLOAT,
                false,
                5 * 4, mVertices);
        // Load the texture coordinate
        mVertices.position(3);
        GLES30.glVertexAttribPointer(mTexCoordLoc, 2, GLES30.GL_FLOAT,
                false,
                5 * 4,
                mVertices);

        GLES30.glEnableVertexAttribArray(mPositionLoc);
        GLES30.glEnableVertexAttribArray(mTexCoordLoc);

        // Bind the texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        // Set the sampler texture unit to 0
        GLES30.glUniform1i(mSamplerLoc, 0);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, mIndices);
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

}
