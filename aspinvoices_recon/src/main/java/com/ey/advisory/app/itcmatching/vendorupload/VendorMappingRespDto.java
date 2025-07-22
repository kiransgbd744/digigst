package com.ey.advisory.app.itcmatching.vendorupload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class VendorMappingRespDto {

	 private String vendorGstin = "";
	    private String vendorName = "";
	    private String statusOfLastGetCall = "Not Initiated";
	    private String lastUpdated = "";
	    private String gstinStatus = "";
	    private String einvoiceApplicability = "";
	    private String constitutionOfBusiness = "";
	    private String taxpayerType = "";
	    private String legalNameOfBusiness = "";
	    private String tradeName = "";
	    private String dateOfRegistration = "";
	    private String dateOfCancellation = "";
	    private String natureOfBusinessActivity = "";
	    private String buildingName = "";
	    private String street = "";
	    private String location = "";
	    private String doorNumber = "";
	    private String stateName = "";
	    private String floorNumber = "";
	    private String pinCode = "";
	    private String centreJurisdiction = "";
	    private String stateJurisdiction = "";
	    
	    private String errorCode = "";
	    private String errorDiscription = "";
	    private String updatedBy = "";
	    
	
	/*private String vendorGstin;
	private String vendorName;
	private String statusOfLastGetCall;
	private String lastUpdated;
	private String gstinStatus;
	private String einvoiceApplicability;
	private String constitutionOfBusiness;
	private String taxpayerType;
	private String legalNameOfBusiness;
	private String tradeName;
	private String dateOfRegistration;
	private String dateOfCancellation;
	private String natureOfBusinessActivity;
	private String buildingName;
	private String street;
	private String location;
	private String doorNumber;
	private String stateName;
	private String floorNumber;
	private String pinCode;
	private String centreJurisdiction;
	private String stateJurisdiction;
*/
	
	
}
