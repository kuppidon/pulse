package ua.pulse.component;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.PatientBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.BloodGroup;
import ua.pulse.libs.BloodRhesus;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.Sex;
import ua.pulse.service.PatientService;

@SuppressWarnings("serial")
public class CreateEditPatientWindow extends Window {

	private PatientService patientService;
	private PatientBean patientBean;

	private final  Embedded iconOpen = new Embedded(null,new ThemeResource("img/open.png"));
	private final  Embedded iconClose = new Embedded(null,new ThemeResource("img/close.png"));

	private final BeanFieldGroup<PatientBean> fieldGroup;

	@PropertyId("firstName")
	private TextField firstNameField;
	@PropertyId("lastName")
	private TextField lastNameField;
	@PropertyId("middleName")
	private TextField middleNameField;
	@PropertyId("sex")
	private OptionGroup sexField;
	@PropertyId("email")
	private TextField emailField;
	@PropertyId("address")
	private TextField addressField;
	@PropertyId("birthDate")
	private DateField birthDateField;
	@PropertyId("note")
	private TextArea noteField;
	@PropertyId("phone")
	private TextField phoneField;
	@PropertyId("bloodGroup")
	private ComboBox bloodGroupField;
	@PropertyId("bloodRhesus")
	private ComboBox bloodRhesusField;

	public CreateEditPatientWindow(PatientBean patientBeanSpring){

		this.patientService = patientBeanSpring.getPatientService();
		this.patientBean = patientBeanSpring;

		Responsive.makeResponsive(this);
		center();
		setIcon(FontAwesome.USER);
		setModal(true);
		// setCloseShortcut(KeyCode.ESCAPE, null);
		setResizable(false);
		setClosable(false);
		setWidth(50.0f, Unit.PERCENTAGE);
		if(patientBeanSpring.getId() == null){
			setCaption(" Создание пациента");
		}
		else{
			setCaption(" Редактирование пациента");
		}



		VerticalLayout content = new VerticalLayout();
		setContent(content);

		TabSheet detailsWrapper = new TabSheet();
		detailsWrapper.setSizeFull();
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		//detailsWrapper.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
		detailsWrapper.addSelectedTabChangeListener(event -> {
			//TabSheet h = event.getTabSheet().getSelectedTab();
		});
		content.addComponent(detailsWrapper);
		content.setExpandRatio(detailsWrapper, 1f);

		detailsWrapper.addTab(buildDataPatients()).setId("data");
		detailsWrapper.addTab(buildCourses()).setId("courses");
		content.addComponent(buildFooter());

		fieldGroup = new BeanFieldGroup<>(PatientBean.class);
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(patientBeanSpring);

		setDeleteComponentErrorForName(lastNameField.getValue(),firstNameField.getValue(),middleNameField.getValue());

	}

	private Component buildDataPatients() {

		VerticalLayout details = new VerticalLayout();
		details.setMargin(new MarginInfo(false, true, true, true));
		details.setSpacing(true);
		details.setIcon(FontAwesome.USER);
		details.setCaption("Данные");

		lastNameField = new TextField("Фамилия");
		lastNameField.setWidth("100%");
		lastNameField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				setDeleteComponentErrorForName(event.getText(),firstNameField.getValue(),middleNameField.getValue());
			}
		});

		firstNameField = new TextField("Имя");
		firstNameField.setWidth("100%");
		firstNameField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				setDeleteComponentErrorForName(lastNameField.getValue(),event.getText(),middleNameField.getValue());
			}
		});

		middleNameField = new TextField("Отчество");
		middleNameField.setWidth("100%");
		middleNameField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				setDeleteComponentErrorForName(lastNameField.getValue(), firstNameField.getValue(),event.getText());
			}
		});

		details.addComponents(lastNameField,firstNameField,middleNameField);

		HorizontalLayout group = new HorizontalLayout();
		group.setSpacing(true);

		birthDateField = new DateField("Дата рождения");
		birthDateField.setRequired(true);
		birthDateField.setResolution(Resolution.DAY);
		birthDateField.setDateFormat("dd.MM.yyyy");
		birthDateField.setComponentError(new UserError("Укажите дату рождения"));
		birthDateField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(event.getProperty().getValue() == null && !birthDateField.isRequired()){
					birthDateField.setComponentError(new UserError("Укажите дату рождения"));
					birthDateField.setRequired(true);
				}
				else{
					birthDateField.setComponentError(null);
					birthDateField.setRequired(false);
				}
			}
		});

		sexField = new OptionGroup("Пол", Sex.getContainer());
		sexField.setItemCaptionPropertyId("view");
		sexField.addStyleName("horizontal");
		sexField.setIcon(FontAwesome.VENUS_MARS);
		sexField.addStyleName(ValoTheme.LABEL_H4);
		sexField.addStyleName(ValoTheme.LABEL_COLORED);
		group.addComponents(birthDateField, new Label(), sexField);
		details.addComponent(group);

		VerticalLayout contactInfoGroup = new VerticalLayout();

		Label section = new Label("Контактная информация");
		section.addStyleName(ValoTheme.LABEL_H4);
		section.addStyleName(ValoTheme.LABEL_COLORED);
		contactInfoGroup.addComponent(section);

		phoneField = new TextField("Телефон");
		phoneField.setWidth("100%");
		phoneField.setInputPrompt("38(XXX)XXXXXXX");
		contactInfoGroup.addComponent(phoneField);

		addressField = new TextField("Адрес");
		addressField.setWidth("100%");
		contactInfoGroup.addComponent(addressField);

		emailField = new TextField("Email");
		emailField.setWidth("100%");
		contactInfoGroup.addComponent(emailField);

		details.addComponent(contactInfoGroup);

		VerticalLayout otherInfoGroup = new VerticalLayout();
		section = new Label("Дополнительная информация");
		section.addStyleName(ValoTheme.LABEL_H4);
		section.addStyleName(ValoTheme.LABEL_COLORED);
		otherInfoGroup.addComponent(section);

		HorizontalLayout detalesInfoGroup = new HorizontalLayout();
		detalesInfoGroup.setSpacing(true);

		noteField = new TextArea("Комментарий");
		noteField.setWidth("100%");
		noteField.setIcon(FontAwesome.INFO);
		noteField.setRows(4);

		VerticalLayout otherMedInfoGroup = new VerticalLayout();
		otherMedInfoGroup.setSpacing(true);
		otherMedInfoGroup.setWidthUndefined();

		bloodGroupField = new ComboBox("Группа крови",BloodGroup.getContainer());
		bloodGroupField.setStyleName(ValoTheme.COMBOBOX_SMALL);
		bloodGroupField.setItemCaptionPropertyId("name");
		bloodGroupField.setTextInputAllowed(false);
		bloodGroupField.setNullSelectionAllowed(false);
		bloodGroupField.setWidth("85px");

		bloodRhesusField = new ComboBox("Rезус-фактор",BloodRhesus.getContainer());
		bloodRhesusField.setStyleName(ValoTheme.COMBOBOX_SMALL);
		bloodRhesusField.setItemCaptionPropertyId("view");
		bloodRhesusField.setNullSelectionAllowed(false);
		bloodRhesusField.setTextInputAllowed(false);
		bloodRhesusField.setWidth("85px");

		otherMedInfoGroup.addComponents(bloodGroupField,bloodRhesusField);

		detalesInfoGroup.addComponents(noteField,otherMedInfoGroup);
		detalesInfoGroup.setWidth("100%");
		detalesInfoGroup.setExpandRatio(noteField, 1);

		otherInfoGroup.addComponent(detalesInfoGroup);
		details.addComponent(otherInfoGroup);
		return details;
	}

	private Component buildCourses(){

		VerticalLayout details = new VerticalLayout();
		details.setMargin(new MarginInfo(false, true, true, true));
		details.setSpacing(true);
		details.setIcon(FontAwesome.PLUS_SQUARE);
		details.setCaption("Курсы");

		Table table = new Table();
		table.setMultiSelect(false);
		table.setSelectable(true);
		table.setSizeFull();
		table.setSortEnabled(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.addStyleName("small_table");


		BeanContainer beanContainer = new BeanContainer(CourseBean.class);
		beanContainer.setBeanIdProperty("id");
		beanContainer.addAll(CourseBean.findAllByPatient(patientBean));
		table.setContainerDataSource(beanContainer);


		table.addGeneratedColumn("department", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				BeanItem beanItem = (BeanItem) source.getItem(itemId);
				return ((CourseBean) beanItem.getBean()).getDepartmentEnd().getFullView();
			}
		});

		table.addGeneratedColumn("status", new Table.ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				BeanItem beanItem = (BeanItem) source.getItem(itemId);
				CourseBean courseBean = (CourseBean) beanItem.getBean();
				return courseBean.getStatus().equals(CourseStatus.OPENED)?iconOpen:new Embedded(null,new ThemeResource("img/close.png"));
			}
		});
		table.setColumnAlignment("status", Table.Align.CENTER);

		table.setColumnHeader("status", "Статус");
		table.setColumnHeader("startDate", "Дата поступления");
		table.setColumnHeader("endDate", "Дата выписки");
		table.setColumnHeader("diagnosisMKB10","Диагноз");
		table.setColumnHeader("responsible", "Лечащий доктор");
		table.setColumnHeader("department", "Отделение");

		table.setVisibleColumns(new Object[] {"status", "startDate", "endDate","diagnosisMKB10", "responsible", "department"});
		table.setSortContainerPropertyId("endDate");
		table.addItemClickListener(event -> {
			if (event.isDoubleClick()){
				getUI().addWindow(CourseBean.editCourseById((Long) event.getItemId()));
			}
		});

		details.addComponent(table);
		details.setExpandRatio(table,1);
		return details;
	}

	private void setDeleteComponentErrorForName(String lastName, String firstName, String middleName){
		if(lastName.isEmpty() && firstName.isEmpty() && middleName.isEmpty()){
			middleNameField.setComponentError(new UserError("Укажите отчество"));
			middleNameField.setRequired(true);
			firstNameField.setRequired(true);
			firstNameField.setComponentError(new UserError("Укажите имя"));
			lastNameField.setRequired(true);
			lastNameField.setComponentError(new UserError("Укажите фамилию"));
		}
		else if(middleNameField.isRequired() || firstNameField.isRequired() || lastNameField.isRequired()){
			firstNameField.setComponentError(null);
			firstNameField.setRequired(false);
			middleNameField.setComponentError(null);
			middleNameField.setRequired(false);
			lastNameField.setComponentError(null);
			lastNameField.setRequired(false);
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
					PatientBean patientBean = fieldGroup.getItemDataSource().getBean();
					boolean create = patientBean.isNew();
					patientBean.fixName();
					patientBean = patientService.saveAndFlush(patientBean);
					Notification success = new Notification(
							"Пациент \"" + patientBean.getFullName() + "\" успешно сохранен");
					success.setDelayMsec(2000);
					success.setStyleName("bar success small");
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());
					close();

					//оповещаем всех слущателей о создании нового или редактировании пациента
					PulseEventBus.post(new PulseEvent.CreateEditPatientEvent(patientBean,create));

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
