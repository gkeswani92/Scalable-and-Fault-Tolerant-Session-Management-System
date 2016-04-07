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

			//read file contents to list format
			List<String> destinationIPAddresses = ClusterMembership.getMemberIPAddress();
			for(String destIp: destinationIPAddresses){
				System.out.println("BGT BGT: "+destIp);


			try {
				Thread.sleep(2000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}