package com.widget.tools;
import java.io.*;
import javax.net.ssl.*;


public class FileSender{
    public static void SendFile(SSLSocket clientSocket, File fout )throws Exception{
       try
       {
    	//Socket clientSocket=new Socket(servaddress,port);
        //InputStream in=clientSocket.getInputStream();
        OutputStream out=clientSocket.getOutputStream();
        
        //PrintStream ps=new PrintStream(out);
        
        byte[] fsizearray = new byte[(int) fout.length()];  
        FileInputStream f_instream=new FileInputStream(fout);
        BufferedInputStream bin = new BufferedInputStream(f_instream);  
        
        DataInputStream dis = new DataInputStream(bin);     
        dis.readFully(fsizearray, 0, fsizearray.length);  
        
        //Sending file name and file size to the server  
        DataOutputStream dos = new DataOutputStream(out);     
        dos.writeUTF(fout.getName());     
        dos.writeLong(fsizearray.length);     
        dos.write(fsizearray, 0, fsizearray.length);     
        dos.flush();
        
        
        out.close();
       }
       catch (IOException e) {
		System.out.println(e.toString());
	}
    }
}
