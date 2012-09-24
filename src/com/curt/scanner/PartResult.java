package com.curt.scanner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.curt.images.ImageHelper;
import com.curt.parts.Part;
import com.curt.parts.PartImage;
import com.curt.vehicle.Vehicle;

public class PartResult extends FragmentActivity {

	public CollectionPagerAdapter pagerAdapter;
	public FragmentManager fManager;
	public ViewPager viewPager;
	public static ArrayList<Part> parts;
	
	public double year;
	public String make;
	public String model;
	public String style;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager_main);
		
		if(parts == null){
			Bundle extras = getIntent().getExtras();
			this.year = extras.getDouble("year");
			this.make = extras.getString("make");
			this.model = extras.getString("model");
			this.style = extras.getString("style");
			
			Vehicle vehicle = new Vehicle();
			vehicle.setYear(this.year);
			vehicle.setMake(this.make);
			vehicle.setModel(this.model);
			vehicle.setStyle(this.style);
			
			new GetPartsAsync().execute(vehicle, null, null);
		}else{
			pagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
			pagerAdapter.parts = parts;
			
			viewPager = (ViewPager)findViewById(R.id.pager);
			viewPager.setAdapter(pagerAdapter);
			viewPager.setCurrentItem(0);
		}
	}
	
	@Override
	public void onResume(){
		
		if(parts == null){
			Bundle extras = getIntent().getExtras();
			this.year = extras.getDouble("year");
			this.make = extras.getString("make");
			this.model = extras.getString("model");
			this.style = extras.getString("style");
			
			Vehicle vehicle = new Vehicle();
			vehicle.setYear(this.year);
			vehicle.setMake(this.make);
			vehicle.setModel(this.model);
			vehicle.setStyle(this.style);
			
			new GetPartsAsync().execute(vehicle, null, null);
		}else{
			pagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
			pagerAdapter.parts = parts;
			
			viewPager = (ViewPager)findViewById(R.id.pager);
			viewPager.setAdapter(pagerAdapter);
			viewPager.setCurrentItem(0);
		}
		
		super.onResume();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.result, menu);
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			getActionBar().setDisplayShowTitleEnabled(false);
		}else{
			getActionBar().setDisplayShowTitleEnabled(true);
		}
		

		MenuItem vehicle = menu.findItem(R.id.vehicle);
		vehicle.setTitle(this.year + " " + this.make + " " + this.model + " "
				+ this.style);
		vehicle.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getApplicationContext(),
						PartResult.class);
				intent.putExtra("year", year);
				intent.putExtra("make", make);
				intent.putExtra("model", model);
				intent.putExtra("style", style);
				startActivity(intent);
				return false;
			}

		});

		MenuItem scan = menu.findItem(R.id.scan);
		scan.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getApplicationContext(),
						Scanner.class);
				startActivity(intent);

				return false;
			}

		});

		return super.onCreateOptionsMenu(menu);
	}
	
	public class CollectionPagerAdapter extends FragmentStatePagerAdapter{

		ArrayList<Part> parts = null;
		
		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int i){
			PartObjectFragment frag = new PartObjectFragment();
			frag.SetPart(parts.get(i));
			
			return frag;
		}
		
		@Override
		public int getCount(){
			return parts.size();
		}
		
		@Override
		public CharSequence getPageTitle(int pos){
			Part part = parts.get(pos);
			return "CURT #" + part.partID;
		}
		
	}
	
	public static class PartObjectFragment extends Fragment{
		public static final String ARG_OBJECT = "part";
		private Part part = null;
		
		
		public PartObjectFragment(){
			setRetainInstance(true);
		}
		
		public void SetPart(Part p){
			this.part = p;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIntanceState){
			View layout = inflater.inflate(R.layout.pager_collection_object, container,false);
			if(part == null){
				return layout;
			}
			if(part.resultDisplay != null){
				return part.resultDisplay;
			}
			
			
			TextView title = (TextView)layout.findViewById(R.id.partResultDesc);
			title.setText(part.shortDesc);
			if (part.images.size() > 0) {
				ImageView img = (ImageView)layout.findViewById(R.id.partResultImage);

				Iterator<PartImage> imgIter = part.images.iterator();
				String imgPath = null;
				while(imgIter.hasNext() && imgPath == null ){
					PartImage pImg = imgIter.next();
					if(pImg.sort == 'a' && pImg.width == 238){
						imgPath = pImg.path;
					}
				}
				if(imgPath == null){
					imgPath = part.images.get(0).path;
				}
				
				img.setTag(imgPath);
				img.setMinimumHeight(238);
				img.setMinimumWidth(238);
				img.setMaxHeight(239);
				img.setMaxWidth(239);
				DownloadImagesAsync imgLoader = new DownloadImagesAsync();
				ProgressBar bar = (ProgressBar)layout.findViewById(R.id.image_loader);
				imgLoader.bar = bar;
				imgLoader.execute(img);
				
			}
			
			return layout;
		}
	}

	public class GetPartsAsync extends
			AsyncTask<Vehicle, Void, ArrayList<Part>> {

		@Override
		protected ArrayList<Part> doInBackground(Vehicle... params) {

			Vehicle vehicle = params[0];

			return vehicle.GetParts();
		}

		protected void onPostExecute(ArrayList<Part> parts) {
			try{
				pagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
				pagerAdapter.parts = parts;
				
				viewPager = (ViewPager)findViewById(R.id.pager);
				viewPager.setAdapter(pagerAdapter);
				viewPager.setCurrentItem(0);
			}catch(Exception e){
				e.printStackTrace();
				Bundle bundle = getIntent().getExtras();
				Bundle extras = getIntent().getExtras();
				double year = extras.getDouble("year");
				String make = extras.getString("make");
				String model = extras.getString("model");
				String style = extras.getString("style");
				
				Vehicle vehicle = new Vehicle();
				vehicle.setYear(year);
				vehicle.setMake(make);
				vehicle.setModel(model);
				vehicle.setStyle(style);
				
				new GetPartsAsync().execute(vehicle, null, null);
			}
		}

	}

	public static class DownloadImagesAsync extends
			AsyncTask<ImageView, Void, Bitmap> {
		ImageView imageView = null;
		ProgressBar bar = null;

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
			this.imageView = imageViews[0];
			String url = (String) imageView.getTag();
			try {
				return download_image(url);
			} catch (MalformedURLException e) {
				Log.e("ImageLoader", "Could not load Bitmap from: " + url);
			} catch (IOException e) {
				Log.e("ImageLoader", "Could not load Bitmap from: " + url);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
			bar.setVisibility(View.INVISIBLE);
			imageView.setVisibility(View.VISIBLE);
			//imageView.setBackgroundDrawable(result);
		}

		private Bitmap download_image(String u) throws IOException {
			URL url = new URL(u);
			URLConnection conn = url.openConnection();
			conn.setUseCaches(true);
			return ImageHelper.getRoundedCornerBitmap(BitmapFactory.decodeStream((InputStream)conn.getContent()), 6);
			
		}
	}
}