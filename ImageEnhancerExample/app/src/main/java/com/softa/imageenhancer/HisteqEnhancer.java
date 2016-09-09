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

		final float[] hist_array_cs = new float[256];
		hist_array_cs[0] = (float)hist_array[0]*scalar;

		// kumulativa fördelningsfunktionen
		for (int i = 1; i < hist_array.length; i++) {
			hist_array_cs[i] = (float)hist_array[i]*scalar + hist_array_cs[i-1];
		}
		progress = 70;

		// Transformera tillbaka
		float old_i,new_i,f255 = 255;
		final int pl = hsvPixels.length/3;
		final int pl2 = pl*2;
		final int pl3 = hsvPixels.length;

		final float[][] hsvPixelsT = hsvPixels;
		final int[] pixelsT = pixels;
		//Loop1
		Thread t1 = new Thread(new Runnable()
		{
			public void run()
			{
				float old_i,new_i,f255 = 255;
				System.out.println("1 loop start: TB");
				for (int i = 0; i < pl; i++) {
					old_i = hsvPixelsT[i][2];
					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;

					hsvPixelsT[i][2] = new_i;

					//Konvertera tillbaka till RGB
					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
				}
			}
		});

		t1.setPriority(10);
		t1.start();

		//Loop1
		Thread t2 = new Thread(new Runnable()
		{
			public void run()
			{
				float old_i,new_i,f255 = 255;
				System.out.println("2 loop start: TB");
				for (int i = pl; i < pl2; i++) {
					old_i = hsvPixelsT[i][2];
					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;

					hsvPixelsT[i][2] = new_i;

					//Konvertera tillbaka till RGB
					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
				}
			}
		});

		t2.setPriority(10);
		t2.start();

		//Loop1
		Thread t3 = new Thread(new Runnable()
		{
			public void run()
			{
				float old_i,new_i,f255 = 255;
				System.out.println("3 loop start: TB");
				for (int i = pl2; i < pl3; i++) {
					old_i = hsvPixelsT[i][2];
					new_i = hist_array_cs[ Math.round( old_i*f255 ) ]/f255;

					hsvPixelsT[i][2] = new_i;

					//Konvertera tillbaka till RGB
					pixelsT[i] = Color.HSVToColor(hsvPixelsT[i]);
				}
			}
		});

		t3.setPriority(10);
		t3.start();

		while(t1.isAlive() ||t2.isAlive()||t3.isAlive()){
			continue;
		}


		pixels = pixelsT;
		hsvPixels = hsvPixelsT;

		progress = 95;

	}



	public int[] make_histogram(float[][] hsvPixels){

		final int[] hist_array = new int[256];


		final float[][] hsvPixelsT = hsvPixels;
		final int pl = hsvPixels.length/3;
		final int pl2 = pl*2;
		final int pl3 = hsvPixels.length;

		//Loop1
		Thread t1 = new Thread(new Runnable()
		{
			public void run()
			{
				int intens;
				System.out.println("1 loop start: Make hist");
				for (int i = 0; i < pl; i++) {
					intens = Math.round( hsvPixelsT[i][2]*255 );

					hist_array[ intens ] += 1;
				}
			}
		});

		t1.setPriority(10);
		t1.start();

		//Loop2
		Thread t2 = new Thread(new Runnable()
		{
			public void run()
			{
				int intens;
				System.out.println("2 loop start: Make hist");
				for (int i = pl; i < pl2; i++) {
					intens = Math.round( hsvPixelsT[i][2]*255 );

					hist_array[ intens ] += 1;
				}
			}
		});

		t2.setPriority(10);
		t2.start();

		//Loop2
		Thread t3 = new Thread(new Runnable()
		{
			public void run()
			{
				int intens;
				System.out.println("3 loop start: Make hist");
				for (int i = pl2; i < pl3; i++) {
					intens = Math.round( hsvPixelsT[i][2]*255 );

					hist_array[ intens ] += 1;
				}
			}
		});

		t3.setPriority(10);
		t3.start();

		while(t1.isAlive() ||t2.isAlive()||t3.isAlive()){
			continue;
		}

		return hist_array;

	}





	private float[][] convertToHSV(int[] pixels) {

		final float[][] hsvPixels = new float[pixels.length][3];
		final int[] pixelsth = pixels;

		final int pl = pixelsth.length/3;
		final int pl2 = pl*2;
		final int pl3 = pixelsth.length;
		//final int pl4 = pixelsth.length;

		//Loop1
		Thread t1 = new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("1 loop start");
				for (int i = 0; i < pl; i++) {
					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);

				}
			}
		});

		t1.setPriority(10);
		t1.start();

		//Loop2
		Thread t2 = new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("2 loop start");
				for (int i = pl; i < pl2; i++) {
					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);

				}
			}
		});

		t2.setPriority(10);
		t2.start();

		//Loop1
		Thread t3 = new Thread(new Runnable()
		{
			public void run()
			{
				System.out.println("3 loop start");
				for (int i = pl2; i < pl3; i++) {
					Color.RGBToHSV(Color.red(pixelsth[i]), Color.green(pixelsth[i]), Color.blue(pixelsth[i]), hsvPixels[i]);

				}
			}
		});

		t3.setPriority(9);
		t3.start();



		while(t1.isAlive() || t2.isAlive() || t3.isAlive()){
			continue;
		}

		return hsvPixels;
	}







	private float[][] convertToHSV2(int[] pixels) {
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


//public class rgb2hsvThread extends Thread {
//
//	public float[][] run(int[] pixels){
//		System.out.println("MyThread running");
//		float[][] hsvPixels = new float[pixels.length][3];
//		for (int i = 0; i < pixels.length; i++) {
//
//
//		}
//		return Color.RGBToHSV(Color.red(pixels[i]), Color.green(pixels[i]), Color.blue(pixels[i]), hsvPixels[i]);;
//	}
//}