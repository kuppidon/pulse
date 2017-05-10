package ua.pulse.view.mainforms;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import org.vaadin.suggestfield.SuggestField;
import ua.pulse.bean.*;
import ua.pulse.component.SelectPeriodWindow;
import ua.pulse.converter.PatientSuggestionConverter;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.VariantSelect;
import ua.pulse.libs.VenueType;
import ua.pulse.vaadin.PulseUI;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("serial")
public class PostView extends VerticalLayout implements View {


    private final VenueBean department;
    private final UserBean currentUser;

    private SuggestField patientField;
    private PatientBean patient;
    private CourseBean course;
    private Button btnHospitalisation;
    private Button btnInfo;
    private boolean selectFromTable;
    private Grid grid;
    private Tree tree;
    private  int allCount = 0;
    private PopupDateField endDateField;
    private PopupDateField startDateField;
    private ComboBox selectVarian;
    private Button btnPeriod;
    private HorizontalLayout panelPatient;
    private HorizontalSplitPanel mainPanel;
    private BeanContainer<Long, CourseBean> container;
    private Grid.FooterCell titleFooter;

    public PostView() {

        setSizeFull();
        addStyleName("postview");
        PulseEventBus.register(this);

        endDateField = new PopupDateField(null, PulseUI.getPulseHelper().endDay(new Date()));

        currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        department  = currentUser.getDepartment();

        addComponent(PulseUI.getPulseHelper().buildToolbar(department));
        addComponent(buildHeaderBar());

        buildGridAndTree();
        addComponent(mainPanel);
        setExpandRatio(mainPanel, 1);

        setVisibleControlsButtom();
    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        PulseEventBus.unregister(this);
    }

    private Component buildHeaderBar(){

        HorizontalLayout header = new HorizontalLayout();
        header.setSpacing(true);
        header.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        header.setWidth("100%");
        header.setMargin(new MarginInfo(false,false,true,false));
        Responsive.makeResponsive(header);

        panelPatient = new HorizontalLayout();
        panelPatient.addStyleName("vlayout-style-border-blue");
        panelPatient.setWidth("100%");

        Label patientTitle = new Label("<b>Пациент: </b>",ContentMode.HTML);
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
        patientField.setNewItemsAllowed(true);
        patientField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                patient = (PatientBean) event.getProperty().getValue();
                if (!selectFromTable) {
                    course = PulseUI.getCourseService().getLastOpenCourseByPatient(patient);
                }
                setVisibleControlsButtom();
                selectFromTable = false;
            }
        });
        patientField.setSuggestionHandler(new SuggestField.SuggestionHandler() {
            @Override
            public List<Object> searchItems(String query) {
                return converter.handleSearchQuery(query);
            }
        });
        patientField.setNewItemsHandler(new SuggestField.NewItemsHandler() {
            @Override
            public Object addNewItem(String newItemText) {
                MessageBox.createQuestion()
                        .withHtmlMessage("В системе  нет пациента с именем <b>\"" + newItemText +"\"</b>. Хотете создать нового пациента?")
                        .withCustomButton(()-> {patientField.getUI().addWindow(PatientBean.createPatient(newItemText));},ButtonOption.caption("Создать"),ButtonOption.style(ValoTheme.BUTTON_PRIMARY))
                        .withCustomButton(()-> {patientField.setValue(newItemText); },ButtonOption.caption("Отмена"),ButtonOption.style(ValoTheme.BUTTON_DANGER))
                        .open();
                return null;
            }
        });

        panelPatient.addComponents(patientTitle, patientField);
        panelPatient.setExpandRatio(patientField,1);
        Component panelButtom = buildPanelButtom();
        header.addComponents(panelPatient,panelButtom);
        header.setExpandRatio(panelPatient,1);
        return header;
    }


    private void buildGridAndTree() {

        grid = new Grid();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setColumnReorderingAllowed(true);
        grid.setEditorEnabled(false);
        grid.addStyleName("smallgrid");
        Grid.SingleSelectionModel selectionModel = new Grid.SingleSelectionModel();
        selectionModel.setDeselectAllowed(false);
        grid.setSelectionModel(selectionModel);

        updateGrid();

        grid.setColumns("status","historyNumber", "startDate", "patient", "responsible", "diagnosisMKB10", "residences");
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

        //устанавливаем заголовки
        Grid.HeaderRow header = grid.getDefaultHeaderRow();
        header.join("status","historyNumber").setHtml(FontAwesome.H_SQUARE.getHtml() + " № истории");
        header.getCell("startDate").setHtml("<wbr>Дата поступления</wbr>");
        header.getCell("patient").setHtml("Пациент");
        header.getCell("responsible").setHtml("<wbr>Лечащий врач</wbr>");
        header.getCell("diagnosisMKB10").setHtml("Диагноз");
        header.getCell("residences").setHtml("Размещение");

       // grid.getColumn("residences").setHidable(true).setHidden(true).setHidingToggleCaption("yvu");
        // Allow column hiding
        for (Grid.Column c : grid.getColumns()) {
            c.setHidable(true);
        }

        //формируем сообщение об истории перевода
        grid.setCellDescriptionGenerator(new Grid.CellDescriptionGenerator() {
            @Override
            public String getDescription(Grid.CellReference cell) {
                String description = null;
                if (cell.getPropertyId().equals("residences")) {
                    List<ResidenceBean> list = container.getItem(cell.getItemId()).getBean().getResidences();
                    if (list.size() != 0) {
                        description = "<b>История переводов:</b>";
                        ResidenceBean currentRes = list.get(list.size() - 1);
                        if (currentRes != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
                            Iterator<ResidenceBean> iterator = list.iterator();
                            while (iterator.hasNext()) {
                                ResidenceBean prevRes = iterator.next();
                                description += "<br>" + prevRes.toString() + " c " + dateFormat.format(prevRes.getStartDate());
                            }
                            return description;
                        }
                    }
                }
                return description;
            }
        });

        grid.setDetailsGenerator(new Grid.DetailsGenerator() {
            @Override
            public Component getDetails(Grid.RowReference rowReference) {
                final CourseBean course = container.getItem(rowReference.getItemId()).getBean();

                HorizontalLayout panel = new HorizontalLayout();
                panel.setSpacing(true);
                panel.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

                Label title = new Label("<b>" + course.getPatient().toString() + ":</b> ", ContentMode.HTML);
                title.setCaptionAsHtml(true);
                title.addStyleName(ValoTheme.LABEL_COLORED);
                panel.addComponent(title);

                if (course.getStatus().equals(CourseStatus.OPENED) && course.getDepartmentEnd().equals(department)) {
                    Button btnDischarge = new Button("Выписать");
                    btnDischarge.setIcon(FontAwesome.WHEELCHAIR);
                    btnDischarge.addStyleName(ValoTheme.BUTTON_PRIMARY);
                    btnDischarge.addStyleName(ValoTheme.BUTTON_TINY);
                    btnDischarge.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            getUI().addWindow(course.createDischaged());
                        }
                    });
                    panel.addComponent(btnDischarge);

                    Button btnTransfer = new Button("Перевод");
                    btnTransfer.setIcon(FontAwesome.EXCHANGE);
                    btnTransfer.addStyleName(ValoTheme.BUTTON_TINY);
                    btnTransfer.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            getUI().addWindow(course.createTransfer());
                        }
                    });
                    panel.addComponent(btnTransfer);
                }

                Button btnEdit = new Button("Детали курса");
                btnEdit.setIcon(FontAwesome.INFO);
                btnEdit.addStyleName(ValoTheme.BUTTON_TINY);
                btnEdit.addClickListener(new ClickListener() {
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
                if (event.isDoubleClick()) {
                    Object itemId = event.getItemId();
                    disableDetails(itemId);
                    grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
                }
            }
        });

        grid.addSelectionListener(e -> {
            if (grid.getSelectedRow() != null) {
                CourseBean courseBean = container.getItem(grid.getSelectedRow()).getBean();
                disableDetails(courseBean);
                patient = (courseBean == null)?null:courseBean.getPatient();
                course  = courseBean.getStatus().equals(CourseStatus.CLOSED)?null:courseBean;
                selectFromTable = true;
                patientField.setConvertedValue(patient);
            }
        });

        grid.setFooterVisible(true);
        Grid.FooterRow footer = grid.appendFooterRow();
        titleFooter = footer.join("status", "historyNumber", "startDate", "patient", "responsible", "diagnosisMKB10", "residences");
        titleFooter.setHtml("<b>Госпитализированно:</b> " + allCount + "; (всего за период: " + container.size() + ")");

        grid.sort("startDate");

        mainPanel = new HorizontalSplitPanel();
        mainPanel.setSizeFull();
        mainPanel.addStyleName("splitpanelexample");
        mainPanel.setSplitPosition(75f,Unit.PERCENTAGE);
        mainPanel.setMaxSplitPosition(90f,Unit.PERCENTAGE);

        mainPanel.setFirstComponent(grid);
        mainPanel.setSecondComponent(buildTree());

    }

    private void updateGrid(){

        Long selectRow = (Long) grid.getSelectedRow();
        java.sql.Date startDate = new java.sql.Date(startDateField.getValue().getTime());
        java.sql.Date endDate   = new java.sql.Date(endDateField.getValue().getTime());
        allCount = 0;

        container = new BeanContainer<>(CourseBean.class);
        container.setBeanIdProperty("id");
        List<CourseBean> dataContainer = PulseUI.getDataProvider().findAllCoursesByDepartmentByPeriod(department,startDate,endDate);
        container.addAll(dataContainer);
        GeneratedPropertyContainer gpcontainer = new GeneratedPropertyContainer(container);
        gpcontainer.getItemIds().stream().filter(itemId -> gpcontainer.getItem(itemId).getItemProperty("status").getValue().equals(CourseStatus.OPENED)).forEach(itemId -> {
            if (container.getItem(itemId).getBean().getDepartmentEnd().equals(department)) {
                allCount++;
            }
        });
        gpcontainer.addGeneratedProperty("residences", getGeneratedPropertyResidens());
        grid.setContainerDataSource(gpcontainer);
        if (titleFooter != null) {
            titleFooter.setHtml("<b>Госпитализированно:</b> " + allCount + "; (всего за период: " + container.size() + ")");
        }

        if (selectRow != null  && container.containsId(selectRow)){
            grid.select(selectRow);
        }

    }

    private Component buildTree(){

        Panel panelTree = new Panel("Размещение");
        panelTree.setIcon(FontAwesome.BED);
        panelTree.setSizeFull();

        tree = new Tree();
        tree.setSizeFull();
        tree.setCaptionAsHtml(true);
        panelTree.setContent(tree);

        updateTree();

        return panelTree;
    }

    private void updateTree(){

        tree.removeAllItems();

        HashMap<VenueBean, List<PatientBean>> mapRoomPatient = new HashMap<VenueBean, List<PatientBean>>();
        List<ResidenceBean> activeRoom = PulseUI.getDataProvider().findAllActiveResidenceByDepartmentByVenuetype(department, VenueType.HOSPITAL_ROOM);
        Iterator<ResidenceBean> iterator = activeRoom.iterator();
        while (iterator.hasNext()){
            ResidenceBean nextResidence = iterator.next();
            VenueBean keyMap = nextResidence.getVenue();
            List<PatientBean> valueFromMap = mapRoomPatient.get(keyMap);
            if (valueFromMap == null){
                List<PatientBean> list = new ArrayList<PatientBean>();
                list.add(nextResidence.getCourse().getPatient());
                mapRoomPatient.put(keyMap,list);
            }
            else {
                valueFromMap.add(nextResidence.getCourse().getPatient());
            }
        }

        List<VenueBean> venueList = PulseUI.getDataProvider().getHospitalRoomByDepartment(department);
        for (VenueBean venue: venueList){
            tree.addItem(venue);
            tree.setItemIcon(venue,FontAwesome.BED);
            int capasity = (venue.getCapacity() == null)?0:venue.getCapacity();
            List<PatientBean> patients = mapRoomPatient.get(venue);
            if (patients == null || patients.size() == 0){
                tree.setItemCaption(venue,venue.getName() + " (0 из " + capasity + ")");
            }
            else {
                tree.setItemCaption(venue,venue.getName() + " (" + patients.size() + " из " + capasity + ")");
                for (PatientBean p : patients){
                    Item itemPatient = tree.addItem(p);
                    tree.setParent(p,venue);
                    tree.setChildrenAllowed(p,false);
                    tree.setItemIcon(p,FontAwesome.MALE);
                }
            }
        }

    }

    private class StatusCourseLight implements Converter {

        @Override
        public Object convertToModel(Object value, Class targetType, Locale locale) throws ConversionException {
            return value;
        }

        @Override
        public Object convertToPresentation(Object value, Class targetType, Locale locale) throws ConversionException {
            String color;
            if (value.equals(CourseStatus.OPENED) ) {
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



    //    private boolean defaultColumnsVisible() {
//        boolean result = true;
//        for (Column<CourseBean, ?> column : collapsibleColumns) {
//            if (column.isHidden() == Page.getCurrent()
//                    .getBrowserWindowWidth() < 800) {
//                result = false;
//            }
//        }
//        return result;
//    }



    private PropertyValueGenerator<String> getGeneratedPropertyResidens() {
        PropertyValueGenerator<String> generatedProrties = new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                CourseBean courseBean = (CourseBean) ((BeanItem) item).getBean();
                if (!courseBean.getDepartmentEnd().equals(department)){
                    return courseBean.getDepartmentEnd().getName();
                }
                List<ResidenceBean> list = courseBean.getResidences();
                if (list.size() != 0){
                    ResidenceBean currentRes = list.get(list.size() - 1);
                    if (currentRes != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
                        return currentRes.getVenue().toString() + " c " + dateFormat.format(currentRes.getStartDate());
                    }
                }
                return null;
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };
        return generatedProrties;
    }


    private Component buildPanelButtom() {

        HorizontalLayout panel = new HorizontalLayout();
        panel.setSpacing(true);

        btnHospitalisation = new Button("Госпитализировать");
        btnHospitalisation.setIcon(FontAwesome.H_SQUARE);
        btnHospitalisation.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnHospitalisation.addStyleName(ValoTheme.BUTTON_SMALL);
        btnHospitalisation.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                 CourseBean coursePatients = PulseUI.getCourseService().getLastOpenCourseByPatient(patient);
                if (coursePatients != null){
                    MessageBox.createWarning()
                            .withMessage("Пациент " + patient.getFullName() + " уже находится в стационаре. Повторная госпитализация запрещена!")
                            .withOkButton()
                            .open();
                    return;
                }
                getUI().addWindow(CourseBean.createHospitalisation(patient));
            }
        });
        panelPatient.addComponent(btnHospitalisation);

        btnInfo = new Button("Сведения");
        btnInfo.setIcon(FontAwesome.INFO);
        btnInfo.addStyleName(ValoTheme.BUTTON_SMALL);
        btnInfo.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getUI().addWindow(patient.editPatient());
            }
        });
        panelPatient.addComponent(btnInfo);
        panelPatient.setComponentAlignment(btnHospitalisation,Alignment.TOP_RIGHT);
        panelPatient.setComponentAlignment(btnInfo,Alignment.TOP_RIGHT);

        Label filterTitle = new Label("<b>Фильтр: </b>",ContentMode.HTML);
        filterTitle.setWidth("50px");
        filterTitle.addStyleName(ValoTheme.LABEL_COLORED);
        filterTitle.addStyleName(ValoTheme.LABEL_SMALL);

        selectVarian = new ComboBox();
        selectVarian.setContainerDataSource(VariantSelect.getContainer());
        selectVarian.setWidth("110px");
        selectVarian.setItemCaptionPropertyId("view");
        selectVarian.setTextInputAllowed(false);
        selectVarian.select(VariantSelect.AN_DATE);
        selectVarian.setNullSelectionAllowed(false);
        selectVarian.addStyleName(ValoTheme.COMBOBOX_SMALL);
        //selectVarian.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
        selectVarian.addValueChangeListener(event -> {
            startDateField.setVisible(event.getProperty().getValue().equals(VariantSelect.PERIOD));
            btnPeriod.setVisible(event.getProperty().getValue().equals(VariantSelect.PERIOD));
            setFormatEndDate();
            if (event.getProperty().getValue().equals(VariantSelect.AN_DATE)){
                updateGrid();
            }
        });

        Label filterFrom = new Label("<b>с: </b>",ContentMode.HTML);
        filterFrom.setWidth("5px");
        filterFrom.addStyleName(ValoTheme.LABEL_COLORED);
        filterFrom.addStyleName(ValoTheme.LABEL_SMALL);

        startDateField = new PopupDateField(null, PulseUI.getPulseHelper().beginDay(new Date()));
        startDateField.setVisible(false);
        startDateField.setWidth("90px");
        startDateField.setIcon(null);
        startDateField.addStyleName("no_icon");
        startDateField.setInvalidAllowed(false);
        startDateField.setResolution(Resolution.DAY);
        startDateField.setDateFormat("dd.MM.yyyy");
        startDateField.setReadOnly(true);
        startDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);

        Label filterTo = new Label("<b>по: </b>",ContentMode.HTML);
        filterTo.setWidth("5px");
        filterTo.addStyleName(ValoTheme.LABEL_COLORED);
        filterTo.addStyleName(ValoTheme.LABEL_SMALL);

        endDateField.setInvalidAllowed(false);
        endDateField.setResolution(Resolution.DAY);
        endDateField.setDateFormat("dd.MM.yyyy");
        endDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        endDateField.addValueChangeListener(event -> {
            startDateField.setReadOnly(false);
            startDateField.setValue(PulseUI.getPulseHelper().beginDay(endDateField.getValue()));
            startDateField.setReadOnly(true);
            updateGrid();
        });
        setFormatEndDate();
        //startDateField.setReadOnly(true);

        btnPeriod = new Button("...");
        btnPeriod.addStyleName(ValoTheme.BUTTON_SMALL);
        btnPeriod.setVisible(false);
        btnPeriod.addClickListener(event -> {
            getUI().addWindow(new SelectPeriodWindow(startDateField.getValue(), endDateField.getValue()));
        });

        panel.addComponents(selectVarian, startDateField, endDateField, btnPeriod);
        panel.addStyleName("vlayout-style-border-blue");

        return panel;
    }

    private void setVisibleControlsButtom(){
        btnInfo.setVisible(patient != null);
        btnHospitalisation.setVisible(patient != null && course == null);
    }

    private void setFormatEndDate(){
        if(selectVarian.getValue().equals(VariantSelect.PERIOD)){
            endDateField.setWidth("90px");
            endDateField.setIcon(null);
            endDateField.addStyleName("no_icon");
            endDateField.setReadOnly(true);
        }
        else {
            endDateField.removeStyleName("no_icon");
            endDateField.setWidth("120px");
            endDateField.setReadOnly(false);
            startDateField.setReadOnly(false);
            startDateField.setValue(PulseUI.getPulseHelper().beginDay(endDateField.getValue()));
            startDateField.setReadOnly(true);
        }
    }

    private void disableDetails(Object currentItem){
        Iterator<?> iterator = grid.getContainerDataSource().getItemIds().iterator();
        while (iterator.hasNext()){
            Object row = iterator.next();
            if (!row.equals(currentItem)){
                if (grid.isDetailsVisible(row)){
                    grid.setDetailsVisible(row, false);
                }
            }
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {}

    @Subscribe
    public void executeHospitalisation(final PulseEvent.EditCourseEven event) {

        updateGrid();

        PatientBean patientBean = event.getCourse().getPatient();
        if (patient != null && patient.getId() == patientBean.getId()){
            course = event.getCourse();
            btnHospitalisation.setVisible(false);
            if (grid.getSelectedRow() == null || !grid.getSelectedRow().equals(course.getId())){
                if (container.getItem(course) != null){
                    grid.select(course.getId());
                }
            }
        }
        updateTree();
    }

    @Subscribe
    public void dischargedPatient (final PulseEvent.DischargedPatientEvent event) {
        updateGrid();
        updateTree();
    }

    @Subscribe
    public void browserResized(final PulseEvent.BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.

//        if (defaultColumnsVisible()) {
//            for (Column<CourseBean, ?> column : collapsibleColumns) {
//                column.setHidden(
//                        Page.getCurrent().getBrowserWindowWidth() < 800);
//            }
//        }
    }

    @Subscribe
    public void executeCreatePatient(final PulseEvent.CreateEditPatientEvent event) {
        patient = event.getPatient();
        patientField.setConvertedValue(patient);
        course = PulseUI.getCourseService().getLastOpenCourseByPatient(patient);
        setVisibleControlsButtom();
    }

    @Subscribe
    public void executeTransfer(final PulseEvent.ExecuteTransfer event){
        updateGrid();
        updateTree();
    }

    @Subscribe
    public void selectPeriod(final PulseEvent.SelectPeriod event) {
        startDateField.setReadOnly(false);
        endDateField.setReadOnly(false);
        startDateField.setValue(PulseUI.getPulseHelper().beginDay(event.getStartDate()));
        endDateField.setValue(PulseUI.getPulseHelper().endDay(event.getEndDate()));
        startDateField.setReadOnly(true);
        endDateField.setReadOnly(true);
        updateGrid();
    }

}
