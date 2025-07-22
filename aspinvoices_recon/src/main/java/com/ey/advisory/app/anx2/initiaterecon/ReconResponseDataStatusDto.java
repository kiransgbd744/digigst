package com.ey.advisory.app.anx2.initiaterecon;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;

import lombok.Data;
/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class ReconResponseDataStatusDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Expose
	private LocalDate dateOfUpload;
	
	@Expose
	private String uploadeBy;
	
	@Expose
	private String dataType;
	
	@Expose
	private String fileType;
	
	@Expose
	private String FileName;
	
	@Expose
	private String fileStatus;
	
	@Expose
	private Integer errorCount;
	
	@Expose
	private Integer totalRecordsCount;
	
	@Expose
	private Integer processedCount;
	
	@Expose
	private Long fileId;

	
	
}
