package ua.pulse.converter;

import org.vaadin.suggestfield.client.SuggestFieldSuggestion;
import ua.pulse.bean.PatientBean;
import ua.pulse.vaadin.PulseUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 16.12.2016.
 */
public class PatientSuggestionConverter extends BeanSuggestionConverter {

    private List<PatientBean> items = new ArrayList<PatientBean>();
    private String prevQuery;

    public PatientSuggestionConverter() {
        super(PatientBean.class, "id", "fullDescription", "fullName");
    }

    @Override
    public Object toItem(SuggestFieldSuggestion suggestion) {

        PatientBean result = null;
        if(suggestion != null && suggestion.getId() != null){
            result = PulseUI.getPatientService().fingOne(Long.valueOf(suggestion.getId()));
        }
        assert result != null : "This should not be happening";
        return result;
    }

    public List<Object> handleSearchQuery(String query) {

        query = query.toLowerCase();

        if ("".equals(query) || query == null) {
            return Collections.emptyList();
        }

        if (prevQuery==null || !query.startsWith(prevQuery) || items.isEmpty()){
            buildItems(query);
        }

        if (items.isEmpty()){
            return Collections.emptyList();
        }

        List<Object> result = new ArrayList<>();

        for (PatientBean patientBean : items) {
            if (patientBean.getFullName().toLowerCase().contains(query)) {
                result.add(patientBean);
            }
        }
        System.out.println("Total: " + result.size());
        prevQuery = query;
        return result;
    }


    private void buildItems(String name){
        List<PatientBean> listPatient = PulseUI.getPatientService().findAllByFullNameLike("%" + name + "%");
        if (listPatient.size() <= 50){
            items = listPatient;
        }
        else {
            items = Collections.emptyList();
        }
    }

}
