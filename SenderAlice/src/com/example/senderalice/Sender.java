package com.example.senderalice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.widget.tools.*;


import com.ipaulpro.afilechooser.utils.FileUtils;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class Sender extends Activity {

	private static final int REQUEST_CHOOSER = 1234;
	
	private String fsendname;
	
	private KeyStore AliceKeyStore;
	  
	  /**
	   * KeyStore for storing the CA's public key
	   */
	  private KeyStore CAStore;
	private SSLContext sslContext;
	static private SecureRandom secureRandom;
	
	
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
			String ipaddport[] = splitname.getText().toString().split(":");
			
			try
			{
				
				TextView tfile = (TextView) findViewById(R.id.textView2);
				tfile.setText("Sending File to" +  fsendname + "\n" + ipaddport[0] +
						"\n"+ipaddport[1]);
				
			//	Alice ali = new Alice();
				send(fsendname, ipaddport[0], ipaddport[1]);
				
				
				
				
			}
			
		catch (UnknownHostException e) {
				
				TextView tfile = (TextView) findViewById(R.id.textView2);
				tfile.setText("Unknown Host\n" + e.toString());
				
			        }
			catch (Exception e) {
				TextView tfile = (TextView) findViewById(R.id.textView2);
				tfile.setText("Please retry \n" + e.toString());
			}
			
		}
		
	}
	
	
	
	public  void send(String path, String hostname,String prt)throws Exception
    {

 // USAGE: java Alice host port pathtofile
// using JSSE java ssl extension api for calls 
//documentation: https://www.ibm.com/developerworks/java/tutorials/j-jsse/section5.html

       InetAddress  hostIA = InetAddress.getByName(hostname);
       String  host = hostIA.getHostName();
       File fs= new File(path);
       int port = Integer.parseInt(prt);

//       TextView splitname=(TextView) findViewById(R.id.edtText);
//       splitname.setText(splitname.getText()+ "\n" + "testing");
//       
//        
	
       try {

   

 		//set necessary keystore properties 
                // Alice Key Store is set up at Alice.jks , password psalice
            System.setProperty("javax.net.ssl.keyStore","Alice.jks");
            System.setProperty("javax.net.ssl.keyStorePassword","psalice");
            System.setProperty("javax.net.ssl.keyStoreType", "JKS");       
                        
            //set necessary truststore properties - using JKS
            // certificates in truststore are trusted and we trust only ca certificate
            // preprocessing of ceritificate generation is performed using openssl and keystore available in scripts.txt
            // generate keypair for ca, ALICe, BOB request to sign their public keys using commands provided by openssl library
            System.setProperty("javax.net.ssl.trustStore","truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword","catrust");   //password for truststore which has ca certificate
            // register a https protocol handler  - this may be required for previous JDK versions
            System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
            
            
             System.out.println("connecting...");


      SSLSocket c = connect( host, port );
     

           System.out.println("Handshaking...");
           c.startHandshake();
         
             System.out.println("Sending File...");
          
 	    FileSender.SendFile(c, fs);
	    c.close();

          System.out.println("done...");
 
        }   catch (IOException e) {
System.out.println("Alice died: " + e.getMessage());
	    e.printStackTrace();
                    }
}


	private void setupAliceKeyStore() throws GeneralSecurityException, IOException {
		  
		  AssetManager asmgr = getResources().getAssets();
		  AliceKeyStore = KeyStore.getInstance( "BKS" );
		  
		  InputStream ims = asmgr.open("Alice.bks");
		  	//keyStore.load(new FileInputStream("Bob.bks"), passwd.toCharArray()); 
		  AliceKeyStore.load( ims,
	                       "psalice".toCharArray() );
	  }

	 private void setupTruststore() throws GeneralSecurityException, IOException {
		 AssetManager asmgr = getResources().getAssets();
		 
		  CAStore = KeyStore.getInstance( "BKS" );
		  
		  InputStream ims = asmgr.open("truststore.bks");
		  	//keyStore.load(new FileInputStream("Bob.bks"), passwd.toCharArray()); 
		  CAStore.load( ims,
	                      "catrust".toCharArray() );

	  }


	 private void setupSSLContext() throws GeneralSecurityException, IOException {
	    TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
	    tmf.init( CAStore );

	    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
	    kmf.init(AliceKeyStore,"psalice".toCharArray());
	    sslContext = SSLContext.getInstance( "TLS" );
	    sslContext.init( kmf.getKeyManagers(),
	                     tmf.getTrustManagers(),
	                     secureRandom );
	  }
	 
	 private SSLSocket connect( String host, int port ) {
		    try {
		      setupTruststore();
		      setupAliceKeyStore();
		      setupSSLContext();

		      SSLSocketFactory sf = sslContext.getSocketFactory();
		      SSLSocket socket = (SSLSocket)sf.createSocket( host, port );
		       return socket;
		    }
		    catch( GeneralSecurityException e ) {
		        e.printStackTrace();
		      } catch( IOException e ) {
		        e.printStackTrace();
		      }
		    return null;
		    
		    
	 }


	
	
	
	
}
