package ua.pulse.view.mainforms;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.ResidenceBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.vaadin.PulseUI;

import java.util.List;

@SuppressWarnings("serial")
public class PostFutureView extends VerticalLayout implements View {

    private final VenueBean department;
    private final UserBean currentUser;
    private Table table;

    public PostFutureView() {

        setSizeFull();
        addStyleName("postview");
        PulseEventBus.register(this);

        currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        department  = currentUser.getDepartment();

        addComponent(PulseUI.getPulseHelper().buildToolbar(department));

        buildTable();
        addComponent(table);
        setExpandRatio(table, 1);

    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        PulseEventBus.unregister(this);
    }

    private void buildTable(){

        table = new Table();
        table.setSizeFull();
        table.setMultiSelect(false);
        table.setSortEnabled(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        table.setSelectable(true);
        table.addStyleName(ValoTheme.TABLE_COMPACT);

        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        List<ResidenceBean> listResidence = PulseUI.getDataProvider().findAllIncomingResidenceByDepartment(department);
        BeanItemContainer container = new BeanItemContainer(CourseBean.class);
        for (ResidenceBean residenceBean:listResidence){
            CourseBean courseBean = residenceBean.getCourse();
            courseBean.setCurrentResidence(residenceBean);
            container.addBean(courseBean);
        }
        table.setContainerDataSource(container);

        table.setColumnHeader("startDate", "Начало лечения");
        table.setColumnHeader("patient", "Пациент");
        table.setColumnHeader("responsible","Лечащий врач");
        table.setColumnHeader("diagnosisMKB10","Диагноз");
        table.addGeneratedColumn("departmentFrom", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                return ((CourseBean) itemId).getDepartmentEnd().getFullView();
            }
        });
        table.setColumnHeader("departmentFrom","Откуда перевод");
        table.addGeneratedColumn("dateTransfer", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                return ((CourseBean) itemId).getCurrentResidence().getStartDate();
            }
        });
        table.setColumnHeader("dateTransfer","Дата перевода");

        table.addGeneratedColumn("btnTransfer", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Button button = new Button("Принять");
                button.addStyleName(ValoTheme.BUTTON_PRIMARY);
                button.addClickListener(event -> {
                        getUI().addWindow(((CourseBean) itemId).createTransfer());
                });
                return button;
            }
        });
        table.setColumnHeader("btnTransfer", FontAwesome.TRASH_O.getHtml());

        table.setSortContainerPropertyId("dateTransfer");
        table.setVisibleColumns(new Object[] {"dateTransfer", "patient", "departmentFrom",  "startDate", "diagnosisMKB10", "responsible","btnTransfer"});
        table.setPageLength(table.size());

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
    public void executeTransfer(final PulseEvent.ExecuteTransfer event){

//        CourseBean selectRow = (CourseBean) table.getSelectedRow();
//
//        BeanItemContainer<CourseBean> dataContainer = PulseUI.getDataProvider().getOpenCourseByDepartment(department);
//        gpcontainer = new GeneratedPropertyContainer(dataContainer);
//        gpcontainer.addGeneratedProperty("residences", getGeneratedPropertyResidens());
//        table.setContainerDataSource(gpcontainer);
//
//        Iterator<?> iterator = table.getContainerDataSource().getItemIds().iterator();
//        while (iterator.hasNext()){
//            CourseBean row = (CourseBean) iterator.next();
//            if (row.equals(selectRow)){
//                table.select(row);
//                break;
//            }
//        }
    }




    @Override
    public void enter(ViewChangeEvent event) {

    }



}
