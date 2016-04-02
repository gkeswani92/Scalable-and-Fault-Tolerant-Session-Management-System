import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.*;

public class parser_dsb {
	
	public static String FILEPATH_NAME = "servers.txt";
	
	//given a file correct file path parses the contents with respect to 3 attributes
	//creates and returns a HashMap <Integer,ArrayList<String>>, AMI_INDEX --> [(IP), (DNS), (REBOOT_COUNT)]
	//note the index of IP is 0, DNS is 1 and REBOOT_COUNT is 2
	public static HashMap <Integer,ArrayList<String>> getMemberIPAddress(String file){
		HashMap <Integer,ArrayList<String>> map = new HashMap<Integer,ArrayList<String>>();
		FileReader input = null;
		try {
			input = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not Read File");
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
				//System.out.println(line_count + ": " + myLine);
				//KEY: AMI_INDEX, Unique Identifier for server 
				if (line_count % num_attributes == 0){
					AMI_INDEX = myLine.substring(8);
					
				}
				//AttributePair_1, IP --> IP_ADDR, AttributePair_2, DNS --> DNS_ADDR
				else if ((line_count % num_attributes) < 3){
					meta_data.add(myLine.substring(24));
				}
				
				//AttributePair_3, REBOOT --> REBOOT_COUNT
				else if (line_count % num_attributes == 3){
					meta_data.add(myLine.substring(24));
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//please input a correct file created from 3 attributes per key value pair in sdb
		//Please specify the file's directory correctly
		HashMap <Integer,ArrayList<String>> map = getMemberIPAddress(FILEPATH_NAME);
		
		//USE TO PRINT MAP
		/*System.out.println("DICT!!!!");
		for ( ArrayList<String> elt : map.values()){
		    for (int i = 0; i < elt.size(); ++i){
		    	System.out.println(elt.get(i));
		    }
		}*/

		System.out.println("DONE");
	}
}