package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr1GetInvoicesReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName(value = "ret_period", alternate = ("returnPeriod"))
	private String returnPeriod;

	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

	@Expose
	@SerializedName("to_time")
	private String toTime;

	@Expose
	@SerializedName("state_cd")
	private String stateCode;

	@Expose
	@SerializedName("action_required")
	private String actionRequired = APIConstants.N;

	@Expose
	@SerializedName("etin")
	private String etin;

	@Expose
	@SerializedName("action")
	private String action = APIConstants.N;

	// Extra Columns used for further processing in the App layer.

	@Expose
	@SerializedName("type")
	private String type;

	// Extra Columns used for gstr2x processing in the App layer.

	@Expose
	@SerializedName("rec_type")
	private String recType;

	@Expose
	@SerializedName("apiSection")
	private String apiSection;

	@Expose
	@SerializedName("groupCode")
	private String groupcode;

	@Expose
	@SerializedName("batchId")
	private Long batchId;

	@Expose
	@SerializedName("isFailed")
	private Boolean isFailed;

	// This to do GSTR2A GET AMDHIST.
	@Expose
	@SerializedName("port_code")
	private String portCode;

	@Expose
	@SerializedName("be_number")
	private Long beNum;

	@Expose
	@SerializedName("be_date")
	private String beDate;

	// This is should not be null for GStr1 Get call dependent class is
	// (Gstr1GetApiJobInsertionController)
	@Expose
	@SerializedName("gstr1Sections")
	private List<String> gstr1Sections = new ArrayList<String>();

	// This is should not be null for GStr1 Get EInv call dependent class is
	// (Gstr1GetApiJobInsertionController)
	@Expose
	@SerializedName("gstr1EinvSections")
	private List<String> gstr1EinvSections = new ArrayList<String>();

	// This is should not be null for GStr2A Get call dependent class is
	// (Gstr2aGetApiJobInsertionController)
	@Expose
	@SerializedName("gstr2aSections")
	private List<String> gstr2aSections = new ArrayList<String>();

	// This is should not be null for GStr2A Get call dependent class is
	// (Gstr2XGetApiJobInsertionController)
	@Expose
	@SerializedName("gstr2XSections")
	private List<String> gstr2XSections = new ArrayList<String>();

	@Expose
	@SerializedName("gstr8Sections")
	private List<String> gstr8Sections = new ArrayList<String>();

	@Expose
	private Long userRequestId;

	@Expose
	@SerializedName(value = "fromPeriod", alternate = ("fromTaxPeriod"))
	private String fromPeriod;

	@Expose
	@SerializedName(value = "toPeriod", alternate = ("toTaxPeriod"))
	private String toPeriod;

	@Expose
	@SerializedName("isFiled")
	private String isFiled;

	@Expose
	@SerializedName("parentSection")
	private String parentSection;

	// This to do GSTR2A GET-Screen.
	@Expose
	@SerializedName("month")
	private List<String> month = Lists.newArrayList();

	@Expose
	@SerializedName("finYear")
	private String finYear;

	// Used for GSTR2A File upload data processing
	@Expose
	private Long fileId;

	@Expose
	private String filePath;

	@Expose
	private String fileName;

	// GET2a incremental related fields

	@Expose
	private Boolean isDeltaGet;

	@Expose
	private Boolean isAutoGet;

	@Expose
	private String refId;

	// For einvoice IRN

	@Expose
	private String irn;

	@Expose
	private String irnSts;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("vendorGstin")
	private String vendorGstin;
	
	@Expose
	@SerializedName("vendorPan")
	private List<String> vendorPan;
	
	@Expose
	@SerializedName("vendorGstins")
	private List<String> vendorGstins;
	
	@Expose
	@SerializedName("docNums")
	private List<String> docNums;

	@Expose
	@SerializedName("docNum")
	private String docNum;

	@Expose
	@SerializedName("fromDate")
	private String fromDate;

	@Expose
	@SerializedName("toDate")
	private String toDate;

	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("supplyType")
	private List<String> supplyType = Lists.newArrayList();

	@Expose
	@SerializedName("irnStatus")
	private List<String> irnStatus = Lists.newArrayList();

	@Expose
	@SerializedName("ids")
	private List<Long> ids;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins = Lists.newArrayList();

	@Expose
	@SerializedName("docIds")
	private Long docId;

	@Expose
	@SerializedName("section")
	private String section;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	@Expose
	@SerializedName("isSavedtoGstn")
	private boolean isSavedtoGstn;

	@Expose
	@SerializedName("supplyTypeInvMgmt")
	private String supplyTypeInvMgmt;
	
	@Expose
	@SerializedName("documentId")
	private String documentId;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;
	
	@Expose
	@SerializedName("tableType")
	private List<String> tableType = Lists.newArrayList();
	
	@Expose
	@SerializedName("docType")
	private List<String> docType = Lists.newArrayList();
	
	@Expose
	@SerializedName("digiGstAction")
	private List<String> digiGstAction = Lists.newArrayList();
	
	@Expose
	@SerializedName("gstnAction")
	private List<String> gstnAction = Lists.newArrayList();
	
	@Expose
	@SerializedName("saveId")
	private Long saveId;
	
	@Expose
	@SerializedName("id")
	private List<String> id;
	
	@Expose
	@SerializedName("reportTypes")
	private List<String> reportTypes = Lists.newArrayList();
	
	@Expose
	@SerializedName("reportType")
	private String reportType;

	@Expose
	@SerializedName("actionType")
	private List<String> actionType = Lists.newArrayList();
	
	@Expose
	@SerializedName("returnTypes")
	private List<String> returnTypes = Lists.newArrayList();
	
	@Expose
	@SerializedName("req")
	List<InitiateGetCallDto> gstinTaxPeiordList = new ArrayList<>();
	
	//fileType added 
	@Expose
	@SerializedName("fileType")
	private String fileType;

	@Expose
	@SerializedName("imsReturnTypeList")
	private List<String> imsReturnTypeList;
	
	@Expose
	@SerializedName("gstr1aSections")
	private List<String> gstr1aSections = new ArrayList<String>();
}
