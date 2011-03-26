package com.mistaguy.snapit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

// ----------------------------------------------------------------------

public class CameraView extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	static final int FOTO_MODE = 0;
	private static final String TAG = "SnapitTest";
	private static int scaleFactor = 1;
	private int imageQuality = 60;
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;
	Button mCapture;
	RadioGroup mRadioGroup;
	RadioButton radio_low;
	RadioButton radio_normal;

	RadioButton radio_high;
	RadioButton radio_super;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
/**
 * create a folder for storing snapit pictures
 */
		try {
			String newFolder = "/snapit";
			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();
			File myNewFolder = new File(extStorageDirectory + newFolder);
			myNewFolder.mkdir();
		} catch (Exception e) {
			// TODO: handle exception
		}
		Log.e(TAG, "onCreate");

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.surface_camera);
		radio_low = (RadioButton) findViewById(R.id.radio_low);
		radio_normal = (RadioButton) findViewById(R.id.radio_normal);
		radio_high = (RadioButton) findViewById(R.id.radio_high);
		radio_super = (RadioButton) findViewById(R.id.radio_super);
		radio_low.setOnClickListener(radio_listener);
		radio_normal.setOnClickListener(radio_listener);

		radio_high.setOnClickListener(radio_listener);
		radio_super.setOnClickListener(radio_listener);

		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mCapture = (Button) findViewById(R.id.button_camera_capture);
		mCapture.setOnClickListener(this);
		// mSurfaceView.setOnClickListener(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.RadioImageC);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private OnClickListener radio_listener = new OnClickListener() {
		public void onClick(View v) {
			// Perform action on clicks
			RadioButton rb = (RadioButton) v;
			if (rb.getText().equals("Low")) {
				imageQuality = 60;
			}
			if (rb.getText().equals("Normal")) {
				imageQuality = 70;
			}
			if (rb.getText().equals("High Detail")) {
				imageQuality = 80;
			}
			if (rb.getText().equals("Super High Detail")) {
				imageQuality = 100;
			}

		}
	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate our menu which can gather user input for switching camera
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_camera_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle all of the possible menu actions.
		switch (item.getItemId()) {
		case R.id.menu_camera_close:
			finish();
			break;
		case R.id.menu_camera_share_gallery:
			Intent i = new Intent("com.mistaguy.snapit.GalleryView");
			startActivityForResult(i, 1);

			break;

		}
		return super.onOptionsItemSelected(item);

	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {

				StoreByteImage(mContext, imageData, imageQuality, "ImageName");
				setResult(1);
				mCamera.startPreview();
				mCapture.setEnabled(true);
				// finish();

			}
		}
	};

	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPictureFormat(PixelFormat.JPEG);

		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	Boolean focused = false;
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			mCapture.setEnabled(true);
			focused = true;
		}
	};

	public void onClick(View arg0) {
		mCapture.setEnabled(false);
		mCamera.autoFocus(myAutoFocusCallback);
		mCamera.takePicture(null, mPictureCallback, mPictureCallback);

	}

	public static boolean StoreByteImage(Context mContext, byte[] imageData,
			int quality, String expName) {

		File sdImageMainDirectory = new File("/sdcard/snapit");

		FileOutputStream fileOutputStream = null;

		try {

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = scaleFactor;

			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
					imageData.length, options);

			Long now = Long.valueOf(java.lang.System.currentTimeMillis());
			fileOutputStream = new FileOutputStream(
					sdImageMainDirectory.toString() + "/" + now + ".jpg");

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}