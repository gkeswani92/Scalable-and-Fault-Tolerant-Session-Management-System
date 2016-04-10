package rpc;

import java.util.ArrayList;
import java.util.List;
import session.MySession;

public class RPCInfo {
	
	private MySession session;
	private List<String> wqAddress;
	private String serverID;
	
	public RPCInfo(MySession session, List<String> wqAddress, String serverID){
		this.setSession(session);
		this.setServerID(serverID);
		this.wqAddress = new ArrayList<String>();
		this.wqAddress.addAll(wqAddress);
	}

	public MySession getSession() {
		return session;
	}

	public void setSession(MySession session) {
		this.session = session;
	}

	public String getServerID() {
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	
	public String getLocationMetadata() {
		return serverID;
	}
	
	public List<String> getLocations(){
		return wqAddress;
	}
}
