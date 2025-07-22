/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Arun.KA
 *
 */

@Data
public class Gstr8AInvoiceDetails {
	
	@SerializedName("stin")
	@Expose
	String stin;
	
	@SerializedName("rtnPrd")
	@Expose
	String returnPeriod;
	
	@SerializedName("filingdt")
	@Expose
	LocalDate filingDate;
	
	@SerializedName("section")
	@Expose
	String section;
	
	@SerializedName("documents")
	@Expose
	List<GSTR8ADocDetails> docDetails;

}
