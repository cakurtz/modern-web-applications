import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OpenStackMeetingsController {

	private QueryService queryService;
	
	/*
	 * Displays a welcome message when the base URL is called
	 */
	@ResponseBody
    @RequestMapping(value = "/openstackmeetings")
    public String welcomeMessage()
    {
        return "Welcome to OpenStack meeting statistics calculation page. "
        		+ "Please provide project and year as query parameters.";
    }
	
	/*
	 * Gets the data for the queried project and year
	 */
	@ResponseBody
    @RequestMapping(value = "/openstackmeetings", params = {"project", "year"}, method=RequestMethod.GET)
    public String getProjectData(@RequestParam("project") String project, @RequestParam("year") String year)
    {
		String ret = "";
		if (project != null && year != null) {
			ret = queryService.getResponseFromEavesDrop(project.toLowerCase(), year);
			if(!validateYear(year) && !ret.substring(0,12).equals("Invalid year"))
				ret += System.lineSeparator() + "Invalid year " + year + " for project " + project;
		}
		return ret;
    }
	
	/* 
	 * Checks the validity of the year parameter.
	 * The year parameter must be all digits.
	 */
	protected boolean validateYear(String year) {
		boolean valid = true;
		for(int i = 0; i < year.length(); i++) {
			Character ch = year.charAt(i);
			if(!Character.isDigit(ch)) {
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	public String getWithGhostEditor() {
		queryService = new OpenStackMeetingsServiceImpl();
        return welcomeMessage();		
	}
	
	public void setQueryService(QueryService qService) {
		this.queryService = qService;
	}
}
