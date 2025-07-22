package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @POST For ENTITY_CONFIG_PARAMTR
 * 
 * @author Umesh
 *
 */
@Entity
@Table(name = "ENTITY_CONFG_PRMTR")
@Setter
@Getter
@ToString
public class EntityConfigPrmtEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ENTITY_CONFG_PRMTR_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;
	

	@Column(name = "GROUP_ID")
	private Long groupId;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "ENTITY_ID")
	private Long entityId;

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
	        result = prime * result
	                + ((confgPrmtId == null) ? 0 : confgPrmtId.hashCode());
	        result = prime * result
	                + ((entityId == null) ? 0 : entityId.hashCode());
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
	        EntityConfigPrmtEntity other = (EntityConfigPrmtEntity) obj;
	        if (confgPrmtId == null) {
	            if (other.confgPrmtId != null)
	                return false;
	        } else if (!confgPrmtId.equals(other.confgPrmtId))
	            return false;
	        if (entityId == null) {
	            if (other.entityId != null)
	                return false;
	        } else if (!entityId.equals(other.entityId))
	            return false;
	        return true;
	    }
	
}