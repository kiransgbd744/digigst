package com.ey.advisory.app.vendor.service;

/**
 * @author vishal.verma
 */

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class VendorDto {

	@XmlElement(name = "VENDOR_GSTIN")
	private String vendorGSTIN;

	@XmlElement(name = "FINANCIAL_YEAR")
	private String financialYear;

	@XmlElement(name = "VENDORLEGALNAME")
	private String vendorLegalName;

	@XmlElement(name = "GSTN_STATUS")
	private String gstnStatus;

	@XmlElement(name = "TAXPAYER_TYPE")
	private String taxpayerType;

	@XmlElement(name = "EINVAPPLICLIBITY")
	private String eInvoiceAppliclibity;

	@XmlElement(name = "CONSTOFBUSINESS")
	private String constitutionofBusiness;

	@XmlElement(name = "CENTRJURISDICTIN")
	private String centreJurisdiction;

	@XmlElement(name = "TRADENAME")
	private String tradeName;

	@XmlElement(name = "NATOFBUSACTIVITY")
	private String natureofBusinessActivity;

	@XmlElement(name = "BUILDINGNAME")
	private String buildingName;

	@XmlElement(name = "STREET")
	private String street;

	@XmlElement(name = "LOCATION")
	private String location;

	@XmlElement(name = "DOORNUMBER")
	private String doorNumber;

	@XmlElement(name = "STATENAME")
	private String stateName;

	@XmlElement(name = "FLOORNUMBER")
	private String floorNumber;

	@XmlElement(name = "PINCODE")
	private String pincode;

	@XmlElement(name = "DTOFREGISTRATION")
	private String dateofRegistration;

	@XmlElement(name = "STATJURISDICTION")
	private String stateJurisdiction;

	@XmlElement(name = "DTOFCANCELLATION")
	private String dateOfCancellation;

	@XmlElement(name = "FILING_TYPE")
	private String filingType;

	@XmlElement(name = "RETURNTYPE")
	private String returnType;

	@XmlElement(name = "TOTALRETURNFILED")
	private String totalReturnFiled;

	@XmlElement(name = "RETRNFILEDINTIME")
	private String returnFiledInTime;

	@XmlElement(name = "LATERETURNFILED")
	private String lateReturnFiled;

	@XmlElement(name = "TOTRETRNNOTFILED")
	private String returnNotFiled;

	@XmlElement(name = "GSTCOMPLNCESCORE")
	private String gstComplianceScoreFY;

	@XmlElement(name = "POTENTIALNONCOMP")
	private String potentialNonComplianceCategory;

	@XmlElement(name = "PERIODITCMATCH")
	private String periodForITCMatchingPoints;

	@XmlElement(name = "ITCMATCHINGPOINT")
	private String itcMatchingPointsFY;

	@XmlElement(name = "ERRORMESSAGE")
	private String errorMessage;

	@XmlElement(name = "LASTUPDATEDAT")
	private LocalDateTime lastUpdatedAt;

	@XmlElement(name = "PAYLOADID")
	private String payloadId;

	@XmlElement(name = "APRIL")
	private String april;

	@XmlElement(name = "MAY")
	private String may;

	@XmlElement(name = "JUNE")
	private String june;

	@XmlElement(name = "JULY")
	private String july;

	@XmlElement(name = "AUGUST")
	private String august;

	@XmlElement(name = "SEPTEMBER")
	private String september;

	@XmlElement(name = "OCTOBER")
	private String october;

	@XmlElement(name = "NOVEMBER")
	private String november;

	@XmlElement(name = "DECEMBER")
	private String december;

	@XmlElement(name = "JANUARY")
	private String january;

	@XmlElement(name = "FEBRUARY")
	private String february;

	@XmlElement(name = "MARCH")
	private String march;
	
	//Not available in Req params
	
	/*@XmlElement(name = "VENDOR_CODE")
	private String vendorCode;
	
	@XmlElement(name = "VENDOR_PAN")
	private String vendorPan;
	
	@XmlElement(name = "INITIATED_FROM")
	private String initiatedFrom;
	
	@XmlElement(name = "PUSHTODIGISTATUS")
	private String pushToDigiGSTStatus;
	
	@XmlElement(name = "MANDT")
	private String mandt;
	*/
	
}
