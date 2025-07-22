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
public class B2bAInvoice {
	
	@SerializedName("itms")
	@Expose
	public List<B2bAItem> itms = null;
	
	@SerializedName("val")
	@Expose
	public BigDecimal val;
	
	@SerializedName("oinum")
	@Expose
	public String oinum;
	
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
	
	@SerializedName("oidt")
	@Expose
	public String oidt;
	
	@SerializedName("chksum")
	@Expose
	public String chksum;

}
