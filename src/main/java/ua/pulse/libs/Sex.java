package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum Sex {
	
	MALE("Мужской"),
	FEMALE("Женский");
	
	private String view;
	
	private Sex(String view){
		this.view = view;
	}
	public String getView(){
		return view;
	}
	
	public String getName(){
		return name();
	}	

	static public BeanItemContainer<Sex> getContainer(){
		BeanItemContainer<Sex> container = new BeanItemContainer<Sex>(Sex.class);
		container.addAll(EnumSet.allOf(Sex.class));		
		return container;
	}
	
}
