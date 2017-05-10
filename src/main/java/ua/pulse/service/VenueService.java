package ua.pulse.service;

import com.vaadin.data.util.BeanItemContainer;
import org.springframework.transaction.annotation.Transactional;
import ua.pulse.bean.HospitalBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.libs.VenueType;

import java.util.List;

public interface VenueService {

	List<VenueBean> findAllDepartmen();
	
	VenueBean saveAndFlush(VenueBean venueBean);

    @Transactional
    List<VenueBean> findAll();

    BeanItemContainer<VenueBean> findAllVenueByTypeToContainer(VenueType type);

	List<VenueBean> findAllVenueByTypeAndOwnerToContainer(VenueBean owner, VenueType type);

	List<VenueBean> findAllVenueByTypeAndHospitalToContainer(VenueType type, HospitalBean hospitalBean);

	VenueBean findOne(Long id);

	void deleteById(Long id) throws Exception;

    Integer getCapacityDepartment(VenueBean department);
}
