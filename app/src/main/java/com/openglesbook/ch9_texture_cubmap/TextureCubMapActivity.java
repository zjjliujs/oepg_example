package com.openglesbook.ch9_texture_cubmap;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;

public class TextureCubMapActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new SimpleTextureCubemapRenderer(this);
    }
}
