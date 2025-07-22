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
public class GetGstr2aDetailStatusRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("b2bTimeStamp")
	private String b2bTimeStamp;

	@Expose
	@SerializedName("b2bStatus")
	private String b2bStatus;

	@Expose
	@SerializedName("b2baTimeStamp")
	private String b2baTimeStamp;

	@Expose
	@SerializedName("b2baStatus")
	private String b2baStatus;

	@Expose
	@SerializedName("cdnTimeStamp")
	private String cdnTimeStamp;

	@Expose
	@SerializedName("cdnStatus")
	private String cdnStatus;

	@Expose
	@SerializedName("cdnaTimeStamp")
	private String cdnaTimeStamp;

	@Expose
	@SerializedName("cdnaStatus")
	private String cdnaStatus;

	@Expose
	@SerializedName("isdTimeStamp")
	private String isdTimeStamp;

	@Expose
	@SerializedName("isdStatus")
	private String isdStatus;

	@Expose
	@SerializedName("isdaTimeStamp")
	private String isdaTimeStamp;

	@Expose
	@SerializedName("isdaStatus")
	private String isdaStatus;

	@Expose
	@SerializedName("amdhistTimeStamp")
	private String amdhistTimeStamp;

	@Expose
	@SerializedName("amdhistStatus")
	private String amdhistStatus;

	@Expose
	@SerializedName("impgTimeStamp")
	private String impgTimeStamp;

	@Expose
	@SerializedName("impgStatus")
	private String impgStatus;

	@Expose
	@SerializedName("impgSezTimeStamp")
	private String impgSezTimeStamp;

	@Expose
	@SerializedName("impgSezStatus")
	private String impgSezStatus;

}
