package ua.pulse.service;

import com.vaadin.data.util.BeanItemContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pulse.bean.HospitalBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Hospital;
import ua.pulse.entity.Venue;
import ua.pulse.libs.VenueType;
import ua.pulse.repository.VenueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {

	@Autowired
	VenueRepository venueRepository;
	
	@Autowired
	ObjectsConverterService objectsConverterService;

	@Override
	@Transactional
	public List<VenueBean> findAllDepartmen() {
		return venueRepository.findAllByType(VenueType.DEPARTMEN).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<VenueBean> findAll() {
		return venueRepository.findAll().stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public BeanItemContainer<VenueBean> findAllVenueByTypeToContainer(VenueType type) {
		BeanItemContainer<VenueBean> container = new BeanItemContainer<VenueBean>(VenueBean.class);
		container.addAll(venueRepository.findAllByType(type).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList()));
		return container;		
	}

	@Override
	@Transactional
	public List<VenueBean> findAllVenueByTypeAndOwnerToContainer(VenueBean ownerBean, VenueType type) {
		Venue owner = objectsConverterService.convert(ownerBean);
		return venueRepository.findByTypeAndOwner(type, owner).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<VenueBean> findAllVenueByTypeAndHospitalToContainer(VenueType type, HospitalBean hospitalBean) {
		Hospital hospital = objectsConverterService.convert(hospitalBean);
		return venueRepository.findAllByTypeAndHospital(type,hospital).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

	@Override
	public VenueBean findOne(Long id) {
		return objectsConverterService.convert(venueRepository.findOne(id));
	}

    @Override
    public void deleteById(Long id) throws Exception{
        venueRepository.delete(id);
    }

    @Override
    public Integer getCapacityDepartment(VenueBean department) {
        if (department == null) {
            return 0;
        }
        Venue dep = objectsConverterService.convert(department);
        return venueRepository.getCapasityDepartment(dep, VenueType.HOSPITAL_ROOM);
    }

    @Override
	public VenueBean saveAndFlush(VenueBean venueBean) {
		if (venueBean == null){
			return null;
		}
		Venue currentUnit = objectsConverterService.convert(venueBean);
		return objectsConverterService.convert(venueRepository.saveAndFlush(currentUnit));
	}
	
}
