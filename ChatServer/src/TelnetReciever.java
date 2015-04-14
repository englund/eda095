import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TelnetReciever extends Thread {
	BufferedReader input;
	Socket socket;

	public TelnetReciever(Socket socket) throws IOException {
		this.socket = socket;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public void closeConnection() {
		try {
			input.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String line;
		try {
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}