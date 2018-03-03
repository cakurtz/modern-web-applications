package assign.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import assign.domain.Year;
import assign.domain.Meetings;
import assign.domain.Output;
import assign.domain.Project;
import assign.domain.Projects;
import assign.services.EavesdropService;

@Path("/projects")
public class EavesdropResource {
	
	EavesdropService eavesdropService;
	
	public EavesdropResource() {
		this.eavesdropService = new EavesdropService();
	}
	
	/*
	 * Handles request to get all projects
	 * GET http://localhost:8080/assignment3/myeavesdrop/projects/
	 */
	@GET
	@Path("/")
	@Produces("application/xml")
	public StreamingOutput getProjects() {
		List<String> projectNames = eavesdropService.getProjects();
		Projects projects = new Projects();
		projects.setProjects(projectNames);
		
		return new StreamingOutput() {
	         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
	            outputProjects(outputStream, projects);
	         }
	      };	   
	}
	
	/*
	 * Handles request to get data for a specified project
	 * GET http://localhost:8080/assignment3/myeavesdrop/projects/{project}/meetings
	 */
	@GET
	@Path("/{project}/meetings")
	@Produces("application/xml")
	public StreamingOutput getProject(@PathParam("project") String name) {
		List<String> projectData = eavesdropService.getProject(name);
		
		//List<Year> years = stringToYear(projectData);
		if(projectData != null) {
			Meetings meeting = new Meetings();
			meeting.setMeetings(projectData);
			
			return new StreamingOutput() {
		         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
		            outputProjects(outputStream, meeting);
		         }
		      };	 
		} else {
			Output o = new Output();
			o.setError("Project " + name + " does not exist");
			
			return new StreamingOutput() {
		         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
		            outputProjects(outputStream, o);
		         }
		      };	
		}
	}
	
	/*
	 * Output for Meetings
	 */
	protected void outputProjects(OutputStream os, Meetings meetings) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Meetings.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(meetings, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}
	
	/*
	 * Output for Year
	 */
	protected void outputProjects(OutputStream os, Year year) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Year.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(year, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}
	
	/*
	 * Output for Output/Error
	 */
	protected void outputProjects(OutputStream os, Output output) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Output.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(output, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}
	
	/*
	 * Output for Projects
	 */
	protected void outputProjects(OutputStream os, Projects projects) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Projects.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(projects, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}	
	
	/*
	 * Output for Project
	 */
	protected void outputProjects(OutputStream os, Project project) throws IOException {
		try { 
			JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(project, os);
		} catch (JAXBException jaxb) {
			jaxb.printStackTrace();
			throw new WebApplicationException();
		}
	}
}