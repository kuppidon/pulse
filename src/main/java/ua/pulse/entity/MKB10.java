package ua.pulse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="MKB10")
public class MKB10 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Long id;

	@NotNull
	@Column(length = 10)
	private String code;

	@NotNull
	@Column(length = 1000)
	private String name;

	@ManyToOne
	@JoinColumn(name="parent_id")
	private MKB10 parent;

	public MKB10(){}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MKB10 getParent() {
		return parent;
	}

	public void setParent(MKB10 parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MKB10 mkb10 = (MKB10) o;

		if (id != null ? !id.equals(mkb10.id) : mkb10.id != null) return false;
		if (code != null ? !code.equals(mkb10.code) : mkb10.code != null) return false;
		return name != null ? !name.equals(mkb10.name) : mkb10.name != null;


	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		return result;
	}
}
