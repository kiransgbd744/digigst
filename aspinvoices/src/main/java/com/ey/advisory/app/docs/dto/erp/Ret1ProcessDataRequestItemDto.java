/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mahesh.Golla
 *
 */
@XmlRootElement(name="item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Ret1ProcessDataRequestItemDto {
	
	private String entityName;
	private String entityPan;
	private String companyCode;
	private String returnPerod;
	private String gstin;
	
	@XmlElement(name = "ProcessData")
	private Ret1ProcessReviewSummItemRequestDto processReviewSumDto;
	
	@XmlElement(name = "ReviewData")
	private Ret1SummaryDataRequestItemDto headerReviewSummDto;
}