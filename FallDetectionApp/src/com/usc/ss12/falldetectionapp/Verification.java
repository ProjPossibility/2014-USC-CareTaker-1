package com.usc.ss12.falldetectionapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

public class Verification extends Activity{
	private Button buttonYes;
	private Button buttonNo;
	WindowManager.LayoutParams layoutParams;
	boolean bright = false;
	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		final Timer tim = new Timer();
		final TimerTask flash1 = new TimerTask() {
			public void run()
			{
				layoutParams.screenBrightness= (bright ? 0F : .7F); //-1 = default, 0F = min, 1F = full
				getWindow().setAttributes(layoutParams);
				bright = (bright ? false : true);
			}
		};
		final TimerTask flash2 = new TimerTask() {
			public void run()
			{
				layoutParams.screenBrightness= (bright ? 0F : 1F); //-1 = default, 0F = min, 1F = full
				getWindow().setAttributes(layoutParams);
				bright = (bright ? false : true);
			}
		};
		final TimerTask lvl3 = new TimerTask() { //After 480 seconds, call emergency contact
			public void run()
			{
				tim.cancel(); //Drop all alarms
				//Call emergency contact
			}
		};
		final TimerTask lvl2 = new TimerTask() { //After 300 seconds, increase alarm intensity
			public void run()
			{
				r.stop();
				AudioManager aman = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				aman.setStreamVolume(r.getStreamType(),aman.getStreamMaxVolume(r.getStreamType()),AudioManager.FLAG_PLAY_SOUND);
				r.play();
				flash1.cancel();
				tim.schedule(flash2,500,500);
				tim.schedule(lvl3,180000);
			}
		};
		final TimerTask lvl1 = new TimerTask() { //After 180 seconds, set off alarm/flash/vibrate
			public void run()
			{
				r.play();
				tim.schedule(flash1,500,500);
				tim.schedule(lvl2,120000);
			}
		};
		tim.schedule(lvl1, 180000); 
	}
	
}
