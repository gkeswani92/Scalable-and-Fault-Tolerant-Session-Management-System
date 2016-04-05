package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import cluster.ClusterMembership;
import cookie.LocationMetadata;
import session.MySession;

public class Client {
	
	static int count = -1;
	private final static int operationSESSIONREAD = 1;
	private final static int operationSESSIONWRITE = 2;
	private final static int portProj1bRPC = 5300;
	private final static String DELIMITER = "_";
	private final static int TIMEOUT = 300;
	public  final static int MAX_PACKET_SIZE = 4096;
	
	//Tunable parameters to maintain 1 resiliency
	private static final Double WQ = 0.25;
	private static final Double W = 0.5;
	
	public DatagramPacket sessionRead(MySession session, LocationMetadata locationData) {
		
		System.out.println("Reading session data on client");
		String sessionId = session.getSessionID(); 
		int version = session.getVersionNumber(); 
		
		try {
			@SuppressWarnings("resource")
			DatagramSocket rpcSocket = new DatagramSocket();
			rpcSocket.setSoTimeout(TIMEOUT);
			
			//Generating a unique id for this call/request to ignore responses
			//to stale requests
			String callID = UUID.randomUUID().toString();
			
			//Fill outBuf with [ callID, operationSESSIONREAD, sessionID, version ]
			String obuf = callID + DELIMITER + operationSESSIONREAD + DELIMITER 
					+  sessionId + DELIMITER + version;
			byte[] outBuf = obuf.getBytes();
			System.out.println("Generated the data that is to be sent");
			
			//Getting the addresses of the instances that have the required data
			//and sending the request to all of them
			List<String> ipAddress = locationData.getWqaddress();
			for(String destIp: ipAddress){
				DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, InetAddress.getByName(destIp), portProj1bRPC);
				rpcSocket.send(sendPkt);
			}
			System.out.println("Sent packet to the other instances");
			
			//Waiting for the first successful response and exiting
			int responses = 0;
			int numServers = ipAddress.size();
			while (responses <= numServers) {
				byte[] inBuf = new byte[MAX_PACKET_SIZE];
				DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
				String[] responseParams = null;

				try {
					do {
						//Receive datagram packet and check if the packet is for
						//the current call id
						recvPkt.setLength(inBuf.length);
						rpcSocket.receive(recvPkt);
						inBuf = recvPkt.getData();
						if (inBuf != null) {
							String response = Arrays.toString(inBuf);
							responseParams = response.split(DELIMITER);
						}
					} while (responseParams == null || !responseParams[0].equals(callID));
				} catch (SocketTimeoutException stoe) {
					responses++;
					recvPkt = null;
					continue;
				} catch (IOException ioe) {
					responses++;
					recvPkt = null;
					ioe.printStackTrace();
					continue;
				}

				// If we reach this point, it means the call id in the response
				// params matches the call id we sent out
				System.out.println("Received a packet from one of the instance");
				rpcSocket.close();
				return recvPkt;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean sessionWrite(MySession session) {
		
		String sessionId = session.getSessionID(); 
		int version = session.getVersionNumber(); 
		String data = session.getMessage(); 
		Date discardTime = session.getExpirationDate();
		
		try {
			//Generating a unique id for this call/request to ignore responses
			//to stale requests
			DatagramSocket rpcSocket = new DatagramSocket();
			String callID = UUID.randomUUID().toString();
			
			//Fill outBuf with [ callID, operationSESSIONWRITE, sessionID, version, data, discard time ]
			String obuf = callID + DELIMITER + operationSESSIONWRITE + DELIMITER 
					+  sessionId + DELIMITER + version + DELIMITER + data 
					+ DELIMITER + discardTime;
			byte[] outBuf = obuf.getBytes();
			System.out.println("RPC Client sending "+obuf);
			
			//Send the packet out to all the members and wait for WQ successful 
			//responses 
			//TODO: Choose numServers * W nodes at random to send out the write
			//request. Currently sending it to all nodes
			System.out.println("RPC Client sending write request to other instances");
			List<String> destinationIPAddresses = ClusterMembership.getMemberIPAddress();
			int numServers = destinationIPAddresses.size();
			for(String destIp: destinationIPAddresses){
				DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, 
						InetAddress.getByName(destIp), portProj1bRPC);
				rpcSocket.send(sendPkt);
				System.out.println("RPC Client sending to "+destIp);
			}
			
			//Continue probing for successful responses until we get enought
			//successes or we run out of servers 
			System.out.println("RPC Client waiting for responses from WQ instances");
			int responses = 0;
			int successfulResponses = 0;
			while(successfulResponses < Math.ceil(WQ * numServers) && responses < numServers){
				byte [] inBuf = new byte[MAX_PACKET_SIZE];
				DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
				String[] responseParams = null;
				
				//Keep probing until we get a packet that has the matching call id
				//that we had sent out for this request
				try {
					do {
						recvPkt.setLength(inBuf.length);
						rpcSocket.receive(recvPkt);
						if (inBuf != null) {
							String response = Arrays.toString(inBuf);
							responseParams = response.split(DELIMITER); 
						}
					} while(responseParams == null || !responseParams[0].equals(callID));
				} 
				catch(SocketTimeoutException stoe) {
					stoe.printStackTrace();
					responses++;
				    recvPkt = null; 
				    continue; //Not sure if we need to catch a socket timeout in this implementation
				  } catch(IOException ioe) {
				    ioe.printStackTrace();
				    rpcSocket.close(); //IOException leads to exit with a false flag
				    return false;
				  }
				
				//We get to this point only if a response was successful
				successfulResponses++;
				responses++;
			}
			System.out.println("Consensus has been received");
			rpcSocket.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
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
}