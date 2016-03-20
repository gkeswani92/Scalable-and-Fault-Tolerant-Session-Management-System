package servlet;

import java.io.IOException;
import java.util.Calendar;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cookie.MyCookie;
import session.MySession;
import session.SessionManager;

@WebServlet("/")
public class Servlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	SessionManager sessionTable = new SessionManager();
	
	public Servlet() {
        super();
	}
        
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
										throws ServletException, IOException {
		
		sessionTable.cleanUpExpiredSessions();
		MySession newSession = null;
		MyCookie newCookie = null;
		
		//Get all the cookie that was received in the request and find if any
		//of them came from our server
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);

		//Gets the session if it already exists, otherwise creates a new one
		newSession = getSession(cookie);
		
		//Retrieving the newly created cookie and sending it back in the response
		newCookie = newSession.getCustomCookie();
		response.addCookie(newCookie);
		
		//Render the web page with the details
		displayWebPage(response, newCookie, newSession);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
										throws ServletException, IOException {
		
		sessionTable.cleanUpExpiredSessions();
		
		//Get all the cookie that was received in the request and find the one 
		//that was sent by our server. There has to be one since this is a POST
		//request and one would have been created in the GET request
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);
		
		MySession session = getSession(cookie);
		
		if(request.getParameter("replace") != null){
			System.out.println("The replace button has been pressed");	
			String newState = request.getParameter("message");
			
			//Changing the state unless the replacement text was null
			if(newState != null){
				System.out.println("The new state needs to be " + newState);
				session.setMessage(newState);
			} else {
				System.out.println("New state cannot be empty. Not changing it");
			}
		} else if(request.getParameter("refresh") != null){
			session.refreshSession();
		} else {
			sessionTable.terminateSession(session);
			PrintWriter out = response.getWriter();
			out.println("You have been logged out");
			return;
		}
		
		//Render the web page with the updated details and send back the 
		//latest cookie to the client
		MyCookie myCookie = session.getCustomCookie();
		response.addCookie(myCookie);
		displayWebPage(response, myCookie, session);
	}
	
	/**
	 * Goes through the list of cookies sent by the browser and returns the first
	 * cookie with the name CS5300PROJ1SESSION. If such a cookie is found, it 
	 * means it is not a fresh session since only our custom cookies have the
	 * name CS5300PROJ1SESSION
	 * 
	 * @param cookies
	 * @return
	 */
	public Cookie findCorrectCookie(Cookie[] cookies){
		for(Cookie c: cookies){
			if(c.getName().equals("CS5300PROJ1SESSION")){
				return c;
			}
		}
		return null;
	}
	

	/**
	 * If the session id in the cookie already exists, get that session. Otherwise
	 * create a new session and cookie and return this new session
	 * @param cookie
	 * @return
	 */
	private MySession getSession(Cookie cookie) {
		MySession newSession;
		
		// Create a new session or refresh an existing one depending on whether
		// a cookie was passed to us by the browser that was created by us
		if(cookie == null){
			System.out.println("New cookie and session needs to be created");
			
			//Created a new session and added it to the session table
			newSession = new MySession();
			sessionTable.addSession(newSession);
			System.out.println("New session and cookie have been created");
		} else {
			System.out.println("Old cookie has been received. New one does not need to be created");
			
			//Retrieving details about the session using the session id stored in
			//cookie
			String sessionID = cookie.getValue();
			System.out.println(sessionTable.getSessionTableSize());
			newSession = sessionTable.getSession(sessionID);
			
			//If the cookie had a stale session that has been discarded, we 
			//need to create a new session and a new cookie
			if(newSession == null){
				newSession = new MySession();
				sessionTable.addSession(newSession);
				System.out.println("New session has been created since session id in the cookie was terminated");
			} else {
				newSession.incrementVersionNumber();
			}
		}
		return newSession;
	}
	
	public void displayWebPage(HttpServletResponse response, MyCookie newCookie, 
						MySession newSession) throws IOException{
		
		PrintWriter out = response.getWriter();
		Calendar cal = Calendar.getInstance();
		
		out.println("<html>");
		out.println("<head> <title>Gaurav Keswani - gk368</title> </head>");
		out.println("<body>");
		out.println("<div class = 'row'>");
		out.println("<b>NetID:</b> gk368");
		out.println("<b>Session:</b>" + newCookie.getSessionID());
		out.println("<b>Version: </b>" + newCookie.getVersionNumber());
		out.println("<b>Date:</b>" + cal.getTime());
		out.println("</div>");
		out.println("<h1>" + newSession.getMessage() + "</h1>");
		out.println("<form method='POST' action='/Session_Management/'>");
		out.println("<div class='container'>");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' id='replace' name='replace' value='Replace' />");
		out.println("<input type='text' id='message' name='message' />");
		out.println("</div");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' id='refresh' name='refresh' value='Refresh' />");
		out.println("</div>");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' id='logout' name='logout' value='Logout' />");
		out.println("</div");
		out.println("</div>");
		out.println("<br/><br/>");
		out.println("<div class = 'row'>");
		out.println("<b>Cookie: </b>" + newCookie.toString());
		out.println("<b>Expires: </b> " + newSession.getExpirationDate());
		out.println("</div>");
		out.println("</form>");
		out.println("</body>");
		out.println("<html>");
	}
}
