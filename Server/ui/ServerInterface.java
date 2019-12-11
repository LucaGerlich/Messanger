package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import cmd.ServerCMD;
import socket.Server;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * Name: ServerInterface
 * Beschreibung: Beinhaltet das Komplette Server Interface 
 */

public class ServerInterface extends Thread {

	private JFrame ServerInterface;
	private JTextField textfieldPort;
	public JTextField txtServerName;
	

	ServerCMD SCMD = new ServerCMD();
	Server server = new Server(0);


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerInterface window = new ServerInterface();
					window.ServerInterface.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		ServerInterface = new JFrame();
		ServerInterface.getContentPane().setBackground(Color.WHITE);
		ServerInterface.setResizable(false);
		ServerInterface.setTitle("Server UI");
		ServerInterface.setBounds(100, 100, 300, 267);
		ServerInterface.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton btnStop = new JButton("Exit");
		btnStop.setBounds(60, 185, 89, 23);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerStop();
			}
		});
		ServerInterface.getContentPane().setLayout(null);
		ServerInterface.getContentPane().add(btnStop);

		JButton btnStart = new JButton("Start");
		btnStart.setBounds(159, 185, 89, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ServerPort = Integer.parseInt(textfieldPort.getText());
				ServerStart(ServerPort);
			}
		});
		ServerInterface.getContentPane().add(btnStart);

		textfieldPort = new JTextField();
		textfieldPort.setBounds(90, 69, 112, 20);
		textfieldPort.setText("8080");
		ServerInterface.getContentPane().add(textfieldPort);
		textfieldPort.setColumns(10);

		JLabel lblServerPort = new JLabel("Server Port:");
		lblServerPort.setBounds(10, 72, 106, 14);
		ServerInterface.getContentPane().add(lblServerPort);

		JLabel lblServerName = new JLabel("Servername");
		lblServerName.setBounds(10, 41, 86, 14);
		ServerInterface.getContentPane().add(lblServerName);

		txtServerName = new JTextField();
		txtServerName.setText("Java chat server");
		txtServerName.setColumns(10);
		txtServerName.setBounds(90, 38, 112, 20);
		ServerInterface.getContentPane().add(txtServerName);

		JLabel lblServerText = new JLabel("IP adresse:");
		lblServerText.setBounds(10, 144, 70, 14);
		ServerInterface.getContentPane().add(lblServerText);

		JLabel lblServerIP = new JLabel("0.0.0.0");
		lblServerIP.setBounds(90, 144, 106, 14);
		ServerInterface.getContentPane().add(lblServerIP);

		JLabel lblHostName = new JLabel("Host name:");
		lblHostName.setBounds(10, 124, 70, 14);
		ServerInterface.getContentPane().add(lblHostName);

		JLabel lblHost = new JLabel("Host");
		lblHost.setBounds(90, 124, 70, 14);
		ServerInterface.getContentPane().add(lblHost);

		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			lblServerIP.setText(inetAddress.getHostAddress());
			lblHost.setText(inetAddress.getHostName());

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//Startet den Server mit dem angebenen Port
	private void ServerStart(int Port) {
		System.out.println(SCMD.ServerMSG + "Server wird gestartet!");
		System.out.println(SCMD.ServerMSG + "Port: " + Port);
		System.out.println(SCMD.ServerMSG + "Der Server wurde erfolgreich gestartet!");

		ServerInterface.setTitle(txtServerName.getText());

		Server server = new Server(Port);
		server.start();
	}

	
	//Beendet den Server
	private void ServerStop() {
		System.exit(0);
	}
}
