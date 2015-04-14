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
	private Mailbox mailbox;

	public ClientConnection(Mailbox mailbox, Socket socket) {
		this.mailbox = mailbox;
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
		// * ska kunna ta emot text-baserade kommandon:
		//     - 'M: Content of the message' to send a message to all the participants
		//     - 'E: Content of the message' to echo the message to the user for testing purposes
		//     - 'Q:' to disconnect
		try {
			String line;
            while ((line = input.readLine()) != null) {
            	char command = line.charAt(0);
        		switch (command) {
        		case 'M':
        			this.mailbox.post(line.substring(3));
        			break;
        		case 'E':
        			this.postMessage(line.substring(3));
        			break;
        		case 'Q':
        			this.closeConnection();
        			break;
        		}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println(String.format("Closing connection: %s:%d",
					socket.getInetAddress(), socket.getPort()));
			closeConnection();
		}
	}

	private void closeConnection() {
		try {
			this.output.close();
			this.input.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void postMessage(String message) {
		output.write(String.format("%s\r\n", message));
		output.flush();
	}

}
