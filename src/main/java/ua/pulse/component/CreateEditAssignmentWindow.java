package ua.pulse.component;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import ua.pulse.bean.AssignmentBean;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.UserBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.vaadin.PulseUI;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 05.01.2017.
 */
public class CreateEditAssignmentWindow extends Window {

    private DateField dateField;
    private Table table;
    private final CourseBean courseBean;
    private final UserBean currentUser;
    private final int firstCount;

    public CreateEditAssignmentWindow(CourseBean courseBean){

        setModal(true);
        setClosable(false);
        setIcon(FontAwesome.MEDKIT);
        center();
        setHeight("90%");
        setWidth(50.0f, Unit.PERCENTAGE);

        this.courseBean = courseBean;
        this.currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();

        String caption = " План лечения ";
        caption = caption + "(<b>" + courseBean.getPatient().getFullName() + "</b>";
        Integer age = courseBean.getPatient().getAge();
        if (age != null){
            caption = caption + ", <b>" + age + "</b> лет)";
        }
        else {
            caption = caption + ")";
        }
        setCaption(caption);
        setCaptionAsHtml(true);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);

        dateField = new DateField("Дата назначений ");
        dateField.setResolution(Resolution.DAY);
        dateField.addStyleName("horizontal_caption");
        dateField.addStyleName(ValoTheme.DATEFIELD_SMALL);
        dateField.setDateFormat("dd-MM-yyyy");
        content.addComponent(dateField);
        Date lastDate = PulseUI.getDataProvider().getLastDateAssignmentByCourse(courseBean);
        if (lastDate != null) {
            dateField.setValue(lastDate);
            dateField.setRangeStart(lastDate);
        }
        else {
            dateField.setValue(new java.util.Date());
            dateField.setRangeStart(courseBean.getStartDate());
        }
        dateField.addContextClickListener(event -> {
            Notification.show("Дата, с которой назначаются назначения");
        });
        dateField.addValueChangeListener(event -> {
            if (table.size() != 0){
                MessageBox.createQuestion()
                        .withHtmlMessage("Скопировать текущие назначения на новую дату?")//<br>" +
                              //  " <font color=\"red\"> (Все новые (добавленные) назначения не будут сохранены)</font>")
                        .withOkButton(()->{copyAssignmentToDate();}, ButtonOption.caption("Скопировать"),ButtonOption.style(ValoTheme.BUTTON_PRIMARY))
                        .withCancelButton(()->{},ButtonOption.caption("Нет"))
                        .open();
            }
        });


        List<AssignmentBean> listAssignment = PulseUI.getDataProvider().getAssignmentsByDateAndCourse(lastDate,courseBean);
        BeanItemContainer<AssignmentBean> container = new BeanItemContainer<>(AssignmentBean.class, listAssignment);
        firstCount = listAssignment.size();
        table = new Table();
        table.setEditable(true);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setSizeFull();
        table.setContainerDataSource(container);
        table.setImmediate(true);

        table.addGeneratedColumn("btnDelete", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                Button btnDelete = new Button();
                btnDelete.setIcon(FontAwesome.TRASH);
                btnDelete.addStyleName(ValoTheme.BUTTON_DANGER);
                btnDelete.addClickListener(event -> {
                    source.removeItem(itemId);
                });
                return btnDelete;
            }
        });

        table.setVisibleColumns(new Object[] {"description","btnDelete"});
        table.setColumnHeader("description","Назначение");
        table.setColumnHeader("btnDelete","");
        table.setColumnExpandRatio("description",1);
        table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        table.setTableFieldFactory(new ImmediateFieldFactory());

        content.addComponent(table);
        content.addComponent(buildFooter());
        content.setExpandRatio(table,1);
    }

    public class ImmediateFieldFactory extends DefaultFieldFactory {
        @Override
        public Field<?> createField(Container container, Object itemId,
                                    Object propertyId, Component uiContext) {
            // Let the DefaultFieldFactory create the fields
            Field<?> field = super.createField(container, itemId,
                    propertyId, uiContext);

            // ...and just set them as immediate
            ((AbstractField<?>) field).setImmediate(true);

            // Also modify the width of TextFields
            if (field instanceof TextField)
                field.setWidth("100%");
                field.setRequired(true);
                ((TextField) field).setInputPrompt("Введите назначение");
                field.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
                field.focus();

            return field;
        }
    }

    private void copyAssignmentToDate(){
        BeanItemContainer<AssignmentBean> newContainer = new BeanItemContainer<>(AssignmentBean.class);
        for ( Object item:table.getItemIds()){
            if (((AssignmentBean) item).getDescription().isEmpty()){
                continue;
            }
            AssignmentBean newAssignment = new AssignmentBean();
            newAssignment.setCourse(((AssignmentBean) item).getCourse());
            newAssignment.setDoctor(((AssignmentBean) item).getDoctor());
            newAssignment.setDescription(((AssignmentBean) item).getDescription());
            newAssignment.setDateAssignment(new Date(dateField.getValue().getTime()));
            newContainer.addItem(newAssignment);
        }
        table.setEditable(false);
        table.setContainerDataSource(newContainer);
        table.setVisibleColumns(new Object[] {"description","btnDelete"});
        table.setEditable(true);
    }


    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button add = new Button("Добавить");
        add.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        add.setIcon(FontAwesome.PLUS);
        add.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        add.addClickListener(event -> {
            BeanItemContainer container = (BeanItemContainer) table.getContainerDataSource();
            AssignmentBean newAssignment = new AssignmentBean();
            newAssignment.setCourse(courseBean);
            newAssignment.setDoctor(currentUser);
            newAssignment.setDateAssignment(new Date(dateField.getValue().getTime()));
            container.addItem(newAssignment);
            table.setEditable(false);
            table.setContainerDataSource(container);
            table.setVisibleColumns(new Object[] {"description","btnDelete"});
            table.setEditable(true);
            //table.setCurrentPageFirstItemId(table.lastItemId());
        });
        footer.addComponent(add);
        footer.setComponentAlignment(add,Alignment.MIDDLE_LEFT);

        Button ok = new Button("Сохранить");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                List<AssignmentBean> listAssignment = table.getContainerDataSource().getItemIds().stream()
                        .filter(item -> !((AssignmentBean) item).getDescription().isEmpty())
                        .map(item -> (AssignmentBean) item)
                        .collect(Collectors.toList());

                Date dateAssignment = new Date(dateField.getValue().getTime());
                //                        else {
//                            //item.setDateAssignment(dateAssignment);
//                        }

                try {
                    if (!listAssignment.isEmpty() || firstCount != 0){
                        PulseUI.getAssignmentService().saveAssignmentByCourseAndDate(courseBean,dateAssignment,listAssignment);
                        Notification success = new Notification("Назначения успешно сохранены");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar success small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());
                        close();

                        //оповещаем всех слущателей о создании нового или редактировании пациента
                        PulseEventBus.post(new PulseEvent.CreateUpdateAssignment());
                    }
                    else {
                        Notification success = new Notification("Нет заполненных назначений для записи");
                        success.setDelayMsec(2000);
                        success.setStyleName("bar warning small");
                        success.setPosition(Position.BOTTOM_CENTER);
                        success.show(Page.getCurrent());
                    }
                } catch (Exception e) {
                    Notification.show("Ошибка записи",e.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        ok.focus();

        Button cancel = new Button("Отмена");
        cancel.addStyleName(ValoTheme.BUTTON_DANGER);
        cancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        footer.addComponents(ok,cancel);
        footer.setExpandRatio(ok, 1);
        footer.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT);
        footer.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
        return footer;
    }

}
