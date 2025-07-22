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
public class CdnA {
	
	@SerializedName("cfs")
	@Expose
	public String cfs;
	
	@SerializedName("nt")
	@Expose
	public List<CdnANt> nt = null;
	
	@SerializedName("ctin")
	@Expose
	public String ctin;

}
