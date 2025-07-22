package com.ey.advisory.app.docs.dto.erp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OutwardPayloadErrorItemDto {

	private String id;
	private String entityPan;
	private String entityName;
	private String docType;
	private String docNo;
	private String payloadId;
	private String supplierGstin;
	private String companycode;
	private String fiscalyear;
	private String accVcherNo;
	private String docDate;
	private String receivedDate;
	
	private OutwardErrorItemsDto errors;
	private String entityId;
}
