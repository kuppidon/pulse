package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

/**
 * Created by Alex on 31.01.2017.
 */
public enum ReportsDataType {

    BY_DEPARTMENT("По отделению"), BY_DOCTOR("По врачу");

    private String view;

    private ReportsDataType(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public String getName() {
        return name();
    }

    static public BeanItemContainer<ReportsDataType> getContainer() {
        BeanItemContainer<ReportsDataType> container = new BeanItemContainer<ReportsDataType>(ReportsDataType.class);
        container.addAll(EnumSet.allOf(ReportsDataType.class));
        return container;
    }
}
