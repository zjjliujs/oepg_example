package com.openglesbook.multi_texture;

import android.opengl.GLSurfaceView;

import com.openglesbook.base.GLBaseActivity;
import com.openglesbook.multitexture.MultiTextureRenderer;

public class MultiTextureActivity extends GLBaseActivity {

    @Override
    protected GLSurfaceView.Renderer getRenderer() {
        return new MultiTextureRenderer(this);
    }
}
