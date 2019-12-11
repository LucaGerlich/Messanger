package worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cmd.ServerCMD;
import socket.Server;
import ui.ServerInterface;

/*
 * Name: ServerThread
 * Beschreibung: Erstellt einen Thread für jeden neuen Client der sich zum Server verbindet
 * und handled die ganze Befehle die von Client an den Server geschickt werden.
 */

public class ServerThread extends Thread {
	ServerCMD SCMD = new ServerCMD();
	ServerInterface SI = new ServerInterface();

	private Socket clientSocket;
	private final Server server;
	
	// Globalisiert den I/O Befehl
	public OutputStream outputStream;
	public InputStream inputStream;

	private String login = null;

	
	// Konstruktor der Class ServerThread
	public ServerThread(Server server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
	}

	// Startet den Thread und die Methode HandleServerWork
	@Override
	public void run() {
		try {
			HandleServerWork();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void HandleServerWork() throws IOException, InterruptedException {

		// Ermöglicht es den Inputstream für jeden Thread(Client) zu benutzen
		this.inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();

		// Erstellt einen neuen "reader" der den kompletten InputStream ausließt
		// Es wird jede Zeile ausgelesen
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		// while-schleife die jede Zeile ausließt die nicht Null ist
		while ((line = reader.readLine()) != null) {

			// Erstellt aus jedem Wort ein Token um befehle und eingaben besser zu trennen
			// basiert auf der Apache commons-lang libary
			String[] tokens = StringUtils.split(line);
			if (tokens != null && tokens.length > 0) {
				// Alle Befehle die der User eingeben kann

				// Token[0] = Der Befehl
				String command = tokens[0];
				if ("quit".equalsIgnoreCase(command)) {
					LoginSystemOff();
					break;
				} else if ("login".equalsIgnoreCase(command)) {
					LoginSystem(outputStream, tokens);

				} else if ("msg".equalsIgnoreCase(command)) {
					String[] tokenmsg = StringUtils.split(line, null, 3);
					HandleMessage(tokenmsg);

				} else {
					String msg = SCMD.ServerMSG + command + " ist kein gueltiger Befehl!";

					outputStream.write(msg.getBytes());
				}
			}
		}

		// Löscht alle im Cache gespeicherten Einträge
		// Beendet die Verbindung zum Client
		outputStream.flush();
		clientSocket.close();
		System.out.println(SCMD.ServerMSG + "Verbindung zum Client " + clientSocket + " wurde beendet");

	}

	// Login Methode um Benutzern einen Namen zu geben (im moment noch Hard coded)
	// Tokens werden benutzt um beim Login command (login name passwort) die Woerter
	// zu trennen
	// Sendet eine Nachricht an alle Benutzer das der User jetzt Online ist
	public void LoginSystem(OutputStream outputStream, String[] tokens) throws IOException {
		if (tokens.length == 2) {

			// Weißt den beiden Strings die Login und Passwort Daten zu
			String login = tokens[1];

			// Schickt an den Client eine Nachricht an den Client das der Login vorgang
			// erfolgreich war
			String msg = "ok login\r\n";
			outputStream.write(msg.getBytes());

			this.login = login;
			System.out.println(SCMD.ServerMSG + "Der Benutzer: " + login + " hat sich erfolgreich angemeldet!");

			List<ServerThread> threadlist = server.getThreadList();

			// Sendet eine Nachricht an alle Benutzer das der User Online ist#
			for (ServerThread thread : threadlist) {
				if (!login.equals(thread.getLogin())) {
					String onlineMsg2 = "Online " + thread.getLogin() + SCMD.LineSplit;
					send(onlineMsg2);
				}
			}

			// Sendet eine Liste mit den Usern die Aktuell online sind
			for (ServerThread thread : threadlist) {
				if (!login.equals(thread.getLogin())) {
					String onlineMsg = "Online " + login + SCMD.LineSplit;
					thread.send(onlineMsg);
				}
			}
		}

	}

	// Sendet an alle aktiven Nutzer das ein anderer User jetzt offline ist
	public void LoginSystemOff() throws IOException {
		List<ServerThread> threadlist = server.getThreadList();
		((Server) server).removeThread(this);

		String OfflineMsg = "Offline " + login + SCMD.LineSplit;
		for (ServerThread thread : threadlist) {
			if (!login.equals(thread.getLogin())) {
				thread.send(OfflineMsg);
			}
		}
		clientSocket.close();
	}

	// Schickt nachrichten an ein bestimmtes Thema oder an einen Bestimmten Benutzer
	public void HandleMessage(String[] tokens) throws IOException {
		String sendTo = tokens[1];
		String message = tokens[2];

		List<ServerThread> threadlist = server.getThreadList();
		for (ServerThread thread : threadlist) {
			if (sendTo.equalsIgnoreCase(thread.getLogin())) {
				String sendMsg = "msg " + login + " " + message + SCMD.LineSplit;
				thread.send(sendMsg);
			}
		}
	}

	// Die send Methode dient dazu nachrichten zu verschicken
	public void send(String msg) throws IOException {

		if (login != null) {
			outputStream.write(msg.getBytes());
		}
	}

	public String getLogin() {
		return login;
	}
}
