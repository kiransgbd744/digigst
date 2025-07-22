package com.ey.advisory.app.data.entities.client;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Entity
@Table(name = "TBL_REPORT_DOWNLOAD_REQUEST_RPT_TYPE")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class AsyncFileStatusRptTypeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "RPT_DWLD_ID")
	private Long reportDwnldId;

	@Expose
	@Column(name = "RPTTYP")
	private String reportType;

	@Expose
	@Column(name = "FILE_PATH")
	private String filePath;

	@Expose
	@Column(name = "ISDWNLD")
	private boolean isDownloadable;
	
	@Expose
	@Column(name = "DOC_ID")
	private String docId;

}
