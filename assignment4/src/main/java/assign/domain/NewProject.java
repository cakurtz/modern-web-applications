package assign.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewProject {

	String name;
	String description;
	
	@XmlAttribute(name="id")
	int project_id;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getProjectId() {
		return project_id;
	}
	
	public void setProjectId(int project_id) {
		this.project_id = project_id;
	}
}