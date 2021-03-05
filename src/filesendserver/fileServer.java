/*
 * License Sinelnikov Oleg
 */
package filesendserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Передача файлов 
*/
public class fileServer implements Runnable {

  public final static int SOCKET_PORT = 16445;
  public final static  String FILE_TO_SEND = System.getProperty("user.dir")
          +File.separator+"client.conf" ;
  FileInputStream fis ;
  BufferedInputStream bis;
  OutputStream os;
  ServerSocket servsock;
  Socket sock;
  
  
  
  public void fileServer()  { //String [] args 
    fis = null;
    bis = null;
    os = null;
    servsock = null;
    sock = null;
  }  

  @Override
  public void run() {
      int i = 0;
    try {
      servsock = new ServerSocket(SOCKET_PORT);

      while (true) {
        System.out.println("Waiting...");
        try {
          sock = servsock.accept();
          while (!servsock.accept().isConnected()) {
            System.out.println("Waiting..."+(i++));
          }
          System.out.println("Accepted connection : " + sock);
          // send file
          File myFile = new File (FILE_TO_SEND);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = sock.getOutputStream();
          System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
        } catch (IOException ex) {
          System.out.println(ex.
                  getMessage()+": An Inbound Connection Was Not Resolved");
        }finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }catch (IOException ex){
        System.out.println(ex.
                  getMessage()+": An Inbound Connection Was Not Resolved");
    }
    finally {
      if (servsock != null)
        try {
            servsock.close();
      } catch (IOException ex) {
          Logger.getLogger(fileServer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
