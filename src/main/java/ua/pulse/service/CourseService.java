package ua.pulse.service;

import ua.pulse.bean.CourseBean;
import ua.pulse.bean.PatientBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;

import java.sql.Date;
import java.util.List;

public interface CourseService {

    CourseBean openCourse(CourseBean course, VenueBean venueBean);

    void closeCourse(CourseBean course);

    void cancelCourse(CourseBean course);

    CourseBean saveAndFlush(CourseBean course);

    CourseBean getLastOpenCourseByPatient(PatientBean patientBean);

    List<CourseBean> findAllCoursesByDepartmentByPeriod(VenueBean department, Date startDate, Date endDate);

    List<CourseBean> getOpenCourseByDepartmentAndDoctor(VenueBean departmen, UserBean doctor);

    List<CourseBean> getCoursesByUserFromPeriod(Date startDate, Date endDate, UserBean userBean);

    List<CourseBean> findAllByPatient(PatientBean patientBean);

    CourseBean findOne(Long id);

    List<Object[]> diagnosByPeriodByDepartment(VenueBean department, Date startDate, Date endDate);

    List<Object[]> hospitalisationByPeriodByDepartment(VenueBean department, Date startDate, Date endDate);

    List<Object[]> dischargedByPeriodByDepartment(VenueBean department, Date startDate, Date endDate);

    List<Object[]> corseByResponsibleByDepartment(VenueBean department, Date startDate, Date endDate);

    List<Object[]> getDischargedStructereByDepartmentByPeriod(VenueBean department, Date startDate, Date endDate);


}
