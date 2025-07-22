package com.ey.advisory.common.client.domain;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
 * @POST For ENTITY_CONFIG_PARAMTR
 * 
 * @author Siva Reddy
 *
 */
@Entity
@Table(name = "B2C_QR_AMT_CONF")
@Setter
@Getter
@ToString
public class B2CQRAmtConfigEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Expose
	@SerializedName("pan")
	@Column(name = "PAN")
	private String pan;

	@Expose
	@SerializedName("identifier")
	@Column(name = "IDENTIFIER")
	private String identifier;

	@Expose
	@SerializedName("value")
	@Column(name = "VALUE")
	private String value;

	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@SerializedName("entityName")
	@Column(name = "ENTITY_NAME")
	private String entityName;
	
	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Expose
	@SerializedName("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;
}