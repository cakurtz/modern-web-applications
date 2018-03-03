package assign.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EavesdropService {

	private JSoupHandler jsoupHandler = new JSoupHandler();
	private String baseURL = "http://eavesdrop.openstack.org/meetings/";
	
	public EavesdropService() {
		this.jsoupHandler = new JSoupHandler();
	}
	
	/*
	 * Gets all the projects located at:
	 * http://eavesdrop.openstack.org/meetings/
	 */
	public List<String> getProjects() {
		List<String> result = new ArrayList<String>();
		try {
			Elements data = doDataQuery(baseURL);
			result = extractData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * Gets the projects located at:
	 * http://eavesdrop.openstack.org/meetings/{project}
	 * where {project} is the specific name of the project
	 */
	public List<String> getProject(String name) {
		List<String> result = new ArrayList<String>();
		try {
			Elements data = doDataQuery(baseURL + name);
			result = extractData(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/* 
	 * Performs the data query
	 */
	protected Elements doDataQuery(String URL) 
            throws IOException
    {
		Elements links = null;
		
		try {
			links = jsoupHandler.getElements(URL);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return links;
    }
	
	/*
	 * Extracts the names of the projects
	 */
	protected List<String> extractData(Elements links) {
		List<String> result = null;
		
		if(links != null) {
			result = new ArrayList<String>();
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
	    				result.add(s);	    			
	    			}
		    }
		}
		return result;
	}
	
}
