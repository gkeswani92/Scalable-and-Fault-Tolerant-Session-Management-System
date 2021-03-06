package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cluster.Infrastructure;
import cluster.Instance;
import cookie.LocationMetadata;
import cookie.MyCookie;
import rpc.Client;
import rpc.RPCInfo;
import rpc.Server;
import rpc.SessionCleanerThread;
import session.MySession;
import session.SessionManager;

@WebServlet("/")
public class Servlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static SessionManager sessionTable = new SessionManager();
	private static Client rpcClient;
	private static Server rpcServer;
	private static SessionCleanerThread sessionCleaner;
	
	public Servlet(){
		System.out.println("Starting Client and Session Table Cleaner");
		rpcClient = new Client();
		sessionCleaner = new SessionCleanerThread();
		new Thread(sessionCleaner).start();
		Infrastructure.getLatestInfrastructureInfo();
	}
	
	public void initialize(){
		System.out.println("Servlet init called by Context Listener and starting RPC Server");
		rpcServer = new Server();
		new Thread(rpcServer).start();
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
										throws ServletException, IOException {
		
		MySession newSession = null;
		MyCookie newCookie = null;
		List<String> wqaddress = null;
		String svrID = null;
		
		System.out.println("Servlet: Ami index: "+Instance.getAmiIndex());
		System.out.println("Servlet: Ip address: "+Instance.getIpAddr());
		System.out.println("Servlet: Reboot count: "+Instance.getRebootCount());
		
		//Get all the cookie that was received in the request and find if any
		//of them came from our server
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);
		
		//Gets the session if it already exists, otherwise creates a new one
		RPCInfo sessionInfo = getSession(cookie);
		if(sessionInfo != null){
			newSession = sessionInfo.getSession();
			wqaddress = sessionInfo.getLocations();
			svrID = sessionInfo.getServerID();
		
			//Render the web page if session was found/created. Else display error page
			if(newSession != null){
				//Retrieving the newly created cookie and sending it back in the response
				newCookie = new MyCookie(newSession.getSessionID(), newSession.getVersionNumber(), 
						new LocationMetadata(wqaddress), MySession.AGE);
				response.addCookie(newCookie);
				System.out.println("Servlet: Added the new cookie in the response");
						
				//Render the web page with the details
				displayWebPage(response, newCookie, newSession, svrID);
				
				System.out.println(" ");
			} else {
				PrintWriter out = response.getWriter();
				out.println("Could not display web page as session could not be found/created");
				return;
			}
		} else {
			PrintWriter out = response.getWriter();
			out.println("Could not display web page as session could not be found/created");
			return;
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
										throws ServletException, IOException {
		
		List<String> wqaddress = null;
		MySession session = null;
		String svrID = null;
		
		//Get all the cookies that were received in the request and find the one 
		//that was sent by our server. There has to be one since this is a POST
		//request and one would have been created in the GET request. We then
		//find the corresponding session using the session id stored in the cookie
		Cookie[] cookies =  request.getCookies();
		Cookie cookie = findCorrectCookie(cookies);
		
		//Getting the session and the location metadata
		RPCInfo sessionInfo = getSession(cookie);
		session = sessionInfo.getSession();
		wqaddress = sessionInfo.getLocations();
		svrID = sessionInfo.getServerID();
		
		//If the cookie had a stale session that has been discarded, we need to
		//create a new session and a new cookie
		if(session == null){
			session = new MySession();
			sessionTable.addSession(session);
			System.out.println("New session has been created since session id "
					+ "in the cookie was terminated. Ignore post params");
		} else {
		
			if(request.getParameter("replace") != null) {
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
				//Already taking care of the refresh before this
			} else {
				//Sending an expired cookie back to the client so that it doesnt send it back to us
				MyCookie myCookie = new MyCookie(session.getSessionID(), session.getVersionNumber(), new LocationMetadata(wqaddress), 0);
				response.addCookie(myCookie);
				
				sessionTable.terminateSession(session);
				PrintWriter out = response.getWriter();
				out.println("You have been logged out");
				return;
			}
		}
		
		//At this point, we have either gathered the session data from other
		//nodes or created a new one. So we send it to other nodes and gather
		//the ami indexes of nodes that responded successfully
		wqaddress = sessionWriteHelper(session);
		System.out.println("Servlet: Consensus has been received");
		
		//Render the web page with the updated details and send back the 
		//latest cookie to the client
		MyCookie myCookie = new MyCookie(session.getSessionID(), session.getVersionNumber(), new LocationMetadata(wqaddress), MySession.AGE);
		response.addCookie(myCookie);
		displayWebPage(response, myCookie, session, svrID);
		
		System.out.println(" ");
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
		if(cookies != null){
			for(Cookie c: cookies){
				if(c.getName().equals("CS5300PROJ1SESSION")){
					System.out.println("Servlet: A cookie was sent by the browser");
					return c;
				}
			}
		}
		System.out.println("Servlet: No cookie was sent by the browser");
		return null;
	}
	

	/**
	 * If the session id in the cookie already exists, get that session. Otherwise
	 * create a new session and cookie and return this new session
	 * @param cookie
	 * @return
	 */
	private RPCInfo getSession(Cookie cookie) {

		//Create a new session or refresh an existing one depending on whether
		//a cookie was passed to us by the browser that was created by us		
		RPCInfo communicationInfo = null;
		MySession newSession;
		
		//Created a new session and added it to the session table
		if(cookie == null){
			System.out.println("Servlet: New cookie and session needs to be created");	
			newSession = new MySession();
			sessionTable.addSession(newSession);
			System.out.println("Servlet: New cookie and session have been created");
			
			//At this point, we have created a new session and so we send it to 
			//other nodes and gather the ami indexes of nodes that responded 
			//successfully
			System.out.println("Servlet: New session data is to be transmitted to other instances");
			
			communicationInfo = new RPCInfo(newSession, sessionWriteHelper(newSession), Instance.getAmiIndex().toString());
			System.out.println("Servlet: Consensus has been received");
		} 
		
		else {
			System.out.println("Servlet: Old cookie has been received. New one does"
					+ " not need to be created");
			
			//Retrieving details about the session using the session id stored in
			//cookie
			String cookieDetails = cookie.getValue();
		    String[] cookie_params = cookieDetails.split("_");
	        String sessionID = cookie_params[0];
	        Integer versionNumber = Integer.parseInt(cookie_params[1]);
	        LocationMetadata locationData = new LocationMetadata(cookie_params[2]);
		    System.out.println("Servlet: Session ID: " + sessionID + " and "
		    		+ "Locations: "+locationData.toString());
	        
			//Calling session read using the RPC client to get data back from the
			//first instance that replies
		    String[] sessionData = rpcClient.sessionRead(sessionID, locationData, versionNumber);
			
		    if(sessionData.length == 6){
				newSession = new MySession(sessionData[1], Integer.parseInt(sessionData[2]), 
						sessionData[3], sessionData[4]);
				newSession.refreshSession();
				sessionTable.addSession(newSession);
				System.out.println("Servlet: Session data has been gathered from "
						+ "another instance and has been stored in the local table");
				
				communicationInfo = new RPCInfo(newSession, sessionWriteHelper(newSession), sessionData[5]);
		    } else {
		    	System.out.println("Servlet: Session data received was of invalid length: "+sessionData.length);
		    }
		}
		return communicationInfo;
	}
	
	public List<String> sessionWriteHelper(MySession sess) {
		System.out.println("Servlet: New session data is to be transmitted to other instances");
		List<String> wqaddress = new ArrayList<String>();
		do{
			wqaddress = rpcClient.sessionWrite(sess);
		} while( wqaddress.size() == 0 );
		return wqaddress;
		
	}
	
	public void displayWebPage(HttpServletResponse response, MyCookie newCookie, 
						MySession newSession, String svrID) throws IOException{
		
		PrintWriter out = response.getWriter();
		Calendar cal = Calendar.getInstance();
		
		out.println("<html>");
		out.println("<head> <title>gk368, tmm259, bi49</title> </head>");
		out.println("<body>");
		out.println("<br/><b>NetID's:</b> gk368, tmm259, bi49");
		out.println("<br/><b>Session:</b>" + newCookie.getSessionID());
		out.println("<br/><b>Version: </b>" + newCookie.getVersionNumber());
		out.println("<br/><b>Date:</b>" + cal.getTime());
		out.println("<br/><br/>");
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
		out.println("</form>");
		
		out.println("<br/><br/>");
		out.println("<b>Cookie: </b>" + newCookie.toString());
		out.println("<br/><b>Expires: </b> " + newSession.getExpirationDate());
		out.println("<br/><b>Current Server ID: </b> " + Instance.getAmiIndex());
		out.println("<br/><b>Reboot Count: </b> " + Instance.getRebootCount());
		out.println("<br/><b>Cookie Domain: </b> " + newCookie.getDomain());
		out.println("<br/><b>Server ID Serving Data: </b> " + svrID);
		out.println("</body>");
		out.println("<html>");
	}
}
