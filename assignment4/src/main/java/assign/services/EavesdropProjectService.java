package assign.services;

import assign.domain.Project;
import assign.domain.NewProject;

public interface EavesdropProjectService {

	public NewProject addProject(NewProject p) throws Exception;
	
	public NewProject getProject(int projectId) throws Exception;

    public NewProject getProject_correct(int projectId) throws Exception;
    
    public int updateProject(int projectId, String updatedName, String updatedDescription) throws Exception;
    
    public int deleteProject(int projectId) throws Exception;

}
