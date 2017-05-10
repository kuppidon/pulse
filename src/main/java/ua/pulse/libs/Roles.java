package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

/**
 * Created by Alex on 14.01.2017.
 */
public enum Roles {

    POST("Постовая медсестра"),
    DOCTOR("Врач"),
    HEAD_OF_DEPARTMENT("Заведующий отделением"),
    ADMIN("Администратор");

    private String view;

    private Roles(String view){
        this.view = view;
    }
    public String getView(){
        return view;
    }

    public String getName(){
        return name();
    }

    static public BeanItemContainer<Roles> getContainer(){
        return new BeanItemContainer<Roles>(Roles.class,EnumSet.allOf(Roles.class));
    }
}
