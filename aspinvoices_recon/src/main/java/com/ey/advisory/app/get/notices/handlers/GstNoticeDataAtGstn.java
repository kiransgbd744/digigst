package com.ey.advisory.app.get.notices.handlers;

public interface GstNoticeDataAtGstn {

	public Long getGstNoticesData(GstNoticesReqDto dto,
			String groupCode, String type, String jsonReq);

}