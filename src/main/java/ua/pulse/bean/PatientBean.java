package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import ua.pulse.component.CreateEditPatientWindow;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.libs.BloodGroup;
import ua.pulse.libs.BloodRhesus;
import ua.pulse.libs.Sex;
import ua.pulse.service.PatientService;
import ua.pulse.vaadin.PulseUI;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.StringTokenizer;

@Primary
@SpringComponent
public class PatientBean {

	private Long id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String fullName;
	private Date birthDate;
	private Sex sex;
	private String address;
	private String email;
	private String phone;
	private BloodGroup bloodGroup;
	private BloodRhesus bloodRhesus;
	private String note;
	private List<CourseBean> courses;

	@Autowired
	private PatientService patientService;

	@Autowired
	ObjectsConverterService objectsConverterService;

	public PatientBean(){
		this.sex = Sex.MALE;
		this.address = "";
		this.firstName = "";
		this.lastName = "";
		this.middleName = "";
		this.fullName = "";
		this.phone = "";
		this.email = "";
		this.note = "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<CourseBean> getCourses() {
		return courses;
	}

	public void setCourses(List<CourseBean> courses) {
		this.courses = courses;
	}

	public PatientService getPatientService() {
		return patientService;
	}

	public boolean isNew(){
		return  getId() == null;
	}

	public Integer getAge(){

		if (birthDate != null){
			LocalDate today = new Date(System.currentTimeMillis()).toLocalDate();
			LocalDate localDate = birthDate.toLocalDate();
			localDate.minusDays(1); //исключаем сам день рождения
			int age = today.getYear() - localDate.getYear();
			if (today.getDayOfYear() <= localDate.getDayOfYear()) {
				age--;
			}
			return age;
		}
		return null;
	}

	public static Window createPatient(String name){

		PatientBean newPatient = (PatientBean) PulseUI.getSpringBean("patientBeanSpring");
		if (name != null && !name.isEmpty()){
			StringTokenizer nameTok = new StringTokenizer(name);
			int index = 0;
			while (nameTok.hasMoreElements()){
				String nextToken = nameTok.nextToken().trim();
				if (index == 0){
					newPatient.setLastName(nextToken);
				}
				else if (index == 1){
					newPatient.setFirstName(nextToken);
				}
				else {
					String currentMiddleName = newPatient.getMiddleName();
					if (currentMiddleName == null){
						newPatient.setMiddleName(nextToken);
					}
					else{
						newPatient.setMiddleName(currentMiddleName + " " + nextToken);
					}
				}
				index++;
			}
		}

		return new CreateEditPatientWindow(newPatient);
	}

	public Window editPatient(){

		PatientBean patientSpringBean = PulseUI.getObjectsConverterService().convertToSpringBean(this);
		return new CreateEditPatientWindow(patientSpringBean);
	}

	public void fixName(){

		lastName   = lastName==null?"":WordUtils.capitalize(lastName.trim().toLowerCase());
		firstName  = firstName==null?"":WordUtils.capitalize(firstName.trim().toLowerCase());
		middleName = middleName==null?"":WordUtils.capitalize(middleName.trim().toLowerCase());
		/*
		 * generic full name
		 */
		StringBuilder bilder = new StringBuilder();
		if(!lastName.isEmpty()){
			bilder.append(lastName);
		}
		if(!lastName.isEmpty() && !firstName.isEmpty()){
			bilder.append(" ");
		}
		bilder.append(firstName);
		if(bilder.length() != 0 && !middleName.isEmpty()){
			bilder.append(" ");
		}
		bilder.append(middleName);
		fullName = bilder.toString();

	}

	@Override
	public String toString() {
		return fullName;
	}

	public String getFullDescription(){
		return getFullName() + " <i>(" + getBirthDate() + ", " + getAddress() + ")</i>";
	}


}
