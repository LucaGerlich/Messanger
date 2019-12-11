package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cmd.ServerCMD;
import worker.ServerThread;

public class Server extends Thread {
	ServerCMD SCMD = new ServerCMD();

	private final int ServerPort;

	// Liste erstellen damit Nachrichten an jeden Client geschickt werden
	private ArrayList<ServerThread> ThreadList = new ArrayList<>();

	public Server(int ServerPort) {
		this.ServerPort = ServerPort;
	}

	public List<ServerThread> getThreadList() {
		return ThreadList;
	}

	@Override
	public void run() {
		try {

			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(ServerPort);
			// Erstellt eine Schleife um dauerhaft Client anfragen anzunehmen
			while (true) {
				// Akzeptiert eine Verbindungsanfrage von einem Client
				Socket clientSocket = serverSocket.accept();
				System.out.println(SCMD.ServerMSG + "Verbindung zum Server hergestellt" + " Client informationen: "
						+ clientSocket);

				// Thread um mehrere Clients zu Verbinden
				ServerThread Thread = new ServerThread(this, clientSocket);
				ThreadList.add(Thread);
				Thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeThread(ServerThread serverThread) {
		ThreadList.remove(serverThread);

	}
}
