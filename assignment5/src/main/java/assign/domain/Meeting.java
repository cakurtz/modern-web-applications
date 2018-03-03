package assign.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table( name = "meetings" )
public class Meeting {
	
	@XmlAttribute(name="id")
	private Long id;
    private String name;
    private String year;
    private NewProject project; // project meeting belongs to
    
    public Meeting() {
    	// this form used by Hibernate
    }
    
    public Meeting(String name, String year) {
    	// for application use, to create new assignment
    	this.name = name;
    	this.year = year;
    } 
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="meeting_id")
    public Long getId() {
		return id;
    }

    private void setId(Long id) {
		this.id = id;
    }

	@Column(name = "name")
    public String getName() {
		return name;
    }

    public void setName(String name) {
		this.name = name;
    }
    
    @ManyToOne
    @JoinColumn(name="project_id")
    @XmlTransient
    public NewProject getProject() { // property named project available on this object
    		return this.project;
    }

    public void setProject(NewProject p) {
    		this.project = p;
    }

    public String getYear() {
		return year;
    }

    public void setYear(String year) {
		this.year = year;
    }
}
