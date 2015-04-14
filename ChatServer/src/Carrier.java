import java.util.ArrayList;

public class Carrier extends Thread {
	Mailbox mailbox;
	ArrayList<ClientConnection> clients;

	public Carrier(Mailbox mailbox, ArrayList<ClientConnection> clients) {
		this.mailbox = mailbox;
		this.clients = clients;
	}

	public void run() {
		String msg;
		while (true) {
			msg = mailbox.fetch();
			for (ClientConnection client : this.clients) {
				client.postMessage(msg);
			}
		}
	}
}
