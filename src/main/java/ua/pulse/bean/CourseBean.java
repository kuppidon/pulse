package ua.pulse.bean;


import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import ua.pulse.component.CreateDischargedWindow;
import ua.pulse.component.CreateEditCourseWindow;
import ua.pulse.component.CreateTransferWindow;
import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.DepartureTypes;
import ua.pulse.libs.DischargedType;
import ua.pulse.libs.HospitalizationType;
import ua.pulse.service.*;
import ua.pulse.vaadin.PulseUI;

import java.sql.Timestamp;
import java.util.List;

@SpringComponent
public class CourseBean {

	private Long id;
	private PatientBean patient;
	private HospitalBean hospital;
	private VenueBean departmentStart; // start
	private VenueBean departmentEnd; // end
	private Timestamp startDate;
	private Timestamp endDate;
	private CourseStatus status;
	private DepartureTypes arrivalType;
	private HospitalizationType hospitalizationType;
	private DischargedType dischargedType;
	private UserBean responsible;
	private String historyNumber;
	private MKB10Bean diagnosisMKB10;
	private String diagnosisGuide;
	private String diagnosisStart;
	private String diagnosisClinic;
	private String diagnosisEnd;
	private String forwarded;
	private List<ResidenceBean> residences;
	private ResidenceBean currentResidence;

	public final static String NAME_SPRING_BEAN = "courseBeanSpring";
	
	@Autowired
    CourseService courseService;
	
	@Autowired
    VenueService venueService;
	
	@Autowired
    ResidenceService residenceService;

	@Autowired
	ResidenceBean residenceBean;

	@Autowired
    MKB10Service mkb10Service;

	@Autowired
    UserService userService;

	public CourseBean(PatientBean patient){
		this.patient = patient;
		this.startDate = new Timestamp(System.currentTimeMillis());
		this.diagnosisClinic = "";
		this.diagnosisStart = "";
		this.diagnosisGuide = "";
		this.diagnosisEnd = "";
		this.historyNumber = "";
		this.hospitalizationType = HospitalizationType.SCHEDULED;
		this.arrivalType = DepartureTypes.SELF;
		this.forwarded = "";
		this.status = CourseStatus.OPENED;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PatientBean getPatient() {
		return patient;
	}

	public void setPatient(PatientBean patient) {
		this.patient = patient;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public CourseStatus getStatus() {
		return status;
	}

	public void setStatus(CourseStatus status) {
		this.status = status;
	}

	public DepartureTypes getArrivalType() {
		return arrivalType;
	}

	public void setArrivalType(DepartureTypes arrivalType) {
		this.arrivalType = arrivalType;
	}

	public UserBean getResponsible() {
		return responsible;
	}

	public void setResponsible(UserBean responsible) {
		this.responsible = responsible;
	}

	public String getHistoryNumber() {
		return historyNumber;
	}

	public void setHistoryNumber(String historyNumber) {
		this.historyNumber = historyNumber;
	}

	public MKB10Bean getDiagnosisMKB10() {
		return diagnosisMKB10;
	}

	public void setDiagnosisMKB10(MKB10Bean diagnosisMKB10) {
		this.diagnosisMKB10 = diagnosisMKB10;
	}

	public String getDiagnosisGuide() {
		return diagnosisGuide;
	}

	public void setDiagnosisGuide(String diagnosisGuide) {
		this.diagnosisGuide = diagnosisGuide;
	}

	public String getDiagnosisStart() {
		return diagnosisStart;
	}

	public void setDiagnosisStart(String diagnosisStart) {
		this.diagnosisStart = diagnosisStart;
	}

	public String getDiagnosisClinic() {
		return diagnosisClinic;
	}

	public void setDiagnosisClinic(String diagnosisClinic) {
		this.diagnosisClinic = diagnosisClinic;
	}

	public String getDiagnosisEnd() {
		return diagnosisEnd;
	}

	public void setDiagnosisEnd(String diagnosisEnd) {
		this.diagnosisEnd = diagnosisEnd;
	}

	public List<ResidenceBean> getResidences() {
		return residences;
	}

	public void setResidences(List<ResidenceBean> residences) {
		this.residences = residences;
	}

	public CourseService getCourseService() {
		return courseService;
	}

	public VenueBean getDepartmentStart() {
		return departmentStart;
	}

	public void setDepartmentStart(VenueBean departmentStart) {
		this.departmentStart = departmentStart;
	}

	public VenueBean getDepartmentEnd() {
		return departmentEnd;
	}

	public void setDepartmentEnd(VenueBean departmentEnd) {
		this.departmentEnd = departmentEnd;
	}

	public HospitalizationType getHospitalizationType() {
		return hospitalizationType;
	}

	public void setHospitalizationType(HospitalizationType hospitalizationType) {
		this.hospitalizationType = hospitalizationType;
	}

	public VenueService getVenueService() {
		return venueService;
	}

	public ResidenceService getResidenceService() {
		return residenceService;
	}

	public MKB10Service getMkb10Service() {
		return mkb10Service;
	}

	public String getForwarded() {
		return forwarded;
	}

	public void setForwarded(String forwarded) {
		this.forwarded = forwarded;
	}

	public UserService getUserService() {
		return userService;
	}

	public ResidenceBean getResidenceBean() {
		if (residenceBean == null){
			return null;
		}
		residenceBean.setCourse(this);
		return residenceBean;
	}

	public HospitalBean getHospital() {
		return hospital;
	}

	public void closeCourse(){
		getCourseService().closeCourse(this);
	}

	public CourseBean openCourse(VenueBean residence){
		return  getCourseService().openCourse(this, residence);
	}

	public CourseBean saveAndFlush(){
		return  getCourseService().saveAndFlush(this);
	}

	public void setHospital(HospitalBean hospital) {
		this.hospital = hospital;
	}

	public Window createTransfer(){
		CourseBean courseBean = PulseUI.getObjectsConverterService().convertToSpringBean(this);
		return new CreateTransferWindow(courseBean);
	}

	public Window createDischaged(){
		CourseBean courseBean = PulseUI.getObjectsConverterService().convertToSpringBean(this);
		return new CreateDischargedWindow(courseBean);
	}

	public static Window createHospitalisation(PatientBean patient){
		CourseBean courseSpringBean = (CourseBean) PulseUI.getSpringBean(NAME_SPRING_BEAN);
		courseSpringBean.setPatient(patient);
		return new CreateEditCourseWindow(courseSpringBean);
	}

	public Window editCourse(){
		CourseBean courseSpringBean = PulseUI.getObjectsConverterService().convertToSpringBean(this);
		return new CreateEditCourseWindow(courseSpringBean);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CourseBean that = (CourseBean) o;
		return id == that.id;
	}

	@Override
	public String toString() {
		return "CourseBean{" +
				"id=" + id +
				'}';
	}

	public String getDescDiagnosHtml(){
		String startDiagnos = getDiagnosisStart();
		String clinicDiagnos = getDiagnosisClinic();
		String endDiagnos = getDiagnosisEnd();
		String caption = "";
		if (!startDiagnos.isEmpty()){
			caption = "<b>Начальный: </b> " + startDiagnos + "<br>";
		}
		if (!clinicDiagnos.isEmpty()){
			caption = caption + "<b>Клинический: </b> " + clinicDiagnos + "<br>";
		}
		if (!endDiagnos.isEmpty()){
			caption = caption + "<b>Заключитекльный: </b> " + endDiagnos + "<br>";
		}
		return  caption;
	}

	public DischargedType getDischargedType() {
		return dischargedType;
	}

	public void setDischargedType(DischargedType dischargedType) {
		this.dischargedType = dischargedType;
	}

	public ResidenceBean getCurrentResidence() {
		return currentResidence;
	}

	public void setCurrentResidence(ResidenceBean currentResidence) {
		this.currentResidence = currentResidence;
	}

	public static List<CourseBean> findAllByPatient(PatientBean patient){
		return ((CourseBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getCourseService().findAllByPatient(patient);
	}

	public static Window editCourseById(Long itemId) {
		return  ((CourseBean) PulseUI.getSpringBean(NAME_SPRING_BEAN)).getCourseService().findOne(itemId).editCourse();
	}
}
