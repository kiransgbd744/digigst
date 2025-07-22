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
 */

@Entity
@Table(name = "MASTER_NATURE_OF_SUPPLIES")
@Setter
@Getter
@ToString
public class NatureOfSupEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("section")
	@Column(name = "SECTION")
	protected String section;

	@Expose
	@SerializedName("subSection")
	@Column(name = "SUBSECTION")
	protected String subSection;

	@Expose
	@SerializedName("natureOfSup")
	@Column(name = "NATUREOFSUPPLIES")
	protected String natureOfSup;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;
}
