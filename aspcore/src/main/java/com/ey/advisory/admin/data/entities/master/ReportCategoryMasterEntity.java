package com.ey.advisory.admin.data.entities.master;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_ASYNC_REPORT_CATEGORY")
@Data
public class ReportCategoryMasterEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "DATA_TYPE")
	private String dataType;
	
	@Expose
	@Column(name = "REPORT_CATEGORY")
	private String repCateg;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "CREATED_ON")
	private String createdOn;
	
	@Expose
	@Column(name = "IS_DELETE")
	private boolean  isDelete;
}
