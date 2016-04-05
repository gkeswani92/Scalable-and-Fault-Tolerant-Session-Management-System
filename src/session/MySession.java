package session;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MySession implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String sessionID;
	private String message;
	private int versionNumber;
	private Date timeOfCreation;
	private Date expirationDate;
	public static final int AGE = 120;
	
	public MySession(){
		
		// Setting information that also needs to be passed to the cookie
		this.setSessionID(UUID.randomUUID().toString().replaceAll("[^\\d.]", ""));
		this.message = "Hello, User!";
		this.setVersionNumber(1);
		
		// Setting information that will be used to determine if a session has 
		// expired
		Calendar cal = Calendar.getInstance();
		this.setTimeOfCreation(cal.getTime());
		cal.add(Calendar.SECOND, AGE);
		this.setExpirationDate(cal.getTime());
	}
	
	public MySession(String sessionID, Integer versionNumber, String message, String expirationTime){
		
		// Setting information that also needs to be passed to the cookie
		this.setSessionID(sessionID);
		this.message = message;
		this.setVersionNumber(versionNumber);
		
		// Setting information that will be used to determine if a session has 
		// expired
		Calendar cal = Calendar.getInstance();
		this.setTimeOfCreation(cal.getTime());
		
		//TODO: Need to set the expiration data by figuring out how to convert
		//string to Calender date
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		try {
			cal.setTime(sdf.parse(expirationTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Refreshing a session includes updating the last access time and updates
	 * the expiration date by 360 seconds
	 */
	public void refreshSession(){
		Calendar cal = Calendar.getInstance();		
		cal.add(Calendar.SECOND, AGE);
		this.setExpirationDate(cal.getTime());		
	}
	
	public String toString(){
		return this.sessionID + "_" + this.versionNumber + "_" + this.message + "_" + this.expirationDate;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		refreshSession();
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public void incrementVersionNumber(){
		this.versionNumber++;
	}

	public Date getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(Date timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
}
