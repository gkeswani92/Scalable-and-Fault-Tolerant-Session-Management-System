package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.catalina.tribes.util.Arrays;

import session.MySession;
import session.SessionManager;

public class Server implements Runnable {
	
	private final static int portProj1bRPC = 5300;
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
		while (isRunning) {
			byte[] inBuf = new byte[Client.getMaxPacketSize()];
			byte[] outBuf = new byte[Client.getMaxPacketSize()];
		    DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
		    
		    try {
			    rpcSocket.receive(recvPkt);
			    InetAddress returnAddr = recvPkt.getAddress();
			    int returnPort = recvPkt.getPort();
			    
			    String[] requestParams = Arrays.toString(recvPkt.getData()).split(Client.getDelimiter());
			    
			    //The inBuf contains the callID and operationCode
			    int operationCode = Integer.parseInt(requestParams[1]); // get requested operationCode
			    switch ( operationCode ) {
			    	case 1:
			    		//SessionRead accepts call args and returns call results 
			    		//by finding the session corresponding to the sesison id
			    		outBuf = sessionRead(recvPkt.getData(), recvPkt.getLength());
			    		break;
			    	case 2:
			    		outBuf = sessionWrite(recvPkt.getData(), recvPkt.getLength());
			    		break;
			    }
			    
			    //Here outBuf should contain the callID and results of the call
			    //Sending the packet back to the address and port it had come from
			    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
			    	returnAddr, returnPort);
			    rpcSocket.send(sendPkt);
			    System.out.println("Send packet back to the client");
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
	public byte[] sessionRead(byte[] data, Integer len){
		String input = data.toString();
		String[] requestParams = input.split(Client.getDelimiter());
		
		//Extracting the params that tell us what data and version is being requested
		if(requestParams.length != 4){
			System.out.println("Invalid request params received");
			return null;
		}
		
		//Finding the local session information known corresponding to the
		//requested session id
		String sessionID = requestParams[2];
		Integer versionNumber = Integer.parseInt(requestParams[3]);
		MySession session = SessionManager.sessionInformation.get(sessionID);
		
		//NOTE: May not be a valid case but just in case the version numbers
		//dont match. Should never happen!
		if(session.getVersionNumber() != versionNumber){
			System.out.println("Invalid version number received");
			return null;
		}
		
		byte[] outBuf = session.toString().getBytes();
		return outBuf;
	}
	
	/**
	 * Read the request params received by the server and return the session 
	 * data corresponding to that request
	 * @param data
	 * @param len
	 * @return
	 */
	public byte[] sessionWrite(byte[] data, Integer len){
		String input = data.toString();
		String[] requestParams = input.split(Client.getDelimiter());
		
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
			System.out.println("Session information was not found. Creating new session");
			session = new MySession(sessionID, versionNumber, message, expirationTime);
		}
		
		//NOTE: May not be a valid case but just in case the version numbers
		//dont match. Should never happen!
		if(session.getVersionNumber() != versionNumber){
			System.out.println("Invalid version number received");
			return null;
		}
		
		byte[] outBuf = (callID + '_' + session).toString().getBytes();
		return outBuf;
	}
}