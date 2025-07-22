/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class EInvAndEwbGenRequest {
	
	@Expose
	@SerializedName("docIds")
	private List<Long> docIds;

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;
	
	@Expose
	@SerializedName("docId")
	private Long docId;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;


}
