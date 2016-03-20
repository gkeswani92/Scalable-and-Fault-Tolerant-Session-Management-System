package session;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;

public class SessionManager extends HttpServlet{

	private static final long serialVersionUID = 1L;
	public static ConcurrentHashMap<String, MySession> sessionInformation;
	
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
	
	/**
	 * Cleaning up all sessions which have already expired
	 */
	public void cleanUpExpiredSessions(){
		Iterator<Entry<String, MySession>> i = sessionInformation.entrySet().iterator();
		while(i.hasNext()){
			Entry<String, MySession> sessionEntry = i.next();
			if(sessionEntry.getValue().getExpirationDate().before(new Date())){
				System.out.println("Cleaned up an expired session");
				i.remove();
			}
		}
	}
}
