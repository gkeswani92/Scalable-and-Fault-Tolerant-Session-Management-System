package session;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import cookie.MyCookie;

public class MySession {
	
	private String sessionID;
	private String message;
	private String versionNumber;
	private Date timeOfCreation;
	private Date lastAccess;
	private Date expirationDate;
	private MyCookie customCookie;
	private final int cookie_age = 30;
	
	public MySession(){
		
		// Setting information that also needs to be passed to the cookie
		this.setSessionID(UUID.randomUUID().toString().replaceAll("[^\\d.]", ""));
		this.setMessage("Hello, User!");
		this.setVersionNumber("1");
		
		// Setting information that will be used to determine if a session has 
		// expired
		Calendar cal = Calendar.getInstance();
		this.setTimeOfCreation(cal.getTime());
		this.setLastAccess(cal.getTime());
		cal.add(Calendar.SECOND, cookie_age);
		this.setExpirationDate(cal.getTime());
		
		setCustomCookie(new MyCookie(this.sessionID, this.versionNumber, null, cookie_age));
	}
	
	/**
	 * Refreshing a session includes updating the last access time and updates
	 * the expiration date by 360 seconds
	 */
	public void refreshSession(){
		Calendar cal = Calendar.getInstance();
		this.setLastAccess(cal.getTime());
		
		cal.add(Calendar.SECOND, cookie_age);
		this.setExpirationDate(cal.getTime());
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
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Date getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(Date timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
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
