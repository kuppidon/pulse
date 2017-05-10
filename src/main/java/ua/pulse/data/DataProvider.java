package ua.pulse.data;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.bean.*;
import ua.pulse.libs.VenueType;
import ua.pulse.service.*;

import java.sql.Date;
import java.util.List;

/**
 * Created by Alex on 12.12.2016.
 */
@SpringComponent
public class DataProvider {


    @Autowired
    CourseService courseService;

    @Autowired
    PatientService patientService;

    @Autowired
    ResidenceService residenceService;

    @Autowired
    VenueService venueService;

    @Autowired
    AssignmentService assignmentService;

    @Autowired
    UserService userService;

    public UserBean authenticate(String userName, String password){
        return userService.findOneByUserNameAndPassword(userName,password);
    }

    public List<CourseBean> findAllCoursesByDepartmentByPeriod(VenueBean department, Date startDate, Date endDate){
        return courseService.findAllCoursesByDepartmentByPeriod(department,startDate,endDate);
    }

    public List<CourseBean> getOpenCourseByDepartmentAndDoctor(VenueBean departmen, UserBean doctor){
        return courseService.getOpenCourseByDepartmentAndDoctor(departmen,doctor);
    }

    public List<CourseBean> getCoursesByUserFromPeriod(Date startDate, Date endDate, UserBean userBean){
        return courseService.getCoursesByUserFromPeriod(startDate, endDate, userBean);
    }

    public BeanItemContainer<PatientBean> getListPatiens(){
        return patientService.getPatientsContainer();
    }

    public List<ResidenceBean> findAllActiveResidenceByDepartmentByVenuetype(VenueBean departmentBean, VenueType type){
        return residenceService.findAllActiveResidenceByDepartmentByVenuetype(departmentBean, type);
    }

    public List<ResidenceBean> findAllIncomingResidenceByDepartment(VenueBean departmentBean){
        return residenceService.findAllIncomingResidenceByDepartment(departmentBean);
    }

    public List<VenueBean> getHospitalRoomByDepartment(VenueBean department){
        return venueService.findAllVenueByTypeAndOwnerToContainer(department, VenueType.HOSPITAL_ROOM);
    }

    public VenueBean getVenueById(Long id){
        return venueService.findOne(id);
    }

    public Date getLastDateAssignmentByCourse(CourseBean courseBean){
        return assignmentService.getLastDateAssignmentByCourse(courseBean);
    }

    public List<AssignmentBean> getAssignmentsByDateAndCourse(Date date, CourseBean courseBean){
        return assignmentService.getAssignmentByDateAndCourse(date,courseBean);
    }


}
