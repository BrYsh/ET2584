package com.softa.imageenhancer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class AMlevelsEnhancer implements ImageEnhancer {

	private int progress;

	public AMlevelsEnhancer() {

	}

	// Att göra: testa Texten, fixa någon start i histeq att testa

	public Bitmap enhanceImageHSV(Bitmap theImage, int action) {

		// Set progress
		// action = vilken kanal som ska jobbas i
		progress = 0;
		
		// Get the image pixels
		int height = theImage.getHeight();
		int width = theImage.getWidth();
		Log.d("DEBUG", "Image size is " + width + "px by " + height + "px." );
		int[] pixels = new int[height * width];
		theImage.getPixels(pixels, 0, width,0,0, width, height);
		
		progress = 5;

		Log.d("DEBUG", "pixels length = " + pixels.length);
		
		//Convert pixels to brightness values;
		float[][] hsvPixels = convertToHSV(pixels);
		
		progress = 40;
		
		Log.d("DEBUG", "hsvPixels length = " + hsvPixels.length);

		// Here below some manipulations of the image is made as examples.
		// This should be changed to your image enhancement algorithms.



		Log.d("DEBUG", "al bins = " + action);

		double gamma = (double)action/100.0;

		auto_levels(hsvPixels, pixels, gamma);


		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
	}


	public void auto_levels(float[][] hsvPixels, int[] pixels, double gamma) {

		//Histogram
		int[] hist_array = make_histogram(hsvPixels);

		// Total intensitet i bilden
		int sum_intensity = 0;
		for (int i = 0 ; i < hist_array.length; ++i ) {
			sum_intensity += hist_array[i];
		}

		// Find lv
		int lv = 0;
		int lv_sum = hist_array[0];
		int limit = sum_intensity/1000;
		while(lv_sum < limit){
			++lv;
			lv_sum += hist_array[lv];
		}
		Log.d("DEBUG", "AL: lv = " + lv);

		// Find hv
		int hv = 255;
		int hv_sum = hist_array[255];
		limit = sum_intensity/1000;
		while(hv_sum < limit){
			--hv;
			hv_sum += hist_array[hv];
		}

		progress = 50;
		Log.d("DEBUG", "AL: hv = " + hv);

		int[] T = new int[256];
		float k = (float)255/(float)(hv-lv);
		Log.d("DEBUG", "AL: k = " + k);
		int m = - Math.round(k*(float)lv);
		Log.d("DEBUG", "AL: m = " + m);

		for(int i = lv; i < hv; ++i){
			T[i] = Math.round(k*(float)i + (float)m);
		}
		Log.d("DEBUG", "AL: F1-done");

		for(int i = hv; i < 256; ++i){
			T[i] = 255;
		}
		Log.d("DEBUG", "AL: F2-done");
		progress = 55;
		// Transformera bilden utjämna kontrasten med linjär transformation
		Log.d("DEBUG", "AL: Tranformerar bild steg 1");
		float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ Math.round( old_i*(float)255 ) ]/(float)255;
			hsvPixels[i][2] = new_i;

		}

		progress = 60;
		// Fas två interpolera gamma-kurva



		progress = 80;
		Log.d("DEBUG", "A-Levels: Gamma: " + gamma);
		float bdf = (float)255;
		double bdd = 255.0;

		//Koda in T
		for(int x = 0; x <= 255; ++x){

			T[x] = Math.round(bdf*(float)(Math.pow((double)x/bdd,gamma)));

		}

		progress = 90;
		Log.d("DEBUG", "A-Levels: Transformera tillbaka bilden");
		// Transformera bilden
		//float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ Math.round( old_i*bdf ) ]/bdf;
			hsvPixels[i][2] = new_i;

			//Konvertera tillbaka till RGB
			pixels[i] = Color.HSVToColor(hsvPixels[i]);
		}


	}

	public int[] make_histogram(float[][] hsvPixels){

		int[] hist_array = new int[256];
		int intens;
		for (int i = 0; i < hsvPixels.length; i++) {
			intens = Math.round( hsvPixels[i][2]*255 );

			hist_array[ intens ] += 1;

		}

		return hist_array;

	}

	private float[][] convertToHSV(int[] pixels) {
		float[][] hsvPixels = new float[pixels.length][3];
		for (int i = 0; i < pixels.length; i++) {
			Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPixels[i]);
			
		}
		return hsvPixels;
	}

	public int getProgress() {
		// Log.d("DEBUG", "Progress: "+progress);
		return progress;
	}

	@Override
	public Bitmap enhanceImage(Bitmap bitmap, int configuration) {

			return enhanceImageHSV(bitmap, configuration);

	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Auto Levels"};
	}

}
