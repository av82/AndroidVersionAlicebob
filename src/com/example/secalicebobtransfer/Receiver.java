package com.example.secalicebobtransfer;
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


public class Receiver extends Activity {

	//private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receiver);
try
{
	

		InitServer("pwdbob", 6000);
}
catch (Exception e) {
	System.out.println(e.toString());
}



	}
	
	
	
	 private void InitServer(String pass, int prt) throws Exception
	    {
	        
	        
	        
	        String passphrase = pass;
	        int port = prt;
	        
	        ServerSocket s=null;
	        
	        int bytesRead=0;        
	        int read=0;
	        long size=0;
	        
	        //System.out.println("USAGE: java Receiver port passphrase");

	        try {
	  
	            System.setProperty("javax.net.ssl.trustStore","truststore.bks");
	            System.setProperty("javax.net.ssl.trustStorePassword","catrust");
	            // register a https protocol handler  - this may be required for previous JDK versions
	            System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");

	            //System.out.println("creating socket...");
	            
	        	setContentView(R.layout.activity_receiver);
	        	TextView textView = (TextView) findViewById(R.id.text2);
	        	textView.setText("Initializing Server");
	        	textView.setText(textView.getText()+"\ncreating socket...at port "+prt);
		          
	        	AssetManager assetManager = getResources().getAssets();
	        	
	        	
	              ServerSocketFactory ssf =
			           Receiver.getServerSocketFactory(assetManager, passphrase);
		      s = ssf.createServerSocket(port);
		      
		      ((SSLServerSocket)s).setNeedClientAuth(true);
		      textView.setText(textView.getText()+"\n"+"waiting for connection...");
	            
		      
	           // System.out.println("waiting for connection...");  
		       
		       String ipaddress= Receiver.GetLocalIpAddress();
		       
		       textView.setText(textView.getText()+"\n"+ "at"+ ipaddress + port);
	            
		       
	            
	            while(true) {  
	                Socket clientSocket = null;  
	                clientSocket = s.accept();  
	                  
	                InputStream in = clientSocket.getInputStream();  
	                  
	                DataInputStream clientData = new DataInputStream(in);   
	                  
	                String fileName = clientData.readUTF();     
	                OutputStream output = new FileOutputStream(fileName);     
	                size= clientData.readLong();     
	                byte[] buffer = new byte[1024];     
	                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
	                {     
	                    output.write(buffer, 0, bytesRead);     
	                    size -= bytesRead;     
	                }  
	                  
	                // Closing the FileOutputStream handle  
	                output.close();  
	                textView.setText(textView.getText()+"Received File.. " + fileName + "  from Alice");
	                break;
	            }
	        	  
		      
	          
	        
	        } catch (IOException e) {
	                System.out.println("Server Error: " + e.getMessage());
		          e.printStackTrace();
	        }
	               
	        System.out.println("done...");
	   }

	   private static ServerSocketFactory getServerSocketFactory(AssetManager asmgr, String passwd) {
		    SSLServerSocketFactory ssf = null;
                 
		    try {
		    	
		    		 String keyStoreType = "BKS";
		    	   KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		    	   InputStream ims = asmgr.open("Bob.bks");
			  	//keyStore.load(new FileInputStream("Bob.bks"), passwd.toCharArray()); 
			  	keyStore.load(ims,passwd.toCharArray());
			     
			  	String keyalg=KeyManagerFactory.getDefaultAlgorithm();
			      KeyManagerFactory kmf=KeyManagerFactory.getInstance(keyalg);
			      kmf.init(keyStore, passwd.toCharArray());
			      
			      
			  
			      SSLContext context = SSLContext.getInstance("TLS");
			     
			      context.init(kmf.getKeyManagers(), null, null);
			       ssf=context.getServerSocketFactory();
		       return ssf;
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		
		   return null;
		   
		   
		   
		   
		  
	   }

	
	   private static String GetLocalIpAddress()
	    {
	        try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
	                        return inetAddress.getHostAddress().toString();
	                    }
	                }
	            }
	        } catch (SocketException ex) {
	            return "ERROR Obtaining IP";
	        }
	        return "No IP Available";   
	    }

	
}
