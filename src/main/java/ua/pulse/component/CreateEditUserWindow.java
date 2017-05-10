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
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.HospitalBean;
import ua.pulse.bean.SpecializationBean;
import ua.pulse.bean.UserBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.Roles;
import ua.pulse.service.UserService;

@SuppressWarnings("serial")
public class CreateEditUserWindow extends Window {

    private UserService userService;

    private final BeanFieldGroup<UserBean> fieldGroup;

    @PropertyId("userName")
    private TextField loginField;
    @PropertyId("password")
    private PasswordField passwordField;
    @PropertyId("fullName")
    private TextField fullNameField;
    @PropertyId("shortName")
    private TextField shortNameField;
    @PropertyId("specialization")
    private ComboBox specializationField;
    @PropertyId("department")
    private ComboBox departmentField;
    @PropertyId("hospital")
    private ComboBox hospitalField;
    @PropertyId("role")
    private ComboBox roleField;
    @PropertyId("isDisabled")
    private CheckBox isDisabledField;

    public CreateEditUserWindow(UserBean userBeanSpring){

        this.userService = userBeanSpring.getUserService();

        Responsive.makeResponsive(this);
        center();
        setIcon(FontAwesome.USER);
        setModal(true);
        // setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setWidth(50.0f, Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);

        setContent(content);
        Component body = body();
        content.addComponent(body);
        content.setExpandRatio(body, 1);
        content.addComponent(buildFooter());

        fieldGroup = new BeanFieldGroup<>(UserBean.class);
        fieldGroup.bindMemberFields(this);
        fieldGroup.setItemDataSource(userBeanSpring);

        if(userBeanSpring.getId() == null){
            setCaption(" Создание пользователя");
        }
        else{
            setCaption(" Редактирование пользователя");
            setVisibleComponentsByRoles();
        }
    }

    private Component body() {

        FormLayout details = new FormLayout();
        details.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        details.setMargin(new MarginInfo(false, true, true, true));
        details.setSpacing(true);

        Label sectionA = new Label("Аунтентификация");
        sectionA.addStyleName(ValoTheme.LABEL_H4);
        sectionA.addStyleName(ValoTheme.LABEL_COLORED);

        loginField = new TextField("Login");
        loginField.setWidth("100%");

        passwordField = new PasswordField("Password");
        passwordField.setWidth("100%");

        Label sectionB = new Label("Данные пользователя");
        sectionB.addStyleName(ValoTheme.LABEL_H4);
        sectionB.addStyleName(ValoTheme.LABEL_COLORED);

        fullNameField = new TextField("ФИО");
        fullNameField.setWidth("100%");

        shortNameField = new TextField("Краткое представление");
        shortNameField.setWidth("100%");

        Label sectionС = new Label("Права и ограничения");
        sectionС.addStyleName(ValoTheme.LABEL_H4);
        sectionС.addStyleName(ValoTheme.LABEL_COLORED);

        HorizontalLayout group = new HorizontalLayout();
        group.setWidth("100%");
        group.setSpacing(true);

        roleField = new ComboBox("Роль", Roles.getContainer());
        roleField.setNullSelectionAllowed(false);
        roleField.setInvalidAllowed(false);
        roleField.setNewItemsAllowed(false);
        roleField.setItemCaptionPropertyId("view");
        roleField.setWidth("100%");
        roleField.addValueChangeListener(event -> {
            setVisibleComponentsByRoles();
        });

        isDisabledField = new CheckBox("Отлючен");
        group.addComponents(roleField,isDisabledField);
        group.setExpandRatio(roleField,1);

        specializationField = new ComboBox("Специализация", new BeanItemContainer<>(SpecializationBean.class,SpecializationBean.getAllSpecializations()));
        specializationField.setNullSelectionAllowed(false);
        specializationField.setInvalidAllowed(false);
        specializationField.setNewItemsAllowed(false);
        specializationField.setItemCaptionPropertyId("name");
        specializationField.setWidth("100%");

        departmentField = new ComboBox("Отделение", new BeanItemContainer<>(VenueBean.class,VenueBean.findAllDepartment()));
        departmentField.setNullSelectionAllowed(false);
        departmentField.setInvalidAllowed(false);
        departmentField.setNewItemsAllowed(false);
        departmentField.setItemCaptionPropertyId("fullView");
        departmentField.setWidth("100%");

        hospitalField = new ComboBox("Госпиталь", new BeanItemContainer<>(HospitalBean.class,HospitalBean.findAll()));
        hospitalField.setNullSelectionAllowed(false);
        hospitalField.setInvalidAllowed(false);
        hospitalField.setNewItemsAllowed(false);
        hospitalField.setItemCaptionPropertyId("name");
        hospitalField.setWidth("100%");

        details.addComponents(sectionA,loginField,passwordField,sectionB,fullNameField,shortNameField,sectionС,group,specializationField, departmentField, hospitalField);

        return details;
    }

    private void setVisibleComponentsByRoles(){
        specializationField.setVisible(!roleField.getValue().equals(Roles.ADMIN));
        departmentField.setVisible(!roleField.getValue().equals(Roles.ADMIN));
        hospitalField.setVisible(roleField.getValue().equals(Roles.ADMIN));
        if (roleField.getValue().equals(Roles.ADMIN)){
            if (specializationField.getValue() != null){
                specializationField.setValue(null);
            }
            if (departmentField.getValue() != null){
                departmentField.setValue(null);
            }
        }
    }


    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button("Сохранить");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    fieldGroup.commit();
                    UserBean userBean = fieldGroup.getItemDataSource().getBean();
                    if (!userBean.getRole().equals(Roles.ADMIN)){
                        if (userBean.getDepartment() != null){
                            userBean.setHospital(userBean.getDepartment().getHospital());
                        }
                    }
                    userService.saveAndFlush(userBean);
                    Notification success = new Notification(
                            "Пользователь \"" + userBean.getFullName() + "\" успешно сохранен");
                    success.setDelayMsec(2000);
                    success.setStyleName("bar success small");
                    success.setPosition(Position.BOTTOM_CENTER);
                    success.show(Page.getCurrent());
                    close();
                    PulseEventBus.post(new PulseEvent.CreateEditUserEvent());
                } catch (FieldGroup.CommitException e) {
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
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);
        return footer;
    }


}
