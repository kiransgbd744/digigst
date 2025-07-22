/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp.gstr6;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Umesha M
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr6AProcessAndReviewItemSummaryDto {

	@XmlElement(name = "EntityPan")
	private String entityPan;
	
	@XmlElement(name = "EntityName")
	private String entityName;

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "ReturnPeriod")
	private String returnPeriod;

	@XmlElement(name = "GstinNumber")
	private String gstinNumber;
	
	@XmlElement(name = "ProcessSummary")
	private Gstr6AProcessHeaderSummaryDto processSummary;

	@XmlElement(name = "ReviewSummary")
	private Gstr6AReviewHeaderSummaryDto reviewSummary;

}