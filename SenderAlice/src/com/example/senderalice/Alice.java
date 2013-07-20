package com.example.senderalice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import com.widget.tools.*;
import android.content.res.AssetManager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Enumeration;

import javax.net.*;
import javax.net.ssl.*;
import android.content.res.AssetManager;


public class Alice extends Activity{

private KeyStore AliceKeyStore;
  
  /**
   * KeyStore for storing the CA's public key
   */
  private KeyStore CAStore;
private SSLContext sslContext;
static private SecureRandom secureRandom;
  

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

Alice nalice= new Alice();

      SSLSocket c = nalice.connect( host, port );
     

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
	  AliceKeyStore = KeyStore.getInstance( "JKS" );
	  
	  InputStream ims = asmgr.open("Alice.bks");
	  	//keyStore.load(new FileInputStream("Bob.bks"), passwd.toCharArray()); 
	  AliceKeyStore.load( ims,
                       "psalice".toCharArray() );
  }

 private void setupTruststore() throws GeneralSecurityException, IOException {
	 AssetManager asmgr = this.getResources().getAssets();
	 
	  CAStore = KeyStore.getInstance( "JKS" );
	  
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


