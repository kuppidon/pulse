package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum BloodGroup {
	
	I,II,III,IV;
	
	public String getName(){
		return name();
	}	

	static public BeanItemContainer<BloodGroup> getContainer(){
		BeanItemContainer<BloodGroup> container = new BeanItemContainer<BloodGroup>(BloodGroup.class);
		container.addAll(EnumSet.allOf(BloodGroup.class));		
		return container;
	}
}
