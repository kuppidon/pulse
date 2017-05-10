package ua.pulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pulse.entity.User;
import ua.pulse.entity.Venue;
import ua.pulse.libs.Roles;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAllByRoleInAndDepartment(List<Roles> roles, Venue department);

    User findOneByUserNameAndPassword(String userName, String password);
}
