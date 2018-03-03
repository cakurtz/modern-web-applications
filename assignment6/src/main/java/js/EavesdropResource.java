package js;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import js.EavesdropService;

@Path("/projects")
public class EavesdropResource {
	
	EavesdropService eavesdropService;
	
	public EavesdropResource() {
		this.eavesdropService = new EavesdropService();
	}
	
	/*
	 * Handles request to get data for Solum project
	 */
	@GET
	@Path("/solum-team-meeting")
	@Produces("text/html")
	public String getSolumData() {
		StringBuilder result = new StringBuilder("");
		List<String> years = eavesdropService.getProjectDataSolum();
		
		for(String year : years) {
			List<String> files = eavesdropService.getProjectYearData(year);
			String prevAdded = "";
			int count = 0;
			for(String file : files) {
				String noExtension = removeFileExtensions(file);
				if(prevAdded.equals("") || !prevAdded.equals(noExtension)) {
					prevAdded = noExtension;
					count++;
				} 
			}
			result.append(year.substring(0,year.length()-1) + " " + count + ",");
		}
		result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}
	
	/*
	 * Removes file extensions of the following forms:
	 * .html, .log, and .txt
	 */
	private String removeFileExtensions(String file) {
		StringBuilder result = new StringBuilder(file);
		boolean complete = false;
		while(!complete) {
			String lastFive = result.substring(result.length()-5); // .html extension
			String lastFour = result.substring(result.length()-4); // .log and .txt extensions
			if(lastFive.equals(".html"))
				result = result.delete(result.length()-5, result.length());
			else if(lastFour.equals(".log") || lastFour.equals(".txt"))
				result = result.delete(result.length()-4, result.length());
			else
				complete = true;
		}
		return result.toString();
	}
}