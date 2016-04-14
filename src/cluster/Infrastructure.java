package cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Infrastructure {
	
	private static String filePath = "/extra.txt";
	private static Integer N;
	private static Integer F;
	private static Integer R;
	private static Integer W;
	private static Integer WQ;
	
	public static void getLatestInfrastructureInfo(){
		
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
					N = Integer.parseInt(currentLine.trim());
				} else if(lineCount == 1){
					F = Integer.parseInt(currentLine.trim());
				}
				lineCount++;
			}
			computeSSMParameters();
		} catch (NumberFormatException e) {
			System.out.println("Number Format Exception while trying to read instance data");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("I/O Exception while trying to read instance data");
			e.printStackTrace();
		}
	}

	private static void computeSSMParameters(){
		R = F + 1;
		W = 2*F + 1;
		WQ = F + 1;
		System.out.println("Number of servers: "+N+" Resiliency: "+F);
		System.out.println("R: "+ R +" W: "+ W + "WQ: "+WQ);
	}
	
	public static Integer getN() {
		return N;
	}

	public static Integer getF() {
		return F;
	}

	public static Integer getR() {
		return R;
	}

	public static Integer getW() {
		if (W > N){
			return N;
		} 
		return W;
	}

	public static Integer getWQ() {
		return WQ;
	}
}
