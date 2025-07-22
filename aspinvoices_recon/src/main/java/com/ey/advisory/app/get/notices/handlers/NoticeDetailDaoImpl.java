package com.ey.advisory.app.get.notices.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.TblGetNoticesRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("NoticeDetailDaoImpl")
public class NoticeDetailDaoImpl implements NoticeDetailDao {

	@Autowired
	private TblGetNoticesRepository noticeRepo;

	@Override
	public Map<String, NoticeStats> fetchNoticeStats(GstnNoticeReqDto reqDto) {
		
		LOGGER.debug(" reqDto {} ",reqDto);
		
		Map<String, NoticeStats> summaryMap = new HashMap<>();

		try {
			LOGGER.info("Entering fetchNoticeStats with ");
			List<TblGetNoticesEntity> noticeEntity = noticeRepo
					.findConsolidatedDataByParams(reqDto);

			summaryMap = noticeEntity.stream().collect(Collectors
					.groupingBy(TblGetNoticesEntity::getGstin, Collectors
							.collectingAndThen(Collectors.toList(), notices -> {
								int total = notices.size();
								int responded = (int) notices.stream()
										.filter(n -> n.getIsResponded())
										.count();
								int pending = total - responded;
								String gstin = notices.get(0).getGstin();
								return new NoticeStats(gstin, total, responded,
										pending);
							})));

		} catch (Exception e) {
			LOGGER.error(
					"Exception occurred while fetching notice stats for GSTIN: {}",
					e);
		}

		return summaryMap;
	}

	@Override
	public Pair<List<TblGetNoticesEntity>, Integer> fetchGstnNoticeStats(
			GstnNoticeReqDto reqDto) {

		try {

			int recordsToStart = reqDto.getPageNum();

			int noOfRowstoFetch = reqDto.getPageSize();

			Pageable pageReq = PageRequest.of(recordsToStart, noOfRowstoFetch,
					Direction.DESC, "id");

			Page<TblGetNoticesEntity> pageResult = noticeRepo
					.findConsolidatedDataByParams(reqDto, pageReq);

			List<TblGetNoticesEntity> noticeEntity = pageResult.getContent();

			List<TblGetNoticesEntity> totalnoticeEntity = noticeRepo
					.findConsolidatedDataByParams(reqDto);

			return new Pair<>(noticeEntity,
					totalnoticeEntity != null ? totalnoticeEntity.size() : 0);

		} catch (Exception ex) {
			throw new AppException(ex);
		}
	}

}