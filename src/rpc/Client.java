package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Client {
	static int count = -1;
	public final static int operationSESSIONREAD = 1;
	public final static int operationSESSIONWRITE = 2;
	public final static int portProj1bRPC = 5300;
	// 512 bytes
	public final static int MAX_PACKET_SIZE = 4096;
	public final static String DELIMITER = "_";

	
	public static DatagramPacket sessionReadClient(String sessionId, List<String> destIps) {
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
			
			
			// SEND PACKET
			// Generate unique id for call
			String callID = UUID.randomUUID().toString();
			//fill outBuf with [ callID, operationSESSIONREAD, sessionID ]
			String obuf = callID + DELIMITER + operationSESSIONREAD + DELIMITER +  sessionId;
			byte[] outBuf = obuf.getBytes();
			
			for(String destIp: destIps) {
			    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, InetAddress.getByName(destIp), portProj1bRPC);
			    rpcSocket.send(sendPkt);
			  }
			
			
			// RESPONSE PACKET
			byte [] inBuf = new byte[MAX_PACKET_SIZE];
			DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
		    String[] responseParams = null;

			try {
			    do {
			      recvPkt.setLength(inBuf.length);
			      rpcSocket.receive(recvPkt);
			      if (inBuf != null) {
				      String response = Arrays.toString(inBuf);
				      responseParams = response.split(DELIMITER); 
			      }

			    } while(responseParams == null || !responseParams[0].equals(callID));
			  } catch(SocketTimeoutException stoe) {
			    // timeout 
			    recvPkt = null;
			  } catch(IOException ioe) {
			    // other error 
			    ioe.printStackTrace();
			  }
			
			rpcSocket.close();
			return recvPkt;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}