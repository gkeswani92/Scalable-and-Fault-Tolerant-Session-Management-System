package rpc;

import java.io.IOException;

import org.apache.catalina.tribes.util.Arrays;

import session.MySession;
import session.SessionManager;
import cluster.ClusterMembership;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BackgroundThread implements Runnable {
	
	@Override
	public void run() {
		boolean flag = true;	
		int i = 0;
		while(flag){
			i++;
			System.out.println("Thread started... Call Number Is: ==> " + i);	
			//-----Background Thread Functionality Goes Here-----

			//read and parse file
			List<String> reboot_counts = ClusterMembership.getMemberReboot();
			for(String reboot: reboot_counts){
				System.out.println("REBOOT COUNT: "+reboot);
			}

			try {
				Thread.sleep(60000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}