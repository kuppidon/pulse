package ua.pulse.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.*;
import ua.pulse.entity.*;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ObjectsConverterServiceImpl implements ObjectsConverterService{
	
	@Autowired
	private HospitalBean hospitalBeanSpring;
	
	@Autowired
	private PatientBean patientBeanSpring;

	@Autowired
	private CourseBean courseBeanSpring;

	@Autowired
	private SpecializationBean specializationBeanSpring;

	@Autowired
	private UserBean userBeanSpring;

	@Autowired
	private VenueBean venueBeanSpring;
	
	/**
	 * convert HospitalEntity to HospitalBean 
	 * @param hospital
	 * @return HospitalBean
	 */
	@Override
	public HospitalBean convert(Hospital hospital){
		if(hospital == null){
			return null;
		}
		HospitalBean hospitalBean = new HospitalBean();
		hospitalBean.setId(hospital.getId());
		hospitalBean.setName(hospital.getName());
		hospitalBean.setAddress(hospital.getAddress());		
		return hospitalBean;
	}
	
	/**
	 * convert HospitalBean to HospitalEntity
	 * @param hospitalBean
	 * @return HospitalEntity
	 */
	@Override
	public Hospital convert(HospitalBean hospitalBean){
		if(hospitalBean == null){
			return null;
		}
		Hospital hospital = new Hospital();
		hospital.setId(hospitalBean.getId());
		hospital.setName(hospitalBean.getName());
		hospital.setAddress(hospitalBean.getAddress());		
		return hospital;
	}
	
	@Override
	public HospitalBean convertToSpringBean(HospitalBean hospital){
		if(hospital == null){
			return null;
		}
		hospitalBeanSpring.setId(hospital.getId());
		hospitalBeanSpring.setName(hospital.getName());
		hospitalBeanSpring.setAddress(hospital.getAddress());		
		return hospitalBeanSpring;
	}
	
	@Override
	public VenueBean convert(Venue venue){
		if(venue == null){
			return null;
		}
		VenueBean venueBean = new VenueBean();
		venueBean.setId(venue.getId());
		venueBean.setCapacity(venue.getCapacity());
		venueBean.setName(venue.getName());
		venueBean.setType(venue.getType());
		venueBean.setHospital(convert(venue.getHospital()));
		venueBean.setOwner(convert(venue.getOwner()));
		return venueBean;
	}
	
	@Override
	public Venue convert(VenueBean venueBean){
		if(venueBean == null){
			return null;
		}
		Venue venue = new Venue();
		venue.setId(venueBean.getId());
		venue.setCapacity(venueBean.getCapacity());
		venue.setName(venueBean.getName());
		venue.setType(venueBean.getType());
		venue.setOwner(convert(venueBean.getOwner()));
		venue.setHospital(convert(venueBean.getHospital()));
		return venue;
	}

    @Override
    public VenueBean convertToSpringBean(VenueBean venueBean) {
		if(venueBean == null){
			return null;
		}
		VenueBean venue = venueBeanSpring;
		venue.setId(venueBean.getId());
		venue.setCapacity(venueBean.getCapacity());
		venue.setName(venueBean.getName());
		venue.setType(venueBean.getType());
		venue.setOwner(venueBean.getOwner());
		venue.setHospital(venueBean.getHospital());
		return venue;
    }

    @Override
	public PatientBean convert(Patient patient) {
		if(patient == null){
			return null;
		}
		PatientBean patientBean = new PatientBean();
		patientBean.setId(patient.getId());
		patientBean.setFirstName(patient.getFirstName());
		patientBean.setLastName(patient.getLastName());
		patientBean.setMiddleName(patient.getMiddleName());
		patientBean.setFullName(patient.getFullName());
		patientBean.setBirthDate(patient.getBirthDate());
		patientBean.setBloodGroup(patient.getBloodGroup());
		patientBean.setBloodRhesus(patient.getBloodRhesus());
		patientBean.setSex(patient.getSex());
		patientBean.setAddress(patient.getAddress());
		patientBean.setPhone(patient.getPhone());
		patientBean.setNote(patient.getNote());
		patientBean.setEmail(patient.getEmail());
		return patientBean;
	}

	@Override
	public Patient convert(PatientBean patientBean) {
		if(patientBean == null){
			return null;
		}
		Patient patient = new Patient();
		patient.setId(patientBean.getId());
		patient.setFirstName(patientBean.getFirstName());
		patient.setLastName(patientBean.getLastName());
		patient.setMiddleName(patientBean.getMiddleName());
		patient.setFullName(patientBean.getFullName());
		patient.setBirthDate(patientBean.getBirthDate());
		patient.setBloodGroup(patientBean.getBloodGroup());
		patient.setBloodRhesus(patientBean.getBloodRhesus());
		patient.setSex(patientBean.getSex());
		patient.setAddress(patientBean.getAddress());
		patient.setPhone(patientBean.getPhone());
		patient.setNote(patientBean.getNote());
		patient.setEmail(patientBean.getEmail());
		return patient;
	}

	@Override
	public PatientBean convertToSpringBean(PatientBean patientBean) {
		if(patientBean == null){
			return null;
		}
		patientBeanSpring.setId(patientBean.getId());
		patientBeanSpring.setFirstName(patientBean.getFirstName());
		patientBeanSpring.setLastName(patientBean.getLastName());
		patientBeanSpring.setMiddleName(patientBean.getMiddleName());
		patientBeanSpring.setFullName(patientBean.getFullName());
		patientBeanSpring.setBirthDate(patientBean.getBirthDate());
		patientBeanSpring.setBloodGroup(patientBean.getBloodGroup());
		patientBeanSpring.setBloodRhesus(patientBean.getBloodRhesus());
		patientBeanSpring.setSex(patientBean.getSex());
		patientBeanSpring.setAddress(patientBean.getAddress());
		patientBeanSpring.setPhone(patientBean.getPhone());
		patientBeanSpring.setNote(patientBean.getNote());
		patientBeanSpring.setEmail(patientBean.getEmail());
		return patientBeanSpring;
	}


	@Override
	public CourseBean convert(Course course) {

		if(course == null){
			return  null;
		}
		CourseBean courseBean = new CourseBean(convert(course.getPatient()));
		courseBean.setId(course.getId());
		courseBean.setArrivalType(course.getArrivalType());
		courseBean.setHospital(convert(course.getHospital()));
		courseBean.setDepartmentEnd(convert(course.getDepartmentEnd()));
		courseBean.setDepartmentStart(convert(course.getDepartmentStart()));
		courseBean.setDiagnosisClinic(course.getDiagnosisClinic());
		courseBean.setDiagnosisEnd(course.getDiagnosisEnd());
		courseBean.setDiagnosisGuide(course.getDiagnosisGuide());
		courseBean.setDiagnosisMKB10(convert(course.getDiagnosisMKB10()));
		courseBean.setDiagnosisStart(course.getDiagnosisStart());
		courseBean.setEndDate(course.getEndDate());
		courseBean.setDischargedType(course.getDischargedType());
		courseBean.setHistoryNumber(course.getHistoryNumber());
		courseBean.setHospitalizationType(course.getHospitalizationType());
		courseBean.setResponsible(convert(course.getResponsible()));
		courseBean.setStartDate(course.getStartDate());
		courseBean.setStatus(course.getStatus());
		courseBean.setPatient(convert(course.getPatient()));
		courseBean.setDischargedType(course.getDischargedType());

		List<Residence> listResidences = course.getResidences();
		if(listResidences != null) {
			courseBean.setResidences(listResidences.stream().map(e -> convert(e,courseBean)).collect(Collectors.toList()));
		}

		return courseBean;
	}

	@Override
	public Course convert(CourseBean courseBean) {

		if(courseBean == null){
			return  null;
		}
		Course course = new Course();
		course.setId(courseBean.getId());
		course.setArrivalType(courseBean.getArrivalType());
		course.setHospital(convert(courseBean.getHospital()));
		course.setDepartmentEnd(convert(courseBean.getDepartmentEnd()));
		course.setDepartmentStart(convert(courseBean.getDepartmentStart()));
		course.setDiagnosisClinic(courseBean.getDiagnosisClinic());
		course.setDiagnosisEnd(courseBean.getDiagnosisEnd());
		course.setDiagnosisGuide(courseBean.getDiagnosisGuide());
		course.setDiagnosisMKB10(convert(courseBean.getDiagnosisMKB10()));
		course.setDiagnosisStart(courseBean.getDiagnosisStart());
		course.setEndDate(courseBean.getEndDate());
		course.setDischargedType(courseBean.getDischargedType());
		course.setHistoryNumber(courseBean.getHistoryNumber());
		course.setHospitalizationType(courseBean.getHospitalizationType());
		course.setResponsible(convert(courseBean.getResponsible()));
		course.setStartDate(courseBean.getStartDate());
		course.setStatus(courseBean.getStatus());
		course.setPatient(convert(courseBean.getPatient()));
		List<ResidenceBean> listResidences = courseBean.getResidences();
		if(listResidences != null) {
			course.setResidences(listResidences.stream().map(e -> convert(e,course)).collect(Collectors.toList()));
		}

		return course;
	}

    @Override
    public CourseBean convertToSpringBean(CourseBean course) {
		if(course == null){
			return  null;
		}
		CourseBean courseBean = courseBeanSpring;
		courseBean.setId(course.getId());
		courseBean.setArrivalType(course.getArrivalType());
		courseBean.setHospital(course.getHospital());
		courseBean.setDepartmentEnd(course.getDepartmentEnd());
		courseBean.setDepartmentStart(course.getDepartmentStart());
		courseBean.setDiagnosisClinic(course.getDiagnosisClinic());
		courseBean.setDiagnosisEnd(course.getDiagnosisEnd());
		courseBean.setDiagnosisGuide(course.getDiagnosisGuide());
		courseBean.setDiagnosisMKB10(course.getDiagnosisMKB10());
		courseBean.setDiagnosisStart(course.getDiagnosisStart());
		courseBean.setEndDate(course.getEndDate());
		courseBean.setForwarded(course.getForwarded());
		courseBean.setHistoryNumber(course.getHistoryNumber());
		courseBean.setHospitalizationType(course.getHospitalizationType());
		courseBean.setResponsible(course.getResponsible());
		courseBean.setStartDate(course.getStartDate());
		courseBean.setStatus(course.getStatus());
		courseBean.setResidences(course.getResidences());
		courseBean.setPatient(course.getPatient());
		courseBean.setDischargedType(course.getDischargedType());

		return courseBean;
    }

    @Override
	public ResidenceBean convert(Residence residence) {

		if(residence == null){
			return  null;
		}
		ResidenceBean residenceBean = new ResidenceBean();
		residenceBean.setStartDate(residence.getStartDate());
		residenceBean.setCourse(convert(residence.getCourse()));
		residenceBean.setId(residence.getId());
		residenceBean.setVenue(convert(residence.getVenue()));

		return residenceBean;
	}

	@Override
	public ResidenceBean convert(Residence residence, CourseBean courseBean) {

		if(residence == null){
			return  null;
		}
		ResidenceBean residenceBean = new ResidenceBean();
		residenceBean.setStartDate(residence.getStartDate());
		residenceBean.setCourse(courseBean);
		residenceBean.setId(residence.getId());
		residenceBean.setVenue(convert(residence.getVenue()));

		return residenceBean;
	}

	@Override
	public Residence convert(ResidenceBean residenceBean) {

		if(residenceBean == null){
			return null;
		}
		Residence residence = new Residence();
		residence.setStartDate(residenceBean.getStartDate());
		residence.setCourse(convert(residenceBean.getCourse()));
		residence.setId(residenceBean.getId());
		residence.setVenue(convert(residenceBean.getVenue()));

		return residence;
	}

	@Override
	public Residence convert(ResidenceBean residenceBean, Course course) {

		if(residenceBean == null){
			return null;
		}
		Residence residence = new Residence();
		residence.setStartDate(residenceBean.getStartDate());
		residence.setCourse(course);
		residence.setId(residenceBean.getId());
		residence.setVenue(convert(residenceBean.getVenue()));

		return residence;
	}

	@Override
	public UserBean convert(User user) {

		if(user == null){
			return null;
		}
		UserBean userBean = new UserBean();
		userBean.setId(user.getId());
		userBean.setDepartment(convert(user.getDepartment()));
		userBean.setDisabled(user.getDisabled());
		userBean.setFullName(user.getFullName());
		userBean.setPassword(user.getPassword());
		userBean.setShortName(user.getShortName());
		userBean.setTimeLimit(user.getTimeLimit());
		userBean.setUserName(user.getUserName());
		userBean.setRole(user.getRole());
		userBean.setHospital(convert(user.getHospital()));
		userBean.setSpecialization(convert(user.getSpecialization()));

		return userBean;
	}

	@Override
	public User convert(UserBean userBean) {

		if(userBean == null){
			return null;
		}
		User user = new User();
		user.setId(userBean.getId());
		user.setDepartment(convert(userBean.getDepartment()));
		user.setDisabled(userBean.getDisabled());
		user.setFullName(userBean.getFullName());
		user.setPassword(userBean.getPassword());
		user.setShortName(userBean.getShortName());
		user.setTimeLimit(userBean.getTimeLimit());
		user.setUserName(userBean.getUserName());
		user.setRole(userBean.getRole());
		user.setHospital(convert(userBean.getHospital()));
		user.setSpecialization(convert(userBean.getSpecialization()));

		return user;
	}

    @Override
    public UserBean convertToSpringBean(UserBean userBean) {

		if(userBean == null){
			return null;
		}
		UserBean user = userBeanSpring;
		user.setId(userBean.getId());
		user.setDepartment(userBean.getDepartment());
		user.setDisabled(userBean.getDisabled());
		user.setFullName(userBean.getFullName());
		user.setPassword(userBean.getPassword());
		user.setShortName(userBean.getShortName());
		user.setTimeLimit(userBean.getTimeLimit());
		user.setUserName(userBean.getUserName());
		user.setRole(userBean.getRole());
		user.setSpecialization(userBean.getSpecialization());
		user.setHospital(userBean.getHospital());

		return user;
    }

    @Override
	public MKB10Bean convert(MKB10 mkb10) {
		if(mkb10 == null){
			return  null;
		}
		MKB10Bean mkb10Bean = new MKB10Bean();
		mkb10Bean.setId(mkb10.getId());
		mkb10Bean.setParent(convert(mkb10.getParent()));
		mkb10Bean.setCode(mkb10.getCode());
		mkb10Bean.setName(mkb10.getName());

		return mkb10Bean;
	}

	@Override
	public MKB10 convert(MKB10Bean mkb10Bean) {
		if(mkb10Bean == null){
			return null;
		}
		MKB10 mkb10 = new MKB10();
		mkb10.setId(mkb10Bean.getId());
		mkb10.setParent(convert(mkb10Bean.getParent()));
		mkb10.setCode(mkb10Bean.getCode());
		mkb10.setName(mkb10Bean.getName());

		return mkb10;
	}

    @Override
    public Assignment convert(AssignmentBean assignmentBean) {
		if(assignmentBean == null){
			return null;
		}
		Assignment assignment = new Assignment();
		assignment.setId(assignmentBean.getId());
		assignment.setCourse(convert(assignmentBean.getCourse()));
		assignment.setDateAssignment(assignmentBean.getDateAssignment());
		assignment.setDescription(assignmentBean.getDescription());
		assignment.setDoctor(convert(assignmentBean.getDoctor()));
        return assignment;
    }

	@Override
	public AssignmentBean convert(Assignment assignment) {
		if(assignment == null){
			return null;
		}
		AssignmentBean assignmentBean = new AssignmentBean();
		assignmentBean.setId(assignment.getId());
		assignmentBean.setCourse(convert(assignment.getCourse()));
		assignmentBean.setDateAssignment(assignment.getDateAssignment());
		assignmentBean.setDescription(assignment.getDescription());
		assignmentBean.setDoctor(convert(assignment.getDoctor()));
		return assignmentBean;
	}

	@Override
	public Specialization convert(SpecializationBean specializationBean) {
		if (specializationBean == null) {
			return null;
		}
		Specialization specialization = new Specialization();
		specialization.setId(specializationBean.getId());
		specialization.setName(specializationBean.getName());
		return specialization;
	}

	@Override
    public SpecializationBean convert(Specialization specialization) {
		if (specialization == null) {
			return null;
		}
		SpecializationBean specializationBean = new SpecializationBean();
		specializationBean.setId(specialization.getId());
		specializationBean.setName(specialization.getName());
		return specializationBean;
    }
	@Override
	public SpecializationBean convertToSpringBean(SpecializationBean specialization) {
		if (specialization == null) {
			return null;
		}
		SpecializationBean specializationBean = specializationBeanSpring;
		specializationBean.setId(specialization.getId());
		specializationBean.setName(specialization.getName());
		return specializationBean;
	}


}
