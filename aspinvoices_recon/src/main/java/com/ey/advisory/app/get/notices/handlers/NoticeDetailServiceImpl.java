package com.ey.advisory.app.get.notices.handlers;

import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;

@Component("NoticeDetailServiceImpl")
public class NoticeDetailServiceImpl implements NoticeDetailService  {
	
	
	@Autowired
	@Qualifier("NoticeDetailDaoImpl")
	private NoticeDetailDao noticeDetailDao;

	@Override
	public Map<String, NoticeStats> fetchNoticeStats(GstnNoticeReqDto reqDto) {
		return noticeDetailDao.fetchNoticeStats(reqDto);
	}

	@Override
	public Pair<List<TblGetNoticesEntity>, Integer> fetchGstnNoticeStats(GstnNoticeReqDto reqDto) {

		return noticeDetailDao.fetchGstnNoticeStats(reqDto);
	}
	
	

}
