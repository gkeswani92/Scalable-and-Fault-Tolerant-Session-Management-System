package cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Instance {
	
	private static String filePath = "/instance_info.txt";
    	private static Integer amiIndex;
    	private static String ipAddr;
    	private static Integer rebootCount;
    	    	
    	public static void getLatestInstanceInfo(){
    		
    		FileReader input = null;
			try {
				input = new FileReader(filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Could not read file as it was not found");
			}
			BufferedReader bufRead = new BufferedReader(input);
			String currentLine = null;
			Integer lineCount = 0;
			
			try {
				while ((currentLine = bufRead.readLine()) != null)
				{    
					if (currentLine.isEmpty()) 
						break;

					if(lineCount == 0){
						setAmiIndex(Integer.parseInt(currentLine.substring(6)));
					} else if(lineCount == 1){
						setIpAddr(currentLine.substring(14));
					} else if(lineCount == 2){
						lineCount++;
						continue;
					} else {
						setRebootCount(Integer.parseInt(currentLine.substring(18)));
					}
					lineCount++;
				}
			} catch (NumberFormatException e) {
				System.out.println("Number Format Exception while trying to read instance data");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("I/O Exception while trying to read instance data");
				e.printStackTrace();
			}
    	}

		public static Integer getAmiIndex() {
			getLatestInstanceInfo();
			return amiIndex;
		}

		public static void setAmiIndex(Integer amiIndex) {
			Instance.amiIndex = amiIndex;
		}

		public static String getIpAddr() {
			getLatestInstanceInfo();
			return ipAddr;
		}

		public static void setIpAddr(String ipAddr) {
			Instance.ipAddr = ipAddr;
		}

		public static Integer getRebootCount() {
			getLatestInstanceInfo();
			return rebootCount;
		}

		public static void setRebootCount(Integer rebootCount) {
			Instance.rebootCount = rebootCount;
		}
}
