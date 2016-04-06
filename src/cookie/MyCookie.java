package cookie;

import javax.servlet.http.Cookie;

public class MyCookie extends Cookie{
	
	private static final long serialVersionUID = 1L;
	private static String cookieName = "CS5300PROJ1SESSION";
	private String sessionID;
	private Integer versionNumber;
	private LocationMetadata locationData;
	
	/**
	 * Constructor to create a cookie with a session id, version number, location
	 * data and expiration date
	 * @param sessionID
	 * @param versionNumber
	 * @param locationData
	 * @param expiration
	 */
	public MyCookie(String sessionID, Integer versionNumber, LocationMetadata locationData, Integer expiration) {
		super(cookieName, sessionID);
		this.sessionID = sessionID;
		this.versionNumber = versionNumber;
		this.locationData = locationData;
		super.setDomain(".amazonaws.com");
		super.setPath("/");
		this.setMaxAge(expiration);
	}
	
	/**
	 * Cookie details are concatenated version of the session ID, version Number
	 * and the location Data
	 */
	public String toString(){
		return this.sessionID + "_" + this.versionNumber + "_" + this.locationData.toString();
	}
	
	public void refreshCookie(Integer cookie_age){
		this.setMaxAge(cookie_age);
	}
	
	/**
	 * @return the locationData
	 */
	public LocationMetadata getLocationData() {
		return locationData;
	}

	/**
	 * @return the versionNumber
	 */
	public Integer getVersionNumber() {
		return versionNumber;
	}
	
	/**
	 * @return the versionNumber
	 */
	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
}
