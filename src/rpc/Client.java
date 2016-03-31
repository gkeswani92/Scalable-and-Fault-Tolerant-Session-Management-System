package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
	static int count = -1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("HELLO Client!");
		
		//Random Server IPs///////////////////////////////////////
		//These are all made up, change the line(on 42) and the for loop(on 38) to make localhost
		byte[] ipAddr0 = { 127, 0, 0, 1 };
		byte[] ipAddr1 = { 127, 0, 0, 2 };
		byte[] ipAddr2 = { 127, 0, 0, 3 };
		byte[] ipAddr3 = { 127, 0, 0, 4 };
		ArrayList<byte[]> v = new ArrayList<byte[]>();
		v.add(ipAddr0);
		v.add(ipAddr1);
		v.add(ipAddr2);
		v.add(ipAddr3);
		//////////////////////////////////////////////////////////
		
		DatagramSocket skt;
		try {
			skt = new DatagramSocket();
			int serverSocket = 5300;
			
			//convert the message to a stream of bytes and store in arr
			String msg = Integer.toString(count);
			byte [] arr = msg.getBytes();
			
			//send the request to all servers in the ArrayList
			for (int i = 0; i < v.size(); ++i){
				//create a DatagramPacket for the respective Server
				InetAddress host;
				try {
					host = InetAddress.getByAddress(v.get(i));
					DatagramPacket request = new DatagramPacket(arr, arr.length, host, serverSocket);
					try {
						//send request
						skt.send(request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//now recv the message
			byte [] arr_rcv = new byte[512];
			DatagramPacket recvPkt = new DatagramPacket(arr_rcv, arr_rcv.length);
			
			try{
				do{
					recvPkt.setLength(arr_rcv.length);
					skt.receive(recvPkt);
				}while (new String(recvPkt.getData()) != "-1");
				skt.close();
			}
			catch(SocketTimeoutException stoe) {
			    // timeout 
			    recvPkt = null;
			} 
			catch(IOException ioe) {
			    // other error 
			}
			catch(Exception ex){
				System.out.println("EXCEPTION CAUGHT");
			}
			
			skt.close();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//skt.receive(reply);
		//System.out.println("Client Received: "+ new String(reply.getData()));
		
	}

}