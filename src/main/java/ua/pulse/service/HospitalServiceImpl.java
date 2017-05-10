package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.HospitalBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.entity.Hospital;
import ua.pulse.repository.HospitalRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalServiceImpl implements HospitalService {

	@Autowired
	HospitalRepository hospitalRepository;
	
	@Autowired
	ObjectsConverterService objectsConverterService;
	
	@Override
	public HospitalBean save(HospitalBean hospital) {
		if (hospital == null){
			return null;
		}
		Hospital hospitalEntity = objectsConverterService.convert(hospital);
		return objectsConverterService.convert(hospitalRepository.saveAndFlush(hospitalEntity));
	}

	@Override
	public List<HospitalBean> findAll() {
		return hospitalRepository.findAll().stream().map(e -> objectsConverterService.convert(e)).collect(Collectors.toList());
	}

    @Override
    public void delete(HospitalBean hospitalBean) throws Exception{
        hospitalRepository.delete(objectsConverterService.convert(hospitalBean));
    }

    @Override
    public void deleteById(Long id) throws Exception {
		hospitalRepository.delete(id);
    }

}
