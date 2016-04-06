package servlet;

import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletListener implements ServletContextListener {
	
	@Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Servlet starting up automatically!");
        new Servlet();
        System.out.println("Servlet has started running in the background");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }
}
