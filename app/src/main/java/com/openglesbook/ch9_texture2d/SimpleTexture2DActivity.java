package com.openglesbook.ch9_texture2d;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;

public class SimpleTexture2DActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new SimpleTexture2DRenderer(this);
    }
}
