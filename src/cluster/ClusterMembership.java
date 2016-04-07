package cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterMembership {
	
	private static Map<Integer,List<String>> membership;
	private static Map<String,Integer> ipToAMI;
	public static String FILEPATH_NAME = "/servers.txt";
	
	public static void collectClusterData(){
		membership = getMemberIPAddress(FILEPATH_NAME);
		ipToAMI = getIPToAMIMapping(membership);
	}
	
	public static Map<String,Integer> getIPToAMIMapping(Map<Integer,List<String>> membership){
		ipToAMI = new HashMap<String, Integer>();
		for(Integer amiIndex: membership.keySet()){
			String currentIP = membership.get(amiIndex).get(0);
			ipToAMI.put(currentIP, amiIndex);
		}
		return ipToAMI;
	}
	
	/**
	 * Gets the ip addresses of the instances on record
	 * @return
	 */
	public static List<String> getMemberIPAddress(){
		collectClusterData();
		List<String> ipAddr = new ArrayList<String>();
		for(Integer amiIndex: membership.keySet()){
			String currentIP = membership.get(amiIndex).get(0);
			ipAddr.add(currentIP);
		}
		
		return ipAddr;
	}

	/**
	 * Gets the ami index of the ip addresses
	 * @return
	 */
	public static Integer getAMIFromIP(String ipAddress){
		return ipToAMI.get(ipAddress);
	}
	
	//given a file correct file path parses the contents with respect to 3 attributes
		//creates and returns a HashMap <Integer,ArrayList<String>>, AMI_INDEX --> [(IP), (DNS), (REBOOT_COUNT)]
		//note the index of IP is 0, DNS is 1 and REBOOT_COUNT is 2
		public static HashMap <Integer, List<String>> getMemberIPAddress(String file){
			HashMap <Integer,List<String>> map = new HashMap<Integer, List<String>>();
			FileReader input = null;
			try {
				input = new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Could not read file as it was not found");
			}
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			int num_attributes = 4;
			int line_count = 0;
			ArrayList<String> meta_data = new ArrayList<String>();
			String AMI_INDEX = "-1";
			try {
				while ( (myLine = bufRead.readLine()) != null)
				{    

					if (myLine.isEmpty()) break;
					//System.out.println(line_count + ": " + myLine);
					//KEY: AMI_INDEX, Unique Identifier for server 
					if (line_count % num_attributes == 0){
						AMI_INDEX = myLine.substring(6);
						
					}
					//AttributePair_1, IP --> IP_ADDR
					else if ((line_count % num_attributes) == 1){
						meta_data.add(myLine.substring(14));
					}
					//AttributePair_2, DNS --> DNS_ADDR
					else if ((line_count % num_attributes) == 2){
						meta_data.add(myLine.substring(15));
					}
					
					//AttributePair_3, REBOOT --> REBOOT_COUNT
					else if (line_count % num_attributes == 3){
						meta_data.add(myLine.substring(18));
						map.put(Integer.parseInt(AMI_INDEX), meta_data);
						meta_data = new ArrayList<String>();
					}
					++line_count;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in Reading Line");
			}
			return map;
		}
}
