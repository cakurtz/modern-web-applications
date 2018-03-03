package assign.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import assign.domain.Meeting;
import assign.domain.NewProject;
import assign.service.EavesdropProjectService;

@Path("/projects")
public class EavesdropProjectResource {
	
	EavesdropProjectService eavesdropProjectService;
	String password;
	String username;
	String dburl;	
	String dbhost, dbname;
	public EavesdropProjectResource(@Context ServletContext servletContext) {
		dbhost = servletContext.getInitParameter("DBHOST");
		dbname = servletContext.getInitParameter("DBNAME");
		dburl = "jdbc:mysql://" + dbhost + ":3306/" + dbname;
		username = servletContext.getInitParameter("DBUSERNAME");
		password = servletContext.getInitParameter("DBPASSWORD");
		this.eavesdropProjectService = new EavesdropProjectService();		
	}
	
	/*
	 * Get database credentials
	 */
	@GET
	@Path("/helloworld")
	@Produces("text/html")
	public String helloWorld() {
		System.out.println("Inside helloworld");
		System.out.println("DB creds are:");
		System.out.println("DBURL:" + dburl);
		System.out.println("DBUsername:" + username);
		System.out.println("DBPassword:" + password);		
		return "Hello world " + dburl + " " + username + " " + password;		
	}
	
	/*
	 * Creates a new project in the database from input xml
	 */
	@POST
	@Consumes("application/xml")
	public Response createProject(InputStream is) throws Exception {
	      NewProject newProject = readNewProject(is);
	      if(newProject.getName() == null || newProject.getName() == "" || 
	    		 newProject.getDescription() == null || newProject.getDescription() == "" ||
	    		 onlySpaces(newProject.getName()) || onlySpaces(newProject.getDescription())) {
	    	  	return Response.status(400).build();
	      }
	      Long id = this.eavesdropProjectService.addProject(newProject);
	      return Response.created(URI.create("/projects/" + id)).build();
	}
	
	/*
	 * Creates a new meeting in the database from input xml
	 */
	@POST
	@Path("/{id}/meetings")
	@Consumes("application/xml")
	public Response createMeeting(@PathParam("id") Long id, InputStream is) throws Exception {
	      Meeting newMeeting = readMeeting(is);
	      if(newMeeting.getName() == null || newMeeting.getName() == "" || 
	    		 newMeeting.getYear() == null || newMeeting.getYear() == "" || 
	    		 onlySpaces(newMeeting.getName()) || onlySpaces(newMeeting.getYear()) || 
	    		 !validYear(newMeeting.getYear())) {
	    	  	return Response.status(400).build();
	      }
	      NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }
	      
	      Long meetingId = this.eavesdropProjectService.addMeeting(newMeeting, current.getProjectId());
	      return Response.created(URI.create("/projects/" + id + "/meetings/" + meetingId)).build();
	}
	
	/*
	 * Grabs a meeting with specified project and meeting ids and updates it
	 */
	@PUT
	@Path("/{pid}/meetings/{mid}")
	@Consumes("application/xml")
	public Response updateMeeting(@PathParam("pid") Long pid, @PathParam("mid") Long mid, InputStream is) throws Exception{
		  Meeting update = readMeeting(is);
		  if(update.getName() == null || update.getName() == "" || 
			 update.getYear() == null || update.getYear() == "" ||
			 onlySpaces(update.getName()) || onlySpaces(update.getYear()) || 
			 !validYear(update.getYear())) {
		    	  	return Response.status(400).build();
		  }
	      NewProject current = this.eavesdropProjectService.getProject(pid);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }
	      
	      Set<Meeting> meetingSet = current.getMeetings();
	      Meeting meetingToUpdate = null;
	      for(Meeting m : meetingSet) {
	    	  	if(m.getId().equals(mid)) {
	    	  		meetingToUpdate = m;
	    	  	}
	      }
	      if(meetingToUpdate == null) {
	    	  	return Response.status(404).build();
	      }
	
	      meetingToUpdate.setName(update.getName());
	      meetingToUpdate.setYear(update.getYear());
	      this.eavesdropProjectService.updateMeeting(meetingToUpdate);
	      return Response.status(200).build();
	}
	
	/*
	 * Gets the project with specified id
	 */
	@GET
	@Path("/{id}")
	@Produces("application/xml")
	public Response getProject(@PathParam("id") Long id) throws Exception{
	      final NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }

	      return Response.ok(new StreamingOutput() { 
	    	  		public void write(OutputStream outputStream) throws IOException, WebApplicationException { 
	    	  				outputProject(outputStream, current); 
	    	  		}}).build();
	}
	
	/*
	 * Deletes the project and associated meetings from the database
	 */
	@DELETE
	@Path("/{id}")
	public Response deleteProject(@PathParam("id") Long id) throws Exception{
	      final NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }
	      
	      Set<Meeting> meetings = current.getMeetings();
	      for(Meeting m : meetings)
	    	  	this.eavesdropProjectService.deleteMeeting(m);

	      this.eavesdropProjectService.deleteProject(id);
	      return Response.status(200).build();
	}
	
	/* 
	 * Output xml for a project 
	 */
	protected void outputProject(OutputStream os, NewProject project) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(NewProject.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(project, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}		
	
	/*
	 * Creates a new NewProject from and InputStream reading in xml
	 */
	protected NewProject readNewProject(InputStream is) {
	      try {
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	         Document doc = builder.parse(is);
	         Element root = doc.getDocumentElement();
	         NewProject project = new NewProject();
	         NodeList nodes = root.getChildNodes();
	         for (int i = 0; i < nodes.getLength(); i++) {
	            Element element = (Element) nodes.item(i);
	            if (element.getTagName().equals("name")) {
	               project.setName(element.getTextContent());
	            }
	            else if (element.getTagName().equals("description")) {
	               project.setDescription(element.getTextContent());
	            }
	         }
	         return project;
	      }
	      catch (Exception e) {
	         throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
	      }
	   }
	
	/*
	 * Creates a new Meeting from an InputStream reading in xml
	 */
	protected Meeting readMeeting(InputStream is) {
	      try {
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	         Document doc = builder.parse(is);
	         Element root = doc.getDocumentElement();
	         Meeting meeting = new Meeting();
	         NodeList nodes = root.getChildNodes();
	         for (int i = 0; i < nodes.getLength(); i++) {
	            Element element = (Element) nodes.item(i);
	            if (element.getTagName().equals("name")) {
	               meeting.setName(element.getTextContent());
	            }
	            else if (element.getTagName().equals("year")) {
	               meeting.setYear(element.getTextContent());
	            }
	         }
	         return meeting;
	      }
	      catch (Exception e) {
	         throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
	      }
	   }
	
	/*
	 * Used to check the input xml fields for containing only spaces
	 */
	private boolean onlySpaces(String s) {
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) != ' ') {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Used to check the input meeting year xml field for containing a valid year
	 */
	private boolean validYear(String s) {
		for(int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);
			if(!Character.isDigit(c)) 
				return false;
		}
		Long year = Long.parseLong(s);
		if(year < 2010 || year > 2017)
			return false;
		return true;
	}
}