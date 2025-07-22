package com.ey.advisory.app.get.notices.handlers;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class GstnNoticeDetailReportRespDto {

	private String entityName;

	private String gstin;

	private String referenceId;

	private String ackNo;

	private String dateOfIssue;

	private String dueDate;

	private String dateOfResponse;

	private String noticeStatus;

	private String timeLeftForResponse;

	private String fromTaxPeriod;

	private String toTaxPeriod;

	private String noticeType;

	private String noticeTypeDescription;

	private String taxableValue;

	private String totalTax;

	private String uniqueModuleCode;

	private String uniqueModuleDesc;

	private String uniqueAlertCode;

	private String uniqueAlertDesc;

	private String mainDocType;

	private String mainDocId;

	private String mainDocHashCode;

	private String suppDocType;

	private String suppDocId;

	private String suppDocHashCode;

}
