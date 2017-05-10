package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.UserError;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.SpecializationBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;

@SuppressWarnings("serial")
public class CreateEditSpecialisationWindow extends Window {

	private final BeanFieldGroup<SpecializationBean> fieldGroup;

	@PropertyId("name")
	private TextField nameField;


	public CreateEditSpecialisationWindow(SpecializationBean specializationBean){

		Responsive.makeResponsive(this);
		center();
		setIcon(FontAwesome.HAND_SPOCK_O);
		setModal(true);
		setResizable(false);
		setClosable(false);
		setWidth(50.0f, Unit.PERCENTAGE);
		if(specializationBean.getId() == null){
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
		nameField.focus();
		content.addComponent(nameField);
		content.setExpandRatio(nameField, 1);
		content.addComponent(buildFooter());

		fieldGroup = new BeanFieldGroup<SpecializationBean>(SpecializationBean.class);
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(specializationBean);

	}

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button("Сохранить");
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addStyleName(ValoTheme.BUTTON_TINY);
		ok.addClickListener(event -> {
			try {
				fieldGroup.commit();
				SpecializationBean specializationBean = fieldGroup.getItemDataSource().getBean();
				specializationBean.saveAndFlush();
				Notification success = new Notification("Специализация сохранена");
				success.setDelayMsec(2000);
				success.setStyleName("bar success small");
				success.setPosition(Position.BOTTOM_CENTER);
				success.show(Page.getCurrent());
				close();

				PulseEventBus.post(new PulseEvent.CreateEditSpecializationEvent());
			} catch (CommitException e) {
				Notification.show("Ошибка записи специализации",e.getMessage(),
						Type.ERROR_MESSAGE);
			}

		});
		//ok.focus();

		Button cancel = new Button("Отмена");
		cancel.addStyleName(ValoTheme.BUTTON_DANGER);
		cancel.addStyleName(ValoTheme.BUTTON_TINY);
		cancel.addClickListener(event ->  {
					close();
				}
		);

		footer.addComponents(ok,cancel);
		footer.setExpandRatio(ok, 1);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
		return footer;
	}

}
