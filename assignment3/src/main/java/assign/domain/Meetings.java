package assign.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "meetings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Meetings {

    private List<String> year = null;
 
    public List<String> getMeetings() {
        return year;
    }
 
    public void setMeetings(List<String> years) {
        this.year = years;
    }	
}
