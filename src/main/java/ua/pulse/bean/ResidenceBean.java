package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.service.ResidenceService;
import ua.pulse.vaadin.PulseUI;

import java.sql.Timestamp;


@SpringComponent
public class ResidenceBean {

    private Long id;
    private CourseBean course;
    private Timestamp startDate;
    private VenueBean venue;

    public final static String NAME_SPRING_BEAN = "residenceBeanSpring";

    @Autowired
    private ResidenceService residenceService;

    public ResidenceBean(){
        this.startDate = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseBean getCourse() {
        return course;
    }

    public void setCourse(CourseBean course) {
        this.course = course;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public VenueBean getVenue() {
        return venue;
    }

    public void setVenue(VenueBean venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return venue.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResidenceBean that = (ResidenceBean) o;

        if (!id.equals(that.id)) return false;
        if (!course.equals(that.course)) return false;
        if (!startDate.equals(that.startDate)) return false;
        return venue.equals(that.venue);

    }

    @Override
    public int hashCode() {
        int result = course != null ? course.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (venue != null ? venue.hashCode() : 0);
        return result;
    }

    public static ResidenceBean createResidenceBean(){
        return (ResidenceBean) PulseUI.getSpringBean(NAME_SPRING_BEAN);
    }

    public ResidenceService getResidenceService() {
        return residenceService;
    }

    public ResidenceBean saveAndFlush(){
        return  getResidenceService().saveAndFlush(this);
    }

    public static ResidenceBean getCurrentResidenceByCourse(CourseBean courseBean){
        return ((ResidenceBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getResidenceService().getCurrentResidenceByCourse(courseBean);
    }
}
