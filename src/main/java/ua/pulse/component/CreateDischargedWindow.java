package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.viritin.ListContainer;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.MKB10Bean;
import ua.pulse.bean.UserBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.DischargedType;

import java.sql.Timestamp;

@SuppressWarnings("serial")
public class CreateDischargedWindow extends Window {

    private CourseBean courseBean;


    private final BeanFieldGroup<CourseBean> fieldGroup;

    @PropertyId("endDate")
    private PopupDateField endDateField;
    @PropertyId("responsible")
    private ComboBox responsibleField;
    @PropertyId("dischargedType")
    private ComboBox dischargedTypeField;
    @PropertyId("diagnosisEnd")
    private TextArea diagnosisEndField;
    @PropertyId("diagnosisMKB10")
    private ComboBox MKB10CodeField;
    private ComboBox MKB10NameField;


    public CreateDischargedWindow(CourseBean courseBean){

        this.courseBean  = courseBean;
        courseBean.setEndDate(new Timestamp(System.currentTimeMillis()));

        center();
        setClosable(false);
        setModal(true);
        setResizable(false);
        setCaption(" Выписка пациент <b>" + courseBean.getPatient().getFullName() + "</b>");
        setCaptionAsHtml(true);
        setIcon(FontAwesome.WHEELCHAIR);
        setWidth(50.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        setContent(content);

        Component body = buildBody();
        content.addComponent(body);
        content.setExpandRatio(body,1);

        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<CourseBean>(CourseBean.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(courseBean);

        //endDateField.setValue(new Date());
    }

    private Component buildBody(){

        VerticalLayout body = new VerticalLayout();
        body.setMargin(new MarginInfo(false,true,false,true));
        body.setSpacing(true);

        endDateField = new PopupDateField("Дата и время выписки");
        endDateField.setResolution(Resolution.MINUTE);
        endDateField.setDateFormat("dd.MM.yyyy HH:mm");
        endDateField.setRangeStart(courseBean.getStartDate());
        endDateField.setRequired(true);
        endDateField.setRequiredError("Укажите дату выписки");

        dischargedTypeField = new ComboBox("Виписан", DischargedType.getContainer());
        dischargedTypeField.setTextInputAllowed(false);
        dischargedTypeField.setItemCaptionPropertyId("view");
        dischargedTypeField.setNullSelectionAllowed(false);
        dischargedTypeField.setRequired(true);
        dischargedTypeField.setWidth("100%");

        HorizontalLayout panelA = new HorizontalLayout(endDateField,dischargedTypeField);
        panelA.setWidth("100%");
        panelA.setSpacing(true);
        panelA.setExpandRatio(dischargedTypeField,1);
        body.addComponent(panelA);

        responsibleField = new ComboBox("Лечащий врач");
        responsibleField.setNewItemsAllowed(false);
        responsibleField.setContainerDataSource(new BeanItemContainer<>(UserBean.class, UserBean.findAllDoctorsByDepartment(courseBean.getDepartmentEnd())));
        responsibleField.setItemCaptionPropertyId("fullName");
        responsibleField.setWidth("100%");
        body.addComponent(responsibleField);

        diagnosisEndField = new TextArea("Диагноз");
        diagnosisEndField.setIcon(FontAwesome.INFO);
        diagnosisEndField.setRows(4);
        diagnosisEndField.setWidth("100%");
        diagnosisEndField.setWordwrap(true);
        body.addComponent(diagnosisEndField);

        ListContainer<MKB10Bean> sourceMKB10 = new ListContainer<>(MKB10Bean.class, MKB10Bean.findAll());
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

        HorizontalLayout groupMKB10 = new HorizontalLayout();
        groupMKB10.setWidth("100%");
        Label sectionMKB10 = new Label("Диагноз по МКБ10");
        sectionMKB10.addStyleName(ValoTheme.LABEL_COLORED);
        groupMKB10.setSpacing(false);
        groupMKB10.addComponents(sectionMKB10,MKB10CodeField,MKB10NameField);
        groupMKB10.setExpandRatio(MKB10NameField, 1);
        body.addComponents(groupMKB10);

        return body;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button("Выписать",FontAwesome.THUMBS_UP);
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    fieldGroup.commit();
                    courseBean = fieldGroup.getItemDataSource().getBean();
                    courseBean.closeCourse();
                    Notification success = new Notification(
                            "Пациент \"" + courseBean.getPatient().getFullName() + "\" успешно выписан с отделения " + courseBean.getDepartmentEnd().getName());
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                    close();

                    //оповещаем всех слущателей о выписке пациента
                    PulseEventBus.post(new PulseEvent.DischargedPatientEvent(courseBean));
                } catch (FieldGroup.CommitException e){
                    Notification.show("Ошибка обновления данных",e.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                } catch (Exception e) {
                    Notification.show("Ошибка выписки",e.getMessage(),
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
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }

}
