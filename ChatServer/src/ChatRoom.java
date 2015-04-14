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
	
	public synchronized void postMessage(String message) {
		// * borde inte skicka till sig själv.
		// * enligt labbmanual borde vi använda en mailbox-klass
		// * mailbox borde använda wait() och notifyAll() enl. labbmanual
		for (ClientConnection connection : this.connections) {
			connection.postMessage(message);
		}
	}
	
	public static void main(String[] args) {
		ChatRoom chat = new ChatRoom();
		ServerSocket server = null;
		try {
			server = new ServerSocket(30000);
			Socket socket = null;
			while ((socket = server.accept()) != null) {
				ClientConnection connection = new ClientConnection(chat, socket);
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
