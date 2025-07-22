/**
 * 
 */
package com.ey.advisory.app.gstr2jsonupload;

import java.math.BigDecimal;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CdnNt {
	
	@SerializedName("itms")
	@Expose
	public List<CdnItem> itms = null;
	
	@SerializedName("val")
	@Expose
	public BigDecimal val;
	
	@SerializedName("ntty")
	@Expose
	public String ntty;
	
	@SerializedName("idt")
	@Expose
	public String idt;
	
	@SerializedName("nt_num")
	@Expose
	public String ntNum;
	
	@SerializedName("inum")
	@Expose
	public String inum;
	
	@SerializedName("nt_dt")
	@Expose
	public String ntDt;
	
	@SerializedName("p_gst")
	@Expose
	public String pGst;
	
	@SerializedName("chksum")
	@Expose
	public String chksum;
	
	
}
