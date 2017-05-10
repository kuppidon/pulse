package ua.pulse.entity;

import ua.pulse.libs.BloodGroup;
import ua.pulse.libs.BloodRhesus;
import ua.pulse.libs.Sex;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	
	@Column(name = "first_name", length = 150)
	private String firstName;
	
	@Column(name = "last_name", length = 150)
	private String lastName;
	
	@Column(name = "middle_name",length = 150)
	private String middleName;
	
	@Column(name = "full_name", length = 400)
	private String fullName;
	
	@Column(name = "birth_date")
	private Date birthDate;
	
	@Enumerated(EnumType.STRING)
	private Sex sex;
	
	@Column(length = 250)
	private String address;
	
	@Column(length = 100)
	private String email;
	
	private String phone;
	
	@Column(name = "blood_group")
	@Enumerated(EnumType.STRING)
	private BloodGroup bloodGroup;
	
	@Column(name = "blood_rhesus")
	@Enumerated(EnumType.STRING)
	private BloodRhesus bloodRhesus;
	
	private String note;
	
	@OneToMany(mappedBy = "patient",fetch = FetchType.LAZY)
	private List<Course> courses;

	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BloodGroup getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(BloodGroup bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public BloodRhesus getBloodRhesus() {
		return bloodRhesus;
	}

	public void setBloodRhesus(BloodRhesus bloodRhesus) {
		this.bloodRhesus = bloodRhesus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
