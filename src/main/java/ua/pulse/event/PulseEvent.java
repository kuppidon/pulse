package ua.pulse.event;


import ua.pulse.bean.CourseBean;
import ua.pulse.bean.PatientBean;
import ua.pulse.bean.VenueBean;
import ua.pulse.vaadin.PulseUI;
import ua.pulse.view.PulseViewType;

import java.util.Date;


/*
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class PulseEvent {

    public static final class UserLoginRequestedEvent {
        private final String userName, password;

        public UserLoginRequestedEvent(final String userName,
                                       final String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class BrowserResizeEvent {}

    public static class UserLoggedOutEvent {}

    public static class FutureCountUpdatedEvent {}

    public static final class CurrentCountUpdatedEvent {}

    public static final class PostViewChangeEvent {
        private final PulseViewType view;

        public PostViewChangeEvent(final PulseViewType view) {
            this.view = view;
        }

        public PulseViewType getView() {
            return view;
        }
    }

    public static class CloseOpenWindowsEvent {}

    public static class ProfileUpdatedEvent {}

    public static class CreateEditPatientEvent{
        private final PatientBean patientBean;
        private final Boolean create;
        public CreateEditPatientEvent(final PatientBean patientBean, Boolean create){
            this.create = create;
            this.patientBean = patientBean;
        }
        public PatientBean getPatient(){return  patientBean;}
        public Boolean getCreate(){return create;}
    }

    public static class ExecuteTransfer extends PushEvent{

        private final VenueBean venueFrom;
        private final VenueBean venueTo;

        public ExecuteTransfer(final CourseBean courseBean, final VenueBean venueFrom, final VenueBean venueTo){
            super();
            super.session = PulseUI.getCurrent().getSession().getSession().getId();
            super.courseBean = courseBean;
            this.venueFrom = venueFrom;
            this.venueTo = venueTo;
            Broadcaster.broadcast(this);
        }

        public VenueBean getVenueFrom(){return venueFrom;}
        public VenueBean getVenueTo(){return venueTo;}
    }

    public static class EditCourseEven extends PushEvent{
        public EditCourseEven(final CourseBean courseBean){
            super();
            super.session    = PulseUI.getCurrent().getSession().getSession().getId();
            super.courseBean = courseBean;
        }
    }

    public static class CreateUpdateAssignment{}

    public static class CreateEditHospital{}

    public static class SelectPeriod{
        private final Date startDate;
        private final Date endDate;
        public SelectPeriod(final Date startDate,final Date endDate){
            this.startDate = startDate;
            this.endDate   = endDate;
        }
        public Date getStartDate(){
            return  startDate;
        }
        public Date getEndDate(){return endDate;}
    }

    public static class DischargedPatientEvent extends PushEvent{
        public DischargedPatientEvent(CourseBean courseBean) {
            super();
            super.session    = PulseUI.getCurrent().getSession().getSession().getId();
            super.courseBean = courseBean;
        }
    }

    public static class ChangeSectionMenu{
        private final PulseViewType pulseViewType;
        public ChangeSectionMenu(final PulseViewType pulseViewType){this.pulseViewType = pulseViewType;}
        public PulseViewType getViewType(){return pulseViewType;}
    }

    public static class CreateEditSpecializationEvent {}

    public static class CreateEditUserEvent {}

    public static class CreateEditVenueEvent {}
}