import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OpenStackMeetingsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	JSoupHandler jsoupHandler;

	List<String> history = new ArrayList<String>(); // Holds the history of correct queries only
	Cookie cookie; // Holds the session cookie
	
	public OpenStackMeetingsServlet() {
		if (jsoupHandler == null) {
			jsoupHandler = new JSoupHandler();
		}
	}

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException
    {
		PrintWriter w = response.getWriter();
		String session = request.getParameter("session");
		Elements data; // Holds the data from the query
		boolean valid = true; // Checks if parameters are valid for a query
		
		// Handles session 
		if(session != null) {
			session = session.toLowerCase();
			if(session.equals("start")) {
				cookie = new Cookie("knownSession", "1");
				cookie.setDomain("localhost");
				cookie.setPath("/assignment1" + request.getServletPath());
				cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
				response.addCookie(cookie);
				history = new ArrayList<String>();
				history.add(request.getRequestURL().toString() + "?" + request.getQueryString().toLowerCase());
				doPrintInfo(request, w, null, cookie != null);
			}
			else if(session.equals("end")) {
				if(cookie == null) {
					doPrintInfo(request, w, null, cookie != null);
				}
				else {
					if(cookieCheck(request)){
						doPrintInfo(request, w, null, cookie != null);
						cookie = null;
						history = new ArrayList<String>();
					}
				}
			}
		}
		
		// Handles project and year queries
		else {
			String project = request.getParameter("project");
			String year = request.getParameter("year");
			if((year == null || year == "") && (project == null || project == "")) {
				w.println("Required parameters project and year are missing");
			}
			else if(project == null || project == "") {
				w.println("Required parameter project is missing");
			}
			else if(year == null || year == "") {
				w.println("Required parameter year is missing");
			}
			else {
				// Valid query in terms of necessary parameters, proceed to perform data query
				if(valid) {
					project = project.toLowerCase();
					data = doDataQuery(request, response, w, project, year);
					if(data != null) {
						if(cookie != null) {
							cookieCheck(request);
						}
						doPrintInfo(request, w, data, cookie != null);
					}
				}
			}
		}
    }
	
	private void doPrintInfo(HttpServletRequest request, PrintWriter w, Elements links, boolean isSession) {
		// Print History
		w.println("History");
		if(isSession) {
			for(int index = 0; index < history.size() - 1; index++) {
				w.println(history.get(index));
			}
		}
		w.println();
		
		// Print Data
	    w.println("Data");
		if(links != null) {
			ListIterator<Element> iter = links.listIterator();
			
		    // Skip over header information, such as "Name" and "Parent Directory"
		    // and add custom header
		    for(int i = 0; i < 5; i++) {
		    		if(iter.hasNext()) {
		    			iter.next();
		    		}
		    }
		    while(iter.hasNext()) {
	    			Element e = (Element) iter.next();
	    			String s = e.html();
	    			if ( s != null) {
	    				w.println(s);		    			
	    			}
		    }
		}
	}
	
	/* Checks to see if server cookie exists in request and adds to history if it does  */
	private boolean cookieCheck(HttpServletRequest request) {
		Cookie[] temp = request.getCookies();
		if(temp != null && temp.length != 0) {
			for(int i = 0; i < temp.length; i++) {
				if(temp[i].getName().equals(cookie.getName()) && temp[i].getValue().equals(cookie.getValue())){
					history.add(request.getRequestURL().toString() + "?" + request.getQueryString().toLowerCase());
					return true;
				}
			}
		}
		return false;
	}
	
	/* Performs the data query and checks the response for specific errors */
	private Elements doDataQuery(HttpServletRequest request, HttpServletResponse response, PrintWriter w, String project, String year) 
            throws ServletException, IOException
    {
		Elements links = null;
		
		try {		    
			String source = "http://eavesdrop.openstack.org/meetings/" + project + "/" + year + "/";	
			links = jsoupHandler.getElements(source);
			if(links == null) {
				try {
					source = "http://eavesdrop.openstack.org/meetings/" + project + "/";
					links = jsoupHandler.getElements(source);
				} catch (Exception projectExp) {
					projectExp.printStackTrace();
				}
				// Problem lies with project name
				if(links == null) {
					w.println("Project with " + project + " not found");
				}
				// Problem lies with year
				else {
					w.println("Invalid year " + year + " for project " + project);
					links = null;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return links;
    }
	
	public void setJSoupHandler(JSoupHandler jsoupHandler) {
		this.jsoupHandler = jsoupHandler;
	}
}