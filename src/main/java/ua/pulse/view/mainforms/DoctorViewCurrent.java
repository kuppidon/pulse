package ua.pulse.view.mainforms;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.MessageBox;
import org.vaadin.suggestfield.SuggestField;
import ua.pulse.bean.*;
import ua.pulse.component.CreateEditAssignmentWindow;
import ua.pulse.converter.PatientSuggestionConverter;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.CourseStatus;
import ua.pulse.vaadin.PulseUI;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alex on 02.01.2017.
 */
public class DoctorViewCurrent extends VerticalLayout implements View {

    private final VenueBean department;
    private SuggestField patientField;
    private PatientBean patient;
    private CourseBean course;
    private Table table;
    private Accordion assignments;
    private final UserBean currentUser;
    private HorizontalSplitPanel mainPanel;
    private Button btnAddAssignment;
    private boolean selectFromTable;
    private Panel panelAssignments;
    private Label diagnos;
    private Button btnDischarge;
    private Button btnInfo;

    public DoctorViewCurrent(){

        setSizeFull();
        addStyleName("postview");
        PulseEventBus.register(this);

        currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        department  = currentUser.getDepartment();

        addComponent(PulseUI.getPulseHelper().buildToolbar(department));
        addComponent(buildHeaderBar());

        buildMainTableAndAssignment();
        addComponent(mainPanel);

        setExpandRatio(mainPanel,1);
    }

    private void buildMainTableAndAssignment(){

        VerticalSplitPanel vPanel = new VerticalSplitPanel();
        vPanel.setSplitPosition(100f,Unit.PIXELS,true);

        table = new Table();
        table.setMultiSelect(false);
        table.setSizeFull();
        table.setSortEnabled(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.addStyleName("small_table");
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        updateData();

        table.addGeneratedColumn("residences", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {

                CourseBean courseBean = (CourseBean) itemId;
                List<ResidenceBean> list = courseBean.getResidences();
                if (list.size() != 0){
                    MenuBar menuBar = new MenuBar();
                    menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
                    menuBar.addStyleName(ValoTheme.LABEL_LIGHT);

                    ResidenceBean currentRes = list.get(list.size() - 1);

                    if (currentRes != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
                        MenuBar.MenuItem currentValue = menuBar.addItem(currentRes.getVenue() + " c " + dateFormat.format(currentRes.getStartDate()), null);
                        Iterator<ResidenceBean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            ResidenceBean prevRes = iterator.next();
                            if (prevRes.equals(currentRes)){
                                continue;
                            }
                            currentValue.addItem(prevRes.getVenue() + " c " + dateFormat.format(prevRes.getStartDate()), FontAwesome.BED,null)
                                    .setEnabled(false);
                        }
                        return menuBar;
                    }
                }

                return "не указано";
            }
        });

        table.setColumnHeader("historyNumber", "№ истории");
        table.setColumnHeader("startDate", "Дата поступления");
        table.setColumnHeader("patient", "Пациент");
        table.setColumnHeader("diagnosisMKB10", "Диагноз");
        table.setColumnHeader("residences", "Размещение");
        table.setColumnAlignment("historyNumber", Table.Align.CENTER);
        table.setSortContainerPropertyId("patient");
        table.setPageLength(table.size());

        table.addValueChangeListener(event -> {
            CourseBean selectCourse = (CourseBean) event.getProperty().getValue();
            patient = (selectCourse == null)?null:selectCourse.getPatient();
            course  = selectCourse;
            selectFromTable = true;
            patientField.setConvertedValue(patient);
            panelAssignments.setCaption(patient==null?"Назначения":"Назначения для " + patient.getFullName());
            buildDiagnos();
            updateAssignments();
            setVisibleButtom();

            // setVisibleControlsButtom();
        });

        mainPanel = new HorizontalSplitPanel();
        mainPanel.setSizeFull();
        mainPanel.addStyleName("splitpanelexample");
        mainPanel.setSplitPosition(75f,Unit.PERCENTAGE);
        mainPanel.setMaxSplitPosition(90f,Unit.PERCENTAGE);

        vPanel.setFirstComponent(table);
        diagnos = new Label();
        diagnos.setWidth("100%");
        diagnos.setCaptionAsHtml(true);
        HorizontalLayout panelDiagnos = new HorizontalLayout();
        Label titleDiagnos = new Label(" " + FontAwesome.INFO.getHtml() + " <b>Диагноз: </b>", ContentMode.HTML);
        titleDiagnos.addStyleName(ValoTheme.LABEL_COLORED);
        titleDiagnos.setCaptionAsHtml(true);
        panelDiagnos.addComponents(titleDiagnos,diagnos);
        panelDiagnos.setExpandRatio(diagnos,1);
        vPanel.setSecondComponent(panelDiagnos);
        mainPanel.setFirstComponent(vPanel);
        buildAssignments();
        mainPanel.setSecondComponent(panelAssignments);
    }

    private void updateData() {
        List<CourseBean> listCourse = PulseUI.getDataProvider().getOpenCourseByDepartmentAndDoctor(department, currentUser);
        table.setContainerDataSource(new BeanItemContainer(CourseBean.class, listCourse));
        table.setVisibleColumns(new Object[]{"historyNumber", "startDate", "patient", "diagnosisMKB10", "residences"});

    }

    private void buildDiagnos(){
        if (course == null){
            diagnos.setCaption("");
            return;
        }
        diagnos.setCaption(course.getDescDiagnosHtml());
    }

    public void buildAssignments() {

        panelAssignments = new Panel("Назначения");
        panelAssignments.setIcon(FontAwesome.MEDKIT);
        panelAssignments.setSizeFull();
        panelAssignments.addStyleName("small_line_height");

        assignments = new Accordion();
        assignments.setSizeFull();
        panelAssignments.setContent(assignments);

    }

    private void updateAssignments(){
        PulseUI.getPulseHelper().updateAssignmentAccordion(course,assignments);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private Component buildHeaderBar(){

        HorizontalLayout header = new HorizontalLayout();
       // header.setSpacing(true);
        header.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        header.setWidth("100%");
        header.setMargin(false);
        Responsive.makeResponsive(header);

        Label patientTitle = new Label("<b>Пациент: </b>", ContentMode.HTML);
        patientTitle.setSizeUndefined();
        patientTitle.setHeight("31px");
        patientTitle.addStyleName(ValoTheme.LABEL_COLORED);
        patientTitle.addStyleName(ValoTheme.LABEL_SMALL);

        patientField = new SuggestField();
        PatientSuggestionConverter converter = new PatientSuggestionConverter();
        //patientField.setIcon(FontAwesome.MALE);
        patientField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        patientField.addStyleName("patientField");
        patientField.setSuggestionConverter(converter);
        patientField.setMinimumQueryCharacters(4);
        patientField.setEnabled(true);
        patientField.setWidth("300px");
        patientField.setPopupWidth(500);
        patientField.setInputPrompt("Выберите пациента");
        patientField.setNewItemsAllowed(false);
        patientField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                patient = (PatientBean) event.getProperty().getValue();
                if (!selectFromTable) {
                    course = PulseUI.getCourseService().getLastOpenCourseByPatient(patient);
                    updateAssignments();
                    buildDiagnos();
                    setVisibleButtom();
                   // table.unselect(table.getValue());
                }
                panelAssignments.setCaption(patient==null?"Назначения":"Назначения для " + patient.getFullName());
                selectFromTable = false;
            }
        });
        patientField.setSuggestionHandler(new SuggestField.SuggestionHandler() {
            @Override
            public List<Object> searchItems(String query) {
                return converter.handleSearchQuery(query);
            }
        });

        btnInfo = new Button();
        btnInfo.setVisible(false);
        btnInfo.setIcon(FontAwesome.INFO);
        btnInfo.addStyleName(ValoTheme.BUTTON_SMALL);
        btnInfo.addClickListener(event -> {
                getUI().addWindow(patient.editPatient());
            }
        );

        HorizontalLayout panelPatient = new HorizontalLayout(patientField,btnInfo);
        header.addComponents(patientTitle,panelPatient,buildPanelButtom());
        header.setExpandRatio(panelPatient,1);

        return header;
    }

    private Component buildPanelButtom() {

        HorizontalLayout panel = new HorizontalLayout();
        panel.setSpacing(true);

        btnDischarge = new Button("Выписать");
        btnDischarge.setVisible(false);
        btnDischarge.setIcon(FontAwesome.WHEELCHAIR);
        btnDischarge.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnDischarge.addStyleName(ValoTheme.BUTTON_SMALL);
        btnDischarge.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!course.getStatus().equals(CourseStatus.OPENED)) {
                    MessageBox.createWarning()
                            .withHtmlMessage("Курс уже закрыт")
                            .withCloseButton()
                            .open();
                    return;
                }
                getUI().addWindow(course.createDischaged());
            }
        });
        panel.addComponent(btnDischarge);

        btnAddAssignment = new Button("Создать назначения");
        btnAddAssignment.setVisible(false);
        btnAddAssignment.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnAddAssignment.addStyleName(ValoTheme.BUTTON_SMALL);
        btnAddAssignment.setIcon(FontAwesome.PLUS_CIRCLE);
        btnAddAssignment.addClickListener(event -> {
            getUI().addWindow(new CreateEditAssignmentWindow(course));
        });
        panel.addComponents(btnDischarge,btnAddAssignment);
        panel.setComponentAlignment(btnDischarge, Alignment.TOP_RIGHT);
        panel.setComponentAlignment(btnAddAssignment, Alignment.TOP_RIGHT);

        return panel;
    }

    private void setVisibleButtom(){
        btnInfo.setVisible(patient != null);
        btnAddAssignment.setVisible(course != null);
        btnDischarge.setVisible(course != null && course.getResponsible() != null && currentUser != null && course.getResponsible().equals(currentUser));
    }

    @Subscribe
    public void updateAssignments(final PulseEvent.CreateUpdateAssignment event){
        updateAssignments();
    }

    @Subscribe
    public void dischargedPatient(final PulseEvent.DischargedPatientEvent event) {
        updateData();
    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        PulseEventBus.unregister(this);
    }

}
