package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetGstr2aDetailStatusSubItemDetailsRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("b2bLastUpdate")
	private String b2bLastUpdate;

	@Expose
	@SerializedName("b2bStatus")
	private String b2bStatus;

	@Expose
	@SerializedName("b2baLastUpdate")
	private String b2baLastUpdate;

	@Expose
	@SerializedName("b2baStatus")
	private String b2baStatus;

	@Expose
	@SerializedName("cdnLastUpdate")
	private String cdnLastUpdate;

	@Expose
	@SerializedName("cdnStatus")
	private String cdnStatus;

	@Expose
	@SerializedName("cdnaLastUpdate")
	private String cdnaLastUpdate;

	@Expose
	@SerializedName("cdnaStatus")
	private String cdnaStatus;

	@Expose
	@SerializedName("isdLastUpdate")
	private String isdLastUpdate;

	@Expose
	@SerializedName("isdStatus")
	private String isdStatus;

	@Expose
	@SerializedName("isdaLastUpdate")
	private String isdaLastUpdate;

	@Expose
	@SerializedName("isdaStatus")
	private String isdaStatus;

	@Expose
	@SerializedName("tcsLastUpdate")
	private String tcsLastUpdate;

	@Expose
	@SerializedName("tcsStatus")
	private String tcsStatus;

	@Expose
	@SerializedName("tdsLastUpdate")
	private String tdsLastUpdate;

	@Expose
	@SerializedName("tdsStatus")
	private String tdsStatus;

	@Expose
	@SerializedName("tdsaLastUpdate")
	private String tdsaLastUpdate;

	@Expose
	@SerializedName("tdsaStatus")
	private String tdsaStatus;

	@Expose
	@SerializedName("getLastUpdate")
	private String getLastUpdate;

	@Expose
	@SerializedName("getStatus")
	private String getStatus;

}
