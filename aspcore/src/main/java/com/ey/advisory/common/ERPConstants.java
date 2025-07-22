/**
 * 
 */
package com.ey.advisory.common;

/**
 * @author Hemasundar.J
 *
 */
public class ERPConstants {

	public static final String EVENT_BASED_JOB = "EVENT_BASED_JOB";
	public static final String BACKGROUND_BASED_JOB = "BACKGROUND_BASED_JOB";

	public static final String INVOICE_STATUS_HEADER = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'> <soapenv:Header/> <soapenv:Body> <urn:_-digigst_-saveDocResp>";
	public static final String INVOICE_STATUS_FOOTER = "</urn:_-digigst_-saveDocResp> </soapenv:Body> </soapenv:Envelope>";

	public static final String APPROVAL_REQUEST_HEADER = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?> <soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/><soapenv:Body><urn:ZwfAnx1save>";
	public static final String APPROVAL_REQUEST_FOOTER = "</urn:ZwfAnx1save></soapenv:Body></soapenv:Envelope>";

	public static final String PROCESSED_RECORDS_HEADER = "<soapenv:Envelope xmlns:soapenv= \"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\"> <soapenv:Header/>" + "<soapenv:Body> <urn:ZGST_ANX_1_PROCESS_SUM>";
	public static final String PROCESSED_RECORDS_FOOTER = "</urn:ZGST_ANX_1_PROCESS_SUM></soapenv:Body></soapenv:Envelope>";

	public static final String VENDOR_MISMATCH_HEADER = "<soapenv:Envelope xmlns:soapenv= \"http://schemas.xmlsoap.org/soap/ envelope/\" xmlns:urn=\"urn:sap-com:document: sap:rfc:functions\"><soapenv:Header/><soapenv:Body> <urn:ZGST_VEN_COM_IN>";
	public static final String VENDOR_MISMATCH_FOOTER = "</urn:ZGST_VEN_COM_IN></soapenv:Body></soapenv:Envelope>";

	public static final String ANX_DATA_STATUS_HEADER = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:rfc:functions'><soapenv:Header/><soapenv:Body>";
	public static final String ANX_DATA_STATUS_FOOTER = "</soapenv:Body></soapenv:Envelope>";

	public static final String ANX1A_PROCESS_REVIEW_SUMM_HEADER = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'> <soapenv:Header/><soapenv:Body> <urn:ZupdateAnx1aAspdata>";
	public static final String ANX1A_PROCESS_REVIEW_SUMM_FOOTER = "</urn:ZupdateAnx1aAspdata></soapenv:Body></soapenv:Envelope>";

	public static final String ANX1_REVIEW_SUMM_HEADER = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'><soapenv:Header/> <soapenv:Body><urn:ZupdateAnx1Aspdata>";
	public static final String ANX1_REVIEW_SUMM_FOOTER = "</urn:ZupdateAnx1Aspdata></soapenv:Body></soapenv:Envelope>";
	
}
