package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum HospitalizationType {

	SCHEDULED("Плановая"),URGENT("Ургентная");
	
	private String view;
	
	private HospitalizationType(String view){
		this.view = view;
	}
	public String getView(){
		return view;
	}
	
	public String getName(){
		return name();
	}	

	static public BeanItemContainer<HospitalizationType> getContainer(){
		BeanItemContainer<HospitalizationType> container = new BeanItemContainer<HospitalizationType>(HospitalizationType.class);
		container.addAll(EnumSet.allOf(HospitalizationType.class));		
		return container;
	}
	
}
