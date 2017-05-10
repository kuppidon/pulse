package ua.pulse.service;

import ua.pulse.bean.CourseBean;
import ua.pulse.bean.ResidenceBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.libs.VenueType;

import javax.transaction.Transactional;
import java.util.List;

public interface ResidenceService {

    ResidenceBean getCurrentResidenceByCourse(CourseBean courseBean);

    ResidenceBean saveAndFlush(ResidenceBean residenceBean);

    List<ResidenceBean> findAllActiveResidenceByDepartmentByVenuetype(VenueBean department, VenueType type);

    @Transactional
    List<ResidenceBean> findAllIncomingResidenceByDepartment(VenueBean departmentBean);
}
