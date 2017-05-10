package ua.pulse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ua.pulse.bean.*;


@Configuration
@ComponentScan("ua.pulse")
public class SpringBootConfig {

	@Bean(name = "hospitalBeanSpring")
	@Scope("prototype")
	public HospitalBean hospitalBean() {
		return new HospitalBean();
	}
	
	@Bean(name = "venueBeanSpring")
	@Scope("prototype")
	public VenueBean venueBean() {
		return new VenueBean();
	}
	
	@Bean(name = "courseBeanSpring")
	@Scope("prototype")
	public CourseBean courseBean() {
		return new CourseBean(null);
	}	
	
	@Bean(name = "patientBeanSpring")
	@Scope("prototype")
	public PatientBean patientBean() {
		return new PatientBean();
	}
	
	@Bean(name = "residenceBeanSpring")
	@Scope("prototype")
	public ResidenceBean residenceBean() {
		return new ResidenceBean();
	}
	
	@Bean(name = "assignmentBeanSpring")
	@Scope("prototype")
	public AssignmentBean assignmentBean() {
		return new AssignmentBean();
	}

	@Bean(name = "specializationBeanSpring")
	@Scope("prototype")
	public SpecializationBean specializationBean() {
		return new SpecializationBean();
	}

	@Bean(name = "userBeanSpring")
	@Scope("prototype")
	public UserBean userBean() {
		return new UserBean();
	}
}
