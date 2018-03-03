package assign.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table( name = "projects" )
@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewProject {

	@XmlAttribute(name="id")
	Long project_id;
	String name;
	String description;
	
	@XmlElementWrapper(name="meetings")
	@XmlElement(name="meeting")
	private Set<Meeting> projectMeetings;
	
	public NewProject() {
		projectMeetings = null;
	}
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="project_id")
	public Long getProjectId() {
		return project_id;
	}
	
	public void setProjectId(Long project_id) {
		this.project_id = project_id;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(mappedBy="project")
    @Cascade({CascadeType.DELETE})
    public Set<Meeting> getMeetings() {
    		return this.projectMeetings;
    }
    
    public void setMeetings(Set<Meeting> meetings) {
    		this.projectMeetings = meetings;
    }
}