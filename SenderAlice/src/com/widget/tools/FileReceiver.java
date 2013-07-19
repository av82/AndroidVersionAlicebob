package com.widget.tools;
import java.io.*;
import javax.net.ssl.*;
import java.net.*;

public class FileReceiver{
    public static void Receive(ServerSocket s)throws Exception{
       // ServerSocket ss=new ServerSocket(1234);
    	
    	int read=0;  
        int cur= 0; 
        long size=0;
    	
    	  while(true)
          {
    		  Socket  clSocket=null;
    		  clSocket= s.accept();

               
    		
    		InputStream in_stream=clSocket.getInputStream();
        	DataInputStream cdata = new DataInputStream(in_stream);   
              
            String fname = cdata.readUTF();     
            OutputStream out_stream = new FileOutputStream(fname);     
             size = cdata.readLong();     
            byte[] buffer = new byte[1024];     
            while (size > 0 && (read = cdata.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
            {     
                out_stream.write(buffer, 0, read);     
                size -= read;     
            }  
        
            out_stream.close();
          }
    	  
    	
    	
       
        
    }
}
