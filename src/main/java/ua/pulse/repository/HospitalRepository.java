package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long>{

}
