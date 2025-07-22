package com.ey.advisory.app.services.refidpolling.gstr1;

import com.ey.advisory.common.GSTConstants;

/**
 * @author Siva.Nandam
 *
 */
public class ReversePollingConversion {
	
	public static final String INV = "I";
	public static final String CR = "C";
	public static final String DR = "D";
 private ReversePollingConversion(){}
 
 public static String convert(String docType){
	 String value="";
	 if(INV.equalsIgnoreCase(docType)){
		 value=GSTConstants.INV;
	 }
	 if(CR.equalsIgnoreCase(docType)){
		 value=GSTConstants.CR;
	 }
	 if(DR.equalsIgnoreCase(docType)){
		 value=GSTConstants.DR;
	 }
	return value;
	 
 }
}
