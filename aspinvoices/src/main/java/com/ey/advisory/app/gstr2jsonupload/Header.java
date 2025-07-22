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
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Header {

	@SerializedName("gstin")
	@Expose
	public String gstin;

	@SerializedName("fp")
	@Expose
	public String fp;
	
	@SerializedName("b2b")
	@Expose
	public List<B2b> b2b = null;
	
	@SerializedName("b2ba")
	@Expose
	public List<B2bA> b2bA = null;
	
	@SerializedName("cdn")
	@Expose
	public List<Cdn> cdn = null;
	
	@SerializedName("cdna")
	@Expose
	public List<CdnA> cdnA = null;
	
	@SerializedName("isd")
	@Expose
	public List<Isd> isd = null;
}
