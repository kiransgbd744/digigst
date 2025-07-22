package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.app.data.repositories.client.GetGstr6aStagingB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr6aStagingB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr6aStagingCdnHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr6aStagingCdnaHeaderRepository;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Get6aRepositoryHandler")
public class Get6aRepositoryHandler {

	@Autowired
	private Gstr6aProcessUtil gstr6aProcessUtil;

	@Autowired
	private GetGstr6aStagingB2bHeaderRepository getGstr6aStagingB2bHeaderRepository;

	@Autowired
	private GetGstr6aStagingB2baHeaderRepository getGstr6aStagingB2baHeaderRepository;

	@Autowired
	private GetGstr6aStagingCdnHeaderRepository getGstr6aStagingCdnHeaderRepository;

	@Autowired
	private GetGstr6aStagingCdnaHeaderRepository getGstr6aStagingCdnaHeaderRepository;

	public Pair<Long, Integer> processB2BWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr6aStagingB2bHeaderEntity> b2bList = gstr6aProcessUtil
				.convertB2bWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}]",
					b2bList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(b2bList)) {
			b2bList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCtin() + "_"
						+ data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr6aStagingB2bHeaderEntity> entityList = getGstr6aStagingB2bHeaderRepository
						.findByInvoiceKey(data.getGstin(), data.getCtin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr6aStagingB2bHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr6aStagingB2bHeaderRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BWorkSheetData ->Records ready for insert/update list size [{}]",
					b2bList.size());
		}
		stopWatch.start("Insert/update B2b data");
		List<GetGstr6aStagingB2bHeaderEntity> b2bFilterList = Gstr6aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2bHeaders(b2bList);
		getGstr6aStagingB2bHeaderRepository.saveAll(b2bFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(b2bFilterList)
						&& b2bFilterList.get(0).getBatchId() != null)
								? b2bFilterList.get(0).getBatchId() : 0L,
				b2bFilterList.size());
	}

	public Pair<Long, Integer> processB2BAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr6aStagingB2baHeaderEntity> b2baList = gstr6aProcessUtil
				.convertB2baWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}]",
					b2baList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(b2baList)) {
			b2baList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCtin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr6aStagingB2baHeaderEntity> entityList = getGstr6aStagingB2baHeaderRepository
						.findByInvoiceKey(data.getGstin(), data.getCtin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr6aStagingB2baHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr6aStagingB2baHeaderRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BAWorkSheetData ->Records ready for insert/update list size [{}]",
					b2baList.size());
		}
		stopWatch.start("Insert/update B2ba data");
		List<GetGstr6aStagingB2baHeaderEntity> b2baFilterList = Gstr6aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2baHeaders(b2baList);
		getGstr6aStagingB2baHeaderRepository.saveAll(b2baFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(b2baFilterList)
				&& b2baFilterList.get(0).getBatchId() != null)
						? b2baFilterList.get(0).getBatchId() : 0L,
				b2baFilterList.size());
	}

	public Pair<Long, Integer> processCDNRAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr6aStagingCdnaHeaderEntity> cdnraList = gstr6aProcessUtil
				.convertCdnraWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}]",
					cdnraList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnraList)) {
			cdnraList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_"
						+ data.getCtin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr6aStagingCdnaHeaderEntity> entityList = getGstr6aStagingCdnaHeaderRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCtin(),
								data.getInvDate(),
								data.getInvNum(),
								data.getNoteType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr6aStagingCdnaHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr6aStagingCdnaHeaderRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"processCNDRAWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnraList.size());
		}
		stopWatch.start("Insert/update cdnra data");
		List<GetGstr6aStagingCdnaHeaderEntity> cdnraFilterList = Gstr6aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdnaHeaders(cdnraList);
		getGstr6aStagingCdnaHeaderRepository.saveAll(cdnraFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnraFilterList)
				&& cdnraFilterList.get(0).getBatchId() != null)
						? cdnraFilterList.get(0).getBatchId() : 0L,
				cdnraFilterList.size());
	}

	public Pair<Long, Integer> processCDNRWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr6aStagingCdnHeaderEntity> cdnList = gstr6aProcessUtil
				.convertCdnrWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}]",
					cdnList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(cdnList)) {
			cdnList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_"
						+ data.getCtin()
						+ "_" + data.getInvDate() + "_" + data.getNoteNum() + "_"
						+ data.getNoteType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr6aStagingCdnHeaderEntity> entityList = getGstr6aStagingCdnHeaderRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCtin(),
								data.getInvDate(),
								data.getNoteNum(),
								data.getNoteType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr6aStagingCdnHeaderEntity::getId)
							.collect(Collectors.toList()));
				}
			});
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		String jobName = "Disabling the existing records:::->"
				+ totalIds.size();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(jobName);
		}
		stopWatch.start(jobName);
		if (CollectionUtils.isNotEmpty(totalIds)) {
			List<List<Long>> partions = Lists.partition(totalIds, 1000);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			partions.forEach(partion -> getGstr6aStagingCdnHeaderRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processCNDRWorkSheetData ->Records ready for insert/update list size [{}]",
					cdnList.size());
		}
		stopWatch.start("Insert/update cdnr data");
		List<GetGstr6aStagingCdnHeaderEntity> cdnFilterList = Gstr6aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdnHeaders(cdnList);
		getGstr6aStagingCdnHeaderRepository.saveAll(cdnFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnFilterList)
				&& cdnFilterList.get(0).getBatchId() != null)
						? cdnFilterList.get(0).getBatchId() : 0L,
				cdnFilterList.size());
	}

}
