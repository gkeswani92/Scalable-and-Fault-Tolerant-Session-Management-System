package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import cluster.Instance;
import session.MySession;
import session.SessionManager;

public class Server implements Runnable {
	
	private boolean isRunning = true;
	private DatagramSocket rpcSocket;
		
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Server(){
		try {
			rpcSocket = new DatagramSocket(Client.getPortproj1brpc());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		System.out.println("RPC Server: Running");
		while (isRunning) {
			byte[] inBuf = new byte[Client.getMaxPacketSize()];
			byte[] outBuf = new byte[Client.getMaxPacketSize()];
		    DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
		    
		    try {
			    rpcSocket.receive(recvPkt);
			    InetAddress returnAddr = recvPkt.getAddress();
			    int returnPort = recvPkt.getPort();
			    
			    String input = new String(recvPkt.getData()).trim();
			    String[] requestParams = input.split(Client.getDelimiter());
			    System.out.println("RPC Server: Received packet: " + input);
			    
			    //The inBuf contains the callID and operationCode
			    if(requestParams.length > 1){
				    int operationCode = Integer.parseInt(requestParams[1]); // get requested operationCode
				    switch ( operationCode ) {
				    	case 1:
				    		System.out.println("RPC Server: Received read request");
				    		//SessionRead accepts call args and returns call results 
				    		//by finding the session corresponding to the sesison id
				    		outBuf = sessionRead(requestParams);
				    		break;
				    	case 2:
				    		System.out.println("RPC Server: Received write request");
				    		outBuf = sessionWrite(requestParams);
				    		break;
				    }
				    
				    if(outBuf != null){
					    //Here outBuf should contain the callID and results of the call
					    //Sending the packet back to the address and port it had come from
					    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
					    	returnAddr, returnPort);
					    rpcSocket.send(sendPkt);
					    System.out.println("RPC Server: Sent packet back to the client");
				    } else {
				    	System.out.println("RPC Server: Did not find data to return to client");
				    }
			    } else {
			    	System.out.println("RPC Server: Invalid request params");
			    }
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		}
	}

	/**
	 * Read the request params received by the server and return the session 
	 * data corresponding to that request
	 * @param data
	 * @param len
	 * @return
	 */
	public byte[] sessionRead(String[] requestParams){
		
		//Extracting the params that tell us what data and version is being requested
		if(requestParams.length != 4){
			System.out.println("RPC Server Read: Invalid request params received: "+requestParams);
			return null;
		}
		
		//Finding the local session information known corresponding to the
		//requested session id
		String callID = requestParams[0];
		String sessionID = requestParams[2];
		Integer versionNumber = Integer.parseInt(requestParams[3]);
		SessionManager.displaySessionTable();
		MySession session = SessionManager.sessionInformation.get(sessionID);

		if(session != null){
			//Check if version number being requested is what you have stored locally
			//This is important in case a server rebooted and lost some information
			if(!session.getVersionNumber().equals(versionNumber)){
				System.out.println("RPC Server Read: Invalid version number received. Don't have this data locally stored");
				return null;
			} else {			
				//Populate the output buffer and return with the session and server id
				System.out.println("RPC Server Read: Valid version number received");
				String obuf = (callID + '_' + session.toString() + '_' + Instance.getAmiIndex());
				System.out.println("RPC Server Read: Sending session data back to client: "+obuf);
				return obuf.getBytes();
			}
		} else {
			System.out.println("RPC Server Read: Session info for this session id was not found locally");
			return null;
		}
	}
		
	/**
	 * Read the request params received by the server and return the session 
	 * data corresponding to that request
	 * @param data
	 * @param len
	 * @return
	 */
	public byte[] sessionWrite(String[] requestParams){
		
		//Extracting the params that tell us what data and version is being requested
		if(requestParams.length != 6){
			System.out.println("Invalid request params received");
			return null;
		}
		
		//Finding the local session information known corresponding to the
		//requested session id
		String callID = requestParams[0];
		String sessionID = requestParams[2];
		Integer versionNumber = Integer.parseInt(requestParams[3]);
		String message = requestParams[4];
		String expirationTime = requestParams[5];
		MySession session = SessionManager.sessionInformation.get(sessionID);
		
		if(session == null){
			System.out.println("RPC Server Write: Session information was not found. Creating new session");
			session = new MySession(sessionID, versionNumber, message, expirationTime);
			SessionManager.sessionInformation.put(sessionID, session);
		}
		
		byte[] outBuf = (callID + '_' + session).toString().getBytes();
		return outBuf;
	}
}