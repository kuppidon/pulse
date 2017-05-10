package ua.pulse.libs;

import com.vaadin.data.util.BeanItemContainer;

import java.util.EnumSet;

public enum DepartureTypes {

	SELF("Самообращение"), AMBULANCE("Направлено скорой помощью"), OTHER_DEPARTMEN("Направлено другим стационаром");

	private String view;

	private DepartureTypes(String view){
		this.view = view;
	}

	public String getView() {
		return view;
	}

	public String getName() {
		return name();
	}

	static public BeanItemContainer<DepartureTypes> getContainer() {
		BeanItemContainer<DepartureTypes> container = new BeanItemContainer<DepartureTypes>(
				DepartureTypes.class);
		container.addAll(EnumSet.allOf(DepartureTypes.class));
		return container;
	}

}
