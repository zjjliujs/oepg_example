package com.openglesbook.base;

import android.opengl.GLSurfaceView;
import android.util.Log;

public abstract class MyBaseRenderer implements GLSurfaceView.Renderer {

    protected void logError(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    protected void logDebug(String msg) {
        Log.d(getClass().getSimpleName(), msg);
    }
}
