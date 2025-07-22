package com.ey.advisory.app.data.services.customisedreport;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
public class CustomisedReportReqDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("reportType")
	private String reportType;

	@Expose
	@SerializedName("reportId")
	private String reportId;

	@Expose
	@SerializedName("Basic_Details")
	private List<CustomisedReportListDto> basicDetails;
	
	@Expose
	@SerializedName("DigiGst_Spec")
	private List<CustomisedReportListDto> digiGstSpecFields;

	@Expose
	@SerializedName("Export_Details")
	private List<CustomisedReportListDto> exportDetails;
	
	@Expose
	@SerializedName("EinvEwbGstResp")
	private List<CustomisedReportListDto> einvEwbGstResp;
	
	@Expose
	@SerializedName("GL_Details")
	private List<CustomisedReportListDto> glDetails;

	@Expose
	@SerializedName("ItemVal_Details")
	private List<CustomisedReportListDto> itemValDetails;

	@Expose
	@SerializedName("Supp_Details")
	private List<CustomisedReportListDto> supplierDetails;

	@Expose
	@SerializedName("Cust_Details")
	private List<CustomisedReportListDto> custDetails;

	@Expose
	@SerializedName("Disp_Details")
	private List<CustomisedReportListDto> dispDetails;
	
	@Expose
	@SerializedName("Ship_Details")
	private List<CustomisedReportListDto> shipDetails;

	@Expose
	@SerializedName("Order_Details")
	private List<CustomisedReportListDto> orderDetails;

	@Expose
	@SerializedName("IncTax_Details")
	private List<CustomisedReportListDto> incTaxDetails;

	@Expose
	@SerializedName("OrderRef_Details")
	private List<CustomisedReportListDto> orderRefDetails;

	@Expose
	@SerializedName("Parties_Inv")
	private List<CustomisedReportListDto> partiesInv;
	
	@Expose
	@SerializedName("Other_Details")
	private List<CustomisedReportListDto> otherDetails;
	
	@Expose
	@SerializedName("Trans_Details")
	private List<CustomisedReportListDto> transDetails;

	@Expose
	@SerializedName("OrgHei_Details")
	private List<CustomisedReportListDto> orgheiDetails;

	@Expose
	@SerializedName("UserDef_Details")
	private List<CustomisedReportListDto> userDefDetails;
	
}
