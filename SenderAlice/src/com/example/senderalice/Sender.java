package com.example.senderalice;

import java.io.File;
import java.net.UnknownHostException;

import com.widget.tools.*;


import com.ipaulpro.afilechooser.utils.FileUtils;

import android.R.integer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Sender extends Activity {

	private static final int REQUEST_CHOOSER = 1234;
	private TextView fname;
	private String fsendname;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sender);
		  
	     if( Build.VERSION.SDK_INT >= 9){
	            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	            StrictMode.setThreadPolicy(policy); 
	     }

 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sender, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case REQUEST_CHOOSER:   
	            if (resultCode == RESULT_OK) {  
	                final Uri uri = data.getData();
	                File file = FileUtils.getFile(uri);
	                setContentView(R.layout.activity_sender);
		        	TextView tfile = (TextView) findViewById(R.id.textView2);
		        	tfile.setText(file.toString());
		        	fsendname=file.toString();
		        	
	                
	            }
	    }
	
	}
	
	public void onClick(View view) {
		Intent getContentIntent  = FileUtils.createGetContentIntent();
		Intent intent = Intent.createChooser(getContentIntent, "Select a file");
		startActivityForResult(intent, REQUEST_CHOOSER);
		
	}

	
	public void onClick_Send(View view) {
		
		setContentView(R.layout.activity_sender);
//		if(fsendname.isEmpty())
//		{
//			
//        	TextView tfile = (TextView) findViewById(R.id.textView2);
//        	tfile.setText("Please Select a file!");
//			
//		}
		
		//else
		{
			
			TextView splitname=(TextView) findViewById(R.id.edtText);
			String hostarray[]=splitname.toString().split(":");
			
			try
			{
			SendFile.send("/dev/1.png", "10.0.2.2", 5000);
			}
			
			catch (UnknownHostException e) {
				
				TextView tfile = (TextView) findViewById(R.id.textView2);
				tfile.setText("Please retry \n" + e.toString());
				
				        }
			catch (Exception e) {
				TextView tfile = (TextView) findViewById(R.id.textView2);
				tfile.setText("Please retry \n" + e.toString());
			}
			
		}
		
	}
}
