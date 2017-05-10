package ua.pulse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.pulse.bean.MKB10Bean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.repository.MKB10Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 18.11.2016.
 */
@Service
public class MKB10ServiceImpl implements MKB10Service {

    @Autowired
    MKB10Repository mkb10Repository;

    @Autowired
    ObjectsConverterService objectsConverterService;

    @Override
    public List<MKB10Bean> findAll() {
        return mkb10Repository.findAll().stream().map(mkb10 -> objectsConverterService.convert(mkb10)).collect(Collectors.toList());
    }
}
