package ua.pulse.entity;

import ua.pulse.libs.VenueType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "venue")
public class Venue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@NotNull
	private String name;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="hospital_id")
	private Hospital hospital;
	
	@ManyToOne
	@JoinColumn(name="owner_id")	
	private Venue owner;
	
	@Enumerated(EnumType.STRING)
	private VenueType type;
	
	@Column(name = "is_disabled")
	private Boolean isDisabled;

	private Integer capacity;
	
	public Venue(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getOwner() {
		return owner;
	}

	public void setOwner(Venue owner) {
		this.owner = owner;
	}

	public VenueType getType() {
		return type;
	}

	public void setType(VenueType type) {
		this.type = type;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Long getId() {
		return id;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

}
