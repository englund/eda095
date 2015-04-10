import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection extends Thread {
	private PrintWriter output;
	private BufferedReader input;
	private Socket socket;
	private ChatRoom chat;

	public ClientConnection(ChatRoom chat, Socket socket) {
		this.chat = chat;
		try {
			this.socket = socket;
			this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("New connection: %s:%d",
				socket.getInetAddress(), socket.getPort()));
	}

	public void run() {
		try {
			String line;
            while ((line = input.readLine()) != null) {
            	this.chat.postMessage(line);
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(String.format("Closing connection: %s:%d",
					socket.getInetAddress(), socket.getPort()));
			try {
				this.output.close();
				this.input.close();
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void postMessage(String message) {
		output.write(message);
		output.flush();
	}

}
