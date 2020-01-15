package com.openglesbook.ch9_texture_wrap;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;

public class TextureWrapActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new TextureWrapRenderer(this);
    }
}
