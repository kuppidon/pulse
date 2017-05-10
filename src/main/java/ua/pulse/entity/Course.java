package ua.pulse.entity;

import ua.pulse.libs.CourseStatus;
import ua.pulse.libs.DepartureTypes;
import ua.pulse.libs.DischargedType;
import ua.pulse.libs.HospitalizationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "hospital_id")
	private Hospital hospital;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "department_id_start")
	private Venue departmentStart; // start

	@NotNull
	@ManyToOne
	@JoinColumn(name = "department_id_end")
	private Venue departmentEnd; // start

	@NotNull
	@Column(name = "start_date")
	private Timestamp startDate;
	
	@Column(name = "end_date")
	private Timestamp endDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private CourseStatus status;

	@NotNull
	@Column(name = "arrival_type")
	@Enumerated(EnumType.STRING)
	private DepartureTypes arrivalType;

	@NotNull
	@Column(name = "hospitalization_type")
	@Enumerated(EnumType.STRING)
	private HospitalizationType hospitalizationType;

	@Column(name = "discharged_type")
	@Enumerated(EnumType.STRING)
	private DischargedType dischargedType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsible_id")
	private User responsible;
	
	@Column(name = "history_number")
	private String historyNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diagnosis_MKB10")
	private MKB10 diagnosisMKB10;
	
	@Column(name = "diagnosis_guide")
	private String diagnosisGuide;
	
	@Column(name = "diagnosis_start")
	private String diagnosisStart;
	
	@Column(name = "diagnosis_clinic")
	private String diagnosisClinic;
	
	@Column(name = "diagnosis_end")
	private String diagnosisEnd;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "course")
	private List<Residence> residences;
	
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
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

	public User getResponsible() {
		return responsible;
	}

	public void setResponsible(User responsible) {
		this.responsible = responsible;
	}

	public String getHistoryNumber() {
		return historyNumber;
	}

	public void setHistoryNumber(String historyNumber) {
		this.historyNumber = historyNumber;
	}

	public Long getId() {
		return id;
	}

	public Venue getDepartmentStart() {
		return departmentStart;
	}

	public void setDepartmentStart(Venue departmentStart) {
		this.departmentStart = departmentStart;
	}

	public Venue getDepartmentEnd() {
		return departmentEnd;
	}

	public void setDepartmentEnd(Venue departmentEnd) {
		this.departmentEnd = departmentEnd;
	}

	public MKB10 getDiagnosisMKB10() {
		return diagnosisMKB10;
	}

	public void setDiagnosisMKB10(MKB10 diagnosisMKB10) {
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

	public List<Residence> getResidences() {
		return residences;
	}

	public void setResidences(List<Residence> residences) {
		this.residences = residences;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HospitalizationType getHospitalizationType() {
		return hospitalizationType;
	}

	public void setHospitalizationType(HospitalizationType hospitalizationType) {
		this.hospitalizationType = hospitalizationType;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public DischargedType getDischargedType() {
		return dischargedType;
	}

	public void setDischargedType(DischargedType dischargedType) {
		this.dischargedType = dischargedType;
	}
}
