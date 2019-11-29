package com.ljs.android.oepg_ch6.base;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.openglesbook.common.AppUtils;

public abstract class GLBaseActivity extends MyBaseActivity {
    protected final int CONTEXT_CLIENT_VERSION = 3;
    protected GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        if (AppUtils.detectOpenGLES30(this)) {
            // Tell the surface view we want to create an OpenGL ES 3.0-compatible
            // context, and set an OpenGL ES 3.0-compatible renderer.
            glSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
            glSurfaceView.setRenderer(getRenderer());
        } else {
            logError("OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();
        }

        setContentView(glSurfaceView);
    }

    protected abstract GLSurfaceView.Renderer getRenderer();

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        glSurfaceView.onPause();
    }
}
