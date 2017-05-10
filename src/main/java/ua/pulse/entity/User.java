package ua.pulse.entity;

import ua.pulse.libs.Roles;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	
	@Column(name = "user_name")//login
	private String userName;

	@Column(name = "password",length = 500, columnDefinition = "DEFAULT ''")
	private String password;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "short_name")
	private String shortName;

	@ManyToOne
	@JoinColumn(name = "specialization_id")
	private Specialization specialization;
	
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Venue department;

	@ManyToOne
	@JoinColumn(name = "hospital_id")
	private Hospital hospital;
	
	@Column(name = "time_limit")
	private Date timeLimit;	
	
	@Column(name = "is_disabled")
	private Boolean isDisabled;
	
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Roles role;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Date getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Date timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Specialization getSpecialization() {
		return specialization;
	}

	public void setSpecialization(Specialization specialization) {
		this.specialization = specialization;
	}

	public Venue getDepartment() {
		return department;
	}

	public void setDepartment(Venue department) {
		this.department = department;
	}

	public Boolean getDisabled() {
		return isDisabled;
	}

	public void setDisabled(Boolean disabled) {
		isDisabled = disabled;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}
}
