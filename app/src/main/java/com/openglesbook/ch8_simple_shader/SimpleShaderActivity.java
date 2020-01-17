package com.openglesbook.ch8_simple_shader;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;

public class SimpleShaderActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new SimpleShaderRenderer(this);
    }
}
