package cmd;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Name: ServerCMD
 * Beschreibung: Ermöglicht es Consolen Befehle direkt zu benutzen
 * 
 */

public class ServerCMD {
	// Ein Simples Format welches ermöglicht das Datum und die Uhrzeit darzustellen
	public String ServerMSG = "[Server "
			+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime()) + "]:";

	// Setzt einen Zeilenumbruch der auf Alle Betriebssysteme anwendbar ist
	public String LineSplit = System.getProperty("line.separator");

}
