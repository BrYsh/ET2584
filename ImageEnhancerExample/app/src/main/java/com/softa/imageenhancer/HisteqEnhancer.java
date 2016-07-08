package com.softa.imageenhancer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class HisteqEnhancer implements ImageEnhancer {

	private int progress;

	public HisteqEnhancer() {

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


		Log.d("DEBUG", "Histogram EQ");
		hist_eq(hsvPixels, pixels);

		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
	}

	public void hist_eq(float[][] hsvPixels, int[] pixels){

		Log.d("DEBUG", "EQ: Skapa Histogram");
		int[] hist_array = make_histogram(hsvPixels);
		Log.d("DEBUG", "EQ: Skapa Histogram length = " + hist_array.length);
		progress = 60;

		float N = (float) hsvPixels.length;
		float bitdepth = (float)255;
		float scalar = bitdepth/N;

		float[] hist_array_cs = new float[256];
		hist_array_cs[0] = (float)hist_array[0]*scalar;

		// kumulativa fördelningsfunktionen
		for (int i = 1; i < hist_array.length; i++) {
			hist_array_cs[i] = (float)hist_array[i]*scalar + hist_array_cs[i-1];
		}
		progress = 70;

		// Transformera tillbaka
		float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = hist_array_cs[ Math.round( old_i*(float)255 ) ]/(float)255;

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

			return enhanceImageHSV(bitmap, 0); //HistEQ

	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Histogram EQ"};
	}

}
