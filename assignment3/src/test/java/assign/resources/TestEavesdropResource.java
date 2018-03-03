package assign.resources;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;

public class TestEavesdropResource
{

   private HttpClient client;

   @Before
   public void initClient()
   {
	   client = HttpClientBuilder.create().build();
   }

   /*
    * Test for getting all the projects (EavesdropService.getProjects())
    * GET http://localhost:8080/assignment3/myeavesdrop/projects/
    */
   @Test
   public void testGetProjects() throws Exception
   {
      System.out.println("**** Testing Projects ***");
      
      String url = "http://localhost:8080/assignment3/myeavesdrop/projects/";
      String[] firstFiveProjects = {"3rd_party_ci/", "17_12_2015/", 
    		  						   "2015_09_17/", "2015_10_15/", 
								   "2015_10_29/"};
      String[] lastFiveProjects = {"xen/", "xenapi/", "zaqar/", 
				   				  "zun/", "zuul/"};
      
      // Code snippet taken from https://www.mkyong.com/java/apache-httpclient-examples/
      HttpGet request = new HttpGet(url);
      HttpResponse response = client.execute(request);

      System.out.println("Response Code : "
              + response.getStatusLine().getStatusCode());

	  BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		
	  StringBuffer result = new StringBuffer();
	  String line = "";
	  while ((line = rd.readLine()) != null) {
		  result.append(line);
	  }

      System.out.println(result);
      
      // Parse XML result using Regex
      List<String> names = parseXML("<project>", result);	
      
      // Write asserts to verify the output of the first and
      // last 5 projects
      for(int i = 0; i < firstFiveProjects.length; i++) {
    	  	assertEquals(names.get(i), firstFiveProjects[i]);
      }
      int count = 0;
      for(int i = names.size() - 5; i < names.size(); i++) {
  	  	assertEquals(names.get(i), lastFiveProjects[count]);
  	  	count++;
  	  }
   }   
   
   /*
    * Tests results of EavesdropService.getProject(solum_team_meeting)
    * GET http://localhost:8080/assignment3/myeavesdrop/projects/solum_team_meeting/meetings
    */
   @Test
   public void testGetProjectA() throws Exception
   {
      System.out.println("**** Testing Project: solum_team_meeting ***");
      
      String url = "http://localhost:8080/assignment3/myeavesdrop/projects/solum_team_meeting/meetings";
      String[] years = {"2013/", "2014/", "2015/", "2016/", "2017/"};
      
      // Code snippet taken from https://www.mkyong.com/java/apache-httpclient-examples/
      HttpGet request = new HttpGet(url);
      HttpResponse response = client.execute(request);

      System.out.println("Response Code : "
              + response.getStatusLine().getStatusCode());

	  BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		
	  StringBuffer result = new StringBuffer();
	  String line = "";
	  while ((line = rd.readLine()) != null) {
		  result.append(line);
	  }

      System.out.println(result);
      
      
      // Parse XML result using Regex
      List<String> names = parseXML("<year>", result);	
      
      // Verify the output
      assertEquals(names.size(), years.length);
      for(int i = 0; i < years.length; i++) {
    	  	assertEquals(names.get(i), years[i]);
      }
   }
   
   /*
    * Tests results of EavesdropService.getProject(3rd_party_ci)
    * GET http://localhost:8080/assignment3/myeavesdrop/projects/3rd_party_ci/meetings
    */
   @Test
   public void testGetProjectB() throws Exception
   {
      System.out.println("**** Testing Project: 3rd_party_ci ***");
      
      String url = "http://localhost:8080/assignment3/myeavesdrop/projects/3rd_party_ci/meetings";
      String[] years = {"2014/"};
      
      // Code snippet taken from https://www.mkyong.com/java/apache-httpclient-examples/
      HttpGet request = new HttpGet(url);
      HttpResponse response = client.execute(request);

      System.out.println("Response Code : "
              + response.getStatusLine().getStatusCode());

	  BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		
	  StringBuffer result = new StringBuffer();
	  String line = "";
	  while ((line = rd.readLine()) != null) {
		  result.append(line);
	  }

      System.out.println(result);
      
      
      // Parse XML result using Regex
      List<String> names = parseXML("<year>", result);	
      
      // Verify the output
      assertEquals(names.size(), years.length);
      for(int i = 0; i < years.length; i++) {
    	  	assertEquals(names.get(i), years[i]);
      }
   }
   
   /*
    * Tests results of EavesdropService.getProject(non-existent-project)
    * GET http://localhost:8080/assignment3/myeavesdrop/projects/non-existent-project/meetings
    * Output should result with an Error tag and message
    */
   @Test
   public void testGetProjectC() throws Exception
   {
      System.out.println("**** Testing Project: non-existent-project ***");
      
      String url = "http://localhost:8080/assignment3/myeavesdrop/projects/non-existent-project/meetings";
      String output = "Project non-existent-project does not exist";
      
      // Code snippet taken from https://www.mkyong.com/java/apache-httpclient-examples/
      HttpGet request = new HttpGet(url);
      HttpResponse response = client.execute(request);

      System.out.println("Response Code : "
              + response.getStatusLine().getStatusCode());

	  BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		
	  StringBuffer result = new StringBuffer();
	  String line = "";
	  while ((line = rd.readLine()) != null) {
		  result.append(line);
	  }

      System.out.println(result);
      
      
      // Parse XML result using Regex
      List<String> names = parseXML("<error>", result);	
      
      // Verify the output
      assertEquals(names.size(), 1);
      assertEquals(names.get(0), output);
   }
   
   /*
    * Parses the XML data using the regex specified in pattern parameter.
    */
   private List<String> parseXML(String pattern, StringBuffer data){
	   List<String> names = new ArrayList<String>();
	   
	   //<project>name</project>
	   Pattern startPattern = Pattern.compile(pattern);
	   Matcher startMatch = startPattern.matcher(data);
	   
	   while(startMatch.find()) {
		   int start = startMatch.end();
		   StringBuffer b = new StringBuffer();
		   int i = start;
		   while(data.charAt(i) != '<') {
			   b.append(data.charAt(i));
			   i++;
		   }
		   names.add(b.toString());
		   if(pattern.equals("<year>"))
			   System.out.println("Year: " + b.toString());
		   else if(pattern.equals("<error>"))
			   System.out.println("Error Message: " + b.toString());
		   else
			   System.out.println("Project Name: " + b.toString());
	   }
	   
	   return names;
   }
   
   
}
