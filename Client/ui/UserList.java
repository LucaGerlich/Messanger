package ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import api.ServerClient;
import api.UserStatus;


/*
 *  Name: UserList
 *  Beschreibung: Erstellt ein Simples Fenster nach dem Login in dem Alle aktuell angemeldeten 
 *  Benutzer sind diese können dann durch einen doppelten Linksklick ausgewählt werden und 
 *  ein neues MassageUI fenster erstellt werden
 */
public class UserList extends JPanel implements UserStatus {

	private final ServerClient client;
	private JList<String> userListUI;
	private DefaultListModel<String> userListModel;

	public UserList(ServerClient client) {
		this.client = client;
		this.client.addUserStatus(this);

		userListModel = new DefaultListModel<>();
		userListUI = new JList<>(userListModel);
		setLayout(new BorderLayout());
		add(new JScrollPane(userListUI), BorderLayout.CENTER);

		userListUI.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					String login = userListUI.getSelectedValue();
					MessageUI messageUI = new MessageUI(client, login);

					JFrame f = new JFrame("Nachricht an: " + login);
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					f.setSize(500, 500);
					f.getContentPane().add(messageUI, BorderLayout.CENTER);
					f.setVisible(true);
				}
			}
		});
	}

	public static void main(String[] args) {
	}

	@Override
	public void online(String login) {
		userListModel.addElement(login);
	}

	@Override
	public void offline(String login) {
		userListModel.removeElement(login);

	}

}
