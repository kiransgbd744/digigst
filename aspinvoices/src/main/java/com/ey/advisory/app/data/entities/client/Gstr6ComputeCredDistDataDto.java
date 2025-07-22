package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author kiran s
 *
 */
@Data
public class Gstr6ComputeCredDistDataDto {
	
	private Long requestId;
	private Long noOfGstin;
	private String taxPeriod;
	private String initiatedOn;
	private String InitiatedBy;
	private String completedOn;
	private String filePath;
	private String status;
	private Long entityId;
	private Clob gstins;
	private String docId;
	private List<Map<String, String>> gstinsList;
	
	

	

	

	

}
