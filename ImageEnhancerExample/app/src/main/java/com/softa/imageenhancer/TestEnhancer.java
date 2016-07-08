package com.softa.imageenhancer;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class TestEnhancer implements ImageEnhancer {

	private static final int ACTION_10 = 10;
	private static final int ACTION_9 = 9;
	private static final int ACTION_8 = 8;
	private static final int ACTION_7 = 7;
	private static final int ACTION_6 = 6;
	private static final int ACTION_5 = 5;
	private static final int ACTION_4 = 4;
    private static final int ACTION_3 = 3;
	private static final int ACTION_2 = 2;
	private static final int ACTION_1 = 1;
	private static final int ACTION_0 = 0;
	private int progress;

	public TestEnhancer() {

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
		if (action == 0) { // Histogram EQ
			Log.d("DEBUG", "Histogram EQ");
			hist_eq(hsvPixels, pixels);
		}
		else if(action == 1) {
			int bins = 1;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 2){
			int bins = 2;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
        }
		else if(action == 3) {
			int bins = 3;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 4){
			int bins = 5;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 5){
			int bins = 10;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 6){
			int bins = 20;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 7){
			int bins = 50;
			Log.d("DEBUG", "V-trans bins = " + bins);
			v_transformation(hsvPixels, pixels, bins);
		}
		else if(action == 8){
			int bins = 2;
			Log.d("DEBUG", "al bins = " + bins);
			auto_levels(hsvPixels, pixels, bins);
		}
		else if(action == 9){
			int bins = 10;
			Log.d("DEBUG", "al bins = " + bins);
			auto_levels(hsvPixels, pixels, bins);
		}
		else if(action == 10){
			int bins = 30;
			Log.d("DEBUG", "al bins = " + bins);
			auto_levels(hsvPixels, pixels, bins);
		}
		else { // Black & White
            for (int i = 0; i < hsvPixels.length; i++) {
                hsvPixels[i][1] = 0; // Set color saturation to zero
                pixels[i] = Color.HSVToColor(hsvPixels[i]);
            }
            Log.d("DEBUG", "saturation zeroed");
        }
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

	public void auto_levels(float[][] hsvPixels, int[] pixels, int bins) {

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
		Log.d("DEBUG", "AL: Histogram 2");
		hist_array = make_histogram(hsvPixels);

		// Total intensitet i bilden
		sum_intensity = 0;
		for (int i = 0 ; i < hist_array.length; ++i ) {
			sum_intensity += hist_array[i];
		}



		int sum_bins = 0;
		int edge_i = 0;

		int[] edge_x = new int[bins-1];

		Log.d("DEBUG", "AL: Kanter");

		for (int j = 1 ; j < bins; ++j){

			while(sum_bins < (sum_intensity*j/bins)){
				sum_bins += hist_array[edge_i];
				++edge_i;
			}

			edge_x[j-1] = edge_i;

		}

		//
		// Hitta rätt gamma genom att minimera felfunktionen
		double gamma = 1,dx,bdd = (double)255;

		int curve_side=0;

		int start = 0,stop = edge_x.length;

		if(edge_x.length > 10){ // är bara intresserad i mitten av kurvan
			start = edge_x.length/3;
			stop = edge_x.length - start;
		}


		for(int i = start; i < stop; ++i){
			curve_side += Math.round( edge_x[i] - (float)255/bins*(float)(i+1)  );
		}
		Log.d("DEBUG", "AL:curve_size :" + curve_side);

		// Tar reda på om gamma är <|> 1
		if(curve_side > 0) { // gamma > 1
			dx = 0.01;
			Log.d("DEBUG", "AL:gamma > 1");
		}
		else { //gamma < 1
			dx = -0.01;
			Log.d("DEBUG", "AL:gamma < 1");
		}


		float bdf = 255, binsf = (float)bins,last_errro=0,error=0;
		float[] edge_y = new float[edge_x.length];
		float[] gamma_y = new float[edge_x.length];
		float[] gamma_x = new float[edge_x.length];

		for(int i = 0; i < edge_x.length; ++i){
			edge_y[i] = bdf/binsf*(float)(i+1);
		}



		for(int i = start; i < stop; ++i) {
			gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
			gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
			last_errro += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
		}
		gamma += dx;
		for(int i = start; i < stop; ++i) {
			gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
			gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
			error += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
		}

		// Interpolerar genom att minimera felfunktionen Manhattan norm
		while(error < last_errro) {
			gamma += dx;
			last_errro = error;
			error = 0;
			for(int i = start; i < stop; ++i) {
				gamma_y[i] = bdf*(float)(Math.pow((double)edge_x[i]/bdd, gamma));
				gamma_x[i] = bdf*(float)(Math.pow((double)edge_y[i]/bdd, 1/gamma));
				error += Math.abs( edge_y[i] - gamma_y[i]) + Math.abs( edge_y[i] - gamma_y[i]);
			}
		}
		gamma -= dx;

		// Minskar gamma lite då felfunktionen i vissa fall ger extrema gamma
		if(gamma > 1){
			gamma = (gamma -1.0)/2.0 + 1.0;
		}
		else{
			gamma = 1.0 - (1.0-gamma)/2.0;
		}
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
		switch (configuration) {
		case ACTION_0:
			return enhanceImageHSV(bitmap, 0); //HistEQ
		case ACTION_1:
			return enhanceImageHSV(bitmap, 1); // VT-1
		case ACTION_2:
			return enhanceImageHSV(bitmap, 2); // VT-3
        //case ACTION_3:
          //  return enhanceImageHSV(bitmap, 3); // VT-5
		case ACTION_3:
			return enhanceImageHSV(bitmap, 3); // VT-10
		//case ACTION_5:
			//return enhanceImageHSV(bitmap, 5); // VT-20
		case ACTION_4:
			return enhanceImageHSV(bitmap, 5); // VT-50
		case ACTION_5:
			return enhanceImageHSV(bitmap, 7); // VT-50
		case ACTION_6:
			return enhanceImageHSV(bitmap, 8); // al
		case ACTION_7:
			return enhanceImageHSV(bitmap, 9); // al
		case ACTION_8:
			return enhanceImageHSV(bitmap, 10); // al
		case ACTION_9:
			return enhanceImageHSV(bitmap, 20); // al
		default:
			return enhanceImageHSV(bitmap, 0);
		}
	}

	@Override
	public String[] getConfigurationOptions() {
		return new String[]{ "Histogram EQ", "V-Transform 1", "V-Transform 2", "V-Transform 3", "V-Transform 10", "V-Transform 50","Auto Levels 2","Auto Levels 10","Auto Levels 30", "Black & White"};
	}

}
