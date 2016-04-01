package rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.catalina.tribes.util.Arrays;

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
			    // here inBuf contains the callID and operationCode
			    int operationCode = Integer.parseInt(requestParams[1]); // get requested operationCode
			    switch ( operationCode ) {
			    	case Client.getOperationsessionread():
			    		// SessionRead accepts call args and returns call results 
			    		outBuf = sessionRead(recvPkt.getData(), recvPkt.getLength());
			    		break;
			    	case Client.getOperationsessionwrite():
			    		
			    		
			    }
			    // here outBuf should contain the callID and results of the call
			    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,
			    	returnAddr, returnPort);
			    rpcSocket.send(sendPkt);
		    } catch(IOException e) {
		    	e.printStackTrace();
		    }
		}
	}

}