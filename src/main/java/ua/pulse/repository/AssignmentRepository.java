package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Assignment;
import ua.pulse.entity.Course;

import java.sql.Date;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

    Assignment findTop1ByCourseOrderByDateAssignmentDesc(Course course);

    List<Assignment> getAssignmentsByDateAssignmentAndCourse(Date date, Course course);

    @Modifying
    @Query(value = "DELETE FROM Assignment a WHERE a.dateAssignment = :date AND a.course = :course AND a.id NOT IN :list")
    void deleteAllByDateAssignmentAndCourseAndIdNotIn(@Param("date") Date date, @Param("course") Course course, @Param("list") List<Long> listAssignment);

    @Modifying
    void deleteAllByDateAssignmentAndCourse(Date date,Course course);

    List<Assignment> findAllByCourseOrderByDateAssignmentDesc(Course course);

}
