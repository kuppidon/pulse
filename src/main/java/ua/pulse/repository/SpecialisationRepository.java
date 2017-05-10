package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.pulse.entity.Specialization;

/**
 * Created by Alex on 18.11.2016.
 */
public interface SpecialisationRepository extends JpaRepository<Specialization, Long> {
}
