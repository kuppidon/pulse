package ua.pulse.service;

import com.vaadin.data.util.BeanItemContainer;
import ua.pulse.bean.PatientBean;

import java.util.List;

public interface PatientService {

	PatientBean saveAndFlush(PatientBean patientBean);
	
	BeanItemContainer<PatientBean> getPatientsContainer();
	
	List<PatientBean> findAll();

	List<PatientBean> findAllByFullNameLike(String name);

	PatientBean fingOne(Long id);
}
