package rpc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {

	public static void main(String[] args) {
		System.out.println("HELLO Server!");
		// create a socket
		DatagramSocket skt = null;
		
		try{
			//create a socket on port 5300
			skt = new DatagramSocket(5300);
			//create a buf of bytes of size 256
			byte [] buf = new byte[256];
			
			
			while(true){
				//create a packet
				DatagramPacket req = new DatagramPacket(buf, buf.length);
				//recv the packet
				skt.receive(req);
				//split based on  "_"
				String [] msg = new String(req.getData()).split(" ");
		
				byte [] send_arr = ("Server Got msg[0]: "+ msg[0]).getBytes();
				
				DatagramPacket reply = new DatagramPacket(send_arr, send_arr.length, req.getAddress(), req.getPort());
				
				skt.send(reply);
				
			}
			
		}
		catch(Exception ex){
			System.out.println("ERROR");
		}
	}

}