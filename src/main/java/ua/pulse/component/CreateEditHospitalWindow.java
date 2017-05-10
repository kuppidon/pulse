package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.HospitalBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;

@SuppressWarnings("serial")
public class CreateEditHospitalWindow extends Window {

	private final BeanFieldGroup<HospitalBean> fieldGroup;

    @PropertyId("name")
    private TextField nameField;
    @PropertyId("address")
    private TextField addressField;
   
    public CreateEditHospitalWindow(HospitalBean hospital){
		
    	Responsive.makeResponsive(this);
		center();	 
		setIcon(FontAwesome.HOSPITAL_O);
		setModal(true);
	    // setCloseShortcut(KeyCode.ESCAPE, null);
	    setResizable(false);
	    setClosable(false);
	    setWidth(50.0f, Unit.PERCENTAGE);
	    if(hospital.getId() == null){
	    	setCaption(" Создание");
	    }
	    else{
	    	setCaption(" Редактирование");
	    }
	    
	    VerticalLayout content = new VerticalLayout();
	     setContent(content);
	     content.setSpacing(true);
	     content.setMargin(true);

	     nameField = new TextField("Наименование");
	     nameField.setRequired(true);
	     nameField.setComponentError(new UserError("Укажите наименование"));
	     nameField.setWidth("100%");
	     nameField.addTextChangeListener((TextChangeListener) event -> {
             if(event.getText().isEmpty() && nameField.getComponentError() == null ){
                 nameField.setComponentError(new UserError("укажите наименование"));
                 nameField.setRequired(true);
             }
             else {
                 nameField.setComponentError(null);
                 nameField.setRequired(false);
             }
         });
		if (hospital.getId() == null){
			nameField.focus();
		}
	    content.addComponent(nameField);

	    addressField = new TextField("Адрес");
	    addressField.setWidth("100%");	      
	    content.addComponent(addressField);
	    
	    content.addComponent(buildFooter());
    	
	    fieldGroup = new BeanFieldGroup<>(HospitalBean.class);
	    fieldGroup.bindMemberFields(this);
	    fieldGroup.setItemDataSource(hospital);

    }
    
    private Component buildFooter() {
        
    	HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button("Сохранить");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit(); 
                    HospitalBean hospitalBean = fieldGroup.getItemDataSource().getBean();
                    hospitalBean.save();
                    Notification success = new Notification(
                            "Госпиталь \"" + hospitalBean.getName() + "\" успешно сохранен");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                    close();

					PulseEventBus.post(new PulseEvent.CreateEditHospital());
                } catch (CommitException e) {
                    Notification.show("Ошика записи",e.getMessage(),
                            Type.ERROR_MESSAGE);
                }
            }
        });
       // ok.focus();
        
        Button cancel = new Button("Отмена");
        cancel.addStyleName(ValoTheme.BUTTON_DANGER);
        cancel.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				close();					
			}
		});
        
        footer.addComponents(ok,cancel);
        footer.setExpandRatio(ok, 1);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }

}
