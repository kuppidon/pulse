package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.Patient;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{

    List<Patient> findAllByFullNameLikeOrderByFullName(String name);
}
