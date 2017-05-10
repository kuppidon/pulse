package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.service.HospitalService;
import ua.pulse.vaadin.PulseUI;

import java.util.List;

@SpringComponent
public class HospitalBean {
   
    private Long id;  
    private String name;   
    private String address;  
    
    @Autowired
    private HospitalService hospitalService;

	public final static String NAME_SPRING_BEAN = "hospitalBeanSpring";

	public HospitalBean() {
		name = "";
		address = "";
	}

	public HospitalService getHospitalService() {
		return hospitalService;
	}   
	
	public HospitalBean save(){
		return getHospitalService().save(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "HospitalBean{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HospitalBean that = (HospitalBean) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return address != null ? address.equals(that.address) : that.address == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		return result;
	}

	public static List<HospitalBean> getAllHospitals(){
		return ((HospitalBean) PulseUI.getSpringBean("hospitalBeanSpring")).getHospitalService().findAll();
	}

	public static void deleteById(Long id) throws Exception{
		((HospitalBean) PulseUI.getSpringBean("hospitalBeanSpring")).getHospitalService().deleteById(id);
	}

	public static List<HospitalBean> findAll(){
		return ((HospitalBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getHospitalService().findAll();
	}
}
