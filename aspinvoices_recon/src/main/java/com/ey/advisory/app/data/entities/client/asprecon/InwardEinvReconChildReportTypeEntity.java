package com.ey.advisory.app.data.entities.client.asprecon;

import com.google.gson.annotations.Expose;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author Sakshi.jain
 *
 */

@Entity
@Table(name = "TBL_RECON_EINVPR_DWNLD_RPT_TYP")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class InwardEinvReconChildReportTypeEntity implements Serializable {

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
