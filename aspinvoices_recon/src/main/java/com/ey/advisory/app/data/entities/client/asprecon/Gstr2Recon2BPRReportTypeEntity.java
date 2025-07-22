package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */

@Entity
@Table(name = "TBL_RECON_2BPR_DWNLD_RPT_TYP")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Gstr2Recon2BPRReportTypeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
