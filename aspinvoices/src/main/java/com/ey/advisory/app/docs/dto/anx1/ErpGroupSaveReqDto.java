package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ErpGroupSaveReqDto {
	private String groupCode;
	private Long entityId;
	private List<ErpGroupResDto> erpGroupReq = new ArrayList<>();
	

}
