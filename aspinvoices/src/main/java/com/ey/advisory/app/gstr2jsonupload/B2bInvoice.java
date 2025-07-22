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
public class B2bInvoice {
	
	@SerializedName("itms")
	@Expose
	public List<B2bItem> itms = null;
	
	@SerializedName("val")
	@Expose
	public BigDecimal val;
	
	@SerializedName("inv_typ")
	@Expose
	public String invTyp;
	
	@SerializedName("pos")
	@Expose
	public String pos;
	
	@SerializedName("idt")
	@Expose
	public String idt;
	
	@SerializedName("rchrg")
	@Expose
	public String rchrg;
	
	@SerializedName("inum")
	@Expose
	public String inum;
	
	@SerializedName("chksum")
	@Expose
	public String chksum;

}
