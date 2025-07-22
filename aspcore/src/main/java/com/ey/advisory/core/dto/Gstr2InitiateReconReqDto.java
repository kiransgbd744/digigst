package com.ey.advisory.core.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2InitiateReconReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("requestId")
	private List<Long> requestId;

	@Expose
	@SerializedName("reconType")
	private String reconType;

	@Expose
	@SerializedName("initiationDateFrom")
	private String initiationDateFrom;

	@Expose
	@SerializedName("initiationDateTo")
	private String initiationDateTo;

	@Expose
	@SerializedName("initiationByUserId")
	private List<String> initiationByUserId;

	@Expose
	@SerializedName("initiationByUserEmailId")
	private List<String> initiationByUserEmailId;

	@Expose
	@SerializedName("reconStatus")
	private String reconStatus;
	
	@Expose
	@SerializedName("entityIds")
	private List<Long> entityIds;
	
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;
	
	@Expose
	@SerializedName("returnPeriodFrom")
	private int returnPeriodFrom;

	@Expose
	@SerializedName("returnPeriodTo")
	private int returnPeriodTo;

}
