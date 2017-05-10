package ua.pulse.service;

import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;

import java.util.List;

public interface UserService {

    List<UserBean> getDoctorsByDepartment(VenueBean department);

    UserBean saveAndFlush(UserBean userBean);

    UserBean findOneByUserNameAndPassword(String userName, String password);

    List<UserBean> findAll();

    void deleteById(Long id) throws Exception;

    UserBean findOne(Long id);

}
