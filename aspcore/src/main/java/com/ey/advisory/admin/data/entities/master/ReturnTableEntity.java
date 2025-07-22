package com.ey.advisory.admin.data.entities.master;

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
 * @author Mahesh.Golla
 *
 * This class is works as stateCode master Table
 */
@Entity
@Table(name = "MASTER_RET_RETURN_TABLE")
@Setter
@Getter
@ToString
public class ReturnTableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@SerializedName("returnTable")
	@Column(name = "RETURN_TABLE")
	private String returnTable;
	
	@Expose
	@SerializedName("retType")
	@Column(name = "RETURN_TYPE")
	private String retType;

	@Expose
	@SerializedName("retTableName")
	@Column(name = "RETURN_TABLE_NAME")
	private String retTableName;
	
	@Expose
	@SerializedName("typeDes")
	@Column(name = "TABLE_DESCRIPTION")
	private String typeDes;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	}
