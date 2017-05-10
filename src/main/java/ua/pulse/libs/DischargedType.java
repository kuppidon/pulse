package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

/**
 * Created by Alex on 14.01.2017.
 */
public enum DischargedType {

    HIS_RECOVERY("Выписан с выздоровлением"),
    WITH_IMPROVEMENT("Выписан с улучшением"),
    WITH_DETERIORATING("Выписан с ухудшением"),
    UNCHANGED("Без изменений"),
    DIED("Умер"),
    TO_OTHER("Направлен в другой стационар"),
    HEALTHY("Здоровый");

    private String view;

    private DischargedType(String view){
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public String getName() {
        return name();
    }

    static public BeanItemContainer<DischargedType> getContainer() {
        return new BeanItemContainer<DischargedType>(DischargedType.class,EnumSet.allOf(DischargedType.class));
    }
}
