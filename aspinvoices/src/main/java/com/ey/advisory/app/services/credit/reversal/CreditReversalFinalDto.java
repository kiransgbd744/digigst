package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditReversalFinalDto {

	@Expose
	@SerializedName("summary")
	private CreditReveSummaryDto sumDto;

	@Expose
	@SerializedName("tab")
	private List<CreditReversalDto> reversTurnOverDtos;
}
