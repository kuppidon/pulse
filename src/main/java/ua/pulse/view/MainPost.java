package ua.pulse.view;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import ua.pulse.PulseNavigator;

public class MainPost extends HorizontalLayout {

    public MainPost() {

        setSizeFull();

        addStyleName("mainview");

        addComponent(new PulseMenu());

        ComponentContainer content = new CssLayout();
        content.addStyleName("view-content");
        content.setSizeFull();

        addComponent(content);

        setExpandRatio(content, 1.0f);
        new PulseNavigator(content);
    }
}


