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

package se.kjellstrand.julia;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.rs.levels.ScriptC_julia;

public class JuliaRSActivity extends Activity {

	private Bitmap mBitmap;

	private ImageView mDisplayView;

	private RenderScript mRS;
	private Allocation mInPixelsAllocation;
	private Allocation mOutPixelsAllocation;
	private ScriptC_julia mScript;

	private int mWidth;
	private int mHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mWidth = (int) (size.x);
		mHeight = (int) (size.y);

		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, conf);
		mBitmap = Bitmap.createBitmap(mWidth, mHeight, conf);

		mDisplayView = (ImageView) findViewById(R.id.display);
		mDisplayView.setImageBitmap(mBitmap);

		mRS = RenderScript.create(this);
		mInPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmap,
				Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
		mOutPixelsAllocation = Allocation.createFromBitmap(mRS, mBitmap,
				Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
		mScript = new ScriptC_julia(mRS, getResources(), R.raw.julia);

		mScript.set_height(mHeight - 160);
		mScript.set_width(mWidth);

		mScript.set_precision(24);

		mDisplayView.getLayoutParams().width = mWidth;
		mDisplayView.getLayoutParams().height = mHeight;

		renderJulia(-0.9259259f, 0.30855855f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {

		case (MotionEvent.ACTION_MOVE):
			float cx = 0f,
			cy = 0f;
			float x = event.getAxisValue(MotionEvent.AXIS_X);
			float y = event.getAxisValue(MotionEvent.AXIS_Y);
			cx = ((x / mWidth) * 4f) - 2f;
			cy = ((y / mHeight) * 4f) - 2f;
			renderJulia(cx, cy);
			return true;

		default:
			return super.onTouchEvent(event);
		}
	}

	int[][] a = new int[][]{{1,2,3,4,2},{1,2}};
	
	private void renderJulia(float cx, float cy) {
		Log.d("tag","{"+cx+","+cy+"},");
		mScript.set_cx(cx);
		mScript.set_cy(cy);
		mScript.forEach_root(mInPixelsAllocation, mOutPixelsAllocation);
		mOutPixelsAllocation.copyTo(mBitmap);

		mDisplayView.invalidate();
	}

}
