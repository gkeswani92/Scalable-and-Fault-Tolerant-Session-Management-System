package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cookie.MyCookie;
import session.MySession;
import session.SessionManager;

@WebServlet("/index")
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
		
		//Get the cookie that was received in the request
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);
		
		// Create a new session or refresh an existing one depending on whether
		// a cookie was passed to us by the browser
		if(cookie == null){
			System.out.println("New cookie and session needs to be created");
			
			//Created a new session and added it to the session table
			MySession newSession = new MySession();
			String sessionID = newSession.getSessionID();
			sessionTable.addSession(newSession);
			System.out.println("New session and cookie have been created");
			
			response.addCookie(newSession.getCustomCookie());
			RequestDispatcher view = request.getRequestDispatcher("index.jsp");
			view.forward(request, response);
		} else {
			System.out.println("Old cookie has been received. New one does not need to be created");
		}
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
