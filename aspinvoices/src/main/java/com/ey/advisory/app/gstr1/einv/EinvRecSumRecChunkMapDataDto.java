package com.ey.advisory.app.gstr1.einv;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva Reddy
 *
 */
@Data
public class EinvRecSumRecChunkMapDataDto {
	
	@Expose
	@SerializedName("CHUNK_ID")
	private Long chunkId ;
	
	@Expose
	@SerializedName("REPORT_DOWNLOAD_ID")
	private Integer reportdownloadId;
	
	@Expose
	@SerializedName("REPORT_TYPE")
	private Integer reportType;
	

}
