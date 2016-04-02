package cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClusterMembership {
	
	private static Map<String,List<String>> membership;
	
	public static List<String> getMemberIPAddress(){
		List<String> ipAddr = new ArrayList<String>();
		
		for(String amiIndex: membership.keySet()){
			String currentIP = membership.get(amiIndex).get(0);
			ipAddr.add(currentIP);
		}
		
		return ipAddr;
	}
}
