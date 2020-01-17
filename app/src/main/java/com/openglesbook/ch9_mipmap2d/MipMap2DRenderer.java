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

package com.openglesbook.ch9_mipmap2d;

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

public class MipMap2DRenderer extends MyBaseRenderer {

    private static final int FLOAT_NUMBER_SIZE = 4;
    private static final int POS_COMPONENT_COUNT = 4;
    private static final int TEXTURE_CORD_COMPONENT_COUNT = 2;
    public static final int TOTAL_COMPONENT_COUNT = POS_COMPONENT_COUNT + TEXTURE_CORD_COMPONENT_COUNT;
    public static final int PIXEL_BYTE_SIZE = 3;    //GL_RGB
    private final float[] mVerticesData = {
            -0.5f, 0.5f, 0.0f, 1.5f,  // Position 0
            0.0f, 0.0f,              // TexCoord 0
            -0.5f, -0.5f, 0.0f, 0.75f, // Position 1
            0.0f, 1.0f,              // TexCoord 1
            0.5f, -0.5f, 0.0f, 0.75f, // Position 2
            1.0f, 1.0f,              // TexCoord 2
            0.5f, 0.5f, 0.0f, 1.5f,  // Position 3
            1.0f, 0.0f               // TexCoord 3
    };
    private final short[] mIndicesData = {
            0, 1, 2, 0, 2, PIXEL_BYTE_SIZE
    };
    // Handle to a program object
    private int mProgramObject;
    // Attribute locations
    private int mPositionLoc;
    private int mTexCoordLoc;
    // Sampler location
    private int mSamplerLoc;
    // Offset location
    private int mOffsetLoc;
    // Texture handle
    private int mTextureId;
    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;

    ///
    // Constructor
    //
    public MipMap2DRenderer(Context context) {
        super(context);
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * POS_COMPONENT_COUNT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    ///
    //  From an RGB8 source image, generate the next level mipmap
    //
    private byte[] genMipMap2D(byte[] src, int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        int x, y;
        int texelSize = PIXEL_BYTE_SIZE;

        byte[] dst = new byte[texelSize * (dstWidth) * (dstHeight)];

        for (y = 0; y < dstHeight; y++) {
            for (x = 0; x < dstWidth; x++) {
                int[] srcIndex = new int[POS_COMPONENT_COUNT];
                float r = 0.0f, g = 0.0f, b = 0.0f;
                int sample;

                // Compute the offsets for 2x2 grid of pixels in previous
                // image to perform box filter
                srcIndex[0] =
                        (((y * 2) * srcWidth) + (x * 2)) * texelSize;
                srcIndex[1] =
                        (((y * 2) * srcWidth) + (x * 2 + 1)) * texelSize;
                srcIndex[2] =
                        ((((y * 2) + 1) * srcWidth) + (x * 2)) * texelSize;
                srcIndex[PIXEL_BYTE_SIZE] =
                        ((((y * 2) + 1) * srcWidth) + (x * 2 + 1)) * texelSize;

                // Sum all pixels
                for (sample = 0; sample < POS_COMPONENT_COUNT; sample++) {
                    r += src[srcIndex[sample]];
                    g += src[srcIndex[sample] + 1];
                    b += src[srcIndex[sample] + 2];
                }

                // Average results
                r /= 4.0;
                g /= 4.0;
                b /= 4.0;

                // Store resulting pixels
                dst[(y * (dstWidth) + x) * texelSize] = (byte) (r);
                dst[(y * (dstWidth) + x) * texelSize + 1] = (byte) (g);
                dst[(y * (dstWidth) + x) * texelSize + 2] = (byte) (b);
            }
        }
        return dst;
    }

    ///
    //  Generate an RGB8 checkerboard image
    //
    private byte[] genCheckImage(int width, int height, int checkSize) {
        int x, y;
        byte[] pixels = new byte[width * height * PIXEL_BYTE_SIZE];


        for (y = 0; y < height; y++)
            for (x = 0; x < width; x++) {
                byte rColor = 0;
                byte bColor = 0;

                if ((x / checkSize) % 2 == 0) {
                    rColor = (byte) (127 * ((y / checkSize) % 2));
                    bColor = (byte) (127 * (1 - ((y / checkSize) % 2)));
                } else {
                    bColor = (byte) (127 * ((y / checkSize) % 2));
                    rColor = (byte) (127 * (1 - ((y / checkSize) % 2)));
                }

                pixels[(y * height + x) * PIXEL_BYTE_SIZE] = rColor;
                pixels[(y * height + x) * PIXEL_BYTE_SIZE + 1] = 0;
                pixels[(y * height + x) * PIXEL_BYTE_SIZE + 2] = bColor;
            }

        return pixels;
    }

    ///
    // Create a mipmapped 2D texture image
    //
    private int createMipMappedTexture2D() {
        // Texture object handle
        int[] textureId = new int[1];
        int width = 256, height = 256;
        int level;
        byte[] pixels;
        byte[] prevImage;
        byte[] newImage;

        pixels = genCheckImage(width, height, 8);

        // Generate a texture object
        GLES30.glGenTextures(1, textureId, 0);

        // Bind the texture object
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);

        // Load mipmap level 0
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * PIXEL_BYTE_SIZE);
        pixelBuffer.put(pixels).position(0);

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D
                , 0
                , GLES30.GL_RGB
                , width
                , height
                , 0
                , GLES30.GL_RGB
                , GLES30.GL_UNSIGNED_BYTE
                , pixelBuffer);

        level = 1;
        prevImage = pixels;

        while (width > 1 && height > 1) {
            int newWidth, newHeight;

            newWidth = width / 2;
            if (newWidth <= 0)
                newWidth = 1;

            newHeight = height / 2;
            if (newHeight <= 0)
                newHeight = 1;

            // Generate the next mipmap level
            newImage = genMipMap2D(prevImage, width, height, newWidth, newHeight);

            // Load the mipmap level
            pixelBuffer = ByteBuffer.allocateDirect(newWidth * newHeight * PIXEL_BYTE_SIZE);
            pixelBuffer.put(newImage).position(0);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D
                    , level
                    , GLES30.GL_RGB
                    , newWidth
                    , newHeight
                    , 0
                    , GLES30.GL_RGB
                    , GLES30.GL_UNSIGNED_BYTE
                    , pixelBuffer);

            // Set the previous image for the next iteration
            prevImage = newImage;
            level++;

            // Half the width and height
            width = newWidth;
            height = newHeight;
        }


        // Set the filtering mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST_MIPMAP_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        return textureId[0];

    }

    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        String vShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader_ch9_mipmap2d);

        String fShaderStr = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader_ch9_mipmap2d);

        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Get the attribute locations
        mPositionLoc = GLES30.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES30.glGetAttribLocation(mProgramObject, "a_texCoord");

        // Get the sampler location
        mSamplerLoc = GLES30.glGetUniformLocation(mProgramObject, "s_texture");

        // Get the offset location
        mOffsetLoc = GLES30.glGetUniformLocation(mProgramObject, "u_offset");

        // Load the texture
        mTextureId = createMipMappedTexture2D();

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

        // Use the program object
        GLES30.glUseProgram(mProgramObject);

        // Load the vertex position
        mVertices.position(0);
        int stride = TOTAL_COMPONENT_COUNT * FLOAT_NUMBER_SIZE;
        GLES30.glVertexAttribPointer(mPositionLoc
                , POS_COMPONENT_COUNT
                , GLES30.GL_FLOAT
                , false
                , stride
                , mVertices);
        // Load the texture coordinate
        mVertices.position(POS_COMPONENT_COUNT);
        GLES30.glVertexAttribPointer(mTexCoordLoc
                , TEXTURE_CORD_COMPONENT_COUNT
                , GLES30.GL_FLOAT
                , false
                , stride
                , mVertices);

        GLES30.glEnableVertexAttribArray(mPositionLoc);
        GLES30.glEnableVertexAttribArray(mTexCoordLoc);

        // Bind the texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        // Set the sampler texture unit to 0
        GLES30.glUniform1i(mSamplerLoc, 0);

        // Draw quad with nearest sampling
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glUniform1f(mOffsetLoc, -0.6f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, mIndices);

        // Draw quad with trilinear filtering
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glUniform1f(mOffsetLoc, 0.6f);
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
