package ua.pulse.service;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.User;
import ua.pulse.entity.Venue;
import ua.pulse.libs.Roles;
import ua.pulse.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserBean> getDoctorsByDepartment(VenueBean departmentBean) {
        Venue department = objectsConverterService.convert(departmentBean);
        List<Roles> roles = new ArrayList<>();
        roles.add(Roles.DOCTOR);
        roles.add(Roles.HEAD_OF_DEPARTMENT);
        return userRepository.findAllByRoleInAndDepartment(roles, department).stream().map(user -> objectsConverterService.convert(user)).collect(Collectors.toList());
    }

    @Override
    public UserBean saveAndFlush(UserBean userBean) {

        User userForSave = objectsConverterService.convert(userBean);
        String password = Hashing.sha256().hashBytes(userForSave.getPassword().getBytes()).toString();
        if (userBean.getId() != null){
            User userFromData = userRepository.findOne(userBean.getId());
            if (userFromData != null) {
                if (userForSave.getPassword().equals(userFromData.getPassword())) { //пароль изменился
                    password = userForSave.getPassword();
                }
            }
        }
        userForSave.setPassword(password);
        return objectsConverterService.convert(userRepository.saveAndFlush(userForSave));
    }

    @Override
    public UserBean findOneByUserNameAndPassword(String userName, String password) {
        password = Hashing.sha256().hashBytes(password.getBytes()).toString();
        return objectsConverterService.convert(userRepository.findOneByUserNameAndPassword(userName,password));
    }

    @Override
    public List<UserBean> findAll() {
        return userRepository.findAll().stream().map(user -> objectsConverterService.convert(user)).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) throws Exception {
        userRepository.delete(id);
    }

    @Override
    public UserBean findOne(Long id) {
        return objectsConverterService.convert(userRepository.findOne(id));
    }

}
