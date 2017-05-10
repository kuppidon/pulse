package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

/**
 * Created by Alex on 23.11.2016.
 */
public enum TransferType {

    TO_ROOM("В другую палату"),
    TO_DEPARTMEN("В другое отделение"),
    TO_HOSPITAL("В другой госпиталь");

    private String view;

    private TransferType(String view){
        this.view = view;
    }
    public String getView(){
        return view;
    }

    public String getName(){
        return name();
    }

    static public BeanItemContainer<TransferType> getContainer(){
        BeanItemContainer<TransferType> container = new BeanItemContainer<TransferType>(TransferType.class);
        container.addAll(EnumSet.allOf(TransferType.class));
        return container;
    }

}
