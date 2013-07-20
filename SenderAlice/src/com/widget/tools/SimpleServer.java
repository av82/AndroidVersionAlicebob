package com.widget.tools;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class SimpleServer {

    public static void main(String args[]) throws Exception
    {
        
        
        
        String passphrase = args[1];
        int port = Integer.parseInt(args[0]);
        
        ServerSocket s=null;
        
        int bytesRead=0;        
        int read=0;
        long size=0;
        
        System.out.println("USAGE: java SimpleServer port passphrase");

        try {
  
 System.setProperty("javax.net.ssl.trustStore","truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword","catrust");
            // register a https protocol handler  - this may be required for previous JDK versions
            System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");

            System.out.println("creating socket...");
            ServerSocketFactory ssf =
		           SimpleServer.getServerSocketFactory(passphrase);
	      s = ssf.createServerSocket(port);
           
            
((SSLServerSocket)s).setNeedClientAuth(true);
            System.out.println("waiting for connection...");  
         
            
            
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
                System.out.println("Received File.. " + fileName + "  from Alice");
                break;
            }
        	  
	      
          
        
        } catch (IOException e) {
                System.out.println("SimpleServer died: " + e.getMessage());
	          e.printStackTrace();
        }
               
        System.out.println("done...");
   }

   private static ServerSocketFactory getServerSocketFactory(String passwd) {
	    SSLServerSocketFactory ssf = null;

	    try {
		// set up key manager to do server authentication
		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks;
		char[] passphrase = passwd.toCharArray();

		ctx = SSLContext.getInstance("TLS");
		kmf = KeyManagerFactory.getInstance("SunX509");
		ks = KeyStore.getInstance("bks");

		ks.load(new FileInputStream("Bob.jks"), passphrase);
		kmf.init(ks, passphrase);
		ctx.init(kmf.getKeyManagers(), null, null);

		ssf = ctx.getServerSocketFactory();
		return ssf;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	
	   return null;
  }

}
