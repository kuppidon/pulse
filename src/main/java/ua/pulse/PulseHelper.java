package ua.pulse;

import com.vaadin.data.util.AbstractBeanContainer;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.AssignmentBean;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.vaadin.PulseUI;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Alex on 09.01.2017.
 */
public class PulseHelper {

    public void updateAssignmentAccordion(CourseBean course, Accordion assignments){

        assignments.removeAllComponents();

        List<AssignmentBean> listAssignments = PulseUI.getAssignmentService().getAssignmentsByCourse(course);
        if (!listAssignments.isEmpty()){
            LinkedHashMap<Date,HashMap<UserBean,List<AssignmentBean>>> hashMapAssignment = new LinkedHashMap<>();
            for (AssignmentBean assignmentBean:listAssignments){
                HashMap<UserBean, List<AssignmentBean>> valueMap = hashMapAssignment.get(assignmentBean.getDateAssignment());
                if (valueMap == null){
                    HashMap<UserBean,List<AssignmentBean>> hashMapUserAssignment = new HashMap<>();
                    List<AssignmentBean> assignmentsDoctors = new ArrayList<>();
                    assignmentsDoctors.add(assignmentBean);
                    hashMapUserAssignment.put(assignmentBean.getDoctor(),assignmentsDoctors);
                    hashMapAssignment.put(assignmentBean.getDateAssignment(),hashMapUserAssignment);
                }
                else {
                    List<AssignmentBean> assignmentsDoctors = valueMap.get(assignmentBean.getDoctor());
                    if (assignmentsDoctors == null){
                        assignmentsDoctors = new ArrayList<>();
                    }
                    assignmentsDoctors.add(assignmentBean);
                }
            }

            Iterator<Map.Entry<java.sql.Date, HashMap<UserBean, List<AssignmentBean>>>> iterator = hashMapAssignment.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<java.sql.Date, HashMap<UserBean, List<AssignmentBean>>> item = iterator.next();
                Iterator<Map.Entry<UserBean, List<AssignmentBean>>> iteratorDoctorAssignments = item.getValue().entrySet().iterator();
                VerticalLayout panelAssignment = new VerticalLayout();
                while (iteratorDoctorAssignments.hasNext()){
                    Map.Entry<UserBean, List<AssignmentBean>> doctorAssignments = iteratorDoctorAssignments.next();
                    UserBean doctor = doctorAssignments.getKey();
                    Panel panel = new Panel();
                    if (doctor == null){
                        panel.setCaption("Доктор неопределен");
                    }
                    else {
                        panel.setCaption(doctor.getShortName() + "," + doctor.getSpecialization().getName());
                    }
                    panel.setIcon(FontAwesome.USER_MD);
                    panel.addStyleName("panel-assignment");

                    List<AssignmentBean> assignmensPatients = doctorAssignments.getValue();
                    VerticalLayout listAssignmentsPatients = new VerticalLayout();
                    for (AssignmentBean assignmentBean:assignmensPatients){
                        Label assi = new Label(FontAwesome.FILE_O.getHtml() + " " + assignmentBean.getDescription(), ContentMode.HTML);
                        assi.setCaptionAsHtml(true);
                        listAssignmentsPatients.addComponent(assi);
                    }
                    panel.setContent(listAssignmentsPatients);
                    panelAssignment.addComponent(panel);
                }
                SimpleDateFormat dateAssignmentFormat = new SimpleDateFormat("dd.MM.yyyy");
                assignments.addTab(panelAssignment,"с " + dateAssignmentFormat.format(item.getKey()),FontAwesome.CALENDAR);
            }
        }
    }

    public java.util.Date beginningMonth(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public java.util.Date endMonth(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 997);
        return c.getTime();
    }

    public java.util.Date endDay(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 997);
        return c.getTime();
    }

    public java.util.Date beginDay(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public void setRowFilters(Grid grid, AbstractBeanContainer container){
        //фильтры
        Grid.HeaderRow filterRow = grid.appendHeaderRow();
        // Set up a filter for all columns
        for (Object pid: grid.getContainerDataSource()
                .getContainerPropertyIds()) {
            Grid.HeaderCell cell = filterRow.getCell(pid);
            if (cell == null || pid.equals("status") || !grid.getColumn(pid).isEditable()){
                continue;
            }
            if (grid.getContainerDataSource().getType(pid).equals(Timestamp.class)){
                DateField from = new DateField();
                from.addStyleName(ValoTheme.DATEFIELD_TINY);
                from.setResolution(Resolution.DAY);
                from.setWidth("100px");

                Label lable = new Label(" - ");
                DateField to = new DateField();
                to.setWidth("100px");
                to.addStyleName(ValoTheme.DATEFIELD_TINY);
                to.setResolution(Resolution.DAY);
                HorizontalLayout filterField = new HorizontalLayout();
                from.addValueChangeListener(event -> {
                    container.removeContainerFilters(pid);
                    if (event.getProperty().getValue() != null || to.getValue() != null){
                        container.addContainerFilter(new Between(pid,from.getValue(),to.getValue()));
                    }

                });
                to.addValueChangeListener(event -> {
                    container.removeContainerFilters(pid);
                    if (event.getProperty().getValue() != null || from.getValue() != null){
                        container.addContainerFilter(new Between(pid,from.getValue(),to.getValue()));
                    }
                });
                filterField.addComponents(from,lable,to);
                cell.setComponent(filterField);
            }
            else {
                TextField filterField = new TextField();
                filterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
                filterField.setInputPrompt("Отбор");

                // Set filter field width based on data type
                if (grid.getContainerDataSource()
                        .getType(pid).equals(Integer.class)) {
                    cell.setStyleName("rightalign");
                }
                // Update filter When the filter input is changed
                filterField.addTextChangeListener(change -> {
                    // Can't modify filters so need to replace
                    container.removeContainerFilters(pid);

                    // (Re)create the filter if necessary
                    if (!change.getText().isEmpty())
                        container.addContainerFilter(
                                new SimpleStringFilter(pid,
                                        change.getText(), true, false));
                });
                filterField.setWidth("100%");
                cell.setComponent(filterField);
            }
        }
    }

    public Component buildToolbar(VenueBean department) {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        Responsive.makeResponsive(header);

        Label title = new Label((department==null)?"Отделение не указано":department.getName());
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(title);

        return header;
    }
}
