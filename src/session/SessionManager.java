package session;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;

public class SessionManager extends HttpServlet{

	private static final long serialVersionUID = 1L;
	public static ConcurrentHashMap<String, MySession> sessionInformation;
	private static final int DISCARD_DELAY = -1;
	
	public SessionManager(){
		sessionInformation = new ConcurrentHashMap<String, MySession>();
	}
	
	public void addSession(MySession session){
		sessionInformation.put(session.getSessionID(), session);
	}
	
	public MySession getSession(String sessionID){
		return sessionInformation.get(sessionID);
	}
	
	public void terminateSession(MySession session){
		sessionInformation.remove(session.getSessionID());
	}
	
	public Integer getSessionTableSize(){
		return sessionInformation.size();
	}
	
	public static void displaySessionTable(){
		System.out.println("");
		System.out.println("Session Table:");
		for(String id: sessionInformation.keySet()){
			System.out.println(id + "-->" + sessionInformation.get(id));
		}
		System.out.println("");
	}
	
	/**
	 * Cleaning up all sessions which have already expired
	 */
	public static void cleanUpExpiredSessions(){
		if(sessionInformation != null){
			Iterator<Entry<String, MySession>> i = sessionInformation.entrySet().iterator();
			while(i.hasNext()){
				Entry<String, MySession> sessionEntry = i.next();
				
				// Subtracting delta of 1 seconds from current time to get discard time
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.SECOND, DISCARD_DELAY);
				
				if(sessionEntry.getValue().getExpirationDate().before(cal.getTime())){
					System.out.println("Clean up Thread: Cleaned up an expired session: "+sessionEntry.getKey());
					i.remove();
				}
			}
		}
	}
}
