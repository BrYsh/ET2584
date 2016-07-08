package com.softa.imageenhancer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class BwEnhancer implements ImageEnhancer {

	private int progress;

	public BwEnhancer() {

	}

	// Att göra: testa Texten, fixa någon start i histeq att testa

	public Bitmap enhanceImageHSV(Bitmap theImage) {

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
		 // Black & White
            for (int i = 0; i < hsvPixels.length; i++) {
                hsvPixels[i][1] = 0; // Set color saturation to zero
                pixels[i] = Color.HSVToColor(hsvPixels[i]);
            }
            Log.d("DEBUG", "saturation zeroed");

		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
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

			return enhanceImageHSV(bitmap);

	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Black & White"};
	}

}
