package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

@Data
public class ITC04SummaryScreenRespDto {

	private String table;
	private String aspCount;
	private String aspTaxableValue;
	private String gstnCount;
	private String gstnTaxableValue;
	private String diffCount;
	private String diffTaxableValue;

}
