package com.widget.tools;
import java.net.*;
import java.io.*;
import javax.net.ssl.*;


public class SendFile {

public static void send(String path, String hostname,int port)throws Exception
    {

       InetAddress  hostIA = InetAddress.getByName(hostname);
       String  host = hostIA.getHostName();
        File fs= new File(path);
        
        
  
          try {

 //set necessary keystore properties - using a p12 file
            System.setProperty("javax.net.ssl.keyStore","/dev/Alice.bks");
            System.setProperty("javax.net.ssl.keyStorePassword","psalice");
            System.setProperty("javax.net.ssl.keyStoreType", "BKS");       
                        
            //set necessary truststore properties - using JKS
            System.setProperty("javax.net.ssl.trustStore","/dev/truststore.bks");
            System.setProperty("javax.net.ssl.trustStorePassword","catrust");
            // register a https protocol handler  - this may be required for previous JDK versions
            System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
            

           System.out.println("connecting...");
           SSLSocketFactory sslFact =
               (SSLSocketFactory)SSLSocketFactory.getDefault();
           System.out.println(hostIA.toString());
           SSLSocket c = (SSLSocket)sslFact.createSocket(host, port);
           System.out.println("handshaking...");
           c.startHandshake();
           
           System.out.println("Sending File...");
           
 	    FileSender.SendFile(c, fs);
	    c.close();

          System.out.println("done...");
 
        }   catch (IOException e) {
System.out.println("Sender died: " + e.getMessage());
	    e.printStackTrace();
                    }
}
}
