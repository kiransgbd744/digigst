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
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "ENTITY_AT_VALUE")
@Setter
@Getter
@ToString
public class EntityAtValueEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name ="GROUP_ID")
	private Long groupId;
	
	@Column(name = "GROUP_CODE")
	private String groupCode;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "ENTITY_AT_CONFIG_ID")
	private Long entityAtConfigId;
	
	@Column(name = "AT_VALUE")
	private String atValue;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "AT_CODE")
	private String atCode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atCode == null) ? 0 : atCode.hashCode());
		result = prime * result + ((atValue == null) ? 0 : atValue.hashCode());
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
		EntityAtValueEntity other = (EntityAtValueEntity) obj;
		if (atCode == null) {
			if (other.atCode != null)
				return false;
		} else if (!atCode.equals(other.atCode))
			return false;
		if (atValue == null) {
			if (other.atValue != null)
				return false;
		} else if (!atValue.equals(other.atValue))
			return false;
		return true;
	}

}
