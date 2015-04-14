import java.util.LinkedList;

public class Mailbox {
	LinkedList<String> messageList;
	public Mailbox() {
		messageList = new LinkedList<String>();
	}
	
	synchronized public void post(String msg){
		messageList.addLast(msg);
		notifyAll();
	}
	
	synchronized public String fetch(){
		while (messageList.isEmpty()){
			try {
				wait();
			}
			catch (InterruptedException e) {e.printStackTrace();}
		}
		return messageList.pop();
	}
}