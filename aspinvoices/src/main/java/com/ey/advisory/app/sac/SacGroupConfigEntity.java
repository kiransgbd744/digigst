package com.ey.advisory.app.sac;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_GROUP_CONFIG")
@Data
public class SacGroupConfigEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CONFIG_CODE")
	private String configCode;
	
	@Column(name = "ENTITY_ID")
	private Integer entityId;
	
	@Column(name = "ENTITY_NAME")
	private String entityName;
	
	@Column(name = "CONFIG_VALUE")
	private String configValue;
	
	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	@Column(name = "SP_NAME")
	private String spName;
	
	@Column(name = "DELETED_ON")
	private LocalDateTime deletedOn;
	
	@Column(name = "INS_USER")
	private String insUser;
	
	@Column(name = "GROUP_CODE")
	private String groupCode;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	

}
