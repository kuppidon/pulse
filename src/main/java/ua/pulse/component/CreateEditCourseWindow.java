package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.ListContainer;
import ua.pulse.bean.*;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.DepartureTypes;
import ua.pulse.libs.HospitalizationType;
import ua.pulse.libs.VenueType;
import ua.pulse.vaadin.PulseUI;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class CreateEditCourseWindow extends Window {


	private VenueBean department;
	private UserBean currentUser;

	private final BeanFieldGroup<CourseBean> fieldGroup;

	@PropertyId("hospitalizationType")
	private OptionGroup hospitalizationTypeField;
	@PropertyId("arrivalType")
	private ComboBox arrivalTypeField;
	@PropertyId("forwarded")
	private TextField forwardedField;
	@PropertyId("startDate")
	private DateField startDateField;
	@PropertyId("endDate")
	private DateField endDateField;
	@PropertyId("responsible")
	private ComboBox responsibleField;
	@PropertyId("historyNumber")
	private TextField historyNumberField;
	@PropertyId("diagnosisGuide")
	private TextArea diagnosisGuideField;
	@PropertyId("diagnosisStart")
	private TextArea diagnosisStartField;
	@PropertyId("diagnosisClinic")
	private TextArea diagnosisClinicField;
	@PropertyId("diagnosisEnd")
	private TextArea diagnosisEndField;
	@PropertyId("departmentEnd")
	private ComboBox departmentEnd;

	private ComboBox MKB10CodeField;
	private CourseBean courseBean;
	@PropertyId("diagnosisMKB10")
	private ComboBox MKB10NameField;
	private ComboBox venueField;
	private VerticalLayout groupForward;
	private TextField nameSpecialisation;
	private Accordion assignments;

	public CreateEditCourseWindow(CourseBean course) {

		this.courseBean = course;

		currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
		department  = currentUser.getDepartment();

		Responsive.makeResponsive(this);
		center();
		setIcon(FontAwesome.BED);
		setModal(true);
		setResizable(true);
		setClosable(false);
		setWidth(80.0f, Unit.PERCENTAGE);
		if (course.getId() == null) {
			setCaption(" Госпитализация пациента");
		} else {
			setCaption(" Редактирование курса");
		}

		VerticalLayout content = new VerticalLayout();
		content.setMargin(new MarginInfo(true, false, false, false));
		//content.setSizeFull();
		setContent(content);

		TabSheet detailsWrapper = new TabSheet();
		detailsWrapper.setSizeFull();
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		detailsWrapper.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		detailsWrapper.addSelectedTabChangeListener(event -> {
			//TabSheet h = event.getTabSheet().getSelectedTab();
		});
		content.addComponent(detailsWrapper);
		content.setExpandRatio(detailsWrapper, 1f);

		detailsWrapper.addTab(buildDetailsCourse());
		detailsWrapper.addTab(buildAssignments());
		if (assignments.getComponentCount() == 0){
			detailsWrapper.setTabsVisible(false);
		}

		content.addComponent(buildFooter());
		content.setExpandRatio(detailsWrapper,1);

		fieldGroup = new BeanFieldGroup<CourseBean>(CourseBean.class);
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(course);

		if (course.getId() != null){

			List<ResidenceBean> listResidence = course.getResidences();
			setSourceForVenueFieldAndDoctorField();

			if (listResidence != null && listResidence.size() > 0){
				VenueBean currentResidence = listResidence.get(listResidence.size() - 1).getVenue();
				Iterator<?> iterator = venueField.getItemIds().iterator();
				while (iterator.hasNext()){
					VenueBean nextUnit = (VenueBean) iterator.next();
					if (nextUnit.getId() == currentResidence.getId()){
						currentResidence = nextUnit;
						break;
					}
				}
				venueField.setValue(currentResidence);
				venueField.setReadOnly(true);
				venueField.setTextInputAllowed(false);
			}
		}
		else {
			if (department != null){
				departmentEnd.setValue(department);
				setSourceForVenueFieldAndDoctorField();
			}
			else{
				departmentEnd.addValueChangeListener(event -> {
					setSourceForVenueFieldAndDoctorField();
				});
			}
		}

		departmentEnd.setReadOnly(true);

		setValuesToForm(course);

		setVisibleForwardData(course.getArrivalType());

		addWindowModeChangeListener(e -> {
			if(e.getWindowMode().equals(WindowMode.MAXIMIZED)) {
				content.setSizeFull();
			}
			else {
				content.setSizeUndefined();
				content.setWidth("100%");
			}
		});
	}

	/**
	 * тело формы госпитализации
	 * 
	 * @return
	 */
	private Component buildDetailsCourse() {

		VerticalLayout details = new VerticalLayout();
		details.setMargin(new MarginInfo(false,true,false,true));
		details.setSpacing(true);
		details.setCaption("Детали курса");
		details.setIcon(FontAwesome.INFO);

		TextField patientField = new TextField("Пациент");
		patientField.setWidth("100%");
		patientField.setIcon(FontAwesome.MALE);
		patientField.setConvertedValue(courseBean.getPatient().getFullName());
		details.addComponent(patientField);

		arrivalTypeField = new ComboBox("Прибытие");
		arrivalTypeField.setContainerDataSource(DepartureTypes.getContainer());
		arrivalTypeField.setItemCaptionPropertyId("view");
		arrivalTypeField.setInputPrompt("Тип прибытия");
		arrivalTypeField.setInvalidAllowed(false);
		arrivalTypeField.setNullSelectionAllowed(false);
		arrivalTypeField.setRequired(true);
		arrivalTypeField.setIcon(FontAwesome.AMBULANCE);
		arrivalTypeField.setWidth("100%");
		arrivalTypeField.addValueChangeListener(event -> {
					setVisibleForwardData((DepartureTypes) event.getProperty().getValue());
				}
		);

		hospitalizationTypeField = new OptionGroup("Тип госпитализации");
		hospitalizationTypeField.setContainerDataSource(HospitalizationType.getContainer());
		hospitalizationTypeField.setItemCaptionPropertyId("view");
		hospitalizationTypeField.addStyleName("horizontal");

		HorizontalLayout groupArrivalHospitalizationType = new HorizontalLayout();
		groupArrivalHospitalizationType.setWidth("100%");
		groupArrivalHospitalizationType.setSpacing(true);
		groupArrivalHospitalizationType.addComponents(arrivalTypeField, hospitalizationTypeField);
		groupArrivalHospitalizationType.setExpandRatio(arrivalTypeField, 1);
		details.addComponent(groupArrivalHospitalizationType);

		Label lableDataForward = new Label("Данные направившего ЛПУ");
		lableDataForward.addStyleName(ValoTheme.LABEL_COLORED);

		forwardedField = new TextField("Кем направлен");
		forwardedField.setWidth("100%");

		diagnosisGuideField = new TextArea("Диагноз направившего ЛПУ");
		diagnosisGuideField.setWidth("100%");
		diagnosisGuideField.setRows(2);
		groupForward = new VerticalLayout(lableDataForward, forwardedField, diagnosisGuideField);
		details.addComponent(groupForward);


		/**
		 * dates
		 */
		startDateField = new DateField("Дата и время госпитализации");
		startDateField.setRequired(true);
		startDateField.setResolution(Resolution.MINUTE);
		startDateField.setIcon(FontAwesome.CALENDAR);

		endDateField = new DateField("Дата и время выписки");
		endDateField.setResolution(Resolution.MINUTE);
		endDateField.setIcon(FontAwesome.CALENDAR);
		endDateField.setReadOnly(true);
		endDateField.setEnabled(false);
		if (courseBean.getStatus().equals(CourseStatus.OPENED)){
			endDateField.setReadOnly(true);
			endDateField.setEnabled(false);
			endDateField.addStyleName("no_icon");
		}

		HorizontalLayout groupDates = new HorizontalLayout();
		groupDates.setSpacing(true);
		groupDates.addComponents(startDateField,endDateField);
		details.addComponent(groupDates);

		historyNumberField = new TextField("Номер истории");
		historyNumberField.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		historyNumberField.setIcon(FontAwesome.H_SQUARE);
		historyNumberField.setWidth("100px");


		departmentEnd = new ComboBox("Отделение");
		departmentEnd.setTextInputAllowed(false);
		departmentEnd.setContainerDataSource(new BeanItemContainer<>(VenueBean.class, VenueBean.findAllDepartment()));
		departmentEnd.setItemCaptionPropertyId("name");
		departmentEnd.setWidth("100%");

		venueField = new ComboBox("Размещение");
		venueField.setIcon(FontAwesome.HOTEL);
		venueField.setRequired(true);
		venueField.setImmediate(true);
		venueField.setInvalidAllowed(false);
		venueField.setNewItemsAllowed(false);
		venueField.setNullSelectionAllowed(false);


		HorizontalLayout groupResidentHistory = new HorizontalLayout(historyNumberField, departmentEnd, venueField);
		groupResidentHistory.setWidth("100%");
		groupResidentHistory.setSpacing(true);
		groupResidentHistory.setExpandRatio(departmentEnd,1);
		details.addComponent(groupResidentHistory);

		responsibleField = new ComboBox("Лечащий доктор");
		responsibleField.setIcon(FontAwesome.USER_MD);
		responsibleField.setInputPrompt("Лечащий доктор не указан");
		responsibleField.setNullSelectionAllowed(false);
		responsibleField.setNewItemsAllowed(false);
		responsibleField.setWidth("100%");
		responsibleField.addValueChangeListener(event -> {
			UserBean value = (UserBean) event.getProperty().getValue();
			if (value != null) {
				nameSpecialisation.setReadOnly(false);
				nameSpecialisation.setValue(value.getSpecialization().getName());
				nameSpecialisation.setReadOnly(true);
			}
		});

		nameSpecialisation = new TextField("Специализация");
		nameSpecialisation.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		nameSpecialisation.setSizeUndefined();
		nameSpecialisation.setReadOnly(true);

		HorizontalLayout groupresponsible = new HorizontalLayout();
		groupresponsible.setWidth("100%");
		groupresponsible.setSpacing(true);
		groupresponsible.addComponents(responsibleField,nameSpecialisation);
		groupresponsible.setExpandRatio(responsibleField,1);
		details.addComponent(groupresponsible);


		/**
		 * начальный диагноз
		 */
		diagnosisStartField = new TextArea("Диагноз при госпитализации");
		diagnosisStartField.setWidth("100%");
		diagnosisStartField.setRows(3);
		details.addComponent(diagnosisStartField);


		/**
		 * MKB10
		 */
		ListContainer<MKB10Bean> sourceMKB10 = new ListContainer<>(MKB10Bean.class, MKB10Bean.findAll());

		MKB10NameField = new ComboBox();
		MKB10NameField.setContainerDataSource(sourceMKB10);
		MKB10NameField.setItemCaptionPropertyId("name");
		MKB10NameField.setWidth("100%");
		MKB10NameField.setInputPrompt("Наименование МКБ10");
		MKB10NameField.setTextInputAllowed(true);
		MKB10NameField.setInvalidAllowed(false);
		MKB10NameField.setNullSelectionAllowed(false);
		MKB10NameField.setNewItemsAllowed(false);
		MKB10NameField.addValueChangeListener(event -> {
			MKB10CodeField.select(event.getProperty().getValue());
				}
		);
		MKB10NameField.addStyleName(ValoTheme.COMBOBOX_SMALL);

		MKB10CodeField = new ComboBox();
		MKB10CodeField.setContainerDataSource(sourceMKB10);
		MKB10CodeField.setItemCaptionPropertyId("code");
		MKB10CodeField.setWidth("100px");
		MKB10CodeField.setInputPrompt("Код МКБ10");
		MKB10CodeField.setTextInputAllowed(true);
		MKB10CodeField.setInvalidAllowed(false);
		MKB10CodeField.setNullSelectionAllowed(false);
		MKB10CodeField.setNewItemsAllowed(false);
		MKB10CodeField.addValueChangeListener(event -> {
					MKB10NameField.select(event.getProperty().getValue());
				}
		);
		MKB10CodeField.addStyleName(ValoTheme.COMBOBOX_SMALL);

		HorizontalLayout groupMKB10 = new HorizontalLayout();
		groupMKB10.setWidth("100%");
		Label sectionMKB10 = new Label("Диагноз по МКБ10");
		sectionMKB10.addStyleName(ValoTheme.LABEL_COLORED);
		groupMKB10.setSpacing(false);
		groupMKB10.addComponents(sectionMKB10,MKB10CodeField,MKB10NameField);
		groupMKB10.setExpandRatio(MKB10NameField, 1);
		details.addComponents(groupMKB10);

		/**
		 * заключительный диагноз
		 */
		diagnosisEndField = new TextArea("Диагноз клинический заключительный");
		diagnosisEndField.setWidth("100%");
		diagnosisEndField.setRows(3);
		details.addComponent(diagnosisEndField);

		return details;
	}

	public Component buildAssignments() {

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setIcon(FontAwesome.MEDKIT);
		verticalLayout.setCaption("Назначения");

		Panel panelAssignments = new Panel("Назначения");
		panelAssignments.setIcon(FontAwesome.MEDKIT);
		panelAssignments.setSizeFull();
		panelAssignments.addStyleName("small_line_height");

		assignments = new Accordion();
		//assignments.setSizeFull();
		panelAssignments.setContent(assignments);

		verticalLayout.addComponent(panelAssignments);
		verticalLayout.setExpandRatio(panelAssignments,1);


		PulseUI.getPulseHelper().updateAssignmentAccordion(courseBean,assignments);

		return panelAssignments;

	}


	private void setVisibleForwardData(DepartureTypes arrivalTypes){
		boolean visible = (arrivalTypes != null && arrivalTypes.equals(DepartureTypes.OTHER_DEPARTMEN));
		groupForward.setVisible(visible);
	}

	private void setSourceForVenueFieldAndDoctorField(){

		venueField.setContainerDataSource(new BeanItemContainer<>(VenueBean.class,VenueBean.findAllVenueByTypeAndOwnerToContainer((VenueBean) departmentEnd.getValue(), VenueType.HOSPITAL_ROOM)));
		venueField.setItemCaptionPropertyId("name");

		responsibleField.setContainerDataSource(new BeanItemContainer<>(UserBean.class,UserBean.findAllDoctorsByDepartment((VenueBean)  departmentEnd.getValue())));
		responsibleField.setItemCaptionPropertyId("fullName");
	}

	private void setValuesToForm(CourseBean course){

		diagnosisEndField.setVisible(course.getId() != null);
		endDateField.setVisible(course.getId() != null);

		if(course.getResponsible() != null){
			nameSpecialisation.setValue(course.getResponsible().getSpecialization().getName());
		}

		MKB10Bean mkb10 = course.getDiagnosisMKB10();
		if(mkb10 != null){
			MKB10CodeField.setValue(MKB10NameField.getValue());
			//MKB10NameField.select(MKB10CodeField.getValue());
//			if (1==1){
//				MKB10NameField.select(MKB10CodeField.getValue());
//			}
		}
	}

	/**
	 * панель кнопок
	 * @return
	 */
	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(courseBean.getId()==null?"Госпитализировать":"Редактировать");
		ok.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		ok.addClickListener(even -> {
				try {
					fieldGroup.commit();
					CourseBean courseBean = fieldGroup.getItemDataSource().getBean();
					if (courseBean.getId()==null)
						courseBean = courseBean.openCourse((VenueBean) venueField.getValue());
					else
						courseBean = courseBean.saveAndFlush();
					Notification success = new Notification(
					"Пациент \"" + courseBean.getPatient().getFullName() + "\" успешно	 госпитализирован");
					success.setDelayMsec(2000);
					success.setStyleName("bar success small");
					success.setPosition(Position.BOTTOM_CENTER);
					success.show(Page.getCurrent());
					close();
					//оповещаем всех слущателей о создании или редактировании госпитализации
					PulseEventBus.post(new PulseEvent.EditCourseEven(courseBean));
				} catch (CommitException e) {
					Notification.show("Ошибка госпитализации", e.getMessage(), Type.ERROR_MESSAGE);
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

		footer.addComponents(ok, cancel);
		footer.setExpandRatio(ok, 1);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
		return footer;
	}

}
