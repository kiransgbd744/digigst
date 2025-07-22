package com.ey.advisory.app.gstr2jsonupload;

import java.math.BigDecimal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IsdDocList {
	
	@SerializedName("chksum")
	@Expose
	public String chksum;
	
	@SerializedName("isd_docty")
	@Expose
	public String isdDocty;
	
	@SerializedName("docnum")
	@Expose
	public String docnum;
	
	@SerializedName("docdt")
	@Expose
	public String docdt;
	
	@SerializedName("itc_elg")
	@Expose
	public String itcElg;
	
	@SerializedName("iamt")
	@Expose
	public BigDecimal iamt;
	
	@SerializedName("camt")
	@Expose
	public BigDecimal camt;
	
	@SerializedName("samt")
	@Expose
	public BigDecimal samt;
	
	@SerializedName("cess")
	@Expose
	public BigDecimal cess;

}
