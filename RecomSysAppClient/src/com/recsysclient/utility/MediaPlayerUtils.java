package com.recsysclient.utility;


import com.recsysclient.R;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerUtils {

	private static MediaPlayer mediaPlayer;
	
	public static void playRecStarted(Context context){
		mediaPlayer = MediaPlayer.create(context, R.raw.start_rec);
		mediaPlayer.start();
	}
	
	public static void playRecEnded(Context context){
		mediaPlayer = MediaPlayer.create(context, R.raw.end_rec);
		mediaPlayer.start();
	}
}
