package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
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
import ua.pulse.bean.VenueBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.VenueType;
import ua.pulse.service.HospitalService;
import ua.pulse.service.VenueService;

@SuppressWarnings("serial")
public class CreateEditVenueWindow extends Window {
	
	private HospitalService hospitalService;
	private VenueService venueService;

	private final BeanFieldGroup<VenueBean> fieldGroup;

    @PropertyId("name")
    private TextField nameField;
    @PropertyId("type")
    private ComboBox typeField;
	//@PropertyId("capacity")
	private TextField capacityField;
   // @PropertyId("owner")
    private ComboBox ownerField;

    
    public CreateEditVenueWindow(VenueBean venueBean){
    	
    	 hospitalService = venueBean.getHospitalService();
    	 venueService    = venueBean.getVenueService();
    	
    	 Responsive.makeResponsive(this);
		 center();	 
		 setIcon(FontAwesome.HOSPITAL_O);
		 setModal(true);
	    // setCloseShortcut(KeyCode.ESCAPE, null);
	     setResizable(false);
	     setClosable(false);
	     setWidth(50.0f, Unit.PERCENTAGE);
	     if(venueBean.getId() == null)
	    	 setCaption(" Создание");
	     else
	    	 setCaption(" Редактирование");

	     
	     VerticalLayout content = new VerticalLayout();
	     setContent(content);
	     content.setSpacing(true);
	     content.setMargin(true);

	     nameField = new TextField("Наименование");
	     nameField.setRequired(true);
	     nameField.setComponentError(new UserError("Укажите наименование"));
	     nameField.setWidth("100%");
	     nameField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				if(event.getText().isEmpty() && nameField.getComponentError() == null ){
					nameField.setComponentError(new UserError("укажите наименование"));
					nameField.setRequired(true);
				}				
				else {
					nameField.setComponentError(null);
					nameField.setRequired(false);
				}
				
			}
		});     
	    content.addComponent(nameField);
	    
	    
	    HorizontalLayout otherGroup = new HorizontalLayout();
	    otherGroup.setSpacing(true);
	    otherGroup.setWidth("100%");
	    
	    typeField = new ComboBox("Тип", VenueType.getContainer());
	    typeField.setInputPrompt("Тип не указан");
	    typeField.setNullSelectionAllowed(false);
	    typeField.setTextInputAllowed(false);
	    typeField.setNewItemsAllowed(false);
	    typeField.setItemCaptionPropertyId("view");	    
	    typeField.addValueChangeListener(e -> 
	    	setSourceForOwnerWithCurrentUnitType((VenueType) e.getProperty().getValue()));
	    otherGroup.addComponent(typeField);

		capacityField =  new TextField("Вместимость");
		capacityField.setConverter(Integer.class);
		capacityField.setConversionError("Ошибка преобразования введенного значения в число");
		capacityField.setWidth("50px");
		otherGroup.addComponent(capacityField);

	    ownerField = new ComboBox("Владелец");
	    ownerField.setWidth("100%");;
	    ownerField.setInputPrompt("Владелец не выран");
	    ownerField.setItemCaptionPropertyId("id");
	    ownerField.setNewItemsAllowed(false);
	    ownerField.setNullSelectionAllowed(false);    	    
	    ownerField.setTextInputAllowed(false);
	    ownerField.setRequired(true);	    	    
	    
	    otherGroup.addComponent(ownerField);
	    //otherGroup.setExpandRatio(typeField, 1);
		otherGroup.setExpandRatio(ownerField, 1);
	    content.addComponent(otherGroup);     
	    content.addComponent(buildFooter()); 
    	
    	fieldGroup = new BeanFieldGroup<>(VenueBean.class);
	    fieldGroup.bindMemberFields(this);
	    fieldGroup.setItemDataSource(venueBean);
	    if (venueBean.getId() != null){
		    setSourceForOwnerWithCurrentUnitType(venueBean.getType());
		    if(venueBean.getType().equals(VenueType.DEPARTMEN)){
		    	fieldGroup.bind(ownerField, "hospital");
		    	//ownerField.select(unit.getHospital());
	        }
	        else if(venueBean.getType().equals(VenueType.HOSPITAL_ROOM)){
	        	fieldGroup.bind(ownerField, "owner");
	        	//ownerField.select(unit.getOwner());                  	
	        }
	    }
	    else
			capacityField.setVisible(false);

		if (venueBean.getCapacity() == null)
			capacityField.setValue("0");
		else
			capacityField.setValue(String.valueOf(venueBean.getCapacity()));
    }
    
    
    private void setSourceForOwnerWithCurrentUnitType(VenueType type){
    	if(type.equals(VenueType.DEPARTMEN)){
    		ownerField.setContainerDataSource(new BeanItemContainer<>(HospitalBean.class,hospitalService.findAll()));
    		ownerField.setItemCaptionPropertyId("name");
    		ownerField.setCaption("Госпиталь");
    		ownerField.setInputPrompt("Госпиталь не выран");
    		ownerField.setRequiredError("Необходимо указать госпиталь, которому принадлежит отделение");
    		fieldGroup.bind(ownerField, "hospital");

			capacityField.setVisible(false);

    	}
    	else if(type.equals(VenueType.HOSPITAL_ROOM)){
    		ownerField.setContainerDataSource(new BeanItemContainer<>(VenueBean.class,VenueBean.findAllDepartment()));
    		ownerField.setItemCaptionPropertyId("fullView");
    		ownerField.setCaption("Отделение");
    		ownerField.setInputPrompt("Отделение не вырано");
    		ownerField.setRequiredError("Необходимо указать отделение, в котором размещается палата");
    		fieldGroup.bind(ownerField, "owner");

			capacityField.setVisible(true);
    	}
    	else{
    		ownerField.removeAllItems();
    		ownerField.setCaption("Владелец");
    		ownerField.setInputPrompt("Владелец не выран");
    		ownerField.setRequiredError(null);
    		fieldGroup.bind(ownerField, null);
    	}
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
                    VenueBean сurrentUnitBean = fieldGroup.getItemDataSource().getBean();
                    if(typeField.getValue().equals(VenueType.DEPARTMEN)){
                    	
                    	//сurrentUnitBean.setHospital((HospitalBean) ownerField.getValue());
                    	сurrentUnitBean.setOwner(null);
						сurrentUnitBean.setCapacity(0);
                    }
                    else if(typeField.getValue().equals(VenueType.HOSPITAL_ROOM)){
                    	//сurrentUnitBean.setOwner((CurrentUnitBean) ownerField.getValue());
                    	сurrentUnitBean.setHospital(сurrentUnitBean.getOwner().getHospital());
						сurrentUnitBean.setCapacity(Integer.valueOf(capacityField.getValue()));
                    }                   
                    venueService.saveAndFlush(сurrentUnitBean);
                    Notification success = new Notification(
							сurrentUnitBean.getType().getView() + " " + сurrentUnitBean.getName() + " успешно " + (сurrentUnitBean.getType().equals(VenueType.DEPARTMEN)?"сохранено":"сохранена"));
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                    close();

					PulseEventBus.post(new PulseEvent.CreateEditVenueEvent());
                } catch (CommitException e) {
                    Notification.show("Ошибка записи",e.getMessage(),
                            Type.ERROR_MESSAGE);
                }
            }
        });
        ok.focus();
        
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
