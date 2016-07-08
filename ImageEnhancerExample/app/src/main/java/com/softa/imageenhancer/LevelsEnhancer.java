package com.softa.imageenhancer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.util.Log;

public class LevelsEnhancer implements ImageEnhancer {

	private int progress;

	public LevelsEnhancer() {

	}


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
		float gamma=1;
		//Avkoda action (för att inte behöva ändra klasstruktur ;) )
		int lv = action%1000;
		action = (action-lv)/1000;
		int hv = action%1000;
		action = (action-hv)/1000;
		if(action > 100){
			gamma = action-100;
		}
		else{
			gamma = (float)action/(float)100;
		}

		Log.d("DEBUG", "levels: lv = " + lv);
		Log.d("DEBUG", "levels: hv = " + hv);
		Log.d("DEBUG", "levels: gamma = " + gamma);

		levels(hsvPixels, pixels, lv,hv,gamma);

		progress = 80;
		Log.d("DEBUG","creating BITMAP,width x height "+width+" "+height);
        Bitmap modifiedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		modifiedImage.setPixels(pixels, 0, width, 0, 0, width, height);

		progress = 100;
		return modifiedImage;
	}


	public void levels(float[][] hsvPixels, int[] pixels ,int lv, int hv, float gamma) {


		//
		Log.d("DEBUG", "AL: lv = " + lv);

		//
		Log.d("DEBUG", "AL: hv = " + hv);


		int[] T = new int[256];
		float k = (float)255/(float)(hv-lv);
		Log.d("DEBUG", "AL: k = " + k);
		int m = - Math.round(k*(float)lv);
		Log.d("DEBUG", "AL: m = " + m);

		// Koda T[lv - 255]
		for(int i = lv; i < hv; ++i){
			T[i] = Math.round(k*(float)i + (float)m);
		}

		for(int i = hv; i < 256; ++i){
			T[i] = 255;
		}


		progress = 55;
		// Transformera bilden utjämna kontrasten med linjär transformation
		Log.d("DEBUG", "AL: Tranformerar bild steg 1");
		double old_i,new_i,max_v=255.0;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = (double)hsvPixels[i][2];
			new_i = T[ (int)Math.round(old_i*max_v) ]/max_v;
			hsvPixels[i][2] = (float)new_i;

		}

		progress = 60;
		// Fas två transformera med gamma
		double bdd = (double)255;
		float bdf = (float)bdd;

		progress = 80;
		Log.d("DEBUG", "A-Levels: Gamma: " + gamma);
		for(int x = 0; x <= 255; ++x){

			T[x] = Math.round(bdf*(float)(Math.pow((double)x/bdd,gamma)));

		}

		progress = 90;
		Log.d("DEBUG", "A-Levels: Transformera tillbaka bilden");
		// Transformera bilden
		//float old_i,new_i;
		for (int i = 0; i < hsvPixels.length; i++) {
			old_i = hsvPixels[i][2];
			new_i = T[ (int)Math.round(old_i*max_v) ]/max_v;
			hsvPixels[i][2] = (float)new_i;

			//Konvertera tillbaka till RGB
			pixels[i] = Color.HSVToColor(hsvPixels[i]);
		}


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
