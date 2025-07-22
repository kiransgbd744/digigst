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
 * @author siva
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr7ProcessAndReviewSummaryDto {

	@XmlElement(name = "EntityName")
	private String entity;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "GstinNumber")
	private String gstinNum;

	@XmlElement(name = "ReturnPeriod")
	private String returnPeriod;
	
	@XmlElement(name = "ProcessSum")
	private Gstr7ProcessSummaryDto processSummary;

	@XmlElement(name = "ReviewSum")
	private Gstr7ReverseIntgReviewSummaryDto reviewSummary;
	
	@XmlElement(name = "Tab3Popup")
	private Gstr7ReverseIntgTable3SummaryDto table3;
	
	@XmlElement(name = "Tab4Popup")
	private Gstr7ReverseIntgTable4SummaryDto table4;
	@XmlElement(name = "DiffPopup")
	private Gstr7ReverseIntgDiffSummaryDto diff;
	
}