package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.ResidenceBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Course;
import ua.pulse.entity.Residence;
import ua.pulse.entity.Venue;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.VenueType;
import ua.pulse.repository.ResidenceRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResidenceServiceImpl implements ResidenceService {

    @Autowired
    ResidenceRepository residenceRepository;

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Override
    @Transactional
    public ResidenceBean getCurrentResidenceByCourse(CourseBean courseBean) {
        Course course = objectsConverterService.convert(courseBean);
        Residence residence = residenceRepository.findFirstByCourseOrderByIdDesc(course);
        return objectsConverterService.convert(residence);
    }

    @Override
    @Transactional
    public ResidenceBean saveAndFlush(ResidenceBean residenceBean) {
        if (residenceBean == null){
            return null;
        }
        Residence residence = objectsConverterService.convert(residenceBean);
        return objectsConverterService.convert(residenceRepository.saveAndFlush(residence));
    }

    @Override
    @Transactional
    public List<ResidenceBean> findAllActiveResidenceByDepartmentByVenuetype(VenueBean departmentBean, VenueType type){
        Venue department = objectsConverterService.convert(departmentBean);
        List<Residence> residenceList = residenceRepository.findAllActiveResidenceByDepartmentByVenuetype(CourseStatus.OPENED, department, type);
        return residenceList.stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ResidenceBean> findAllIncomingResidenceByDepartment(VenueBean departmentBean){
        Venue department = objectsConverterService.convert(departmentBean);
        List<Residence> residenceList = residenceRepository.findAllIncomingResidenceByDepartment(CourseStatus.OPENED, department);
        return residenceList.stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
    }

}
