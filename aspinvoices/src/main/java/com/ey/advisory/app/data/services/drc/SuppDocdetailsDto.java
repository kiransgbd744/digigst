package com.ey.advisory.app.data.services.drc;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SuppDocdetailsDto {
	

	
	@SerializedName("id")
	@Expose
	private String suppDocId;

	@SerializedName("ty")
	@Expose
	private String type;

	@SerializedName("ct")
	@Expose
	private String contentType;

	@SerializedName("docname")
	@Expose
	private String docName;

	@SerializedName("hash")
	@Expose
	private String hash;

}
