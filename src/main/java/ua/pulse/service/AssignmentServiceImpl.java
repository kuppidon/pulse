package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.AssignmentBean;
import ua.pulse.bean.CourseBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Assignment;
import ua.pulse.entity.Course;
import ua.pulse.repository.AssignmentRepository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Override
    public Date getLastDateAssignmentByCourse(CourseBean courseBean) {
        Course course = objectsConverterService.convert(courseBean);
        Assignment assignment = assignmentRepository.findTop1ByCourseOrderByDateAssignmentDesc(course);
        if(assignment == null){
            return null;
        }
        return assignment.getDateAssignment();
    }

    @Override
    @Transactional
    public List<AssignmentBean> getAssignmentByDateAndCourse(Date date, CourseBean courseBean) {
        Course course = objectsConverterService.convert(courseBean);
        return assignmentRepository.getAssignmentsByDateAssignmentAndCourse(date, course).stream().map(assignment -> objectsConverterService.convert(assignment)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AssignmentBean> getAssignmentsByCourse(CourseBean courseBean) {
        if (courseBean == null || courseBean.getId() == null){
            return new ArrayList<AssignmentBean>();
        }
        Course course = objectsConverterService.convert(courseBean);
        return assignmentRepository.findAllByCourseOrderByDateAssignmentDesc(course).stream().map(assignment -> objectsConverterService.convert(assignment)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveAssignmentByCourseAndDate(CourseBean courseBean, Date date, Collection<AssignmentBean> listAssignmentBean) {

        Course course = objectsConverterService.convert(courseBean);
        List<Assignment> listAssignment = listAssignmentBean.stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());

        List<Long> listExeption = new ArrayList<>();
        for (Assignment as:listAssignment){
            if (as.getId() != null){
                listExeption.add(as.getId());
            }
        }
        //удаляем то, что удалили в назначениях, если они записаны
        if (!listExeption.isEmpty()){
            assignmentRepository.deleteAllByDateAssignmentAndCourseAndIdNotIn(date,course,listExeption);
        }
        else {
            assignmentRepository.deleteAllByDateAssignmentAndCourse(date,course);
        }

        if (!listAssignmentBean.isEmpty()){
            assignmentRepository.save(listAssignment);
        }
    }
}
