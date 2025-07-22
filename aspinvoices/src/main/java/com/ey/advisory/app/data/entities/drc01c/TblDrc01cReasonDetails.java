package com.ey.advisory.app.data.entities.drc01c;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;


@Data
@Entity
@Table(name = "TBL_DRC01C_REASON_DETAILS")
public class TblDrc01cReasonDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@SerializedName("reasonCode")
	@Column(name = "REASON_CODE")
	private String reasonCode;

	@Expose
	@SerializedName("reasonDesc")
	@Column(name = "REASON_DESCRIPTION")
	private String reasonDesc;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "source")
	private String source;
	
	@ManyToOne
	@JoinColumn(name = "TBL_DRC01C_DETAILS_ID", referencedColumnName = "ID")
	protected TblDrc01cDetails drc01cDetailsId;

}
