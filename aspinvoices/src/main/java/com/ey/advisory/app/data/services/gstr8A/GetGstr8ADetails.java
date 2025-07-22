/**
 * 
 */
package com.ey.advisory.app.data.services.gstr8A;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Arun KA
 *
 */
@Data
public class GetGstr8ADetails {
	
	@SerializedName("gstin")
	@Expose
	String gstin;
	
	@SerializedName("fy")
	@Expose
	String fy;
	
	@SerializedName("docid")
	@Expose
	int docid;
	
	@SerializedName("b2b")
	@Expose
	private List<Gstr8AInvoiceDetails> b2bDetails;

	@SerializedName("b2ba")
	@Expose
	private List<Gstr8AInvoiceDetails> b2baDetails;

	@SerializedName("cdn")
	@Expose
	private List<Gstr8AInvoiceDetails> cdnDetails;

	@SerializedName("cdna")
	@Expose
	private List<Gstr8AInvoiceDetails> cdnaDetails;

}
