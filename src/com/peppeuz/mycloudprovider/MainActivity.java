package com.peppeuz.mycloudprovider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnClickListener, OnCompletionListener{
	private static final int PICK_FILE_REQUEST_CODE = 0;
	Button apriVideo;
	VideoView videoPlayer;
	TextView titolo;
	 private MediaController ctlr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		apriVideo = (Button) findViewById(R.id.apriFile);
		titolo = (TextView) findViewById(R.id.videoTitle);
		videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
		apriVideo.setOnClickListener(this);
		videoPlayer.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v==apriVideo)	
		{
		selectVideo();	
				
		}
	}
	
	public void selectVideo()
	{
		Intent chooseIntent;

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
		    chooseIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		    chooseIntent.addCategory(Intent.CATEGORY_OPENABLE);
		}else{
		    chooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
		}

		chooseIntent.setType("video/*");
		startActivityForResult(chooseIntent, PICK_FILE_REQUEST_CODE);

	}

	//Ottenimento del video
	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent resultData) {
	    if ((requestCode == PICK_FILE_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
	        if (resultData != null) {
	            //Ottieni la fonte dei dati
	            Uri fileUri = resultData.getData();
	            //prelevi il path che ti servirà per mostrare il video
	            String filePath = fileUri.toString();
	            //Ora viene il difficile
	            //Tramite l'array projection specifici quello che ti serve
	            //Se vuoi altre costanti, le metti come altri elementi dell'Array, trovi le costanti supportate qui
	            // http://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html
	            // http://developer.android.com/reference/android/provider/MediaStore.Video.VideoColumns.html
	            String[] projection = {MediaStore.Video.Media.DISPLAY_NAME};
	            //Ottieni il cursore specificando quali campi vuoi estrarre dall'URI
	            Cursor cursor = getContentResolver().query(fileUri,projection,null,null,null);
	            //se trova qualcosa nel primo record, estrae i dati
	            if(cursor.moveToFirst()){
	                int columnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
	                String displayName = cursor.getString(columnIndex);		
	                //Mostra il video
	                playVideo(displayName,filePath);
	            }
	        }
	    }
	}
	
	

public void playVideo(String displayName, String filePath){
	titolo.setText(displayName);
	videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
	ctlr=new MediaController(this);
	//questi sono i controlli, sono facili da creare, puoi provare a modificarli se vuoi, anzi,
	//è ben accetto secondo me, se li modifichiamo unpo'
	videoPlayer.setMediaController(ctlr);
    ctlr.setMediaPlayer(videoPlayer);
	//Questo è il listener per il completamento:
	//Viene chiamato sia quando c'è un errore nella riproduzione e non hai un listener di errore che lo intercetti
	// -- se tale ErrorListener ritorna false, il completion parte comunque, evita di impostarlo a meno che non voglia
	// -- fare azioni sul video, direi
	//oppure viene chiamato quando il video è finito 
	videoPlayer.setOnCompletionListener(this);
	//gli dici che file prendere
	videoPlayer.setVideoPath(filePath);
	//fai partire il player
	videoPlayer.start();
}

@Override
public void onCompletion(MediaPlayer mp) {
	// TODO Auto-generated method stub
	Toast.makeText(getApplicationContext(), "Il video è finito (nd Captain Obvious)", Toast.LENGTH_LONG).show();
}

	
	
	
}
