package com.openglesbook.ch2_hello_triangle;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.openglesbook.base.GLBaseActivity;

/**
 * Activity class for example program that detects OpenGL ES 2.0.
 **/
public class HelloTriangleActivity extends GLBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new HelloTriangleRenderer(this);
    }
}
