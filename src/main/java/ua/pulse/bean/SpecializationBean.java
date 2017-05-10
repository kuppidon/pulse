package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.service.SpecializationService;
import ua.pulse.vaadin.PulseUI;

import java.util.List;

/**
 * Created by Alex on 20.11.2016.
 */
@SpringComponent
public class SpecializationBean {

    private Long id;
    private String name;

    public static final String NAME_SPRING_BEAN = "specializationBeanSpring";

    @Autowired
    private SpecializationService specializationService;

    public SpecializationBean(){
        this.name = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpecializationBean saveAndFlush(){
        return  specializationService.saveAndFlush(this);
    }

    public SpecializationService getSpecializationService() {
        return specializationService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecializationBean that = (SpecializationBean) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public static List<SpecializationBean> getAllSpecializations(){
        return ((SpecializationBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getSpecializationService().getListSpetialisations();
    }

    public static void deleteById(Long id) throws Exception{
        ((SpecializationBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getSpecializationService().deleteById(id);
    }

    @Override
    public String toString() {
        return getName();
    }
}
