package ua.pulse.service;

import ua.pulse.bean.HospitalBean;

import java.util.List;

public interface HospitalService {

	HospitalBean save(HospitalBean hospital);

	List<HospitalBean> findAll();

	void delete(HospitalBean hospitalBean) throws Exception ;

	void deleteById(Long id) throws Exception;
	
}
