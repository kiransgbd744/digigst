package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

@Data
public class ITC04ProcessSummaryScreenRespDto {

	private String gstin;
	private String saveStatus;
	private String gsCount;
	private String gsQuantity;
	private String gsTaxableValue;
	private String grCount;
	private String grQuantityRece;
	private String grQuantityLoss;

}
