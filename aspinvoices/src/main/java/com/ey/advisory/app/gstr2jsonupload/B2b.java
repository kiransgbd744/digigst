/**
 * 
 */
package com.ey.advisory.app.gstr2jsonupload;

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
public class B2b {
	
	@SerializedName("ctin")
	@Expose
	public String ctin;
	
	@SerializedName("cfs")
	@Expose
	public String cfs;
	
	@SerializedName("cname")
	@Expose
	public String cname;
	
	@SerializedName("inv")
	@Expose
	public List<B2bInvoice> inv = null;

}
