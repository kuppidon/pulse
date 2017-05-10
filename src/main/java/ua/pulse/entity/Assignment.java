package ua.pulse.entity;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Table(name = "assignments")
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@Column(name = "date_assignment")
	private Date dateAssignment;
	
	@ManyToOne
	@JoinColumn(name="course_id")
	private Course course;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User doctor;
	
	@Column(length = 1000)
	private String description;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public User getDoctor() {
		return doctor;
	}

	public Date getDateAssignment() {
		return dateAssignment;
	}

	public void setDateAssignment(Date dateAssignment) {
		this.dateAssignment = dateAssignment;
	}
}
