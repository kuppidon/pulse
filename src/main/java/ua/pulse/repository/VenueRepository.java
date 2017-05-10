package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Hospital;
import ua.pulse.entity.Venue;
import ua.pulse.libs.VenueType;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long>{

	List<Venue> findAllByType(VenueType type);

	List<Venue> findByTypeAndOwner(VenueType type, Venue owner);

	List<Venue> findAllByTypeAndHospital(VenueType type, Hospital hospital);

    @Query(value = "SELECT SUM (v.capacity) FROM Venue v " +
            "WHERE v.type = :type " +
            "AND v.owner = :department")
    Integer getCapasityDepartment(@Param("department") Venue department, @Param("type") VenueType type);

}
