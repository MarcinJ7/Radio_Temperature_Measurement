#include <VirtualWire.h> 
// Komunikacja radiowa na podstawie przykładu z biblioteki VirtualWire.h autorstwa Mike McCauley (mikem@airspayce.com)

void setup()
{
    Serial.begin(9600);                       // Rozpoczęcie komunikacji szeregowej (testowanie)
    Serial.println("Dodaj zero do wykresu");  // Jeżeli nie chcemy dodawać 0 na początku do wykresu to usunąć tą linijkę
    
    // Komunikacja radiowa, moduł FS100A
    vw_set_ptt_inverted(true); 
    vw_setup(2000);                           // Ustawienie ilości transmotowanych bitów na sekundę

    vw_rx_start();                            // Rozpoczęcie pracy odbiornika
}

void loop()
{
    uint8_t buf[VW_MAX_MESSAGE_LEN];          // Tablica przechowująca obierane dane
    uint8_t buflen = VW_MAX_MESSAGE_LEN;      // Długość danych - bufor
    String tempMessage = "";                  // Zmienna typu String przechowująca temperaturę odebraną

    if (vw_get_message(buf, &buflen))         // Jeżeli odebrano dane
    { 
      digitalWrite(13, true);                 // Zapalenie diody po prawidłowym odbiorze danych

      for (int i = 0; i < buflen; i++)        // Zapis odbieranych danych do zmiennej (po jedynm znaku)
      {
          tempMessage = tempMessage + (char)buf[i];
      }
      
      Serial.println(tempMessage);            // Wypisanie temperatury w oknie monitora portu szeregowego
      digitalWrite(13, false);                // Zgaszenie diody po prawidłowym odbiorze danych
    }
}
