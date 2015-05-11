import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeServerUDP {
	public static final String MC_ADDRESS = "experiment.mcast.net";
	public static final int MC_PORT = 4096;
	
	public Date getDateFromLocale(String locale) {
		return Calendar.getInstance(Locale.ENGLISH).getTime();
		/*DateFormat df = DateFormat.getDateInstance();
		if (locale.equals("fr")) {
			df = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
		} else if (locale.equals("ch")) {
			df = DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA);
		}
		return df.getCalendar().getTime();*/
	}

	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(4025);
			while (true) {
				byte[] buf = new byte[65536];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				socket.receive(dp);
				String s = new String(dp.getData(), 0, dp.getLength());
				
				System.out.println("Received UDP packet: " + s);
				
				if (s == "Discover"){
					byte[] selfie = InetAddress.getLocalHost().toString().getBytes();
					DatagramPacket packet = new DatagramPacket(selfie, selfie.length, dp.getAddress(), dp.getPort());
					socket.send(packet);
				}
				else{
					byte[] date = getDateFromLocale(s).toString().getBytes();
					DatagramPacket packet = new DatagramPacket(date, date.length, dp.getAddress(), dp.getPort());
					socket.send(packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
	
	public static void main(String[] args) {
		Discover discover = new Discover();
		discover.start();
		
		TimeServerUDP timeServer = new TimeServerUDP();
		timeServer.run();
	}
}

class Discover extends Thread {
	public void run() {
		try {
			MulticastSocket ms = new MulticastSocket(TimeServerUDP.MC_PORT);
			ms.joinGroup(InetAddress.getByName(TimeServerUDP.MC_ADDRESS));
			String expected = "Discover";
			byte[] buf = new byte[expected.getBytes().length];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			while (true) {
				ms.receive(dp);
				String s = new String(dp.getData());

				System.out.println("Received UDP packet: " + s);
				if (expected.equals(s)) {
					byte[] selfie = InetAddress.getLocalHost().getHostName().getBytes();
					DatagramPacket packet = new DatagramPacket(selfie,
							selfie.length, dp.getAddress(), dp.getPort());
					ms.send(packet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}