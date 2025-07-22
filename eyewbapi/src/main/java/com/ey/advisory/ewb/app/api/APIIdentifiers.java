package com.ey.advisory.ewb.app.api;

public interface APIIdentifiers {

	public static final String GET_AUTH_TOKEN = "ACCESSTOKEN";

	public static final String GENERATE_EWB = "GENEWAYBILL";

	public static final String UPDATE_VEHICLE_DETAILS = "VEHEWB";

	public static final String EXTEND_VEHICLE_DETAILS = "EXTENDVALIDITY";

	public static final String GENERATE_CONSOLIDATED_EWB = "GENCEWB";

	public static final String CANCEL_EWB = "CANEWB";

	public static final String REJECT_EWB = "REJEWB";

	public static final String GET_EWB = "GETEWB";

	public static final String GET_EWB_FOR_TRANSPORTER = "GETEWBFORTRANS";

	public static final String REFRESH_EWB = "GETEWAYBILL";

	public static final String GET_TRANSPORTER = "GETTRANSIN";

	public static final String UPDATE_TRANSPORTER = "UPDATETRANSPORTER";

	public static final String GET_EWAYBILLS_FOR_OTHER_PARTY = 
			"GETOTHERPARTYEWBS";

	public static final String GET_EWB_BY_DATE = "GETEWBBYDATE";

	public static final String GET_GSTIN = "GETGSTIN";

	public static final String GET_HSN = "GETHSN";

	public static final String GET_ERROR_LIST = "GETERRORLIST";

	public static final String Gen_CEWB = "GENCEWB";

	public static final String Regen_CEWB = "REGENTRIPSHEET";

	public static final String MultiVehMovement = "MULTIVEHMOVINT";

	public static final String MultiVehUpdate = "MULTIVEHUPD";

	public static final String MultiVehAdd = "MULTIVEHADD";

	public static final String GET_CEWB = "GETCEWB";

	public static final String GetEwayBillsByDate = "GETEWAYBILLSBYDATE";

	public static final String GET_EWB_BY_CONSIGNOR = 
			"GETEWAYBILLGENERATEDBYCONSIGNER";
	
	public static final String GET_EWAY_BILL_GENERATED_BY_CONSIGNER = 
			"GETEWAYBILLGENERATEDBYCONSIGNER";
	
	public static final String GetEwayBillsRejectedByOthers = "GETEWAYBILLSREJECTEDBYOTHERS";
	
	public static final String UPDATE_PARTB = "UPDATEPARTB";

}
