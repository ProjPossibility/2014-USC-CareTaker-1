package com.usc.ss12.falldetectionapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

public class MainActivity extends Activity {
	
	private Timer checkImmobile = new Timer();
    private TimerTask ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onSettingsButtonClick(View v) {
    	Log.d("test", "Clicked settings button");
    	Intent settingsIntent = new Intent(this, SettingsActivity.class);
    	startActivity(settingsIntent);
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) //Whenever anything is pressed
	{
    	if(ok != null)
    		ok.cancel();
    	
    	ok = new TimerTask()
    	{
			public void run()
			{
				int from = 100;
		    	int to = 601;
		    	Calendar c = Calendar.getInstance();
		    	int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
		    	if(t < from && t > to) {
		    		//Intent notif = new Intent(this,PUTSOMETHINGHERE);
		    		//startActivity(notif);
		    	}
		    	else onKeyDown(-1,new KeyEvent(0,0)); //Resets timer if sleeping
			}
		};
		if(keyCode == -1) { //If sleeping, sets timer to 10:00am
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 10);
			c.set(Calendar.MINUTE,0);
			c.set(Calendar.SECOND,0);
			checkImmobile.schedule(ok,c.getTime());	
		}
		else checkImmobile.schedule(ok,14400000); //4 Hours == 14400000
		return false; //Allows event to continue propagating
	}
}
