package com.usc.ss12.falldetectionapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class Verification extends Activity {
	private Button buttonYes;
	private Button buttonNo;
	WindowManager.LayoutParams layoutParams;
	boolean bright;
	Uri notification;
	Ringtone r;
	Timer tim;
	
	private final String contactFileName = "contact.txt";
	
	PowerManager.WakeLock wl;
	
	private final Handler handler = new Handler() {
		 
        public void handleMessage(Message msg) {
             
            int aResponse = msg.getData().getInt("message");

            if ((aResponse == 1)) {
            	layoutParams.screenBrightness = (bright ? 0.1F : 0.7F); //-1 = default, 0F = min, 1F = full
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				getWindow().setAttributes(layoutParams);
            }else
            
            if ((aResponse == 2)){
            	layoutParams.screenBrightness = (bright ? 0.1F : 1F); //-1 = default, 0F = min, 1F = full
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				getWindow().setAttributes(layoutParams);
            }

        }
    };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
		
		bright = false;
		
		layoutParams = this.getWindow().getAttributes();
		notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		tim = new Timer();
		
		buttonYes = (Button) findViewById(R.id.buttonYes);
		buttonYes.setOnClickListener(new OnClickListener() {           
			@Override
			public void onClick(View v) {
				wl.release();
				r.stop();
				tim.cancel();
				finish();
			}    
		});
		
		buttonNo = (Button) findViewById(R.id.buttonNo);
		
		final TimerTask flash1 = new TimerTask() {
			public void run()
			{
				Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("message", 1);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
				bright = !bright;
			}
		};
		final TimerTask flash2 = new TimerTask() {
			public void run()
			{
				Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("message", 2);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
				bright = !bright;
			}
		};
		final TimerTask lvl3 = new TimerTask() { //After 480 seconds, call emergency contact
			public void run()
			{
				tim.cancel(); //Drop all alarms
				r.stop();
				//Contact emergency number
				sendAndCall(null);
				
				tim.cancel();
			}
		};
		final TimerTask lvl2 = new TimerTask() { //After 300 seconds, increase alarm intensity
			public void run()
			{
				AudioManager aman = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				aman.setStreamVolume(r.getStreamType(),aman.getStreamMaxVolume(r.getStreamType()),AudioManager.FLAG_PLAY_SOUND);
				flash1.cancel();
				tim.schedule(flash2,500,500);
				tim.schedule(lvl3,6000);
			}
		};
		final TimerTask lvl1 = new TimerTask() { //After 180 seconds, set off alarm/flash/vibrate
			public void run()
			{
				r.play();
				tim.schedule(flash1,500,500);
				tim.schedule(lvl2,4000);
			}
		};
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		 wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wake");
		 wl.acquire();
		tim.schedule(lvl1, 2000);
	}
	
	@Override
	public void onBackPressed() {
	   // Swallow all back button presses
	}
	
	private Contact loadContact() {
		Contact contact = new Contact();
		
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			in = new BufferedInputStream(openFileInput(this.contactFileName));
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			
			contact.name = br.readLine();
			contact.cell = br.readLine();
			contact.phoneOther = br.readLine();
			contact.email = br.readLine();
			
			br.close();
			isr.close();
			in.close();
		} catch(FileNotFoundException e) {
			// will happen until user creates their emergency contact for the first time
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return contact;
	}
	
	private void sendAndCall(View view) {
		//Contact emergency number
		Contact c = loadContact();
		
		if (c != null) {
			String number = c.cell;
			String msg = "[SmartAlert] There's a good chance I might have fallen and am injured. Please help.";
			
			SmsManager man = SmsManager.getDefault();
			man.sendTextMessage(number, null, msg, null, null);
			
         	Intent callIntent = new Intent(Intent.ACTION_CALL);
         	callIntent.setData(Uri.parse("tel:" + c.cell));
         	startActivity(callIntent);
		}
	}
	
}
