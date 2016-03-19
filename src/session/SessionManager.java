package session;
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
	
	public Integer getSessionTableSize(){
		return sessionInformation.size();
	}
}
