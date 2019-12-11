package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import api.ServerClient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/*
 * Name: Login
 * Beschreibung: Erstellt ein Fenster in dem man die Verbindung zum Server herstellen kann
 * dies wird über drei felder ermöglicht [ServerIP] [Port] [Benutzername]
 */

public class Login {

	private JFrame LoginWindow;
	private JTextField ServerAddress;
	private JTextField loginField;
	private JTextField ServerPortField;

	private String ServerIP;
	private String ServerPort;
	private ServerClient client;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.LoginWindow.setVisible(true);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		LoginWindow = new JFrame();
		LoginWindow.getContentPane().setBackground(Color.WHITE);
		LoginWindow.setTitle("Chat Messenger");
		LoginWindow.setBounds(100, 100, 300, 160);
		LoginWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LoginWindow.getContentPane().setLayout(null);

		JButton loginButton = new JButton("Anmelden");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServerIP = ServerAddress.getText();
				ServerPort = ServerPortField.getText();
				
				dologin();
			}
		});
		loginButton.setBackground(Color.WHITE);
		loginButton.setBounds(90, 87, 101, 23);
		LoginWindow.getContentPane().add(loginButton);

		JLabel lblServerAdresse = new JLabel("Server Adresse:");
		lblServerAdresse.setBounds(10, 26, 101, 14);
		LoginWindow.getContentPane().add(lblServerAdresse);

		ServerAddress = new JTextField();
		ServerAddress.setBounds(121, 23, 105, 20);
		LoginWindow.getContentPane().add(ServerAddress);
		ServerAddress.setColumns(10);
		
		JLabel lblServerPort = new JLabel(":");
		lblServerPort.setBounds(231, 26, 30, 14);
		LoginWindow.getContentPane().add(lblServerPort);
			
		ServerPortField = new JTextField();
		ServerPortField.setBounds(240, 23, 40, 20);
		LoginWindow.getContentPane().add(ServerPortField);
		ServerPortField.setColumns(10);

		JLabel lblBenutzername = new JLabel("Benutzername:");
		lblBenutzername.setBounds(10, 51, 101, 14);
		LoginWindow.getContentPane().add(lblBenutzername);

		loginField = new JTextField();
		loginField.setBounds(121, 51, 105, 20);
		LoginWindow.getContentPane().add(loginField);
		loginField.setColumns(10);
	}

	private void dologin() {
		this.client = new ServerClient(ServerIP, Integer.parseInt(ServerPort));
		LoginWindow.setVisible(false);
		String login_Username = loginField.getText();

		client.connect();

		try {
			if (client.login(login_Username)) {
				UserList userList = new UserList(client);
				JFrame frame = new JFrame("Benutzerliste | Angemeldet als: " + login_Username);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(350, 400);

				frame.getContentPane().add(userList, BorderLayout.CENTER);
				frame.setVisible(true);

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						try {
							System.out.println("Erfolgreich abgemeldet");
							client.logoff();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

			} else {
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}