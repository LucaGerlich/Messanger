package api;

/*
 * Name: MessageListener
 * Beschreibung: Empfängt Nachrichten die vom Client geschickt werden und teilt diese auf in den [Namen] und die [Nachricht] 
 */

public interface MessageListener {
	public void onMessage(String fromLogin, String msgBody);
}
