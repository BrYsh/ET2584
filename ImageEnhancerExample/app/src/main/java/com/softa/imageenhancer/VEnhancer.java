package com.softa.imageenhancer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class VEnhancer implements ImageEnhancer {

	private int progress;

	public VEnhancer() {

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

		Log.d("DEBUG", "V-trans bins = " + action);
		v_transformation(hsvPixels, pixels, action);


		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
	}



	public void v_transformation(float[][] hsvPixels, int[] pixels, int bins) {

		Log.d("DEBUG", "V-trans: Skapa Histogram");
		int[] hist_array = make_histogram(hsvPixels);
		Log.d("DEBUG", "V-trans: Skapa Histogram length = " + hist_array.length);

		// Find lv
		int lv = 0;
		while(hist_array[lv] < 1){
			++lv;
		}
		Log.d("DEBUG", "V-trans: lv = " + lv);
		// Find hv
		int hv = 255;
		while(hist_array[hv] < 1){
			--hv;
		}

		Log.d("DEBUG", "V-trans: hv = " + hv);

		// Total intensitet i bilden
		int sum_intensity = 0;
		for (int i = 0 ; i < hist_array.length; ++i ) {
			sum_intensity += hist_array[i];
		}

		Log.d("DEBUG", "V-trans: Sum intens = " + sum_intensity);
		progress = 50;
		int sum_bins = 0;
		int i = 0;

		int[] edge = new int[bins+1];
		edge[bins] = hv;
		edge[0] = lv;

		Log.d("DEBUG", "V-trans: Kanter");

		// Hitta kanterna
		for (int j = 1 ; j < bins; ++j){

			while((double)sum_bins < ((double)sum_intensity*(double)j/(double)bins)){
				sum_bins += hist_array[i];
				++i;
			}
			if (i > 255){
				edge[j] = 255;
			}
			else {
				edge[j] = i;
			}

		}
		progress = 60;
		Log.d("DEBUG", "V-trans: Kant 1 = " + edge[1]);
		int[] T = new int[256];
		int start = lv;
		int hl = 256/bins;
		int hl_1 = 0;

		//For loop variables
		int stop,m;
		float k,dx;

		Log.d("DEBUG", "V-trans: Skapa T");
		// Beräkna Transformfunktionen T
		for(int l = 1; l <= bins; ++l){

			stop = edge[l];
			dx = (float)(stop-start);
			k = ((float)hl/dx);
			m = hl_1 - Math.round(k*(float)start);

			for(int x = start; x <= stop; ++x){

				T[x] = Math.min(Math.round(k*(float)x) + m,255);

			}

			start = stop;
			hl_1 = hl * l;

		}

		progress = 70;
		Log.d("DEBUG", "V-trans: Transformera tillbaka bilden");
		// Transformera bilden
		float old_i,new_i;
		for (i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ Math.round( old_i*(float)255 ) ]/(float)255;
			hsvPixels[i][2] = new_i;

			//Konvertera tillbaka till RGB
			pixels[i] = Color.HSVToColor(hsvPixels[i]);
		}
		progress = 95;
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
		return new String[]{ "V-Transform"};
	}

}
