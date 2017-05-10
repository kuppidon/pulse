package ua.pulse.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.CourseBean;
import ua.pulse.bean.ResidenceBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.TransferType;
import ua.pulse.libs.VenueType;
import ua.pulse.vaadin.PulseUI;

import java.sql.Timestamp;


public class CreateTransferWindow extends Window {

    private final BeanFieldGroup<ResidenceBean> fieldGroup;
    private final VenueBean department;
    private final UserBean currentUser;
    private ResidenceBean currentResidenceBean;

    private CourseBean course;
    @PropertyId("venue")
    private ComboBox venueTo;
    @PropertyId("startDate")
    private DateField startDateField;
    private ComboBox transferType;
    private Label comment;

    public CreateTransferWindow(CourseBean courseBean){

        this.course = courseBean;

        currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        department  = currentUser.getDepartment();
        currentResidenceBean = ResidenceBean.getCurrentResidenceByCourse(course);

//        if(!course.getStatus().equals(CourseStatus.OPENED)){
//            Notification mes = new Notification("Выбранный курс неактивный. Перевод запрещен");
//            mes.setStyleName("bar error small");
//            mes.setPosition(Position.BOTTOM_CENTER);
//            mes.show(Page.getCurrent());
//            mes.setIcon(FontAwesome.WARNING);
//            fireClose();
//            this.setEnabled(false);
//            return;
//        }

        center();
        setIcon(FontAwesome.EXCHANGE);
        setModal(true);
        Responsive.makeResponsive(this);
        setResizable(false);
        setClosable(false);
        setWidth(50.0f, Unit.PERCENTAGE);
        setCaption("<b> Перевод пациента</b> (" + department.getName() + ")");
        setCaptionAsHtml(true);
        setImmediate(true);

        VerticalLayout content = new VerticalLayout();
        setContent(content);

        Component body = body();
        content.addComponent(body);
        content.setExpandRatio(body, 1);
        content.addComponent(buildFooter());

        ResidenceBean newResidence = ResidenceBean.createResidenceBean();
        newResidence.setCourse(courseBean);
        newResidence.setStartDate(new Timestamp(System.currentTimeMillis()));

        fieldGroup = new BeanFieldGroup<ResidenceBean>(ResidenceBean.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(newResidence);

    }

    private Component body() {

        VerticalLayout details = new VerticalLayout();
        details.setMargin(new MarginInfo(false,true,true,true));
        details.setSpacing(true);

        TextField patientField = new TextField("Пациент");
        patientField.setValue(course.getPatient().getFullName());
        patientField.setReadOnly(true);
        patientField.setWidth("100%");
        patientField.setIcon(FontAwesome.MALE);
        details.addComponent(patientField);

        TextField currentResidence = new TextField("Текущее размещение");
        currentResidence.setWidth("100%");
        currentResidence.setStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        if(currentResidenceBean != null){
            VenueBean curUnit = currentResidenceBean.getVenue();
            //если текущее размещение департамент, значить это перевод между отделениями
            if (currentResidenceBean.getVenue().getType().equals(VenueType.DEPARTMEN)){
                curUnit = course.getDepartmentEnd();
            }
            if (curUnit.getType().equals(VenueType.HOSPITAL_ROOM)){
                currentResidence.setValue(curUnit.getName() + "/" + curUnit.getOwner().getName());
                currentResidence.setIcon(FontAwesome.HOTEL);
            }
            else if (curUnit.getType().equals(VenueType.DEPARTMEN)){
                currentResidence.setValue(curUnit.getName());
                currentResidence.setIcon(FontAwesome.HOSPITAL_O);
            }
        }
        currentResidence.setReadOnly(true);
        details.addComponent(currentResidence);

        transferType = new ComboBox("Тип перевода");
        transferType.setContainerDataSource(TransferType.getContainer());
        transferType.setItemCaptionPropertyId("view");
        transferType.setInputPrompt("Тип перевода");
        transferType.setInvalidAllowed(false);
        transferType.setNullSelectionAllowed(false);
        transferType.setNewItemsAllowed(false);
        transferType.setRequired(true);
        transferType.setIcon(FontAwesome.AMBULANCE);
        transferType.setWidth("100%");
        transferType.setValue(TransferType.TO_ROOM);
        transferType.setTextInputAllowed(false);
        transferType.addValueChangeListener(event -> {
                   setSourceForDestination((TransferType) event.getProperty().getValue());
                }
        );
        transferType.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        details.addComponent(transferType);

        startDateField = new DateField("Дата и время перевода");
        startDateField.setRequired(true);
        startDateField.setResolution(Resolution.MINUTE);
        startDateField.setIcon(FontAwesome.CALENDAR);
        details.addComponent(startDateField);

        venueTo = new ComboBox("Пункт назначения");
        venueTo.setWidth("100%");
        venueTo.setInputPrompt("Пункт назначения не выран");
        venueTo.setItemCaptionPropertyId("name");
        venueTo.setNewItemsAllowed(false);
        venueTo.setNullSelectionAllowed(false);
        venueTo.setTextInputAllowed(false);

        comment = new Label("При переводе в другой госпиталь, курс пациента (история) будет закрыта");
        comment.setStyleName(ValoTheme.LABEL_FAILURE);

        setSourceForDestination((TransferType) transferType.getValue());
        details.addComponents(venueTo,comment);

        return details;
    }

    private void setSourceForDestination(TransferType type){
        if(type.equals(TransferType.TO_ROOM)){
            venueTo.setContainerDataSource(new BeanItemContainer<>(VenueBean.class,VenueBean.findAllVenueByTypeAndOwnerToContainer(department, VenueType.HOSPITAL_ROOM)));
            //venueTo.removeItem(2);
            venueTo.setCaption("Палата");
            venueTo.setInputPrompt("Палата не выбрана");
            venueTo.setRequiredError("Необходимо указать палату, куда производится перевод");
        }
        else if(type.equals(TransferType.TO_DEPARTMEN)){
            venueTo.setContainerDataSource(new BeanItemContainer<>(VenueBean.class,VenueBean.findAllVenueByTypeAndHospitalToContainer(VenueType.DEPARTMEN,course.getHospital())));
            venueTo.setCaption("Отделение");
            venueTo.setInputPrompt("Отделение не вырано");
            venueTo.setRequiredError("Необходимо указать отделение, куда производится перевод");
        }
        else if(type.equals(TransferType.TO_HOSPITAL)){
            venueTo.removeAllItems();
        }
        venueTo.setItemCaptionPropertyId("name");
        venueTo.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        venueTo.setRequired(!type.equals(TransferType.TO_HOSPITAL));
        venueTo.setVisible(!type.equals(TransferType.TO_HOSPITAL));
        comment.setVisible(type.equals(TransferType.TO_HOSPITAL));
    }


    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button("Перевести");
        ok.setIcon(FontAwesome.EXCHANGE);
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener((Button.ClickListener) event -> {
            ResidenceBean residenceBean = fieldGroup.getItemDataSource().getBean();
            try {
                fieldGroup.commit();
                if (transferType.getValue().equals(TransferType.TO_HOSPITAL)){
                    course.setEndDate(residenceBean.getStartDate());
                    course.closeCourse();
                    Notification success = new Notification(
                            "Пациента \"" + course.getPatient().getFullName() + "\" выписан с отделения");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                }
                else {
                    if(transferType.getValue().equals(TransferType.TO_ROOM)){
                        course.setDepartmentEnd(((VenueBean) venueTo.getValue()).getOwner());
                        course.saveAndFlush();
                    }
                    residenceBean.saveAndFlush();
                    Notification success = new Notification(
                            "Перевод пациента \"" + course.getPatient().getFullName() + "\" успешно осуществлен");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                }
                close();
                //оповещаем всех слущателей о переводе пациента
                PulseEventBus.post(new PulseEvent.ExecuteTransfer(course,currentResidenceBean.getVenue(),residenceBean.getVenue()));
            } catch (FieldGroup.CommitException e) {
                Notification.show("Ошибка записи",e.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        });
        ok.focus();

        Button cancel = new Button("Отмена");
        cancel.addStyleName(ValoTheme.BUTTON_DANGER);
        cancel.addClickListener((Button.ClickListener) event -> close());
        footer.addComponents(ok,cancel);
        footer.setExpandRatio(ok, 1);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }

}
