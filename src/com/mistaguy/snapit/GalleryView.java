package com.mistaguy.snapit;


import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryView extends Activity{
	private Gallery gallery;
	private ImageView imgView;
	private Uri[] mUrls=null;
	String[] mFiles = null;
	
	public class AddImageAdapter extends BaseAdapter {
		int GalItemBg;
		private Context cont;

		public AddImageAdapter(Context c) {
			cont = c;
			TypedArray typArray = obtainStyledAttributes(R.styleable.GalleryTheme);
			GalItemBg = typArray.getResourceId(
					R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			typArray.recycle();
		}

		public int getCount() {
			return mUrls.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgView = new ImageView(cont);
			imgView.setImageURI(mUrls[position]);
			imgView.setLayoutParams(new Gallery.LayoutParams(80, 70));
			imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			imgView.setBackgroundResource(GalItemBg);

			return imgView;
		}
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images);
        
        File sdImageMainDirectory = new File("/sdcard/snapit/");
		File[] imagelist = sdImageMainDirectory.listFiles();
		mFiles = new String[imagelist.length];

		for (int i = 0; i < imagelist.length; i++) {
			mFiles[i] = imagelist[i].getAbsolutePath();
		}
		mUrls = new Uri[mFiles.length];

		for (int i = 0; i < mFiles.length; i++) {
			mUrls[i] = Uri.parse(mFiles[i]);
		}
		
        
		imgView = (ImageView) findViewById(R.id.snapitImageView);
		imgView.setImageURI(mUrls[0]);

		gallery = (Gallery) findViewById(R.id.snapitGallery);		
		gallery.setAdapter(new AddImageAdapter(this));
		gallery.setFadingEdgeLength(40);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				
				imgView.setImageURI(mUrls[position]);
				
			}
		});
		
    }
    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	
	 // Inflate our menu which can gather user input for switching camera
	 MenuInflater inflater = getMenuInflater();
	 inflater.inflate(R.menu.list_camera_options_share, menu);
	 return true;
	 }
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle all of the possible menu actions.
	        switch (item.getItemId()) {
	        case R.id.camera_share_facebook:	            
	            finish();
	            break;
	        case R.id.camera_share_twitter:	            
	            
	            break;
	     
	        }
	        return super.onOptionsItemSelected(item);

	    }
    
}
