package com.openglesbook.map_buffers;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.openglesbook.base.GLBaseActivity;

/**
 * Activity class for example program that detects OpenGL ES 3.0.
 **/
public class MapBuffersActivity extends GLBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new MapBuffersRenderer(this);
    }
}
