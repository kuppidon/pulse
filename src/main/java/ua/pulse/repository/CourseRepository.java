package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Course;
import ua.pulse.entity.Patient;
import ua.pulse.entity.User;
import ua.pulse.entity.Venue;
import ua.pulse.libs.CourseStatus;

import java.sql.Date;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

    Course findLastByStatusAndPatient(CourseStatus status,Patient patient);

    List<Course> findAllByDepartmentEndAndStatus(Venue departmentEnd, CourseStatus status);

    List<Course> findAllByPatient(Patient patient);

    List<Course> findAllByDepartmentEndAndResponsibleAndStatus(Venue departmentEnd, User user, CourseStatus status);

    @Query(value = "SELECT c FROM Course c " +
            "WHERE (c.startDate >= :startDate " +
            "AND c.endDate <= :endDate OR c.endDate IS NULL)" +
            "AND c.responsible = :responsible")
    List<Course> findAllByResponsibleAndInPeriod(
            @Param("responsible") User responsible,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT c FROM Course c " +
            "WHERE (c.startDate >= :startDate " +
            "AND c.endDate <= :endDate) OR c.endDate IS NULL " +
            "AND (c.departmentStart = :department OR c.departmentEnd = :department)")
    List<Course> findAllCoursesByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT c.diagnosisMKB10 AS diagnos, SUM(1) AS countDiagnos FROM Course c " +
            "WHERE (c.startDate >= :startDate " +
            "AND c.endDate <= :endDate) OR c.endDate IS NULL " +
            "AND c.departmentEnd = :department " +
            "GROUP BY c.diagnosisMKB10")
    List<Object[]> findAllDiagnosByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT DATE(c.startDate), COUNT (DISTINCT c.patient) FROM Course c " +
            "WHERE c.startDate BETWEEN :startDate AND :endDate " +
            "AND c.departmentStart = :department " +
            "GROUP BY DATE(c.startDate)")
    List<Object[]> findAllHospitalisationByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT DATE(c.endDate), COUNT (DISTINCT c.patient) FROM Course c " +
            "WHERE c.endDate BETWEEN :startDate AND :endDate " +
            "AND c.departmentEnd = :department " +
            "GROUP BY DATE(c.endDate)")
    List<Object[]> findAllDischargedByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT c.responsible, COUNT (DISTINCT c) FROM Course c " +
            "WHERE c.startDate BETWEEN :startDate AND :endDate " +
            "AND c.departmentEnd = :department " +
            "AND c.responsible <> null " +
            "GROUP BY c.responsible")
    List<Object[]> findAllCourseAndResponsibleByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "SELECT c.dischargedType, SUM(1) FROM Course c " +
            "WHERE c.endDate BETWEEN :startDate AND :endDate " +
            "AND c.departmentEnd = :department " +
            "AND c.dischargedType is not null " +
            "GROUP BY c.dischargedType " +
            "ORDER BY c.dischargedType DESC")
    List<Object[]> getDischargedStructereByDepartmentByPeriod(
            @Param("department") Venue department,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

}
