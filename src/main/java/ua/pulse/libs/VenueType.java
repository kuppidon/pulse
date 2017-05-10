package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum VenueType {
	
	DEPARTMEN("Отделение"),
	//BED,
	HOSPITAL_ROOM("Палата");
	
	private String view;
	
	private VenueType(String view){
		this.view = view;
	}
	public String getView(){
		return view;
	}
	
	public String getName(){
		return name();
	}	

	static public BeanItemContainer<VenueType> getContainer(){
		BeanItemContainer<VenueType> container = new BeanItemContainer<VenueType>(VenueType.class);
		container.addAll(EnumSet.allOf(VenueType.class));
		return container;
	}

}
