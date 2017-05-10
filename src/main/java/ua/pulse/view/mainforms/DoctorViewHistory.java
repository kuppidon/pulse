package ua.pulse.view.mainforms;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.PatientBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.component.SelectPeriodWindow;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.CourseStatus;
import ua.pulse.vaadin.PulseUI;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alex on 02.01.2017.
 */
public class DoctorViewHistory extends VerticalLayout implements View {

    private PopupDateField endDate;
    private PopupDateField startDate;
    private VenueBean department;
    private PatientBean patient;
    private CourseBean course;
    private Grid grid;
    private Accordion assignments;
    private final UserBean currentUser;
    private HorizontalSplitPanel mainPanel;
    private Panel panelAssignments;
    private Label diagnos;
    private Object detailItem;
    private BeanItemContainer container;

    public DoctorViewHistory(){

        setSizeFull();
        addStyleName("postview");
        PulseEventBus.register(this);
        startDate = new PopupDateField(null, PulseUI.getPulseHelper().beginningMonth(new java.util.Date()));
        endDate = new PopupDateField(null, new Date());

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

        grid = new Grid();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setEditorEnabled(false);
        grid.addStyleName("smallgrid");
        Grid.SingleSelectionModel selectionModel = new Grid.SingleSelectionModel();
        selectionModel.setDeselectAllowed(false);
        grid.setSelectionModel(selectionModel);
        grid.setColumnReorderingAllowed(true);

        updateDateInPeriod();

        Grid.HeaderRow header = grid.getDefaultHeaderRow();
        header.join("status","historyNumber").setHtml(FontAwesome.H_SQUARE.getHtml() + " № истории");
        header.getCell("startDate").setHtml(FontAwesome.CALENDAR_PLUS_O.getHtml() + " Дата поступления");
        header.getCell("endDate").setHtml(FontAwesome.CALENDAR_MINUS_O.getHtml() + " Дата выписки");
        header.getCell("patient").setHtml(FontAwesome.USER.getHtml() + " Пациент");
        header.getCell("diagnosisMKB10").setHtml("Диагноз");

        for (Grid.Column c : grid.getColumns()) {
            c.setHidable(true);
        }
        grid.getColumn("patient").setExpandRatio(1);
        grid.getColumn("diagnosisMKB10").setHidden(true);
        grid.getColumn("endDate").setHidden(true);

        grid.setDetailsGenerator(new Grid.DetailsGenerator() {
            @Override
            public Component getDetails(Grid.RowReference rowReference) {
                final CourseBean course = (CourseBean) rowReference.getItemId();

                HorizontalLayout panel = new HorizontalLayout();
                panel.setSpacing(true);
                panel.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
                Label title = new Label("<b>" + course.getPatient().toString() + ":</b> ", ContentMode.HTML);
                title.setCaptionAsHtml(true);
                title.addStyleName(ValoTheme.LABEL_COLORED);
                panel.addComponent(title);

                Button btnInfo = new Button("Карта пациента");
                btnInfo.setIcon(FontAwesome.NEWSPAPER_O);
                btnInfo.addStyleName(ValoTheme.BUTTON_TINY);
                btnInfo.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getUI().addWindow(patient.editPatient());
                    }
                });
                panel.addComponent(btnInfo);

                Button btnEdit = new Button("Детали курса");
                btnEdit.setIcon(FontAwesome.INFO);
                btnEdit.addStyleName(ValoTheme.BUTTON_TINY);
                btnEdit.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getUI().addWindow(course.editCourse());
                    }
                });
                panel.addComponent(btnEdit);
                return panel;
            }
        });

        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                Object itemId = event.getItemId();
                if (event.isDoubleClick()) {
                    if ( detailItem != null && !detailItem.equals(itemId)) {
                        grid.setDetailsVisible(detailItem, !grid.isDetailsVisible(detailItem));
                    }
                    grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
                    detailItem = itemId;
                }
                course = (CourseBean) itemId;
                patient = (course == null)?null:course.getPatient();
                panelAssignments.setCaption(patient==null?"Назначения":"Назначения для " + patient.getFullName());
                buildDiagnos();
                updateAssignments();
            }
        });

        //фильтры
        PulseUI.getPulseHelper().setRowFilters(grid,container);

        mainPanel = new HorizontalSplitPanel();
        mainPanel.setSizeFull();
        mainPanel.addStyleName("splitpanelexample");
        mainPanel.setSplitPosition(75f,Unit.PERCENTAGE);
        mainPanel.setMaxSplitPosition(90f,Unit.PERCENTAGE);

        vPanel.setFirstComponent(grid);
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

        panelAssignments = new Panel("Назначения");
        panelAssignments.setIcon(FontAwesome.MEDKIT);
        panelAssignments.setSizeFull();
        panelAssignments.addStyleName("small_line_height");

        assignments = new Accordion();
        assignments.setSizeFull();
        panelAssignments.setContent(assignments);
        mainPanel.setSecondComponent(panelAssignments);
    }

    private void buildDiagnos(){
        if (course == null){
            diagnos.setCaption("");
            return;
        }
        diagnos.setCaption(course.getDescDiagnosHtml());
    }

    private void updateAssignments(){
       PulseUI.getPulseHelper().updateAssignmentAccordion(course,assignments);
    }

    private void updateDateInPeriod(){
        List<CourseBean> listCourse = PulseUI.getDataProvider().getCoursesByUserFromPeriod(
                new java.sql.Date(startDate.getValue().getTime()),
                new java.sql.Date(endDate.getValue().getTime()),
                currentUser);
        container = new BeanItemContainer(CourseBean.class, listCourse);
        grid.setContainerDataSource(container);
        grid.setColumns("status","historyNumber", "startDate", "endDate", "patient","diagnosisMKB10");
        grid.getColumn("status").setConverter(new StatusCourseLight()).setRenderer(new HtmlRenderer());
        grid.getColumn("historyNumber").setMaximumWidth(110);
        grid.setCellStyleGenerator(new Grid.CellStyleGenerator() {
            @Override
            public String getStyle(Grid.CellReference cellReference) {
                if ("historyNumber".equals(cellReference.getPropertyId())) {
                    return "centeralign";
                }
                return null;
            }
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private class StatusCourseLight implements Converter {

        @Override
        public Object convertToModel(Object value, Class targetType, Locale locale) throws ConversionException {
            return value;
        }

        @Override
        public Object convertToPresentation(Object value, Class targetType, Locale locale) throws ConversionException {
            String color;
            if (value.equals(CourseStatus.OPENED)) {
                color = "#2dd085";
            } else {
                color = "#f54993";
            }
           return FontAwesome.CIRCLE.getHtml().replace("style=\"", "style=\"color: " + color + ";");
        }

        @Override
        public Class getModelType() {
            return CourseStatus.class;
        }

        @Override
        public Class getPresentationType() {
            return String.class;
        }
    }

    private Component buildHeaderBar(){

        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        header.setMargin(false);
        Responsive.makeResponsive(header);

        Label filterFrom = new Label("<b>Период: с </b>",ContentMode.HTML);
        //filterFrom.setWidth("5px");
        filterFrom.addStyleName(ValoTheme.LABEL_COLORED);
        filterFrom.addStyleName(ValoTheme.LABEL_SMALL);

        startDate = new PopupDateField(null, new Date());
        startDate.setWidth("90px");
        startDate.setIcon(null);
        startDate.addStyleName("no_icon");
        startDate.setInvalidAllowed(false);
        startDate.setResolution(Resolution.DAY);
        startDate.setDateFormat("dd.MM.yyyy");
        startDate.setReadOnly(true);
        startDate.addStyleName(ValoTheme.DATEFIELD_SMALL);

        Label filterTo = new Label("<b>по:</b> ",ContentMode.HTML);
        filterTo.addStyleName(ValoTheme.LABEL_COLORED);
        filterTo.addStyleName(ValoTheme.LABEL_SMALL);

        endDate.setInvalidAllowed(false);
        endDate.setResolution(Resolution.DAY);
        endDate.setDateFormat("dd.MM.yyyy");
        endDate.addStyleName(ValoTheme.DATEFIELD_SMALL);
        endDate.setWidth("90px");
        endDate.setIcon(null);
        endDate.addStyleName("no_icon");
        endDate.setReadOnly(true);

        Button btnPeriod = new Button("...");
        btnPeriod.addStyleName(ValoTheme.BUTTON_SMALL);
        btnPeriod.addClickListener(event -> {
            getUI().addWindow(new SelectPeriodWindow(startDate.getValue(),endDate.getValue()));
        });

        header.addComponents(filterFrom,startDate,filterTo,endDate, btnPeriod);
        HorizontalLayout panel = new HorizontalLayout();
        panel.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        panel.setWidth("100%");
        panel.addComponent(header);

        return panel;
    }

    @Subscribe
    public void updateAssignments(final PulseEvent.CreateUpdateAssignment event){
        updateAssignments();
    }

    @Subscribe
    public void selectPeriod(final PulseEvent.SelectPeriod event) {
        startDate.setReadOnly(false);
        endDate.setReadOnly(false);
        startDate.setValue(event.getStartDate());
        endDate.setValue(event.getEndDate());
        startDate.setReadOnly(true);
        endDate.setReadOnly(true);
        updateDateInPeriod();
    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        PulseEventBus.unregister(this);
    }
}
