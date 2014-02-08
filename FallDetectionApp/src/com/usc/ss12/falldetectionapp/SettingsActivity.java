package com.usc.ss12.falldetectionapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class SettingsActivity extends Activity {
	private static boolean infoSaved = false;
	private final String contactFileName = "contact.txt";
	private Contact contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		contact = new Contact();
		saveContact();
		
		loadContact();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	private void loadContact() {
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			in = new BufferedInputStream(openFileInput(this.contactFileName));
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			
			this.contact.name = br.readLine();
			this.contact.cell = br.readLine();
			this.contact.phoneOther = br.readLine();
			this.contact.email = br.readLine();
			
			br.close();
			isr.close();
			in.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveContact() {
		FileOutputStream outputStream = null;
		OutputStreamWriter osw = null;

		try {
		  outputStream = openFileOutput(this.contactFileName, Context.MODE_PRIVATE);
		  osw = new OutputStreamWriter(outputStream);
		  
		  osw.write(this.contact.name + "\n");
		  osw.write(this.contact.cell + "\n");
		  osw.write(this.contact.phoneOther + "\n");
		  osw.write(this.contact.email + "\n");
		  
		  osw.close();
		  outputStream.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
