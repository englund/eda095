import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatRoom {
	ArrayList<ClientConnection> connections;
	
	public ChatRoom() {
		this.connections = new ArrayList<ClientConnection>();
	}
	
	public synchronized void addConnection(ClientConnection connection) {
		this.connections.add(connection);
	}
	
	public static void main(String[] args) {
		ChatRoom chat = new ChatRoom();
		Mailbox mailbox = new Mailbox();
		ServerSocket server = null;
		
		Carrier carrier = new Carrier(mailbox, chat.connections);
		carrier.start();
		
		try {
			server = new ServerSocket(30000);
			Socket socket = null;
			while ((socket = server.accept()) != null) {
				ClientConnection connection = new ClientConnection(mailbox, socket);
				chat.addConnection(connection);
				connection.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
