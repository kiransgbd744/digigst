/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Hemasundar.J
 *
 */
public class PerfamanceEventConstants {
	
	/*------- Module Names ---------  */
	public static final String GSTR1_SAVE_TO_GSTN = "GSTR1_SAVE_TO_GSTN";
	public static final String ERP_PUSH = "ERP_PUSH";
	public static final String ERROR_DOCS_ERP_PUSH = "ERROR_DOCS_ERP_PUSH";
	public static final String QRCODEVALIDATOR = "QRCODEVAL";
	public static final String GSTR_2A_PROCESS = "GSTR2AProcess";
	
	public static final String GSTR6_ISD_DISTRIBUTION = "GSTR6_ISD_DIS_FILE_UPLOAD";

	/*------- Event Names ---------  */
	public static final String GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_START = "GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_START";
	public static final String GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_END = "GSTR1_SAVE_TO_GSTN_RETRY_ASYNC_END";

	public static final String GSTR1_SAVE_TO_GSTIN_ASYNC_START = "GSTR1_SAVE_TO_GSTIN_ASYNC_START";
	public static final String SAVE_CAN_INV_START = "SAVE_CAN_INV_START";
	public static final String FIND_CAN_INV_START = "FIND_CAN_INV_START";
	public static final String FIND_CAN_DATA_START = "FIND_CAN_DATA_START";
	public static final String FIND_CAN_DATA_DB_START = "FIND_CAN_DATA_DB_START";
	public static final String FIND_CAN_DATA_DB_END = "FIND_CAN_DATA_DB_END";
	public static final String FIND_CAN_DATA_END = "FIND_CAN_DATA_END";
	public static final String FIND_CAN_INV_SEC_MAP_CREATE_START = "FIND_CAN_INV_SEC_MAP_CREATE_START";
	public static final String FIND_CAN_INV_SEC_MAP_CREATE_END = "FIND_CAN_INV_SEC_MAP_CREATE_END";
	public static final String FIND_SAV_INV_START = "FIND_SAV_INV_START";
	public static final String FIND_INV_DATA_START = "FIND_INV_DATA_START";
	public static final String FIND_INV_DATA_DB_START = "FIND_INV_DATA_DB_START";
	public static final String FIND_INV_DATA_DB_END = "FIND_INV_DATA_DB_END";
	public static final String FIND_SUMM_DATA_START = "FIND_SUMM_DATA_START";
	public static final String FIND_SUMM_DATA_END = "FIND_SUMM_DATA_END";
	public static final String FIND_SUMM_DATA_DB_START = "FIND_SUMM_DATA_DB_START";
	public static final String FIND_SUMM_DATA_DB_END = "FIND_SUMM_DATA_DB_END";
	public static final String SAV_GST1_DATA_START = "SAV_GST1_DATA_START";
	public static final String SAV_GST1_DATA_END = "SAV_GST1_DATA_END";
	public static final String FIND_INV_DATA_END = "FIND_INV_DATA_END";
	public static final String FIND_SAV_INV_END = "FIND_SAV_INV_END";
	public static final String FIND_CAN_INV_END = "FIND_CAN_INV_END";
	public static final String SAVE_CAN_INV_END = "SAVE_CAN_INV_END";
	public static final String SAVE_ACT_INV_START = "SAVE_ACT_INV_START";
	public static final String SAVE_ACT_INV_END = "SAVE_ACT_INV_END";
	public static final String GSTR1_SAVE_TO_GSTIN_ASYNC_END = "GSTR1_SAVE_TO_GSTIN_ASYNC_END";
	public static final String FIND_SUMM_DATA_PROC_START = "FIND_SUMM_DATA_PROC_START";
	public static final String FIND_SUMM_DATA_PROC_END = "FIND_SUMM_DATA_PROC_END";
	
	public static final String SAV_GST1_JSON_FORMATION_START = "SAV_GST1_JSON_FORMATION_START";
	public static final String SAV_GST1_JSON_FORMATION_END = "SAV_GST1_JSON_FORMATION_END";
	
	public static final String SAV_GST1_SANDBOX_START = "SAV_GST1_SANDBOX_START";
	public static final String SAV_GST1_SANDBOX_END = "SAV_GST1_SANDBOX_END";
	
	
	
	public static final String ERROR_DOCS_ERP_PUSH_ASYNC_START = "ERROR_DOCS_ERP_PUSH_ASYNC_START";
	public static final String ERROR_DOCS_ERP_PUSH_ASYNC_END = "ERROR_DOCS_ERP_PUSH_ASYNC_END";
	public static final String ERP_DOCS_TO_ERP_START = "ERP_DOCS_TO_ERP_START";
	public static final String ERP_DOCS_TO_ERP_END = "ERP_DOCS_TO_ERP_END";
	public static final String PUSH_TO_ERP_START = "PUSH_TO_ERP_START";
	public static final String PUSH_TO_ERP_END = "PUSH_TO_ERP_END";
	
	public static final String ERP_DOCS_DB_FETCH_START = "ERP_DOCS_DB_FETCH_START";
	public static final String ERP_DOCS_DB_FETCH_END = "ERP_DOCS_DB_FETCH_END";
	
	public static final String ERP_DOCS_JSON_FORMATION_START = "ERP_DOCS_JSON_FORMATION_START";
	public static final String ERP_DOCS_JSON_FORMATION_END = "ERP_DOCS_JSON_FORMATION_END";

	public static final String DESTINATION_CALL_START = "DESTINATION_CALL_START";
	public static final String DESTINATION_CALL_END = "DESTINATION_CALL_END";

	/*------- class Names ---------  */
	public static final String Gstr1SaveToGstnRetryProcessor = "Gstr1SaveToGstnRetryProcessor";

	public static final String Gstr1SaveToGstnProcessor = "Gstr1SaveToGstnProcessor";
	public static final String Gstr1SaveToGstnJobHandler = "Gstr1SaveToGstnJobHandler";
	public static final String Gstr1CancelledInvicesIdentifierImpl = "Gstr1CancelledInvicesIdentifierImpl";
	public static final String SaveGstr1DataFetcherImpl = "SaveGstr1DataFetcherImpl";
	public static final String DocSearchForSaveCustomRepositoryImpl = "DocSearchForSaveCustomRepositoryImpl";
	public static final String Gstr1SaveInvicesIdentifierImpl = "Gstr1SaveInvicesIdentifierImpl";
	public static final String Gstr1BatchMakerImpl = "Gstr1BatchMakerImpl";
	
	public static final String RateDataToB2bB2baConverter = "RateDataToB2bB2baConverter";
	public static final String Gstr1SaveB2bB2baBatchProcessImpl = "Gstr1SaveB2bB2baBatchProcessImpl";

	public static final String Anx1ErrorDocsRevIntegrationProcessor = "Anx1ErrorDocsRevIntegrationProcessor";
	public static final String Anx1ErrorDocsRevIntegrationHandler = "Anx1ErrorDocsRevIntegrationHandler";
	public static final String DestinationConnectivity = "DestinationConnectivity";

	
	/*------- method Names ---------  */
	public static final String execute = "execute";
	public static final String saveCancelledInvoices = "saveCancelledInvoices";
	public static final String findCanInvoices = "findCanInvoices";
	public static final String findCancelledData = "findCancelledData";
	public static final String findGstr1CancelledData = "findGstr1CancelledData";
	public static final String findGstr1InvoiceLevelData = "findGstr1InvoiceLevelData";
	public static final String findGstr1SummaryLevelData = "findGstr1SummaryLevelData";
	public static final String findGstr1SummaryData = "findGstr1SummaryData";
	public static final String findSaveInvoices = "findSaveInvoices";
	public static final String findInvoiceLevelData = "findInvoiceLevelData";
	public static final String findSummaryData = "findSummaryData";
	public static final String saveGstr1Data = "saveGstr1Data";
	public static final String saveActiveInvoices = "saveActiveInvoices";
	
	public static final String convertToGstr1Object = "convertToGstr1Object";
	
	public static final String erpErrorDocsToErp = "erpErrorDocsToErp";
	public static final String pushToErp = "pushToErp";
	public static final String post = "post";

	
	/*-------  context ---------  */
	public static final String GSTIN_Ret_Period = "GSTIN,Return Period";
	public static final String For_loop_size = "For loop size";
	public static final String GSTIN_Ret_Period_section = "GSTIN,Return Period,Section";
	public static final String Section_Docs_Size = "Section,docs size";
	public static final String BATCH_NO = "BATCH_NO";
	
	
	public static final String FILE_PROCESSING="FILE_PROCESSING";


	
}
