package se.kjellstrand.julia;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class JuliaWallpaperService extends WallpaperService {

	private final String TAG = JuliaWallpaperService.class.getCanonicalName();

	private int mWidth;

	private int mHeight;

	private double[][] juliaSeeds = new double[][] {
			{ -0.9259259, 0.30855855 },//
			{ 0.41851854, 0.42567563 },//
			{ -0.08888888, 0.93468475 },//
			{ -0.57777774, 0.6554055 },//
			{ -1.1592593, 0.33333325 },//
			{ -1.3962963, 0.10810804 },//
			{ -0.7740741, -0.34909904 },//
			{ -0.67777777, -0.5112612 },//
			{ -0.2481482, -0.8626126 },//
			{ 0.437037, 0.18693686 },//
			{ 0.47777772, -0.22072077 },//
			{ -0.26666665, -0.7995496 },//
			{ -0.17037034, -0.8941442 },//
			{ -1.0333333, -0.40090096 },//
			{ 0.34074068, -0.6486486 },//
			{ 0.36666656, 0.06531525 },//
			{ 0.45555544, 0.13513517 },//
			{ 0.36296296, -0.042792797 },//
			{ 0.44074082, 0.25900912 },//
			{ -0.79629624, 0.21846843 },//
			{ -0.80370367, -0.1981982 },//
			{ -0.88518524, 0.27702713 } };

	@Override
	public Engine onCreateEngine() {
		return new DemoEngine();
	}

	class DemoEngine extends Engine {

		private final String LOG_TAG = DemoEngine.class.getCanonicalName();

		JuliaEngine mJuliaRenderer = new JuliaEngine();

		private int pickJuliaSeedByTime;

		private RenderHighQualityTimer hqTimer = new RenderHighQualityTimer();

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);

			Rect rect = holder.getSurfaceFrame();
			mHeight = rect.height();
			mWidth = rect.width();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mJuliaRenderer.destroy();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);

			mJuliaRenderer.init(JuliaWallpaperService.this.getBaseContext(),
					width, height / 2);

			draw(0.5f);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			// TODO !!!!!!!!!!!!!!!

			// mScript stop/start?
			pickJuliaSeedByTime = (int) ((System.currentTimeMillis()/(1000*60*60)) % juliaSeeds.length);
			Log.d(TAG, "seedTime " + pickJuliaSeedByTime);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			draw(xOffset);
			/*
			 * if (xOffset == 1.0f) { mJuliaRenderer.setPrecision(128);
			 * draw(xOffset); }
			 */
			mJuliaRenderer.setPrecision(12);
			hqTimer.setLastFrameTimestamp(System.currentTimeMillis());
		}

		// fada in på vissible med separat timestamp var som alltid checkas vid
		// render, som sätter hur ljus paletten är, 0-1f typ.
		// om 1 så skit i den å kör default, annars gånga ner alla färger för
		// att få en fadein under .3 sekunder eller så
		// låt julia cx cy state va beroende på timestam från senaste fade in

		private void draw(float xOffset) {

			// long startTime = System.currentTimeMillis();

			double x = getX(xOffset);
			double y = getY(xOffset);

			Log.d(LOG_TAG, "X: " + x + "  Y: " + y);
			Bitmap bitmap = mJuliaRenderer.renderJulia(x, y);
			// long renderTime = System.currentTimeMillis() - startTime;
			// Log.d(TAG, "Rendertime: " + (renderTime));

			Canvas c = null;
			SurfaceHolder holder = getSurfaceHolder();
			try {
				c = holder.lockCanvas();
				if (c != null) {
					Matrix rotateMatrix = new Matrix();
					c.drawBitmap(bitmap, rotateMatrix, null);
					rotateMatrix.setRotate(180, 0, 0);
					rotateMatrix.postTranslate(mWidth, mHeight);
					c.drawBitmap(bitmap, rotateMatrix, null);

				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
				long lastFrameTime = System.currentTimeMillis()
						- hqTimer.getLastFrameTimestamp();
				hqTimer.setLastFrameTime(lastFrameTime);
			}
		}

		/*
		 * array med x y size för seed punkter som ger "bra" julias array med
		 * paletter (hur nu de ska funka med dynamisk size på paletten, kanske 5
		 * färger och sen auto smooth mellan dom? en handler eller liknande för
		 * att vänta 2x senaste tiden det tog att rita en frame, och sen rita
		 * med scale == 1
		 */

		private static final double FIRST_SIZE = 0.1;

		private static final double FIRST_SEED_MUL = 9.97;

		// Used to create a smaller circle to avoid repetitions in the julia
		// seed values
		private static final double SECOND_SIZE = 0.1;

		private static final double SECOND_SEED_MUL = 15.97;

		private double getX(double i) {
			return (double) ((Math.sin(i * FIRST_SEED_MUL) * FIRST_SIZE) + (Math
					.sin(i / SECOND_SEED_MUL) * SECOND_SIZE))+juliaSeeds[pickJuliaSeedByTime][0];
		}
		
		private double getY(double i) {
			return (double) ((Math.cos(i * FIRST_SEED_MUL) * FIRST_SIZE) + (Math
					.cos(i / SECOND_SEED_MUL) * SECOND_SIZE))+juliaSeeds[pickJuliaSeedByTime][1];
		}
	}
}
