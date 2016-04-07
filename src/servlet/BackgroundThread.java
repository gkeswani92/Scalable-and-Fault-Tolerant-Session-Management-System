package rpc;

import java.io.IOException;

import org.apache.catalina.tribes.util.Arrays;

import session.MySession;
import session.SessionManager;
import cluster.ClusterMembership;

public class BackgroundThread implements Runnable {
	
	public BackgroundThread(){}
	
	@Override
	public void run() {
		boolean flag = true;	
		int i = 0;
		while(flag){
			i++;
			System.out.println("Thread started... Counter ==> " + i);	
			//-----Background Thread Functionality Goes Here-----

			//read and parse file
			List<String> destinationIPAddresses = ClusterMembership.getMemberIPAddress();


			try {
				Thread.sleep(20000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}