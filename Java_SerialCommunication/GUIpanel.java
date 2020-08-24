package testserialport;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


/**
 *
 * @author MarcinJ7
 */
public class GUIpanel extends JPanel implements ActionListener, SerialPortDataListener // Implementacja interfejsów
{
    private final int WIDTH = 300; // Wys, szer panelu
    private final int HEIGHT = 300;
    private int portChoice = 0; // Wybór portu. Domyslnie SerialPort 0
    private float temperatureRead = 0; // Wartość startowa wykresu - jeżeli chcemy od zmierzonej to zakomentować linijkę w programie ODBIORNIKA Arduino
    SerialPort comPort; // Utworzenie obiektu docelowo przechowującego dostępne porty
    JButton butt1; // Do otwierania wybranego portu
    JButton butt2; // Do zamykania wybranego portu
    JPanel chartPanel; // Panel pod wykres
    JLabel label1; // Etykieta opisowa
    JLabel authorLabel;
    String[] ports = new String[SerialPort.getCommPorts().length]; // Tablica prezechowująca nazwy dostępnych portów
    JComboBox cBox; // ComboBox do wyboru portu
    TimeSeries seria1; // Seria przechowująca dane z których powstaje wykres

    public GUIpanel() 
    {
        super();
        
        setPortsArray();  // Pobieramy dostępne porty do tablicy
        setPanelParameters(); // Ustawia parametry panelu: widczoność, rozmiary...
        addComponents(); // Dodaje niezbędne komponenty (przyciski, ComboBox, panel pod wykres)
        
        seria1 = new TimeSeries("Temperatura mierzona"); // Seria czasowa
    }
    
    void setPanelParameters() //Ustawia parametry panelu
    {
        this.setVisible(true);
        this.setLayout(null);
        Dimension d = new Dimension(WIDTH, HEIGHT);
        this.setSize(d);
        this.setBackground(Color.lightGray);
    }
    
    void addComponents() // Dodaje niezbędne komponenty do panelu i je rozmieszcza
    {
        butt1 = new JButton("Otwórz wybrany port");
        butt1.addActionListener(this);
        butt1.setBounds(280, 20, 190, 50);
        this.add(butt1); // np.Dodanie przycisku butt1 do panelu
        
        butt2 = new JButton("Zamknij port");
        butt2.addActionListener(this);
        butt2.setBounds(880, 20, 190, 50);
        this.add(butt2);
        
        chartPanel = new JPanel();
        chartPanel.setBounds(32, 90, 1300, 580);
        add(chartPanel);
        
        cBox = new JComboBox(ports);
        cBox.setBounds(580, 45, 180, 20);
        cBox.addActionListener(this);
        add(cBox);
        
        label1 = new JLabel("Wybierz port COM z listy i naciśnij Otwórz");
        label1.setBounds(555, 20, 310, 20);
        add(label1);
        
        authorLabel = new JLabel("Autor: Marcin Jurczak");
        authorLabel.setBounds(0, 678, 180, 40);
        add(authorLabel);
    }
    
    void setPortsArray() // Pobiera listę dostępnych portów com
    {
        for(int i = 0 ; i<SerialPort.getCommPorts().length; i++)
        {
            String s = SerialPort.getCommPorts()[i].getDescriptivePortName();
            ports[i] = s;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) // Metoda abstrakcyjne (intrfejs ActionListener)
    {
       Object source = e.getSource();
       
       if(source == butt1) // Otworzenie portu po nacisnieciu przycisku
       {
           comPort = SerialPort.getCommPorts()[this.portChoice];
           comPort.openPort();
           setDataListener(); // Dodanie dataListenera do portu. Słuchaczem jest panel
       }
       
       if(source == butt2) // Zamknięcie otworzonego portu
       {
           comPort.closePort();
           removeDataListener();
       }
       
       if(source == cBox) // Po naciśnięciu ComboBoxa zapamiętuje wybór
       {
           this.portChoice = cBox.getSelectedIndex();
       }
    }
    
    void setDataListener() // Ustawia DataListenera dla wcześniej zdefiniowanego portu
    {
        comPort.addDataListener(this);
    }
    
    void removeDataListener() // Ustawia DataListenera dla wcześniej zdefiniowanego portu
    {
        comPort.removeDataListener();
    }
    
    void addTemperature(double temperature) // Dodanie temperatury na wykres. Pobieranie aktualnego czasu + jego odpowiednie przetworzenie
    {
       Calendar c = Calendar.getInstance(); // Pobranie aktualnych danych (godzina, min, sek, .. rok).
       Second s = new Second(c.get(Calendar.SECOND), c.get(Calendar.MINUTE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.DATE), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));

       seria1.add(s,temperature); // Dodaje do serii czasowej próbkę wraz z aktualynym czasem

       TimeSeriesCollection seriaTestowa = new TimeSeriesCollection(); // Tworzymy serie pojawiające się na grafie
       seriaTestowa.addSeries(seria1);  // U nas będzie tylko jedna seria czasowa, więc dodajemy jedną (powyższą)

       JFreeChart wyk = ChartFactory.createTimeSeriesChart("Pomiar temperatury czujnikiem DS18B20", "Godzina pomiaru", "Temperatura *C", seriaTestowa); // Tworzymy wykres czasowy z opisem osi

       ChartPanel chart = new ChartPanel(wyk); // Tworzymy nowy panel pod wykres i dodajemy na niego wykres.
       
       chartPanel.setLayout(new BorderLayout());
       chartPanel.removeAll(); // Usuwamy z panelu jego zawartość
       chartPanel.add(chart); // Dodajemy na panel utworzony panel z wykresem (zaktualizowanym)
       chartPanel.setBounds(32, 90, 1300, 580);
       chartPanel.revalidate(); // Odświeżamy panel po dodaniu wykresu
    }

    @Override
    public int getListeningEvents()  // Zwracamy wartość określającą, czy dane są możliwe do odczytu z portu szeregowego
    {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent spe)  // Wywołane, gdy dane pojawią się na porcie, pobieramy je
    {                                             // Konwersja pobranej temperatury na Float i zapis na wykresie
        byte[] data = spe.getReceivedData();
        String str = "";  
           for (int i = 0; i < data.length; ++i)
           {
                str = str + (char)data[i];
           }
            try 
            {
                this.temperatureRead = Float.parseFloat(str);
                System.out.println("Dodawana temp. wynosi: "+this.temperatureRead);
            }
            catch(Exception e) // Gdyby inne dane niż cyfry na porcie szeregowym
            {
                System.err.println("Nie mozna wykonac parseFloat - dodawanie próbki zerowej"); 
            }
            
        addTemperature(this.temperatureRead); // Dodajemy przekonwertowaną temp. do wykresu    
    }

}
