package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author kiran s
 *
 */
@Data
public class Gstr2a2bVs3BbReqIdWiseDataDto {
	
	private Long requestId;
	private Long gstinCount;
	private String fromTaxPeriod;
	private String toTaxPeriod;
	private String initiatedOn;
	private String initiatedBy;
	private String completionOn;
	private String filePath;
	private String status;
	private Long entityId;
	private Clob gstinss;
	private String docId;
	private List<Map<String, String>> gstins;
	
	

	

	

	

}
