package towwayserialcomm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Serial
{
    public static void main(String[]args)
    {
        try 
        {
            connect("COM3");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    static boolean connect(String portName) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort comPort = portIdentifier.open("", 2000);
           
            SerialPort serialPort = (SerialPort) comPort;
            serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            SerialReader reader = new SerialReader(in);
            SerialWriter writer = new SerialWriter(out);
            
            Thread readerThread = new Thread(reader);
            Thread writerThread = new Thread(writer);
            writerThread.start();
            readerThread.start();
            
        }
        return true;
    }

    public static class SerialReader implements Runnable {
        
        InputStream in;
        
        public SerialReader(InputStream in) 
        {
            this.in = in;
        }

        @Override
        public void run() 
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = in.read(buffer)) > -1 )
                {
                    String str = new String(buffer,0,len);
                    System.out.print(str);
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }    
        }
    }
    public static class SerialWriter implements Runnable {
       
        OutputStream out;

        public SerialWriter(OutputStream out) 
        {
            this.out = out;
        }

        @Override
        public void run() 
        {
            try 
            {
                Scanner str = new Scanner(System.in);
                String msg;
                while (!Thread.currentThread().isInterrupted()) 
                {
                    msg = str.nextLine();
                    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                    out.write(bytes);
                    out.flush();
                }
               
            }
            catch (Exception ex)
            {
                 System.out.println(ex.getMessage());
            }
        }
    }
}
