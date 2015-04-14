import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Telnet {

	public static void main(String[] args) {
		Socket socket = null;
		PrintWriter output = null;
		TelnetReciever reciever = null;
		try {
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			reciever = new TelnetReciever(socket);
			reciever.start();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		BufferedReader in = new BufferedReader((new InputStreamReader(System.in)));
		while (true) {
			try {
				String line = in.readLine();
				if (line.charAt(0) == 'Q') break;
				output.println(line);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			reciever.closeConnection();
			in.close();
			socket.close();
			System.exit(0); // This command is ignored?! Connection not closed on server?!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
