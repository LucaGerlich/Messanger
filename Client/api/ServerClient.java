package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import cmd.ClientCMD;

/*
 * Name: ServerClient
 * Beschreibung: Dient als API die die Kommunikation zwischen Server und Client
 */

public class ServerClient {

	ClientCMD CCMD = new ClientCMD();

	private String newUsername;
	
	private final String serverName;
	private final int serverPort;
	private Socket socket;
	private OutputStream serverOut;
	private InputStream serverIn;
	private BufferedReader bufferedIn;

	private ArrayList<UserStatus> userStatus = new ArrayList<>();
	private ArrayList<MessageListener> messageStatus = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		ServerClient client = new ServerClient("127.0.0.1", 8080);

		client.addUserStatus(new UserStatus() {

			@Override
			public void online(String login) {
				System.out.println("Online: " + login);

			}

			@Override
			public void offline(String login) {
				System.out.println("Offline: " + login);
			}
		});

		// ermöglicht uns immer wieder Nachrichten von anderen Users zu empfangen
		client.addMessageListener(new MessageListener() {

			@Override
			public void onMessage(String fromLogin, String msgBody) {
				System.out.println("Nachricht von " + fromLogin + ": " + msgBody);

			}

		});

		// Client verbindet sich mit dem Server
		if (!client.connect()) {
			System.out.println("Die Verbindung ist fehlgeschlafen");
		} else {
			System.out.println("Die Verbindung wurde erfolgreich aufgebaut");
		}
	}

	public ServerClient(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	// Stellt eine Server verbindung her
	public boolean connect() {
		try {
			this.socket = new Socket(serverName, serverPort);
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean namecheck(String username) throws IOException {
		String cmd = "namecheck " + username + CCMD.LineSplit;
		serverOut.write(cmd.getBytes());
		
		String response = bufferedIn.readLine();
		System.out.println(response);
		
		if(!username.equalsIgnoreCase(response))
		{
			response = newUsername;
			return true;
		} else {
			return false;
		}		
	}
	// Ermöglicht es dem Benutzer sich anzumelden
	// Der login befehlt wird dem Benutzernamen und dem Passwort an den Server
	// geschickt
	public boolean login(String username) throws IOException {
		String cmd = "login " + username + CCMD.LineSplit;
		serverOut.write(cmd.getBytes());

		String response = bufferedIn.readLine();
		System.out.println("Antwort: " + response);

		if ("ok login".equalsIgnoreCase(response)) {
			startMessageReader();
			return true;
		} else {
			return false;
		}
	}
	
	

	public void startMessageReader() {
		Thread thread = new Thread() {
			public void run() {
				readMessageLoop();
			}
		};
		thread.start();
	}

	// Ließt jede ankommende Nachricht und überprüft ob das erste Wort ein Befehl
	// ist
	private void readMessageLoop() {

		String line;
		try {
			while ((line = bufferedIn.readLine()) != null) {
				String[] tokens = StringUtils.split(line);
				if (tokens != null && tokens.length > 0) {
					String command = tokens[0];
					if ("Online".equalsIgnoreCase(command)) {
						onlineSystem(tokens);
					} else if ("Offline".equalsIgnoreCase(command)) {
						offlineSystem(tokens);
					} else if ("msg".equalsIgnoreCase(command)) {
						String[] tokenmsg = StringUtils.split(line, null, 3);
						messageSystem(tokenmsg);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				logoff();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void messageSystem(String[] tokensmsg) {
		String user = tokensmsg[1];
		String message = tokensmsg[2];

		for (MessageListener listener : messageStatus) {
			listener.onMessage(user, message);
		}
	}

	public void msg(String sendTo, String msgBody) throws IOException {
		String command = "msg " + sendTo + " " + msgBody + CCMD.LineSplit;
		serverOut.write(command.getBytes());
	}

	public void logoff() throws IOException {
		String cmd = "quit" + CCMD.LineSplit;
		serverOut.write(cmd.getBytes());

	}

	// Online und Offline System
	// Zeigt an wenn ein Benutzer online kommt oder offline geht
	public void onlineSystem(String[] tokens) {
		String user = tokens[1];
		for (UserStatus listener : userStatus) {
			listener.online(user);
		}

	}

	public void offlineSystem(String[] tokens) {
		String user = tokens[1];
		for (UserStatus listener : userStatus) {
			listener.offline(user);
		}
	}

	public void addUserStatus(UserStatus listener) {
		userStatus.add(listener);
	}

	public void removeUserStatus(UserStatus listener) {
		userStatus.remove(listener);
	}

	public void addMessageListener(MessageListener listener) {
		messageStatus.add(listener);
	}

	public void removeMessageListener(MessageListener listener) {
		messageStatus.remove(listener);
	}
}
