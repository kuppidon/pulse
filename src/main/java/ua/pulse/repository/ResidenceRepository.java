package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Course;
import ua.pulse.entity.Residence;
import ua.pulse.entity.Venue;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.VenueType;

import java.util.List;

@Repository
public interface ResidenceRepository extends JpaRepository<Residence, Long>{

    Residence findFirstByCourseOrderByIdDesc(Course course);

    @Query("SELECT MAX(r) FROM Residence r" +
            " JOIN r.course c" +
            " WHERE c.status = :status and c.departmentEnd = :department and r.venue.type = :type" +
            " GROUP BY c")
    List<Residence> findAllActiveResidenceByDepartmentByVenuetype(
            @Param("status") CourseStatus status,
            @Param("department") Venue department,
            @Param("type") VenueType type
    );

    @Query("SELECT MAX(r) FROM Residence r" +
            " JOIN r.course c" +
            " WHERE c.status = :status and c.departmentEnd <> :department and r.venue = :department" +
            " GROUP BY c")
    List<Residence> findAllIncomingResidenceByDepartment(
            @Param("status") CourseStatus status,
            @Param("department") Venue department
    );



}
