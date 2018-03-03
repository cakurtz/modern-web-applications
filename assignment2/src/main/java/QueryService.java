
public interface QueryService {
	
	public String getName();
	
	public JSoupHandler getJSoupHandler();
	
	public String getResponseFromEavesDrop(String projectName, String year);
	
	public String findError(String project, String year);

}
