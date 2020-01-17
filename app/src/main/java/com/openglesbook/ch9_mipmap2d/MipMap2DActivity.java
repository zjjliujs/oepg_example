package com.openglesbook.ch9_mipmap2d;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;

public class MipMap2DActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new MipMap2DRenderer(this);
    }
}
