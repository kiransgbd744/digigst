package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsEntityReqDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;

	@Expose
	@SerializedName("gstins")
	public List<String> gstins;

	@Expose
	@SerializedName("gstin")
	public String gstin;

	@Expose
	@SerializedName("returnPeriod")
	public String returnPeriod;

	@Expose
	@SerializedName("returnTypes")
	public List<String> returnTypes;

	@Expose
	@SerializedName("tableTypes")
	public List<String> tableTypes;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	@Expose
	@SerializedName("tableType")
	private String tableType;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("actionType")
	public String actionType;

	@Expose
	@SerializedName("fromRetPeriod")
	public String fromRetPeriod;

	@Expose
	@SerializedName("toRetPeriod")
	public String toRetPeriod;

}
