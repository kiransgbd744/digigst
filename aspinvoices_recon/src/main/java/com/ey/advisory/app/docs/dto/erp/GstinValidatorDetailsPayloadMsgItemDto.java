package com.ey.advisory.app.docs.dto.erp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "ITEM")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GstinValidatorDetailsPayloadMsgItemDto {

	@XmlElement(name = "ID")
	private String id;

	@XmlElement(name = "PAN")
	private String pan;

	@XmlElement(name = "CUSTOMERCODE")
	private String customerCode;

	@XmlElement(name = "GSTIN")
	private String gstin;

	@XmlElement(name = "VALIDATIONSTATUS")
	private String status;

	@XmlElement(name = "TAXPAYERTYPE")
	private String taxType;

	@XmlElement(name = "GSTINSTATUS")
	private String gstinStatus;

	@XmlElement(name = "FILINGTYPE")
	private String filingType;

	@XmlElement(name = "EINVAPPLICLIBITY")
	private String einvApp;

	@XmlElement(name = "CONSTOFBUSINESS")
	private String constOfBuss;

	@XmlElement(name = "CENTJURISDICTION")
	private String centrJuris;

	@XmlElement(name = "GSTNLEGALNAME")
	private String legalName;
	
	@XmlElement(name = "TRADENAME")
	private String tradeName;
	
	@XmlElement(name = "NATOFBUSACTIVITY")
	private String natOfBuss;
	
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
	
	@XmlElement(name = "DATEREGISTRATION")
	    private String dateRegistration;
	
	@XmlElement(name = "STATEJURISDICTN")
	    private String stateJurisdiction;
	
	@XmlElement(name = "DATECANCELLATION")
	    private String dateCancellation;
	
	@XmlElement(name = "ERRORCODE")
    private String errorCode;

	@XmlElement(name = "ERRORDESC")
    private String errorDesc;

}
