package ua.pulse.service;

import com.vaadin.data.util.BeanItemContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.PatientBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Patient;
import ua.pulse.repository.PatientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
    PatientRepository patientRepository;
	
	@Autowired
	ObjectsConverterService objectsConverterService;
	
	@Override
	public PatientBean saveAndFlush(PatientBean patientBean) {
		if (patientBean == null){
			return null;
		}
		Patient patient = objectsConverterService.convert(patientBean);
		return objectsConverterService.convert(patientRepository.saveAndFlush(patient));
	}	

	/**
	 * метод возвращает BeanItemContainer пациентов 
	 */
	@Override
	public BeanItemContainer<PatientBean> getPatientsContainer() {
		BeanItemContainer<PatientBean> container = new BeanItemContainer<PatientBean>(PatientBean.class);
		container.addAll(findAll());		
		return container;
	}
	
	/**
	 * метод возвращает всех пациентов
	 */
	@Override
	public List<PatientBean> findAll() {
		return patientRepository.findAll().stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

	@Override
	public List<PatientBean> findAllByFullNameLike(String name) {
		return patientRepository.findAllByFullNameLikeOrderByFullName(name).stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

	@Override
	public PatientBean fingOne(Long id) {
		return objectsConverterService.convert(patientRepository.findOne(id));
	}

}
