package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum BloodRhesus {
	
	POSITIVE("Rh+"),NEGATIVE("Rh-");
	
	private String view;
	
	private BloodRhesus(String view){
		this.view = view;
	}
	public String getView(){
		return view;
	}
	
	public String getName(){
		return name();
	}	

	static public BeanItemContainer<BloodRhesus> getContainer(){
		BeanItemContainer<BloodRhesus> container = new BeanItemContainer<BloodRhesus>(BloodRhesus.class);
		container.addAll(EnumSet.allOf(BloodRhesus.class));		
		return container;
	}
	
}
