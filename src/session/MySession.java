package session;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import cookie.LocationMetadata;
import cookie.MyCookie;

public class MySession implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String sessionID;
	private String message;
	private int versionNumber;
	private Date timeOfCreation;
	private Date expirationDate;
	private MyCookie customCookie;
	private final int AGE = 30;
	
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
		
		this.customCookie = new MyCookie(this.sessionID, this.versionNumber, new LocationMetadata(), AGE);
	}
	
	/**
	 * Refreshing a session includes updating the last access time and updates
	 * the expiration date by 360 seconds
	 */
	public void refreshSession(){
		Calendar cal = Calendar.getInstance();		
		cal.add(Calendar.SECOND, AGE);
		this.setExpirationDate(cal.getTime());		
		this.customCookie.refreshCookie(AGE);
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
		this.customCookie.setVersionNumber(this.versionNumber);
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

	public MyCookie getCustomCookie() {
		return customCookie;
	}

	public void setCustomCookie(MyCookie customCookie) {
		this.customCookie = customCookie;
	}
}
