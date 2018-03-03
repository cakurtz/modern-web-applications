package assign.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "year")
public class Year {
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}