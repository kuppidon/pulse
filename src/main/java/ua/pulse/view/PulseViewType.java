package ua.pulse.view;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import ua.pulse.libs.Roles;
import ua.pulse.view.mainforms.*;

import java.util.Arrays;
import java.util.List;

public enum PulseViewType {

    DEPARTMEN("Отделение", PostView.class, FontAwesome.HOME, true, true, Roles.POST),
    FUTURE("Поступающие", PostFutureView.class, FontAwesome.AMBULANCE, true, false, Roles.POST),

    CURRENT("Текущие пациенты", DoctorViewCurrent.class, FontAwesome.USERS, true, true, Roles.DOCTOR, Roles.HEAD_OF_DEPARTMENT),
    HISTORY("История пациентов", DoctorViewHistory.class, FontAwesome.CALENDAR_TIMES_O, false, false, Roles.DOCTOR, Roles.HEAD_OF_DEPARTMENT),

    HOSPITALS("Госпитали", AdminView.class, FontAwesome.HOSPITAL_O, true, false, Roles.ADMIN),
    VENUES("Отделения и палаты", AdminView.class, FontAwesome.BED, false, false, Roles.ADMIN),
    USERS("Пользователи", AdminView.class, FontAwesome.USER_MD, false, false, Roles.ADMIN),
    SPECIALISATION("Специализации", AdminView.class, FontAwesome.HAND_SPOCK_O, false, false, Roles.ADMIN),
    MKB10("MKБ-10", AdminView.class, FontAwesome.INFO, false, false, Roles.ADMIN),
    REPORTS("Статистика", ReportsView.class, FontAwesome.INFO, false, false, Roles.HEAD_OF_DEPARTMENT);

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;
    private final List<Roles> roles;
    private final boolean errorView;

    private PulseViewType(final String viewName,
                          final Class<? extends View> viewClass,
                          final Resource icon,
                          final boolean stateful,
                          final boolean errorView,
                          final Roles... roles) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
        this.errorView = errorView;
        this.roles = Arrays.asList(roles);
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static PulseViewType getByViewName(final String viewName) {
        PulseViewType result = null;
        for (PulseViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public boolean isErrorView() {
        return errorView;
    }
}