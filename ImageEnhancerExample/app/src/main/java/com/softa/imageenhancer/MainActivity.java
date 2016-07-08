package com.softa.imageenhancer;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
//import android.app.Activity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends FragmentActivity {

	final static int SELECT_IMAGE = 10;
	private ImageView beforeImageView, afterImageView;
	private Bitmap theImage;
	private Button loadButton;
	//private Button improveButton;
	private Button zButton;
	private Button bwButton;
	private Button vButton;
	private Button alButton;
	private Button histButton;
	private Button almButton;
	private Button lButton;
	private ImageEnhancer selectedEnhancer; // <-- Ska stå i klassen
	private int selectedConfiguration;
	private ProgressDialog progressDialog;



	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		selectedEnhancer = getEnhancers().get(1); // Here we choose which enhancer to use

		//// print how much memory the app is allowed to use on this device
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		Log.d("onCreate", "maxMemory:" + Long.toString(maxMemory));
		//////////////////////////////////

		loadButton = (Button) findViewById(R.id.load_button);
		//improveButton = (Button) findViewById(R.id.improve_button);
		zButton = (Button) findViewById(R.id.z_button);
		bwButton = (Button) findViewById(R.id.bw_button);
		vButton = (Button) findViewById(R.id.v_button);
		alButton = (Button) findViewById(R.id.al_button);
		histButton = (Button) findViewById(R.id.hist_button);
		almButton = (Button) findViewById(R.id.aml_button);
		lButton = (Button) findViewById(R.id.l_button);
		//improveButton.setVisibility(View.INVISIBLE);
		zButton.setVisibility(View.INVISIBLE);
		bwButton.setVisibility(View.INVISIBLE);
		vButton.setVisibility(View.INVISIBLE);
		alButton.setVisibility(View.INVISIBLE);
		histButton.setVisibility(View.INVISIBLE);
		almButton.setVisibility(View.INVISIBLE);
		lButton.setVisibility(View.INVISIBLE);

		beforeImageView = (ImageView) findViewById(R.id.imageview1);
		afterImageView = (ImageView) findViewById(R.id.imageview2);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Processing image");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgressNumberFormat(null);

		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Zelect image:"), SELECT_IMAGE);
				zButton.setVisibility(View.INVISIBLE);
				afterImageView.setVisibility(View.INVISIBLE);

			}
		});

		beforeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (loadButton.getVisibility() == View.VISIBLE)
//					loadButton.setVisibility(View.INVISIBLE);
//				else
//					loadButton.setVisibility(View.VISIBLE);

			}
		});

//		improveButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				selectedEnhancer = getEnhancers().get(0);
//				FragmentManager fm = getSupportFragmentManager();
//				new ConfigurationDialog().show(fm, "configuration_dialog");
//				afterImageView.setVisibility(View.VISIBLE);
//				zButton.setVisibility(View.VISIBLE);
//			}
//		});

		zButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( afterImageView.getVisibility() == View.VISIBLE ){
					afterImageView.setVisibility(View.INVISIBLE);
				}
				else {
					afterImageView.setVisibility(View.VISIBLE);
				}
			}
		});

		bwButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(1);

				// Vad som behöver ändras här ifrån
				selectedConfiguration = 0;
				progressDialog.setProgress(0);
				progressDialog.show();
				new ImproveImageTask().execute(theImage);

				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		vButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(2);

				binsDialog(0);
				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		alButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(3);

				selectedConfiguration = 30;
				progressDialog.setProgress(0);
				progressDialog.show();
				new ImproveImageTask().execute(theImage);
				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		histButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(6);

				selectedConfiguration = 30;
				progressDialog.setProgress(0);
				progressDialog.show();
				new ImproveImageTask().execute(theImage);
				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		almButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(5);

				almDialog();
				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		lButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedEnhancer = getEnhancers().get(4);

				levelsDialog();
				afterImageView.setVisibility(View.VISIBLE);
				zButton.setVisibility(View.VISIBLE);

			}
		});

		afterImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (improveButton.getVisibility() == View.VISIBLE)
//					improveButton.setVisibility(View.INVISIBLE);
//				else
//					improveButton.setVisibility(View.VISIBLE);

			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();

			ParcelFileDescriptor parcelFileDescriptor;
			try {
				parcelFileDescriptor = getContentResolver().openFileDescriptor(
						selectedImage, "r");
				FileDescriptor fileDescriptor = parcelFileDescriptor
						.getFileDescriptor();
				theImage = BitmapFactory.decodeFileDescriptor(fileDescriptor);

				// get screen width and scale image to fit 
				int activityWidth = getWindow().getDecorView().getWidth();
				int activityHeight = getWindow().getDecorView().getHeight() - getStatusBarHeight();
				int width;
				int height;


				// Ändras hur bilden visas i appen
				if (theImage.getWidth() > theImage.getHeight()) { // Landscape
					width = activityWidth;
					height = theImage.getHeight() * width / theImage.getWidth(); //Keep aspect ratio
				} else {
					height = activityHeight*3 / 4;
					width = theImage.getWidth() * height / theImage.getHeight();
				}

				Log.d("DEBUG", "creating scaled BITMAP,width x height " + width + " " + height);
				theImage = Bitmap.createScaledBitmap(theImage, width,
						height, false);


				parcelFileDescriptor.close();
				beforeImageView.setImageBitmap(theImage);
				//improveButton.setVisibility(View.VISIBLE);
				bwButton.setVisibility(View.VISIBLE);
				vButton.setVisibility(View.VISIBLE);
				alButton.setVisibility(View.VISIBLE);
				histButton.setVisibility(View.VISIBLE);
				almButton.setVisibility(View.VISIBLE);
				lButton.setVisibility(View.VISIBLE);
				//loadButton.setVisibility(View.INVISIBLE);  //Hide the loadButton to not obscure the original pic.
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.softa.imageenhancer/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.softa.imageenhancer/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	private class ImproveImageTask extends AsyncTask<Bitmap, Integer, Bitmap> {

		protected Bitmap doInBackground(Bitmap... urls) {

			new Thread(
					new Runnable() {

						public void run() {
							while (progressDialog.isShowing()) {
								publishProgress();
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}).start();

			return selectedEnhancer.enhanceImage(theImage, selectedConfiguration);

		}

		protected void onProgressUpdate(Integer... progress) {

			progressDialog.setProgress(selectedEnhancer.getProgress());

		}

		protected void onPostExecute(Bitmap result) {
			afterImageView.setImageBitmap(result);
			progressDialog.dismiss();

		}
	}

	private List<ImageEnhancer> getEnhancers() {
		ArrayList<ImageEnhancer> enhancers = new ArrayList<ImageEnhancer>();

		enhancers.add(new TestEnhancer()); // Here below additional enhancers can be added
		enhancers.add(new BwEnhancer());
		enhancers.add(new VEnhancer());
		enhancers.add(new AlevelsEnhancer());
		enhancers.add(new LevelsEnhancer());
		enhancers.add(new AMlevelsEnhancer());
		enhancers.add(new HisteqEnhancer());
		return enhancers;
	}

//	public class ConfigurationDialog extends DialogFragment {
//
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//			builder.setTitle(R.string.select_configuration).setItems(
//					selectedEnhancer.getConfigurationOptions(),
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) { // << Här ändras popupen?!?!?!
//							selectedConfiguration = which;
//							progressDialog.setProgress(0);
//							progressDialog.show();
//							new ImproveImageTask().execute(theImage);
//
//						}
//					});
//			return builder.create();
//		}
//	}

	//seekbar för levels(2)
	public void levelsDialog(){

		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

		final View Viewlayout = inflater.inflate(R.layout.levels_dialog,
				(ViewGroup) findViewById(R.id.l_dialog));

		final TextView item1 = (TextView)Viewlayout.findViewById(R.id.l1_txt); // txtItem1
		final TextView item2 = (TextView)Viewlayout.findViewById(R.id.l2_txt); // txtItem1
		final TextView item3 = (TextView)Viewlayout.findViewById(R.id.l3_txt); // txtItem1
		item1.setText("lv: " + 0);
		item3.setText("hv: " + 255);
		item2.setText("gamma: " + 1);
		//popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Select Saturation");
		popDialog.setView(Viewlayout);

		//  seekBar1
		final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.l1_seekBar);
		final SeekBar seek2 = (SeekBar) Viewlayout.findViewById(R.id.l2_seekBar);
		final SeekBar seek3 = (SeekBar) Viewlayout.findViewById(R.id.l3_seekBar);
		seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				//Do something here with new value

				if(progress >= seek3.getProgress()){
					seek1.setProgress(seek3.getProgress()-1);
					progress = seek3.getProgress()-1;
				}
				item1.setText("lv: " + progress);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});

		//  seekBar1

		seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				//Do something here with new value
				double show = 0;
				if(progress+1 <= 32) {
					show = (double) (progress+1) / 32.0;
				}
				else{
					show = progress-31;
				}
				item2.setText("gamma: " + show);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});


		seek3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				//Do something here with new value
				if(progress <= seek1.getProgress()){
					seek3.setProgress(seek1.getProgress()+1);
					progress = seek1.getProgress()+1;
				}
				item3.setText("hv: " + progress);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});


		// Button OK
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						int lv =  + seek1.getProgress();
						int hv =  + seek3.getProgress();
						int gamma_i =  seek2.getProgress();
						float gamma=0;
						if(gamma_i+1 <= 32) {
							gamma = (float) (gamma_i+1) / (float)32;
						}
						else{
							gamma = gamma_i-31;
						}

						Log.d("DEBUG", "levels: lv = " + lv);
						Log.d("DEBUG", "levels: hv = " + hv);
						Log.d("DEBUG", "levels: gamma = " + gamma);

						if(gamma >= 1){
							gamma_i = (100 + (int)gamma)*1000000;
						}
						else{
							gamma_i = ((int)(gamma*100))*1000000;
						}


						int send = lv + (hv*1000) + gamma_i;
						Log.d("DEBUG", "levels: send = " + send);
						selectedConfiguration = send;
						progressDialog.setProgress(0);
						progressDialog.show();
						new ImproveImageTask().execute(theImage);
					}

				});


		popDialog.create();
		popDialog.show();

	}

	//Seekbar för V-trans(1)
	public void binsDialog(int mode){

		final int mode_inner = mode;
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

		final View Viewlayout = inflater.inflate(R.layout.activity_dialog,
				(ViewGroup) findViewById(R.id.v_dialog));

		final TextView item1 = (TextView)Viewlayout.findViewById(R.id.v_txt); // txtItem1
		item1.setText("Value of : " + 1);
		//popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Select number of bins ");
		popDialog.setView(Viewlayout);

		//  seekBar1
		final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.v_seekBar);
		if(mode > 0){
		seek1.setMax(50);
		}
		seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				//Do something here with new value
				item1.setText("Bins : " + progress);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});


		// Button OK
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						int bins = 1 + seek1.getProgress() + mode_inner;
						selectedConfiguration = bins;
						progressDialog.setProgress(0);
						progressDialog.show();
						new ImproveImageTask().execute(theImage);
						Log.d("DEBUG", "V-TRANSFORMATION BINS = " + bins);
					}

				});


		popDialog.create();
		popDialog.show();

	}

	//Seekbar för V-trans(1)
	public void almDialog(){

		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

		final View Viewlayout = inflater.inflate(R.layout.amlevels_dialog,
				(ViewGroup) findViewById(R.id.aml_dialog));

		final TextView item1 = (TextView)Viewlayout.findViewById(R.id.aml_txt); // txtItem1
		item1.setText("gamma : " + 1);
		//popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Select gamma ");
		popDialog.setView(Viewlayout);

		//  seekBar1
		final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.aml_seekBar);
		seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				//Do something here with new value
				double show = 0;
				if(progress+1 <= 32) {
					show = (double) (progress+1) / 32.0;
				}
				else{
					show = progress-31;
				}
				item1.setText("gamma: " + show);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

		});


		// Button OK
		popDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						float gamma =0;
						int gamma_i = seek1.getProgress();
						if(gamma_i+1 <= 32) {
							gamma = (float) (gamma_i+1) / (float)32;
						}
						else{
							gamma = gamma_i-31;
						}

						gamma_i = (int)(gamma*100);
						selectedConfiguration = gamma_i;
						progressDialog.setProgress(0);
						progressDialog.show();
						new ImproveImageTask().execute(theImage);
						Log.d("DEBUG", "V-TRANSFORMATION BINS = " + gamma_i);
					}

				});


		popDialog.create();
		popDialog.show();

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		//getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//
//
//	}


}
