/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetProcessedVsSubmittedStatusRespDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("getType")
	private String getType;
	
	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("lastUpdatedTimeStamp")
	private String lastUpdatedTimeStamp;
	
	/*@Expose
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
	@SerializedName("b2clTimeStamp")
	private String b2clTimeStamp;

	@Expose
	@SerializedName("b2clStatus")
	private String b2clStatus;

	@Expose
	@SerializedName("b2claTimeStamp")
	private String b2claTimeStamp;

	@Expose
	@SerializedName("b2claStatus")
	private String b2claStatus;

	@Expose
	@SerializedName("expTimeStamp")
	private String expTimeStamp;

	@Expose
	@SerializedName("expStatus")
	private String expStatus;

	@Expose
	@SerializedName("expaTimeStamp")
	private String expaTimeStamp;

	@Expose
	@SerializedName("expaStatus")
	private String expaStatus;

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
	@SerializedName("cdnurTimeStamp")
	private String cdnurTimeStamp;

	@Expose
	@SerializedName("cdnurStatus")
	private String cdnurStatus;

	@Expose
	@SerializedName("cdnuraTimeStamp")
	private String cdnuraTimeStamp;

	@Expose
	@SerializedName("cdnuraStatus")
	private String cdnuraStatus;

	@Expose
	@SerializedName("b2csTimeStamp")
	private String b2csTimeStamp;

	@Expose
	@SerializedName("b2csStatus")
	private String b2csStatus;

	@Expose
	@SerializedName("b2csaTimeStamp")
	private String b2csaTimeStamp;

	@Expose
	@SerializedName("b2csaStatus")
	private String b2csaStatus;

	@Expose
	@SerializedName("nilTimeStamp")
	private String nilTimeStamp;

	@Expose
	@SerializedName("nilStatus")
	private String nilStatus;

	@Expose
	@SerializedName("advRecTimeStamp")
	private String advRecTimeStamp;

	@Expose
	@SerializedName("advRecStatus")
	private String advRecStatus;

	@Expose
	@SerializedName("advAdjTimeStamp")
	private String advAdjTimeStamp;

	@Expose
	@SerializedName("advAdjStatus")
	private String advAdjStatus;

	@Expose
	@SerializedName("hsnTimeStamp")
	private String hsnTimeStamp;

	@Expose
	@SerializedName("hsnStatus")
	private String hsnStatus;

	@Expose
	@SerializedName("docSeriesTimeStamp")
	private String docSeriesTimeStamp;

	@Expose
	@SerializedName("docSeriesStatus")
	private String docSeriesStatus;*/
}
