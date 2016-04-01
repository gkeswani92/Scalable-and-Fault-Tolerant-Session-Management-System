package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class Client {
	static int count = -1;
	private final static int operationSESSIONREAD = 1;
	private final static int operationSESSIONWRITE = 2;
	private final static int portProj1bRPC = 5300;
	private final static String DELIMITER = "_";
	private final static int TIMEOUT = 300;
	
	public static int getOperationsessionread() {
		return operationSESSIONREAD;
	}

	public static int getOperationsessionwrite() {
		return operationSESSIONWRITE;
	}

	public static int getPortproj1brpc() {
		return portProj1bRPC;
	}

	public static int getMaxPacketSize() {
		return MAX_PACKET_SIZE;
	}
	
	public static String getDelimiter() {
		return DELIMITER;
	}

	// 512 bytes
	public final static int MAX_PACKET_SIZE = 4096;
	
	public static DatagramPacket sessionReadClient(String sessionId, int version, String destIp) {
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
			rpcSocket.setSoTimeout(TIMEOUT);
			
			// SEND PACKET
			// Generate unique id for call
			String callID = UUID.randomUUID().toString();
			
			//fill outBuf with [ callID, operationSESSIONREAD, sessionID, version ]
			String obuf = callID + DELIMITER + operationSESSIONREAD + DELIMITER +  sessionId
					+ DELIMITER + version;
			byte[] outBuf = obuf.getBytes();
			
			DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, InetAddress.getByName(destIp), portProj1bRPC);
			rpcSocket.send(sendPkt);
			
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
				  recvPkt = null;
				  ioe.printStackTrace();
			  }
			
			rpcSocket.close();
			return recvPkt;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean sessionWrite(String sessionId, int version, String data, Date discardTime, String destIp) {
		try {
			DatagramSocket rpcSocket = new DatagramSocket();
			
			// SEND PACKET
			// Generate unique id for call
			String callID = UUID.randomUUID().toString();
			//fill outBuf with [ callID, operationSESSIONWRITE, sessionID, version, data, discard time ]
			String obuf = callID + DELIMITER + operationSESSIONWRITE + DELIMITER +  sessionId 
					+ DELIMITER + version + DELIMITER + data + DELIMITER + discardTime;
			byte[] outBuf = obuf.getBytes();
			
			DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, InetAddress.getByName(destIp), portProj1bRPC);
			rpcSocket.send(sendPkt);
			
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
			    rpcSocket.close();
			    return false;
			  } catch(IOException ioe) {
			    // other error 
			    ioe.printStackTrace();
			    rpcSocket.close();
			    return false;
			  }
			
			rpcSocket.close();
			return true;
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}