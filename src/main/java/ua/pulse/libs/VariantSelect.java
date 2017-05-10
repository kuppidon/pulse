package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

/**
 * Created by Alex on 21.12.2016.
 */
public enum VariantSelect {

    PERIOD("За период"),
    AN_DATE("На дату");

    private String view;

    private VariantSelect(String view){
        this.view = view;
    }
    public String getView(){
        return view;
    }

    public String getName(){
        return name();
    }

    static public BeanItemContainer<VariantSelect> getContainer(){
        BeanItemContainer<VariantSelect> container = new BeanItemContainer<VariantSelect>(VariantSelect.class);
        container.addAll(EnumSet.allOf(VariantSelect.class));
        return container;
    }
}
