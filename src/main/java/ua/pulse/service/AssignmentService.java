package ua.pulse.service;

import ua.pulse.bean.AssignmentBean;
import ua.pulse.bean.CourseBean;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

public interface AssignmentService {

    Date getLastDateAssignmentByCourse(CourseBean courseBean);

    List<AssignmentBean> getAssignmentByDateAndCourse(Date date, CourseBean courseBean);

    List<AssignmentBean> getAssignmentsByCourse(CourseBean courseBean);

    void saveAssignmentByCourseAndDate(CourseBean courseBean, Date date, Collection<AssignmentBean> listAssignment);

}
