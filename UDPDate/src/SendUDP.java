import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;


public class SendUDP {
	public static void main(String[] args) throws IOException {
		String machine = args[0];
		int port = Integer.parseInt(args[1]);
		String command = args[2];
		
		// Multicast Mode
		if (machine.equals("multicast") || machine.equals("all")){
			MulticastSocket ms = new MulticastSocket();
		    ms.setTimeToLive(1);
		    InetAddress ia = InetAddress.getByName("experiment.mcast.net");
		    
		    System.out.println("Multicast Mode. Discovering servers...");
		    
		    byte[] buf = "Discover".getBytes();
		    DatagramPacket outPacket = new DatagramPacket(buf, buf.length, ia, TimeServerUDP.MC_PORT);
			ms.send(outPacket);
			ms.setSoTimeout(500);
			
			ArrayList<String> machines = new ArrayList<String>();
			try{
				while(true){
					byte[] recBuff = new byte[1024];
					DatagramPacket inPacket = new DatagramPacket(recBuff, recBuff.length);
					ms.receive(inPacket);
					machines.add(new String(inPacket.getData()));
				}
			}
			catch(SocketTimeoutException e){
			}
			if (machines.size() == 0){
				System.out.println("Found no NTP Servers. Exiting.");
				System.exit(0);
			}
			Random random = new Random();
			int index = (machines.size() == 1) ? 0 : random.nextInt(machines.size() - 1);
			machine = machines.get(index);
			System.out.println("Selected server: " + machine);
		}
		
		// Unicast Mode
		InetAddress address = InetAddress.getByName(machine);
		
		byte[] data = command.getBytes();
		DatagramPacket outPacket = new DatagramPacket(data, data.length, address, port);
		
		DatagramSocket socket = new DatagramSocket();
		socket.send(outPacket);
		
		byte[] buf = new byte[65536];
		DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
		
		socket.receive(inPacket);
		String inData = new String(inPacket.getData());
		System.out.println(inData);
		socket.close();
	}
}
