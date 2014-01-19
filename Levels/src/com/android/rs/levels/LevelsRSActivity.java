/* Copyright (C) 2012 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. */

package com.android.rs.levels;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Matrix3f;
import android.renderscript.RenderScript;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class LevelsRSActivity extends Activity {
    private static final float SCALE = 2;
    private final String TAG = "Img";
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private ImageView mDisplayView;

    Matrix3f satMatrix = new Matrix3f();
    float mInWMinInB;
    float mOutWMinOutB;
    // float mOverInWMinInB;

    private RenderScript mRS;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;
    private ScriptC_levels mScript;
    private int i = 0;
    private int mWidth;
    private int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = (int) (size.x / SCALE);
        mHeight = (int) (size.y / SCALE);

        Log.d(TAG, "### h: " + mHeight + " w: " + mWidth);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        mBitmapIn = Bitmap.createBitmap(mWidth, mHeight, conf);
        mBitmapOut = Bitmap.createBitmap(mWidth, mHeight, conf);

        mDisplayView = (ImageView) findViewById(R.id.display);
        mDisplayView.setImageBitmap(mBitmapOut);

        Log.d(TAG, "### mBitmapIn h: " + mBitmapIn.getHeight() + " w: " + mBitmapIn.getWidth());

        mRS = RenderScript.create(this);
        mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        mScript = new ScriptC_levels(mRS, getResources(), R.raw.levels);

        mScript.set_height(mHeight);
        mScript.set_width(mWidth);

        mDisplayView.getLayoutParams().width = (int) (SCALE * mWidth);
        mDisplayView.getLayoutParams().height = (int) (SCALE * mHeight);

        mDisplayView.requestLayout();

        Matrix matrix = new Matrix();
        matrix.postScale(SCALE, SCALE);
        mDisplayView.setImageMatrix(matrix);

        /* int i = 34; cx = (float) Math.sin(i / 10); cy = (float) Math.cos(i /
         * 10); */
        renderJulia(0.5f, 0.5f);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {

            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "Action was MOVE");
                float cx = 0f,
                cy = 0f;
                float x = event.getAxisValue(MotionEvent.AXIS_X) / SCALE;
                float y = event.getAxisValue(MotionEvent.AXIS_Y) / SCALE;
                cx = ((x / mWidth) * 3f) - 1f;
                cy = y / mHeight;
                long t = java.lang.System.currentTimeMillis();
                renderJulia(cx, cy);
                t = java.lang.System.currentTimeMillis() - t;

                Log.d(TAG, "### cx: " + cx + " cy: " + cy + " time: " + t + "ms.");

                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    private void renderJulia(float cx, float cy) {
        mScript.set_cx(cx);
        mScript.set_cy(cy);

        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
        mOutPixelsAllocation.copyTo(mBitmapOut);

        mDisplayView.invalidate();
    }

    public void benchmark(View v) {
        renderJulia(0f, 0f);
        renderJulia(0f, 0f);
        mDisplayView.invalidate();

        float cx = 0f, cy = 0f;
        i++;
        // -1;2
        // 1.1;-1.1
        cx = (float) (Math.sin(i / 10d) + 0d);
        cy = (float) Math.cos(i / 10d);

        renderJulia(cx, cy);

    }
}
