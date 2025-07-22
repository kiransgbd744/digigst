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
public class Gstr3bProcessAndReviewSummaryDto {

	@XmlElement(name = "EntityName")
	private String entity;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "GstinNumber")
	private String gstinNum;

	@XmlElement(name = "ReturnPeriod")
	private String returnPeriod;
	
	@XmlElement(name = "ProcessSum")
	private Gstr3bProcessSummaryDto processSummary;

	@XmlElement(name = "ReviewSum")
	private Gstr3bReverseIntgReviewSummaryDto reviewSummary;
	
	@XmlElement(name = "_--332Popup")
	private Gstr3b32popUpReviewSummaryDto popUp32;
	
	@XmlElement(name = "_--35Popup")
	private Gstr3b5popUpReviewSummaryDto popUp5;
	
	@XmlElement(name = "LibPopup")
	private Gstr3bTabLibpopUpReviewSummaryDto tabLib;
	
	@XmlElement(name = "DiffPopup")
	private Gstr3bDiffLibpopUpReviewSummaryDto diff;
	
	
}