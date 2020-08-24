package testserialport;

import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 *
 * @author MarcinJ7
 */
public class TestSerialPort
{

    public static void main(String[] args)
    {
        
    EventQueue.invokeLater(new Runnable()
    {
        @Override
        public void run() 
        {
            JFrame guiFrame = new GUIframe("guiFrame"); // Tworzymy ramkę -> obiekt klasy GUIframe
        }  
    });
    
    }

    
    
}


