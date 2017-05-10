package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.libs.Roles;
import ua.pulse.service.UserService;
import ua.pulse.vaadin.PulseUI;

import java.sql.Date;
import java.util.List;

@SpringComponent
public class UserBean {

    private Long id;
    private String userName;
    private String password;
    private String fullName;
    private String shortName;
    private Roles role;
    private SpecializationBean specialization;
    private VenueBean department;
    private HospitalBean hospital;
    private Date timeLimit;
    private Boolean isDisabled;

    public static final String NAME_SPRING_BEAN = "userBeanSpring";

    @Autowired
    UserService userService;

    public UserBean(){
        this.userName = "";
        this.fullName = "";
        this.password = "";
        this.shortName = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public SpecializationBean getSpecialization() {
        return specialization;
    }

    public VenueBean getDepartment() {
        return department;
    }

    public void setDepartment(VenueBean department) {
        this.department = department;
    }

    public Date getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Date timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setSpecialization(SpecializationBean specialization) {
        this.specialization = specialization;
    }

    public static List<UserBean> findAll(){
        return ((UserBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getUserService().findAll();
    }

    public static void deleteById(Long id) throws Exception{
        ((UserBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getUserService().deleteById(id);
    }

    public static UserBean findOne(Long id){
        return ((UserBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getUserService().findOne(id);
    }

    public static List<UserBean> findAllDoctorsByDepartment(VenueBean department){
        return ((UserBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getUserService().getDoctorsByDepartment(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserBean userBean = (UserBean) o;

        if (id != null ? !id.equals(userBean.id) : userBean.id != null) return false;
        if (userName != null ? !userName.equals(userBean.userName) : userBean.userName != null) return false;
        if (password != null ? !password.equals(userBean.password) : userBean.password != null) return false;
        if (fullName != null ? !fullName.equals(userBean.fullName) : userBean.fullName != null) return false;
        if (shortName != null ? !shortName.equals(userBean.shortName) : userBean.shortName != null) return false;
        if (role != userBean.role) return false;
        if (department != null ? !department.equals(userBean.department) : userBean.department != null) return false;
        if (timeLimit != null ? !timeLimit.equals(userBean.timeLimit) : userBean.timeLimit != null) return false;
        return isDisabled != null ? isDisabled.equals(userBean.isDisabled) : userBean.isDisabled == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (timeLimit != null ? timeLimit.hashCode() : 0);
        result = 31 * result + (isDisabled != null ? isDisabled.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getShortName().isEmpty()?getFullName():getShortName();
    }

    public HospitalBean getHospital() {
        return hospital;
    }

    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }
}
