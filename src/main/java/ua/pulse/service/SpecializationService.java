package ua.pulse.service;

import ua.pulse.bean.SpecializationBean;

import java.util.List;

/**
 * Created by Alex on 20.11.2016.
 */
public interface SpecializationService {

    List<SpecializationBean> getListSpetialisations();

    SpecializationBean saveAndFlush(SpecializationBean specializationBean);

    void deleteById(Long id) throws Exception;

}
