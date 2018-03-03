package assign.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import assign.domain.Project;
import assign.domain.NewProject;

public class EavesdropProjectServiceImpl implements EavesdropProjectService {

	String dbURL = "";
	String dbUsername = "";
	String dbPassword = "";
	DataSource ds;

	// DB connection information would typically be read from a config file.
	public EavesdropProjectServiceImpl(String dbUrl, String username, String password) {
		this.dbURL = dbUrl;
		this.dbUsername = username;
		this.dbPassword = password;
		
		ds = setupDataSource();
	}
	
	public DataSource setupDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUsername(this.dbUsername);
        ds.setPassword(this.dbPassword);
        ds.setUrl(this.dbURL);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        return ds;
    }
	
	public NewProject addProject(NewProject p) throws Exception {
		Connection conn = ds.getConnection();
		
		String insert = "INSERT INTO projects(name, description) VALUES(?, ?)";
		PreparedStatement stmt = conn.prepareStatement(insert,
                Statement.RETURN_GENERATED_KEYS);
		
		stmt.setString(1, p.getName());
		stmt.setString(2, p.getDescription());
		
		int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating project failed, no rows affected.");
        }
        
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
        		p.setProjectId(generatedKeys.getInt(1));
        }
        else {
            throw new SQLException("Creating course failed, no ID obtained.");
        }
        
        // Close the connection
        conn.close();
        
		return p;
	}

	public NewProject getProject(int projectId) throws Exception {
		String query = "select * from projects where project_id=" + projectId;
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		
		if (!r.next()) {
			return null;
		}
		
		NewProject p = new NewProject();
		p.setDescription(r.getString("description"));
		p.setName(r.getString("name"));
		p.setProjectId(r.getInt("project_id"));
		return p;
	}

    public NewProject getProject_correct(int projectId) throws Exception {
		String query = "select * from projects where project_id=?";
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		s.setString(1, String.valueOf(projectId));
	
		ResultSet r = s.executeQuery();
		
		if (!r.next()) {
		    return null;
		}
		
		NewProject p = new NewProject();
		p.setDescription(r.getString("description"));
		p.setName(r.getString("name"));
		p.setProjectId(r.getInt("project_id"));
		return p;
    }
    
    public int updateProject(int projectId, String updatedName, String updatedDescription) throws Exception {
    		String updateDesc = "update projects set name='" + updatedName + "', description='" + updatedDescription + "' where project_id='" + projectId + "';";
    		Connection conn = ds.getConnection();
    		PreparedStatement s = conn.prepareStatement(updateDesc);
    		
    		return s.executeUpdate(updateDesc);
    }
    
    public int deleteProject(int projectId) throws Exception {
		String updateDesc = "delete from projects where project_id=" + projectId;
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(updateDesc);
		
		return s.executeUpdate(updateDesc);
}

}
