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
import lombok.Data;
/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Data
@Table(name = "MASTER_GSTR3B_ITC")
public class Gstr3BItcEntityMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "DESCRIPTION")
	private String description;

	@Expose
	@Column(name = "TABLE_SECTION")
	private String tableSection;
	
	@Expose
	@Column(name = "TABLE_DESCRIPTION")
	private String tableDescription;
	
	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NO")
	protected Integer serialNo;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	/*@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;*/
}
