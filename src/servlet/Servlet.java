package servlet;

import java.io.IOException;
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
		
		PrintWriter out = response.getWriter();
		MySession newSession = null;
		MyCookie newCookie = null;
		
		//Get all the cookie that was received in the request and find if any
		//of them came from our server
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);

		// Create a new session or refresh an existing one depending on whether
		// a cookie was passed to us by the browser that was created by us
		if(cookie == null){
			System.out.println("New cookie and session needs to be created");
			
			//Created a new session and added it to the session table
			newSession = new MySession();
			sessionTable.addSession(newSession);
			System.out.println("New session and cookie have been created");
			System.out.println(sessionTable.getSessionTableSize());
			
			//Retrieving the newly created cookie and sending it back in the
			//response
			newCookie = newSession.getCustomCookie();
			response.addCookie(newCookie);
		} else {
			System.out.println("Old cookie has been received. New one does not need to be created");
			
			//Retrieving details about the session using the session id stored in
			//cookie
			String sessionID = cookie.getValue();
			newSession = sessionTable.getSession(sessionID);
			newCookie = newSession.getCustomCookie();
		}
		
		out.println("<html>");
		out.println("<head> <title>Gaurav Keswani - gk368</title> </head>");
		out.println("<body>");
		out.println("<div class = 'row'>");
		out.println("<b>NetID:</b> gk368");
		out.println("<b>Session:</b>" + newCookie.getSessionID());
		out.println("<b>Version: </b>" + newCookie.getVersionNumber());
		out.println("<b>Date:</b>");
		out.println("</div>");
		out.println("<h1>" + newSession.getMessage() + "</h1>");
		out.println("<form method='POST' action='/'>");
		out.println("<div class='container'>");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' name='replace' value='Replace' />");
		out.println("<input type='text' name='message' />");
		out.println("</div");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' name='btn-refresh' value='Refresh' />");
		out.println("</div>");
		out.println("<div class = 'row'>");
		out.println("<input type='submit' name='btn-logout' value='Logout' />");
		out.println("</div");
		out.println("</div>");
		out.println("<div class = 'row'>");
		out.println("<b>Cookie: " + newCookie.toString() + "</b>");
		out.println("<b>Expires: " + newSession.getExpirationDate() + "</b>");
		out.println("</div>");
		out.println("</form>");
		out.println("</body>");
		out.println("<html>");
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
		for(int i=0; i<cookies.length;i++){
			System.out.println(cookies[i].getName());
			if(cookies[i].getName().equals("CS5300PROJ!SESSION")){
				return cookies[i];
			}
		}
		return null;
	}
}
