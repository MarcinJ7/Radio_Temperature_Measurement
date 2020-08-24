package testserialport;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author MarcinJ7
 */
public class GUIframe extends JFrame  // Dziedziczy po JFrame
{
    private int WIDTH = 500;
    private int HEIGHT = 500;
    
    
    public GUIframe(String guiFrame) 
    {
        super(guiFrame); // Wywołanie konstruktora klasy JFrame (powsataje ramka o podanej nazwie)
        setFrameParameters(); // Ustawiane parametry ramki
        JPanel panel = new GUIpanel(); // Tworzymy panel
        this.add(panel);  // Panel dodajemy do ramki
    }
    
    void setFrameParameters() // Ustawiamy parametry ramki
    {
        Dimension d = new Dimension(WIDTH, HEIGHT);
        this.setSize(d);
        this.setVisible(true);
        this.setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
}
