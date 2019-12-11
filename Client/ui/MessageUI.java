package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import api.MessageListener;
import api.ServerClient;

/*
 * Name: MessageUI
 * Beschreibung: Erstellt ein neues Fenster anhand des Benutzers mit dem der aktuelle Client schreiben will
 * 
 */
public class MessageUI extends JPanel implements MessageListener {

	private final String login;

	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> messageList = new JList<>(listModel);
	private JTextField inputField = new JTextField();

	public MessageUI(ServerClient client, String login) {
		this.login = login;

		client.addMessageListener(this);

		setLayout(new BorderLayout());
		add(new JScrollPane(messageList), BorderLayout.CENTER);
		add(inputField, BorderLayout.SOUTH);

		inputField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = inputField.getText();
					client.msg(login, text);
					listModel.addElement("Ich: " + text);
					inputField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		if (login.equalsIgnoreCase(fromLogin)) {
			String line = fromLogin + ": " + msgBody;
			listModel.addElement(line);
		}
	}

}