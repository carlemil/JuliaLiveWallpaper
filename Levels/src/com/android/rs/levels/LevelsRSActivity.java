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
import android.graphics.Point;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Matrix3f;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class LevelsRSActivity extends Activity
        implements SeekBar.OnSeekBarChangeListener {
    private final String TAG = "Img";
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private float mCx = 255.0f;
    private SeekBar mCxSeekBar;
    private float mCy = 255.0f;
    private SeekBar mCySeekBar;

    private TextView mBenchmarkResult;
    private ImageView mDisplayView;

    Matrix3f satMatrix = new Matrix3f();
    float mInWMinInB;
    float mOutWMinOutB;
    float mOverInWMinInB;

    private RenderScript mRS;
    private Allocation mInPixelsAllocation;
    private Allocation mOutPixelsAllocation;
    private ScriptC_levels mScript;

    private void setLevels() {
        mOverInWMinInB = 1.f / mInWMinInB;

        mScript.set_cx_(mCx);
        mScript.set_cy_(mCy);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (seekBar == mCxSeekBar) {
                mCx = (float) progress / 127.0f;
                setLevels();
            } else if (seekBar == mCySeekBar) {
                mCy = (float) progress / 127.0f;
                setLevels();
            }

            filter();
            mDisplayView.invalidate();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d(TAG, "### h: " + height + " w: " + width);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        mBitmapIn = Bitmap.createBitmap(width, height, conf);
        mBitmapOut = Bitmap.createBitmap(width, height, conf);

        mDisplayView = (ImageView) findViewById(R.id.display);
        mDisplayView.setImageBitmap(mBitmapOut);

        Log.d(TAG, "### mBitmapIn h: " + mBitmapIn.getHeight() + " w: " + mBitmapIn.getWidth());

        mCxSeekBar = (SeekBar) findViewById(R.id.cxBar);
        mCxSeekBar.setOnSeekBarChangeListener(this);
        mCxSeekBar.setMax(128);
        mCxSeekBar.setProgress(64);
        mCySeekBar = (SeekBar) findViewById(R.id.cyBar);
        mCySeekBar.setOnSeekBarChangeListener(this);
        mCySeekBar.setMax(128);
        mCySeekBar.setProgress(64);

        mBenchmarkResult = (TextView) findViewById(R.id.benchmarkText);
        mBenchmarkResult.setText("Result: not run");

        mRS = RenderScript.create(this);
        mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                                                          Allocation.MipmapControl.MIPMAP_NONE,
                                                          Allocation.USAGE_SCRIPT);
        mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                                                           Allocation.MipmapControl.MIPMAP_NONE,
                                                           Allocation.USAGE_SCRIPT);
        mScript = new ScriptC_levels(mRS, getResources(), R.raw.levels);


        mScript.set_height(height / 2);
        mScript.set_width(width);

        setLevels();
        filter();

        mDisplayView.getLayoutParams().width = width;
        mDisplayView.getLayoutParams().height = height / 2;

        mDisplayView.requestLayout();

        mDisplayView.invalidate();

    }

    // private Bitmap loadBitmap(int resource) {
    // final BitmapFactory.Options options = new BitmapFactory.Options();
    // options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    // Bitmap b = BitmapFactory.decodeResource(getResources(), resource,
    // options);
    // Bitmap b2 = Bitmap.createBitmap(b.getWidth(), b.getHeight(),
    // b.getConfig());
    // Canvas c = new Canvas(b2);
    // c.drawBitmap(b, 0, 0, null);
    // b.recycle();
    // return b2;
    // }

    private void filter() {

        mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
        mOutPixelsAllocation.copyTo(mBitmapOut);
    }

    public void benchmark(View v) {
        filter();
        long t = java.lang.System.currentTimeMillis();
        filter();
        t = java.lang.System.currentTimeMillis() - t;
        mDisplayView.invalidate();
        mBenchmarkResult.setText("Result: " + t + " ms");
    }
}
