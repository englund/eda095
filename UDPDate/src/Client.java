import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {

	public void run() {
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket();
			ms.setTimeToLive(1);
			InetAddress ia = InetAddress.getByName(TimeServerUDP.MC_ADDRESS);
			byte[] buf = "message".getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, ia, TimeServerUDP.MC_PORT);
			ms.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ms.close();
		}
	}
}