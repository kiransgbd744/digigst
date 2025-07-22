package com.ey.advisory.app.get.notices.handlers;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;

public interface NoticeDetailDao {
	
	public Map<String, NoticeStats> fetchNoticeStats(GstnNoticeReqDto reqDto);
	
	public Pair<List<TblGetNoticesEntity>, Integer> fetchGstnNoticeStats(GstnNoticeReqDto reqDto);

}
