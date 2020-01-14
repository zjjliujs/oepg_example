package com.openglesbook.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public abstract class MyBaseRenderer implements GLSurfaceView.Renderer {

    protected Context context;

    public MyBaseRenderer(Context context) {
        this.context = context;
    }

    protected void logError(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    protected void logDebug(String msg) {
        Log.d(getClass().getSimpleName(), msg);
    }
}
