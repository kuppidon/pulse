package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;

import java.sql.Date;

@SpringComponent
public class AssignmentBean {

	private Long id;
	private Date dateAssignment;
	private CourseBean course;
	private UserBean doctor;	
	private String description;
	
	public AssignmentBean(){
		this.description = "";
		this.dateAssignment = new Date(System.currentTimeMillis());
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

	public UserBean getDoctor() {
		return doctor;
	}

	public void setDoctor(UserBean doctor) {
		this.doctor = doctor;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateAssignment() {
		return dateAssignment;
	}

	public void setDateAssignment(Date dateAssignment) {
		this.dateAssignment = dateAssignment;
	}
}
