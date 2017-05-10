package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.*;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Course;
import ua.pulse.entity.Patient;
import ua.pulse.entity.User;
import ua.pulse.entity.Venue;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.VenueType;
import ua.pulse.repository.CourseRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ResidenceService residenceService;

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Override
    @Transactional
    public CourseBean openCourse(CourseBean courseBean, VenueBean venueBean) {

        ResidenceBean residenceBean = new ResidenceBean();
        residenceBean.setCourse(courseBean);
        residenceBean.setStartDate(courseBean.getStartDate());
        residenceBean.setVenue(venueBean);

        List<ResidenceBean> coursesResidences = new ArrayList<>();
        coursesResidences.add(residenceBean);
        courseBean.setResidences(coursesResidences);

        courseBean.setStatus(CourseStatus.OPENED);

        VenueBean department;
        if (venueBean.getType().equals(VenueType.HOSPITAL_ROOM)) {
            department = venueBean.getOwner();
        }
        else {
            department = venueBean;
        }
        courseBean.setDepartmentStart(department);
        courseBean.setDepartmentEnd(department);
        courseBean.setHospital(department.getHospital());

        Course course = objectsConverterService.convert(courseBean);
        return objectsConverterService.convert(courseRepository.saveAndFlush(course));
    }

    @Override
    public void closeCourse(CourseBean course) {
        course.setStatus(CourseStatus.CLOSED);
        courseRepository.saveAndFlush(objectsConverterService.convert(course));
    }

    @Override
    public void cancelCourse(CourseBean course) {
        course.setStatus(CourseStatus.CANCELLED);
        courseRepository.saveAndFlush(objectsConverterService.convert(course));
    }

    @Override
    @Transactional
    public CourseBean saveAndFlush(CourseBean courseBean) {
        Course course = objectsConverterService.convert(courseBean);
        return objectsConverterService.convert(courseRepository.saveAndFlush(course));
    }

    @Override
    @Transactional
    public CourseBean getLastOpenCourseByPatient(PatientBean patientBean) {
        if (patientBean == null){
            return null;
        }
        Patient patient = objectsConverterService.convert(patientBean);
        Course course = courseRepository.findLastByStatusAndPatient(CourseStatus.OPENED,patient);
        return objectsConverterService.convert(course);
    }

    @Override
    @Transactional
    public List<CourseBean> findAllCoursesByDepartmentByPeriod(VenueBean department, Date startDate, Date endDate) {
        Venue departmentEntity = objectsConverterService.convert(department);
        return courseRepository.findAllCoursesByDepartmentByPeriod(departmentEntity,startDate,endDate).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseBean> getOpenCourseByDepartmentAndDoctor(VenueBean department, UserBean doctor) {
        Venue departmentEntity = objectsConverterService.convert(department);
        User user = objectsConverterService.convert(doctor);
       return courseRepository.findAllByDepartmentEndAndResponsibleAndStatus(departmentEntity, user, CourseStatus.OPENED).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseBean> getCoursesByUserFromPeriod(Date startDate, Date endDate, UserBean userBean) {
        User user = objectsConverterService.convert(userBean);
        return courseRepository.findAllByResponsibleAndInPeriod(user, startDate,endDate).stream().map(course -> objectsConverterService.convert(course)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseBean> findAllByPatient(PatientBean patientBean) {
        if (patientBean.getId() == null){
            return new ArrayList<CourseBean>();
        }
        Patient patient = objectsConverterService.convert(patientBean);
        return courseRepository.findAllByPatient(patient).stream().map(course -> objectsConverterService.convert(course)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseBean findOne(Long id) {
        return objectsConverterService.convert(courseRepository.findOne(id));
    }

    @Override
    public List<Object[]> diagnosByPeriodByDepartment(VenueBean department, Date startDate, Date endDate) {
        if (department == null || department.getId() == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        Venue dep = objectsConverterService.convert(department);
        return courseRepository.findAllDiagnosByDepartmentByPeriod(dep, startDate, endDate);
    }

    @Override
    public List<Object[]> hospitalisationByPeriodByDepartment(VenueBean department, Date startDate, Date endDate) {
        if (department == null || department.getId() == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        Venue dep = objectsConverterService.convert(department);
        return courseRepository.findAllHospitalisationByDepartmentByPeriod(dep, startDate, endDate);
    }

    @Override
    public List<Object[]> dischargedByPeriodByDepartment(VenueBean department, Date startDate, Date endDate) {
        if (department == null || department.getId() == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        Venue dep = objectsConverterService.convert(department);
        return courseRepository.findAllDischargedByDepartmentByPeriod(dep, startDate, endDate);
    }

    @Override
    public List<Object[]> corseByResponsibleByDepartment(VenueBean department, Date startDate, Date endDate) {
        if (department == null || department.getId() == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        Venue dep = objectsConverterService.convert(department);
        return courseRepository.findAllCourseAndResponsibleByDepartmentByPeriod(dep, startDate, endDate);
    }

    @Override
    public List<Object[]> getDischargedStructereByDepartmentByPeriod(VenueBean department, Date startDate, Date endDate) {
        if (department == null || department.getId() == null || startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        Venue dep = objectsConverterService.convert(department);
        return courseRepository.getDischargedStructereByDepartmentByPeriod(dep, startDate, endDate);
    }

}
