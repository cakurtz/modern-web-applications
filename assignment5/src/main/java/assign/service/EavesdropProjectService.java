package assign.service;

import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import assign.domain.Meeting;
import assign.domain.NewProject;

import java.util.logging.*;

public class EavesdropProjectService {
	private SessionFactory sessionFactory;
	
	Logger logger;
	
	public EavesdropProjectService() {
		// A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
        logger = Logger.getLogger("EavesdropProjectService");
	}
	
	public void loadData(Map<String, List<String>> data) {
		logger.info("Inside loadData.");
	}
	
	/*
	 * Retrieves a specific project by id from the database
	 */
	public NewProject getProject(Long id) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		NewProject project = (NewProject)session.get(NewProject.class, id);
		if(project == null) {
			return null;
		}
		Hibernate.initialize(project.getMeetings());
		session.close();
		return project;
	}
	
	/*
	 * Adds a new project to the database
	 */
	public Long addProject(NewProject project) throws Exception {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Long id = null;
		try {
			tx = session.beginTransaction(); 
			session.save(project);
		    id = project.getProjectId();
		    tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				throw e;
			}
		}
		finally {
			session.close();
		}
		return id;
	}
	
	/*
	 * Add a new meeting to the database associated with a project with a specific id
	 */
	public Long addMeeting(Meeting meeting, Long id) throws Exception {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Long meetingId = null;
		try {
			tx = session.beginTransaction();
			NewProject project = (NewProject)session.get(NewProject.class, id);
			meeting.setProject(project);
			session.save(meeting);
			meetingId = meeting.getId();
		    tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				throw e;
			}
		}
		finally {
			session.close();
		}
		return meetingId;
	}
	
	/*
	 * Update a meeting name and/or year in the database
	 */
	public boolean updateMeeting(Meeting update) {
		Session session = sessionFactory.openSession();		
		session.beginTransaction();
		session.merge(update);
        session.getTransaction().commit();
        session.close();
		return true;
	}
	
	/* 
	 * Delete a project in the database
	 * Called after project has no meetings associated with it
	 */
	public void deleteProject(Long id) throws Exception {
		Session session = sessionFactory.openSession();		
		session.beginTransaction();
		
		NewProject p = getProject(id);
		
        session.delete(p);

        session.getTransaction().commit();
        session.close();
	}
	
	/* 
	 * Delete a meeting in the database
	 */
	public void deleteMeeting(Meeting m) {
		Session session = sessionFactory.openSession();		
		session.beginTransaction();
		
        session.delete(m);

        session.getTransaction().commit();
        session.close();
	}
}
