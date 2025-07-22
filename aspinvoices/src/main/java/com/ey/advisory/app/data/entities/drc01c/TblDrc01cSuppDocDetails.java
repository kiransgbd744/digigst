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
@Table(name = "TBL_DRC01C_SUPP_DOC_DETAILS")
public class TblDrc01cSuppDocDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@SerializedName("suppDocId")
	@Column(name = "SUPPORTING_DOC_ID")
	private String suppDocId;

	@Expose
	@SerializedName("type")
	@Column(name = "TYPE")
	private String type;

	@Expose
	@SerializedName("contentType")
	@Column(name = "CONTENT_TYPE")
	private String contentType;

	@Expose
	@SerializedName("docName")
	@Column(name = "DOC_NAME")
	private String docName;

	@Expose
	@SerializedName("hash")
	@Column(name = "HASH")
	private String hash;

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
	
	@ManyToOne
	@JoinColumn(name = "TBL_DRC01C_DETAILS_ID", referencedColumnName = "ID")
	protected TblDrc01cDetails drc01cDetailsId;
	
}


