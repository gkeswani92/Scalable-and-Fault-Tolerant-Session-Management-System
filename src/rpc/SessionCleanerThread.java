package rpc;

import session.SessionManager;

public class SessionCleanerThread implements Runnable {
	
	@Override
	public void run() {
		System.out.println("Activated Session Cleaning Thread");	
		while(true){
			try {
				System.out.println("Clean up Thread: Clearing expired sessions");	
				SessionManager.cleanUpExpiredSessions();
				Thread.sleep(60000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}