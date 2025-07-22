package com.ey.advisory.globaleinv.common;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SAPERPRoot {

	@SerializedName("header")
	@Expose
	public SAPERPHeader header;

	@SerializedName("party")
	@Expose
	public List<SAPERPParty> party;

	@SerializedName("itemParticulars")
	@Expose
	public List<SAPERPItemParticular> itemParticulars;

	@SerializedName("conditions")
	@Expose
	public List<SAPERPCondition> conditions;
}
