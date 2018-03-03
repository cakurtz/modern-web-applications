import java.io.IOException;
import java.net.URL;
import java.util.ListIterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OpenStackMeetingsServiceImpl implements QueryService {

	String name = "OpenStackMeetingsServiceImpl";
	URL eavesdropURL = null;
	String baseURL = "http://eavesdrop.openstack.org/";
	JSoupHandler jsoupHandler;
	
	public OpenStackMeetingsServiceImpl() {
		try {
			eavesdropURL = new URL(baseURL);
			if (jsoupHandler == null) {
				jsoupHandler = new JSoupHandler();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the name of this service
	 */
	public String getName() {
		return this.name;
	}
	
	/*
	 * Getter for this instance's JSoupHandler
	 */
	public JSoupHandler getJSoupHandler() {
		return this.jsoupHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see EditorService#getResponseFromEavesDrop(java.lang.String, java.lang.String)
	 * Processes the response query with the desired project and year
	 */
	public String getResponseFromEavesDrop(String projectName, String year) {
		String retVal = "";	
		resetURL();
		baseURL += "/meetings/" + projectName + "/" + year;
		try {
			Elements data = doDataQuery();
			if (data != null) {
				retVal = extractData(data);
			} else {
				retVal = findError(projectName, year);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	/* 
	 * Performs the data query and checks the response for specific errors 
	 */
	protected Elements doDataQuery() 
            throws IOException
    {
		Elements links = null;
		
		try {
			links = jsoupHandler.getElements(baseURL);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return links;
    }
	
	/*
	 * Finds out why the query was not successful
	 * Looks at a project without the year to determine where fault lies
	 * If a project only is successful, year is the problem
	 * Assumes either the project or year is guaranteed to be correct
	 */
	public String findError(String project, String year) {
		Elements links = null;
		try {
			baseURL = "http://eavesdrop.openstack.org/meetings/" + project + "/";
			links = jsoupHandler.getElements(baseURL);
		} catch (Exception projectExp) {
			projectExp.printStackTrace();
		}
		
		// Problem lies with project name
		if(links == null) {
			return "Project with " + project + " not found";
		}
		// Problem lies with year
		else {
			return "Invalid year " + year + " for project " + project;
		}
	}
	
	/*
	 * Extracts the files from headers and counts the number of files
	 */
	protected String extractData(Elements links) {
		String result = "Number of meeting files: ";
		int count = 0;
		
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
	    				count++;	    			
	    			}
		    }
		}
		result += count;
		return result;
	}
	
	/* 
	 * Allows multiple requests in a session by resetting the global values 
	 * for URL variables baseURL and eavesdropURL
	 */
	protected void resetURL() {
		baseURL = "http://eavesdrop.openstack.org/";
		eavesdropURL = null;
	}

}
