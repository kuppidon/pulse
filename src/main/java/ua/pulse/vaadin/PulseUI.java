package ua.pulse.vaadin;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ua.pulse.PulseHelper;
import ua.pulse.bean.PatientBean;
import ua.pulse.bean.UserBean;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.data.DataProvider;
import ua.pulse.event.Broadcaster;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.event.PushEvent;
import ua.pulse.service.AssignmentService;
import ua.pulse.service.CourseService;
import ua.pulse.service.PatientService;
import ua.pulse.view.LoginView;
import ua.pulse.view.MainPost;

import javax.annotation.PreDestroy;


@SpringUI(path="/pulse")
@Theme("pulse")
@Title("PULSE")
@Widgetset("PulseWidgetSet")
@Push
public class PulseUI extends UI implements Broadcaster.BroadcastListener{

	private static final long serialVersionUID = 1L;
	private String SESSION_ID;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CourseService courseServiceSpring;

	@Autowired
	private ObjectsConverterService objectsConverterService;

	@Autowired
	private AssignmentService assignmentServiceBean;

	private static ObjectsConverterService objectsConverter;

	private static CourseService courseService;

	private static AssignmentService assignmentService;

	private UserBean currentUser;

	@Autowired
	private DataProvider dataProvider;

	private final PulseEventBus pulseEventbus = new PulseEventBus();
	private final PulseHelper pulseHelper = new PulseHelper();


	@Override
	protected void init(VaadinRequest vaadinRequest) {

		objectsConverter = objectsConverterService;
		courseService = courseServiceSpring;
		assignmentService = assignmentServiceBean;

		VaadinSession.getCurrent().getSession().setMaxInactiveInterval(1800);
		SESSION_ID = VaadinSession.getCurrent().getSession().getId();

		Broadcaster.register(this);
		PulseEventBus.register(this);

		Responsive.makeResponsive(this);
		addStyleName(ValoTheme.UI_WITH_MENU);

		updateContent();

		Page.getCurrent().addBrowserWindowResizeListener(
				(Page.BrowserWindowResizeListener) event -> PulseEventBus.post(new PulseEvent.BrowserResizeEvent()));

	}

	private void updateContent() {
		currentUser = (UserBean) VaadinSession.getCurrent()
				.getAttribute(UserBean.class.getName());
		if (currentUser != null){
			// Authenticated user
			setContent((Component) new MainPost());
			removeStyleName("loginview");
			getNavigator().navigateTo(getNavigator().getState());
		} else {
			setContent(new LoginView());
			addStyleName("loginview");
		}
	}

	@Subscribe
	public void userLoginRequested(final PulseEvent.UserLoginRequestedEvent event) {
		currentUser = getDataProvider().authenticate(event.getUserName(),
				event.getPassword());
		VaadinSession.getCurrent().setAttribute(UserBean.class.getName(), currentUser);
		updateContent();
	}

	@Subscribe
	public void userLoggedOut(final PulseEvent.UserLoggedOutEvent event) {
		// When the user logs out, current VaadinSession gets closed and the
		// page gets reloaded on the login screen. Do notice the this doesn't
		// invalidate the current HttpSession.
		VaadinSession.getCurrent().close();
		Page.getCurrent().reload();
		Broadcaster.broadcast(null);

	}

	@Subscribe
	public void closeOpenWindows(final PulseEvent.CloseOpenWindowsEvent event) {
		for (Window window : getWindows()) {
			window.close();
		}
	}

	public UserBean getCurrentUser(){
		return currentUser;
	}

	public static Object getSpringBean(String nameBean){return  ((PulseUI) getCurrent()).applicationContext.getBean(nameBean);}

	public static ObjectsConverterService getObjectsConverterService() {
		return objectsConverter;
	}

	public static AssignmentService getAssignmentService(){return assignmentService;}

	public static CourseService getCourseService() {
		return courseService;
	}

	public static PatientService getPatientService(){return ((PatientBean) getSpringBean("patientBean")).getPatientService();}

	public static PulseEventBus getPulseEventbus() {
		return ((PulseUI) getCurrent()).pulseEventbus;
	}

	public static DataProvider getDataProvider() {
		return ((PulseUI) getCurrent()).dataProvider;
	}

	public static PulseHelper getPulseHelper() {
		return ((PulseUI) getCurrent()).pulseHelper;
	}

	/**
		метод перенаправляет события одной сесии на другие сесии, ели они (события) приводят к изменению данных
	 **/
	@Override
	public void receiveBroadcast(PushEvent event) {

		if (event == null){
			return;
		}

		access(() -> {
			if (event.getSession().equals(getSESSION_ID())) return;
			//если изменение курса или закрытие курса
			if (event.getClass().equals(PulseEvent.EditCourseEven.class)
					|| event.getClass().equals(PulseEvent.DischargedPatientEvent.class)) {
				if (event.getCourse().getDepartmentEnd().equals(getCurrentUser().getDepartment()) || event.getCourse().getResponsible().equals(getCurrentUser())) {
					PulseEventBus.post(event);
				}
			}
			//если перевод пациента
			if (event.getClass().equals(PulseEvent.ExecuteTransfer.class)) {
				PulseEvent.ExecuteTransfer eventTransfer = (PulseEvent.ExecuteTransfer) event;
				if (eventTransfer.getCourse().getDepartmentEnd().equals(getCurrentUser().getDepartment())
						|| eventTransfer.getVenueTo().equals(getCurrentUser().getDepartment())
						|| eventTransfer.getVenueFrom().equals(getCurrentUser().getDepartment())) {
					PulseEventBus.post(event);
				}
			}
		});
	}

	@PreDestroy
	private void destroy() {
		Broadcaster.unregister(this);
	}

	public String getSESSION_ID() {
		return SESSION_ID;
	}

}