/**
 * 
 */
package com.ey.advisory.gstnapi;

/**
 * @author Khalid1.Khan
 *
 */
public class GstnApiWrapperConstants {

	private GstnApiWrapperConstants() {
	}

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	public static final String INPROGRESS = "INPROGRESS";
	public static final String SUBMITTED = "SUBMITTED";
	public static final String SYSTEM = "SYSTEM";
	public static final String GSTN_ERROR = "GSTNERROR";
	public static final String INTERNAL_ERROR = "INTERNALERROR";
	public static final String APIINVOCATIONRETRYBLOCK = 
			"ApiInvocationRetryBlock";
	public static final String TOKEN_RESPONSE_RETRY_BLOCK = 
			"TokenResponseRetryBlockImpl";
	public static final String FILE_CHUNK_RESPONSE_RETRY_BLOCK = 
			"FileChunkResponseRetryBlockImpl";
	public static final String URL_FETCH_ERROR = 
			"URLFETCHERROR";
	public static final String FILE_FETCH_ERROR = 
			"FILEFETCHERROR";
	
	public static final String NO_INVOICE_ERR_CODE1 = "RET13508";
	public static final String NO_INVOICE_ERR_CODE2 = "RET13509";
	public static final String NO_INVOICE_ERR_CODE3 = "RET13510";
	public static final String NO_INVOICE_ERR_CODE4 = "RET11416";
	public static final String NO_INVOICE_ERR_CODE5 = "RET11417";
	public static final String NO_INVOICE_ERR_CODE6 = "RET2B1006";
	public static final String NO_INVOICE_ERR_CODE7 = "RET2B1016";
	public static final String NO_INVOICE_ERR_CODE8 = "RET2B1018";
	public static final String NO_INVOICE_ERR_CODE9 = "RET2B1023";
	public static final String NO_INVOICE_ERR_CODE10 = "RT-ANX1AG-1020";
	
	public static final String NO_EINVOICE_ERR_CODE11 = "EINV30107";
	public static final String NO_EINVOICE_ERR_CODE12 = "EINV30108";
	public static final String NO_EINVOICE_ERR_CODE13 = "EINV30131";

	public static final String GATEWAY_TOKEN_URL_PREFIX 
	= "https://api.eygsp.in/fileapi/filedownload";
	public static  Integer URL_RETRY_COUNT = 4;
	
	/**headers keys*/
	public static final String DIGI_GST_USERNAME = "digigst_username";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String API_KEY = "api_key";
	public static final String API_SECRET = "api_secret";
	public static final String IP_USER = "ip-usr";
	public static final String AUTH_TOKEN = "auth-token";
	public static final String USER_NAME = "username";
	public static final String STATE_CODE = "state-cd";
	public static final String TXN = "txn";
	public static final String GSTIN = "gstin";
	public static final String RETURN_PERIOD = "ret_period";
	public static final String CONTENT_TYPE = "Content-Type";
	
	public static final String EINVOICE_IDENTIFIER = "einvdownloads";

	public static final String EINVOICE_IDENTIFIER_TO_BE_REPLACED = "files.gst.gov.in/einvdownloads";

	public static final String EINVOICE_IDENTIFIER_REPLACED_WITH = "api.eygsp.com/fileapi/einvdownloads";

	

}
