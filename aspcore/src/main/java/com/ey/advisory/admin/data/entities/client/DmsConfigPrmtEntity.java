package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @POST For DMS_CONFIG_PARAMTR
 * 
 * @author ashutosh.kar
 *
 */
@Entity
@Table(name = "DMS_CONFG_PRMTR")
@Setter
@Getter
@ToString
public class DmsConfigPrmtEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GROUP_ID")
	private Long groupId;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "CONFG_QUESTION_ID")
	private Long confgPrmtId;

	@Column(name = "QUESTION_CODE")
	private String paramValKeyId;

	@Column(name = "ANSWER")
	private String paramValue;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "DERIVED_ANSWER")
	private Integer derivedAnswer;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((confgPrmtId == null) ? 0 : confgPrmtId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DmsConfigPrmtEntity other = (DmsConfigPrmtEntity) obj;
		if (confgPrmtId == null) {
			if (other.confgPrmtId != null)
				return false;
		} else if (!confgPrmtId.equals(other.confgPrmtId))
			return false;
		return true;
	}

}
