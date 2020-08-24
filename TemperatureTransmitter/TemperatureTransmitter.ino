// Wzorowano się na  przykładzie z biblioteki "DallasTemperature.h".
// Komunikacja radiowa na podstawie przykładu z biblioteki VirtualWire.h autorstwa Mike McCauley (mikem@airspayce.com)

// Dołączanie bibliotek
#include <OneWire.h>
#include <DallasTemperature.h>
#include <VirtualWire.h> // Transmisja radiowa

// Utworzenie obiektu typu OneWire umożliwiającego komunikację programu z czujnikiem temp. DS18B20.
OneWire dsOW(7);                                // Czujnik DS18b20 (OneWire podłączony do pinu 7).
DallasTemperature sensor(&dsOW);                // Przekazanie referencji do obiektu dsOW - możliwe są teraz metody pomiarowe
                                                // wywoływane na obiekcie "sensor".

void setup(void)
{
  Serial.begin(9600);                           // Rozpoczęcie komunikacji przez port szeregowy
  sensor.begin();                               // Rozpoczęcie pomiarów sensora OneWire.

  // Komunikacja radiowa, moduł FS100A:
  vw_set_ptt_inverted(true); 
  vw_setup(2000);                               // Ustawienie ilości transmotowanych bitów na sekundę
}

void loop(void)
{ 
  String temp = "";                             // Zmienna przechowująca aktualną temperaturę
  sensor.requestTemperatures();                 // Komenda - pomiar temperatury
  temp = (String)sensor.getTempCByIndex(0);     // Pobranie zmierzonej temperatury i wyświetlenie
  unsigned int arraySize = temp.length();       // Wymagany rozmiar tablicy (równy ilości znaków zmierzonej temperatury)
  char msg[arraySize] = "";                     // Tablica przechowująca temperaturę (każdy jej znak), zawartość przeznaczona do wysłania.
  temp.toCharArray(msg, (arraySize+1));         // Zapis temperatury (String) do tablicy znaków
 
  // Wysyłamy dane za pomocą nadajnika modułu FS100A:
  vw_send((uint8_t *)msg, strlen(msg));         // Wysłanie tablicy z wiadomością (zmierzoną temperaturą) 
  vw_wait_tx();                                 // Czekamy dopóki cała wiadomość nie zostanie wysłana (każdy element tablicy)
  delay(1500);                                 // Odczekanie czasu do kolejnego pomiaru i wysłania danych
}
