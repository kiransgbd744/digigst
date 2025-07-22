package com.ey.advisory.app.anx.reconresult;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReconResultUpdateReqDto {

	private List<ReconResultUpdateLevelReqDto> reportType;
}
