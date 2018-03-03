package assign.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

import assign.domain.NewProject;
import assign.domain.NotFound;
import assign.domain.Project;
import assign.domain.Projects;
import assign.services.EavesdropProjectService;
import assign.services.EavesdropProjectServiceImpl;

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
		this.eavesdropProjectService = new EavesdropProjectServiceImpl(dburl, username, password);		
	}
	
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
	
	@POST
	@Consumes("application/xml")
	public Response createCourse(InputStream is) throws Exception {
	      NewProject newProject = readNewProject(is);
	      if(newProject.getName() == "" || newProject.getName() == null || 
	    		 newProject.getDescription() == "" || newProject.getDescription() == null) {
	    	  	return Response.status(400).build();
	      }
	      newProject = this.eavesdropProjectService.addProject(newProject);
	      return Response.created(URI.create("/projects/" + newProject.getProjectId())).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes("application/xml")
	public Response updateProject(@PathParam("id") int id, InputStream is) throws Exception{
		  NewProject update = readNewProject(is);
		  if(update.getName() == "" || update.getName() == null || 
		     update.getDescription() == "" || update.getDescription() == null) {
		    	  	return Response.status(400).build();
		  }
	      NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }
	
	      current.setName(update.getName());
	      current.setDescription(update.getDescription());
	      this.eavesdropProjectService.updateProject(id, current.getName(), current.getDescription());
	      return Response.status(204).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/xml")
	public Response getProject(@PathParam("id") int id) throws Exception{
	      final NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }

	      return Response.ok(new StreamingOutput() { 
	    	  		public void write(OutputStream outputStream) throws IOException, WebApplicationException { 
	    	  				outputProject(outputStream, current); 
	    	  		}}).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteProject(@PathParam("id") int id) throws Exception{
	      final NewProject current = this.eavesdropProjectService.getProject(id);
	      if (current == null) {
	    	  	return Response.status(404).build();
	      }

	      this.eavesdropProjectService.deleteProject(id);
	      return Response.status(200).build();
	}
	
	
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
	
	protected void outputCourses(OutputStream os, NotFound notFound) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(NotFound.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(notFound, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}	
	
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
}