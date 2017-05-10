package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.SpecializationBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.repository.SpecialisationRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 20.11.2016.
 */
@Service
public class SpecializationServiceImpl implements SpecializationService {

    @Autowired
    SpecialisationRepository specialisationRepository;

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Override

    public List<SpecializationBean> getListSpetialisations() {
        return specialisationRepository.findAll().stream().map(specialization -> objectsConverterService.convert(specialization)).collect(Collectors.toList());
    }

    @Override
    public SpecializationBean saveAndFlush(SpecializationBean specializationBean) {
        return objectsConverterService.convert(specialisationRepository.saveAndFlush(objectsConverterService.convert(specializationBean)));
    }

    @Override
    public void deleteById(Long id) throws Exception {
        specialisationRepository.delete(id);
    }
}
