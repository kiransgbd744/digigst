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
 * @author Umesha M
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class Gstr1ProcessAndReviewSummaryDto {

	@XmlElement(name = "EntityName")
	private String entity;

	@XmlElement(name = "EntityPan")
	private String entityPan;

	@XmlElement(name = "CompanyCode")
	private String companyCode;

	@XmlElement(name = "RetPer")
	private String retPer;

	@XmlElement(name = "State")
	private String state;

	@XmlElement(name = "GstinNum")
	private String gstinNum;

	@XmlElement(name = "DataType")
	private String dataType;

	@XmlElement(name = "TaxCate")
	private String taxCate;

	@XmlElement(name = "TaxTable")
	private String taxTable;

	@XmlElement(name = "TaxDoctype")
	private String taxDoctype;

	@XmlElement(name = "Erdat")
	private String erDate;

	@XmlElement(name = "Cputm")
	private String currentTime;

	@XmlElement(name = "ProcessData")
	private Gstr1ProcessSummaryDto processSummary;

	@XmlElement(name = "ReviewData")
	private Gstr1ReverseIntgReviewSummaryDto reviewSummary;

	@XmlElement(name = "DiffPopup")
	private DiffProcSummaryRevDto diffPopup;

	@XmlElement(name = "B2csPopup")
	private B2csPopupDto b2csPopupDto;

	@XmlElement(name = "B2csaPopup")
	private B2csaPopupDto b2csaPopupDto;

	@XmlElement(name = "HsnPopup")
	private HsnPopupDto hsnPopupDto;

	@XmlElement(name = "DocPopup")
	private DocPopupDto docPopupDto;

	@XmlElement(name = "NilPopup")
	private NilPopupDto nilPopupDto;

	@XmlElement(name = "Advp111aPopup")
	private Advp111aPopupDto advp111aPopupDto;

	@XmlElement(name = "Advp111bPopup")
	private Advp111bPopupDto advp111bPopupDto;

	@XmlElement(name = "Advp211aPopup")
	private Advp211aPopupDto advp211aPopupDto;

	@XmlElement(name = "Advp211bPopup")
	private Advp211bPopupDto advp211bPopupDto;
}