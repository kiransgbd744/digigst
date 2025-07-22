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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Isd {
	
	@SerializedName("ctin")
	@Expose
	public String ctin;
	
	@SerializedName("cfs")
	@Expose
	public String cfs;
	
	@SerializedName("doclist")
	@Expose
	public List<IsdDocList> doclist = null;

}
