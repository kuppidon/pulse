package ua.pulse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;


@Entity
@Table(name = "hospitals")
public class Hospital {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;
	
	@NotNull
	private String name;
	
	private String address;
	
	@OneToMany(mappedBy = "hospital")
	private List<Venue> structuralUnits;
	
	
	public Hospital(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Venue> getStructuralUnits() {
		return structuralUnits;
	}

	public void setStructuralUnits(List<Venue> structuralUnits) {
		this.structuralUnits = structuralUnits;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
