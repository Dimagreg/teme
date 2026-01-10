package com.example.lab7;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

public class MyActivity extends Activity{

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // added
        GLSurfaceView view = new GLSurfaceView(this);
        view.setEGLConfigChooser(8,8,8,8,16,1);
        view.setRenderer(new CubeRenderer());
        setContentView(view);
    }
}