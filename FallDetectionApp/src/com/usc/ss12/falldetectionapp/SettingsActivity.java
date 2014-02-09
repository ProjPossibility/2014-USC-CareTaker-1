package com.usc.ss12.falldetectionapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private boolean editing = false;
	private final String contactFileName = "contact.txt";
	private Contact contact;
	
	// Buttons
	private Button buttonSave;
	private Button buttonEdit;
	
	// Text fields
	private EditText textName;
	private EditText textCell;
	private EditText textPhone;
	private EditText textEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {           
			@Override
			public void onClick(View v) {
				if (validateData()) {
					retrieveContactInfo();
					saveContact();
					Toast.makeText(SettingsActivity.this, "Contact Saved!", Toast.LENGTH_SHORT).show();
					finish();
				}
			}    
		});
		
		buttonEdit = (Button) findViewById(R.id.buttonEdit);
		buttonEdit.setOnClickListener(new OnClickListener() {           
			@Override
			public void onClick(View v) {
				if (!editing) {
					enableContactEntry();
					editing = !editing;
				}
			}    
		});
		
		// Text fields
		textName = (EditText) findViewById(R.id.editTextName);
		textCell = (EditText) findViewById(R.id.editTextCell);
		textCell.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		textPhone = (EditText) findViewById(R.id.editTextPhoneOptional);
		textPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		textEmail = (EditText) findViewById(R.id.editTextEmail);
		
		contact = new Contact();
		if (loadContact()) {
			populateContactInfo();
		}
		
		disableContactEntry();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void buttonSaveOnClick(View v) {
		saveContact();
		finish();
	}
	
	private boolean loadContact() {
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
			// will happen until user creates their emergency contact for the first time
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
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
	
	private void retrieveContactInfo() {
		contact.name = textName.getText().toString();
		contact.cell = textCell.getText().toString();
		contact.phoneOther = textPhone.getText().toString();
		contact.email = textEmail.getText().toString();
	}
	
	private void populateContactInfo() {
		textName.setText(contact.name);
		textCell.setText(contact.cell);
		textPhone.setText(contact.phoneOther);
		textEmail.setText(contact.email);
	}
	
	private void disableContactEntry() {
		textName.setFocusable(false);
		textName.setFocusableInTouchMode(false);
		textName.setCursorVisible(false);
		textName.setClickable(false);
		textName.setEnabled(false);
		
		textCell.setFocusable(false);
		textCell.setFocusableInTouchMode(false);
		textCell.setCursorVisible(false);
		textCell.setClickable(false);
		textCell.setEnabled(false);
		
		textPhone.setFocusable(false);
		textPhone.setFocusableInTouchMode(false);
		textPhone.setCursorVisible(false);
		textPhone.setClickable(false);
		textPhone.setEnabled(false);
		
		textEmail.setFocusable(false);
		textEmail.setFocusableInTouchMode(false);
		textEmail.setCursorVisible(false);
		textEmail.setClickable(false);
		textEmail.setEnabled(false);
	}
	
	private void enableContactEntry() {
		textName.setFocusable(true);
		textName.setFocusableInTouchMode(true);
		textName.setCursorVisible(true);
		textName.setClickable(true);
		textName.setEnabled(true);
		textName.requestFocus();
		
		textCell.setFocusable(true);
		textCell.setFocusableInTouchMode(true);
		textCell.setCursorVisible(true);
		textCell.setClickable(true);
		textCell.setEnabled(true);
		
		textPhone.setFocusable(true);
		textPhone.setFocusableInTouchMode(true);
		textPhone.setCursorVisible(true);
		textPhone.setClickable(true);
		textPhone.setEnabled(true);
		
		textEmail.setFocusable(true);
		textEmail.setFocusableInTouchMode(true);
		textEmail.setCursorVisible(true);
		textEmail.setClickable(true);
		textEmail.setEnabled(true);
	}
	
	private boolean validateData() {
		return validateCell() && validateEmail();
	}
	
	private boolean validateCell() {
		return true;
	}
	
	private boolean validateEmail() {
		boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = textEmail.getText().toString();

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}

}
