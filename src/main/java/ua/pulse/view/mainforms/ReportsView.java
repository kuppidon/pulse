package ua.pulse.view.mainforms;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.config.PolarAreaChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.data.PolarAreaDataset;
import com.byteowls.vaadin.chartjs.options.Hover;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.Tooltips;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.options.scale.RadialLinearScale;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.ListContainer;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.chart.AbstractChartView;
import ua.pulse.chart.ChartUtils;
import ua.pulse.component.SelectPeriodWindow;
import ua.pulse.entity.MKB10;
import ua.pulse.entity.User;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.DischargedType;
import ua.pulse.libs.ReportsDataType;
import ua.pulse.libs.VenueType;
import ua.pulse.vaadin.PulseUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@UIScope
@SpringView
public class ReportsView extends AbstractChartView {

    private DateField startDateField;
    private DateField endDateField;
    private ComboBox stateData;
    private ComboBox reportsVariant;
    private final VenueBean department;
    private final UserBean doctor;

    private ChartJs chartMKB10;
    private ChartJs chartHospitalisationDischarged;
    private ChartJs chartRatingDoctors;
    private ChartJs chartDischargedStructure;


    public ReportsView() {

        doctor = ((PulseUI) PulseUI.getCurrent()).getCurrentUser();
        department = doctor.getDepartment();

        PulseEventBus.register(this);


        VerticalLayout mainLayot = new VerticalLayout();
        mainLayot.setSpacing(true);
        mainLayot.setMargin(false);
        mainLayot.setSizeFull();

        mainLayot.addComponent(buildPanelButtom());


        Component content = buildChartsLayout();

        mainLayot.addComponent(content);
        mainLayot.setExpandRatio(content, 1);
        setSizeFull();
        addComponent(mainLayot);


    }

    private Component buildChartsLayout() {

        GridLayout content = new GridLayout(2, 2);
        content.setHideEmptyRowsAndColumns(true);
        content.newLine();
        content.setSizeFull();
        content.setSpacing(true);

        buildChartPieMKB10();
        buildChartHospitalisationDischarged();
        buildChartRatingDoctors();
        buildChartDischargedStructure();

        content.addComponent(chartMKB10);
        content.addComponent(chartHospitalisationDischarged);
        content.addComponent(chartRatingDoctors);
        content.addComponent(chartDischargedStructure);


        return content;

    }

    private Component buildPanelButtom() {

        HorizontalLayout panel = new HorizontalLayout();
        panel.setSpacing(true);
        panel.setWidth("100%");

        startDateField = new DateField("Период с: ", PulseUI.getPulseHelper().beginningMonth(new Date()));
        startDateField.setWidth("90px");
        startDateField.setIcon(null);
        startDateField.addStyleName("no_icon");
        startDateField.addStyleName("horizontal_caption");
        startDateField.setInvalidAllowed(false);
        startDateField.setResolution(Resolution.DAY);
        startDateField.setDateFormat("dd.MM.yyyy");
        startDateField.setReadOnly(true);
        startDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);

        endDateField = new DateField("по: ", PulseUI.getPulseHelper().endMonth(new Date()));
        endDateField.setWidth("90px");
        endDateField.setResolution(Resolution.DAY);
        endDateField.setDateFormat("dd.MM.yyyy");
        endDateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        endDateField.addStyleName("horizontal_caption");
        endDateField.setReadOnly(true);
        endDateField.addStyleName("no_icon");
        endDateField.setInvalidAllowed(false);


        Button btnPeriod = new Button("...");
        btnPeriod.addStyleName(ValoTheme.BUTTON_SMALL);
        btnPeriod.addClickListener(event -> {
            getUI().addWindow(new SelectPeriodWindow(startDateField.getValue(), endDateField.getValue()));
        });


        panel.addStyleName("vlayout-style-border-blue");


        reportsVariant = new ComboBox("Область данных: ");
        reportsVariant.addStyleName("horizontal_caption");
        reportsVariant.addStyleName(ValoTheme.COMBOBOX_SMALL);
        reportsVariant.setContainerDataSource(ReportsDataType.getContainer());
        reportsVariant.setTextInputAllowed(false);
        reportsVariant.setItemCaptionPropertyId("view");
        reportsVariant.setNullSelectionAllowed(false);
        reportsVariant.select(ReportsDataType.BY_DEPARTMENT);
        reportsVariant.addValueChangeListener(event -> updateSouceStateData());


        stateData = new ComboBox("Отделение: ");
        stateData.setWidth("300px");
        stateData.setTextInputAllowed(false);
        stateData.setContainerDataSource(new ListContainer<>(VenueBean.class, VenueBean.findAllVenueByTypeAndHospitalToContainer(VenueType.DEPARTMEN, doctor.getHospital())));
        stateData.setItemCaptionPropertyId("name");
        stateData.addStyleName(ValoTheme.COMBOBOX_SMALL);
        stateData.addStyleName("horizontal_caption");
        stateData.setNullSelectionAllowed(false);
        stateData.addValueChangeListener(event -> {

        });
        if (department == null)
            stateData.select(stateData.getItemIds().iterator().next());
        else
            stateData.select(department);
        stateData.setReadOnly(true);

        Button refreshButton = new Button("", FontAwesome.REFRESH);
        refreshButton.setWidth("40px");
        refreshButton.addStyleName(ValoTheme.BUTTON_SMALL);
        refreshButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        refreshButton.addClickListener(e -> {
            refreshAllCharts();
        });

        panel.addComponents(startDateField, endDateField, btnPeriod, stateData, refreshButton);
        panel.setExpandRatio(refreshButton, 1);
        panel.setComponentAlignment(stateData, Alignment.TOP_RIGHT);
        // panel.setComponentAlignment(refreshButton,Alignment.TOP_RIGHT);

        return panel;
    }

    private void updateSouceStateData() {
        if (reportsVariant.getValue().equals(ReportsDataType.BY_DEPARTMENT)) {
            stateData.setContainerDataSource(new ListContainer<>(VenueBean.class, VenueBean.findAllVenueByTypeAndHospitalToContainer(VenueType.DEPARTMEN, doctor.getHospital())));
            stateData.setItemCaptionPropertyId("name");
        } else {
            stateData.setContainerDataSource(new ListContainer<>(UserBean.class, UserBean.findAllDoctorsByDepartment(department)));
            stateData.setItemCaptionPropertyId("fullName");
        }
    }


    private void buildChartPieMKB10() {

        PieChartConfig config = new PieChartConfig();
        config
                .data()
                .extractLabelsFromDataset(true)
                .addDataset(new PieDataset());

        config
                .options()
                .hover().mode(Hover.Mode.SINGLE)
                .and()
                .responsive(true)
                .legend().position(Position.RIGHT)
                .and()
                .title()
                //.fontSize(20)
                .display(true)
                .text("Структура диагнозов")
                .and()
                .animation()
                .animateScale(true)
                .animateRotate(true)
                .and()
                .tooltips()
                .mode(Tooltips.Mode.SINGLE)

                .and()
                .done();


        chartMKB10 = new ChartJs(config);
        chartMKB10.setJsLoggingEnabled(true);

        refreshChartDataMKB10();

        chartMKB10.setSizeFull();
        chartMKB10.addStyleName("vlayout-style-border-blue");

    }

    private void refreshChartDataMKB10() {

        PieChartConfig config = (PieChartConfig) chartMKB10.getConfig();
        VenueBean dep;
        if (reportsVariant.getValue().equals(ReportsDataType.BY_DEPARTMENT)) {
            dep = (VenueBean) stateData.getValue();
        } else {
            dep = ((UserBean) stateData.getValue()).getDepartment();
        }
        List<Object[]> data = PulseUI.getCourseService().diagnosByPeriodByDepartment(dep, new java.sql.Date(startDateField.getValue().getTime()), new java.sql.Date(endDateField.getValue().getTime()));
        PieDataset dataSet = new PieDataset();
        config.data().clear();
        for (Object item : data) {
            Object[] i = (Object[]) item;
            dataSet.addLabeledData(((MKB10) i[0]).getCode(), Double.valueOf((Long) i[1]));

        }
        dataSet.randomBackgroundColors(true);
        config.data().addDataset(dataSet);

        chartMKB10.refreshData();
    }


    private void buildChartDischargedStructure() {

        PolarAreaChartConfig config = new PolarAreaChartConfig();
        config
                .data()
                .extractLabelsFromDataset(true)
                .addDataset(new PolarAreaDataset());

        config.
                options()
                .legend()
                .display(true)
                .position(Position.BOTTOM)
                .and()
                .responsive(true)
                .title()
                .display(true)
                .text("Структура выписки")
                .and()
                .scale(new RadialLinearScale().ticks().beginAtZero(true).and().reverse(false))
                .animation()
                .animateScale(true)
                .animateRotate(false)
                .and()
                .done();

        chartDischargedStructure = new ChartJs(config);

        refreshChartDischargedStructure();

        chartDischargedStructure.setJsLoggingEnabled(true);
        chartDischargedStructure.setSizeFull();
        chartDischargedStructure.setJsLoggingEnabled(true);
        chartDischargedStructure.addStyleName("vlayout-style-border-blue");

    }

    private void refreshChartDischargedStructure() {

        PolarAreaChartConfig config = (PolarAreaChartConfig) chartDischargedStructure.getConfig();
        VenueBean dep;
        if (reportsVariant.getValue().equals(ReportsDataType.BY_DEPARTMENT)) {
            dep = (VenueBean) stateData.getValue();
        } else {
            dep = ((UserBean) stateData.getValue()).getDepartment();
        }

        List<Object[]> data = PulseUI.getCourseService().getDischargedStructereByDepartmentByPeriod(dep, new java.sql.Date(startDateField.getValue().getTime()), new java.sql.Date(endDateField.getValue().getTime()));
        PolarAreaDataset dataSet = new PolarAreaDataset().backgroundColor("#AAB45C", "#46BFBD", "#FDB45C", "#949FB1", "#F7464A", "#4D5360", "#46B1BD");
        config.data().clear();
        for (Object item : data) {
            Object[] i = (Object[]) item;
            dataSet.addLabeledData(((DischargedType) i[0]).getView(), Double.valueOf((Long) i[1]));
        }

        if (data.isEmpty())
            dataSet.addData(Double.NaN);
        config.data().addDataset(dataSet);

        chartDischargedStructure.refreshData();
    }


    private void buildChartHospitalisationDischarged() {

        LineChartConfig lineConfig = new LineChartConfig();
        lineConfig
                .data()
                .addDataset(new LineDataset().label("Поступило").fill(true))
                .addDataset(new LineDataset().label("Выписано"))
                .addDataset(new LineDataset().fill(false).pointRadius(0).label("Коечных мест"))
                .and()
                .options()
                .responsive(true)
                .title()
                .display(true)
                .text("Динамика поступлений")
                .and()
                .tooltips()
                .mode(Tooltips.Mode.LABEL)
                .and()
                .hover()
                .mode(Hover.Mode.LABEL)
                .and()
                .animation()
                .and()
                .scales()
                .add(Axis.X, new CategoryScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Дни")
                        .and())
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .display(true)
                        .labelString("Количество")
                        .and())
                .and()
                .done();

        chartHospitalisationDischarged = new ChartJs(lineConfig);

        refreshChartHospitalisationDischarged();

        chartHospitalisationDischarged.setSizeFull();
        chartHospitalisationDischarged.setJsLoggingEnabled(true);
        chartHospitalisationDischarged.addStyleName("vlayout-style-border-blue");

    }

    private void refreshChartHospitalisationDischarged() {

        LineChartConfig lineConfig = (LineChartConfig) chartHospitalisationDischarged.getConfig();

        VenueBean dep;
        if (reportsVariant.getValue().equals(ReportsDataType.BY_DEPARTMENT)) {
            dep = (VenueBean) stateData.getValue();
        } else {
            dep = ((UserBean) stateData.getValue()).getDepartment();
        }

        Date dateStart = startDateField.getValue();
        Date dateEnd = endDateField.getValue();
        DateFormat df = new SimpleDateFormat("dd.MM.yy");
        List<String> labes = new ArrayList();
        while (dateStart.before(dateEnd)) {
            labes.add(df.format(dateStart));
            dateStart = Date.from(dateStart.toInstant().plus(1, DAYS));
        }
        lineConfig.data().labelsAsList(labes);

        List<Object[]> data = PulseUI.getCourseService().hospitalisationByPeriodByDepartment(dep, new java.sql.Date(startDateField.getValue().getTime()), new java.sql.Date(endDateField.getValue().getTime()));
        LineDataset dataSet = (LineDataset) lineConfig.data().getDatasets().get(0);
        List<Double> currentdata = dataSet.getData();

        if (currentdata != null) dataSet.getData().clear();

        addDataToDataSet(data, dataSet, labes, df);

        dataSet.borderColor(ChartUtils.randomColor(0.4));
        dataSet.backgroundColor(ChartUtils.randomColor(0.5));
        dataSet.pointBorderColor(ChartUtils.randomColor(0.7));
        dataSet.pointBackgroundColor(ChartUtils.randomColor(0.5));
        dataSet.pointBorderWidth(1);

        data = PulseUI.getCourseService().dischargedByPeriodByDepartment(dep, new java.sql.Date(startDateField.getValue().getTime()), new java.sql.Date(endDateField.getValue().getTime()));
        dataSet = (LineDataset) lineConfig.data().getDatasets().get(1);
        currentdata = dataSet.getData();

        if (currentdata != null) dataSet.getData().clear();

        addDataToDataSet(data, dataSet, labes, df);

        dataSet.borderColor(ChartUtils.randomColor(0.4));
        dataSet.backgroundColor(ChartUtils.randomColor(0.5));
        dataSet.pointBorderColor(ChartUtils.randomColor(0.7));
        dataSet.pointBackgroundColor(ChartUtils.randomColor(0.5));
        dataSet.pointBorderWidth(1);

        Integer capacity = VenueBean.getCapacityDepartment((VenueBean) stateData.getValue());
        dataSet = (LineDataset) lineConfig.data().getDatasets().get(2);
        currentdata = dataSet.getData();
        if (currentdata != null) dataSet.getData().clear();
        for (String label : labes) {
            dataSet.addData(Double.valueOf(capacity));
        }
        dataSet.borderColor(ChartUtils.randomColor(0.4));

        chartHospitalisationDischarged.refreshData();
    }

    private void addDataToDataSet(List<Object[]> data, LineDataset dataSet, List<String> labes, DateFormat df) {

        String lastAddDate = "";

        for (String label : labes) {
            for (Object item : data) {
                Object[] i = (Object[]) item;
                if (label.equals(df.format(i[0]))) {
                    dataSet.addData(Double.valueOf((Long) i[1]));
                    lastAddDate = label;
                    break;
                }
            }
            if (!lastAddDate.equals(label)) {
                dataSet.addData(0.0);
            }
        }
    }


    private void buildChartRatingDoctors() {

        BarChartConfig barConfig = new BarChartConfig();
        barConfig.
                data()
                .extractLabelsFromDataset(true)
                //.addDataset(new BarDataset().randomBackgroundColors(true).yAxisID("y-axis-1"))
                .and();
        barConfig.
                options()
                .legend().display(false)
                .and()
                .responsive(true)
                .hover()
                .mode(Hover.Mode.LABEL)
                .animationDuration(400)
                .and()
                .title()
                .display(true)
                .text("Рейтинг докторов")
                .and()
                .scales()
                .add(Axis.Y, new LinearScale()
                        .display(true)
                        .scaleLabel()
                        .labelString("Количество пациентов")
                        .and()
                        .ticks()
                        .beginAtZero(true)
                        .min(0)
                        .and().position(Position.LEFT))//.id("y-axis-1"))
                //.add(Axis.Y, new LinearScale().display(true).position(Position.RIGHT).id("y-axis-2").gridLines().drawOnChartArea(false).and())
                .and()
                .done();


        chartRatingDoctors = new ChartJs(barConfig);

        refreshChartRatingDoctors();

        chartRatingDoctors.setSizeFull();
        chartRatingDoctors.setJsLoggingEnabled(true);
        chartRatingDoctors.addStyleName("vlayout-style-border-blue");

    }

    private void refreshChartRatingDoctors() {

        BarChartConfig barConfig = (BarChartConfig) chartRatingDoctors.getConfig();
        barConfig.data().clear();

        VenueBean dep;
        if (reportsVariant.getValue().equals(ReportsDataType.BY_DEPARTMENT)) {
            dep = (VenueBean) stateData.getValue();
        } else {
            dep = ((UserBean) stateData.getValue()).getDepartment();
        }

        List<Object[]> data = PulseUI.getCourseService().corseByResponsibleByDepartment(dep, new java.sql.Date(startDateField.getValue().getTime()), new java.sql.Date(endDateField.getValue().getTime()));
        BarDataset dataSet = new BarDataset().randomBackgroundColors(true).label("Количество пациентов");//.yAxisID("y-axis-1");

        for (Object item : data) {
            Object[] i = (Object[]) item;
            dataSet.addLabeledData(((User) i[0]).getShortName(), Double.valueOf((Long) i[1]));
        }

        if (data.isEmpty()) {
            dataSet.addLabeledData("", Double.NaN);
        }

        barConfig.data().addDataset(dataSet);

        chartRatingDoctors.refreshData();
    }

    private void refreshAllCharts() {

        refreshChartDataMKB10();
        refreshChartHospitalisationDischarged();
        refreshChartRatingDoctors();
        refreshChartDischargedStructure();

    }

    @Subscribe
    public void selectPeriod(final PulseEvent.SelectPeriod event) {

        startDateField.setReadOnly(false);
        endDateField.setReadOnly(false);
        startDateField.setValue(PulseUI.getPulseHelper().beginDay(event.getStartDate()));
        endDateField.setValue(PulseUI.getPulseHelper().endDay(event.getEndDate()));
        startDateField.setReadOnly(true);
        endDateField.setReadOnly(true);

        refreshAllCharts();
    }

    @Override
    public Component getChart() {
        return null;
    }
}

