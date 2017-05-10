package ua.pulse.bean;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.libs.VenueType;
import ua.pulse.service.HospitalService;
import ua.pulse.service.VenueService;
import ua.pulse.vaadin.PulseUI;

import java.util.List;

@SpringComponent
public class VenueBean {

	@Autowired
	private HospitalService hospitalService;

	@Autowired
	private VenueService venueService;

	private Long id;
	private String name;
	private HospitalBean hospital;
	private VenueBean owner;
	private VenueType type;
	private Boolean isDisabled;
	private Integer capacity;
	private String descForTree;

	public static final String NAME_SPRING_BEAN = "venueBeanSpring";

	public VenueBean(){
		this.name = "";
		this.capacity = 1;
	}

	public HospitalService getHospitalService() {
		return hospitalService;
	}

	public VenueService getVenueService() {
		return venueService;
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

	public HospitalBean getHospital() {
		return hospital;
	}

	public void setHospital(HospitalBean hospitalBean) {
		this.hospital = hospitalBean;
	}

	public VenueBean getOwner() {
		return owner;
	}

	public void setOwner(VenueBean venueBean) {
		this.owner = venueBean;
	}

	public VenueType getType() {
		return type;
	}

	public void setType(VenueType type) {
		this.type = type;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	@Override
	public String toString() {
		return name;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VenueBean venueBean = (VenueBean) o;

		if (id != null ? !id.equals(venueBean.id) : venueBean.id != null) return false;
		if (name != null ? !name.equals(venueBean.name) : venueBean.name != null) return false;
		if (hospital != null ? !hospital.equals(venueBean.hospital) : venueBean.hospital != null) return false;
		if (owner != null ? !owner.equals(venueBean.owner) : venueBean.owner != null) return false;
		if (type != venueBean.type) return false;
		if (isDisabled != null ? !isDisabled.equals(venueBean.isDisabled) : venueBean.isDisabled != null) return false;
		return capacity != null ? capacity.equals(venueBean.capacity) : venueBean.capacity == null;

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (hospital != null ? hospital.hashCode() : 0);
		result = 31 * result + (owner != null ? owner.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (isDisabled != null ? isDisabled.hashCode() : 0);
		result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
		return result;
	}

	public void setDescForTree(String descForTree) {
		this.descForTree = descForTree;
	}

	public String getDescForTree() {
		return descForTree;
	}

	public static List<VenueBean> findAllDepartment(){
		return ((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().findAllDepartmen();
	}

	public static List<VenueBean> findAll(){
		return ((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().findAll();
	}

	public String getFullView(){
		return getName() +"/"+ getHospital().getName();
	}

	public static void deleteById(Long id) throws Exception{
		((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().deleteById(id);
    }

    public static Integer getCapacityDepartment(VenueBean department) {
        return ((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().getCapacityDepartment(department);
    }

	public static List<VenueBean> findAllVenueByTypeAndOwnerToContainer(VenueBean owner, VenueType type){
		return ((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().findAllVenueByTypeAndOwnerToContainer(owner, type);
	}

	public static List<VenueBean> findAllVenueByTypeAndHospitalToContainer(VenueType type, HospitalBean hospital){
		return ((VenueBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getVenueService().findAllVenueByTypeAndHospitalToContainer(type,hospital);
	}
}
