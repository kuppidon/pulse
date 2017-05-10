package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.service.MKB10Service;
import ua.pulse.vaadin.PulseUI;

import java.util.List;

/**
 * Created by Alex on 18.11.2016.
 */
@SpringComponent
public class MKB10Bean {

    private Long id;
    private MKB10Bean parent;

    private String code;
    private String name;

    public final static String NAME_SPRING_BEAN = "MKB10Bean";

    @Autowired
    private MKB10Service mkb10Service;

    public MKB10Bean() {}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code;
    }

    public MKB10Bean getParent() {
        return parent;
    }

    public void setParent(MKB10Bean parent) {
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static List<MKB10Bean> findAll(){
        return ((MKB10Bean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).mkb10Service.findAll();
    }

    public MKB10Service getMkb10Service() {
        return mkb10Service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MKB10Bean mkb10Bean = (MKB10Bean) o;

        if (id != null ? !id.equals(mkb10Bean.id) : mkb10Bean.id != null) return false;
       // if (parent != null ? !parent.equals(mkb10Bean.parent) : mkb10Bean.parent != null) return false;
        if (code != null ? !code.equals(mkb10Bean.code) : mkb10Bean.code != null) return false;
        return name != null ? name.equals(mkb10Bean.name) : mkb10Bean.name == null;

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
       // result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
