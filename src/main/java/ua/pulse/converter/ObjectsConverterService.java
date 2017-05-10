package ua.pulse.converter;

import ua.pulse.bean.*;
import ua.pulse.entity.*;

public interface ObjectsConverterService {

	HospitalBean convert(Hospital hospital);
	Hospital convert(HospitalBean hospitalBean);
	HospitalBean convertToSpringBean(HospitalBean hospital);

	VenueBean convert(Venue currentUnit);
	Venue convert(VenueBean venueBean);
	VenueBean convertToSpringBean(VenueBean venueBean);
	
	PatientBean convert(Patient patient);
	Patient convert(PatientBean patientBean);
	PatientBean convertToSpringBean(PatientBean patient);

	CourseBean convert(Course course);
	Course convert(CourseBean courseBean);
	CourseBean convertToSpringBean(CourseBean courseBean);

	ResidenceBean convert(Residence residence);
	ResidenceBean convert(Residence residence, CourseBean courseBean);
	Residence convert(ResidenceBean residenceBean);
	Residence convert(ResidenceBean residenceBean, Course course);

	UserBean convert(User user);
	User convert(UserBean userBean);
	UserBean convertToSpringBean(UserBean userBean);

	MKB10Bean convert(MKB10 mkb10);
	MKB10 convert(MKB10Bean mkb10Bean);

	Assignment convert(AssignmentBean assignmentBean);
	AssignmentBean convert(Assignment assignment);

	Specialization convert(SpecializationBean specializationBean);
	SpecializationBean convert(Specialization specialization);
	SpecializationBean convertToSpringBean(SpecializationBean specialization);

}
