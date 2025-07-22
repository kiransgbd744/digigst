package com.ey.advisory.app.services.docs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aImpgHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aImpgSezHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2EcomInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2EcomaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingB2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingB2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingEcomInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2StagingEcomaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aImpgRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aImpgSezRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingCdnInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingCdnaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingImpgRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingImpgSezRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingIsdInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2aStagingIsdaInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetCdnInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetCdnaInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdaInvoicesAtGstnRepository;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Get2aRepositoryHandler")
public class Get2aRepositoryHandler {

	@Autowired
	private Gstr2aProcessUtil gstr2aProcessUtil;

	@Autowired
	private GetGstr2StagingB2bInvoicesRepository getGstr2aStagingB2bInvoicesRepository;

	@Autowired
	private GetGstr2StagingB2baInvoicesRepository getGstr2aStagingB2baInvoicesRepository;

	@Autowired
	private GetGstr2aStagingCdnInvoicesRepository getGstr2aStagingCdnInvoicesRepository;

	@Autowired
	private GetGstr2aStagingCdnaInvoicesRepository getGstr2aStagingCdnaInvoicesRepository;

	@Autowired
	private GetGstr2aStagingIsdInvoicesRepository getGstr2aStagingIsdInvoicesRepository;

	@Autowired
	private GetGstr2aStagingIsdaInvoicesRepository getGstr2aStagingIsdaInvoicesRepository;

	@Autowired
	private GetGstr2aStagingImpgRepository getGstr2aStagingImpgRepository;

	@Autowired
	private GetGstr2aStagingImpgSezRepository getGstr2aStagingImpgSezRepository;

	@Autowired
	private GetGstr2B2bInvoicesRepository getGstr2aB2bInvoicesRepository;

	@Autowired
	private GetGstr2B2baInvoicesRepository getGstr2aB2baInvoicesRepository;

	@Autowired
	private Gstr2aGetCdnInvoicesAtGstnRepository getGstr2aCdnInvoicesRepository;

	@Autowired
	private Gstr2aGetCdnaInvoicesAtGstnRepository getGstr2aCdnaInvoicesRepository;

	@Autowired
	private Gstr2aGetIsdInvoicesAtGstnRepository getGstr2aIsdInvoicesRepository;

	@Autowired
	private Gstr2aGetIsdaInvoicesAtGstnRepository getGstr2aIsdaInvoicesRepository;

	@Autowired
	private GetGstr2aImpgRepository getGstr2aImpgRepository;

	@Autowired
	private GetGstr2aImpgSezRepository getGstr2aImpgSezRepository;
	
	@Autowired
	private GetGstr2StagingEcomInvoicesRepository getGstr2aStagingEcomInvoicesRepository;

	@Autowired
	private GetGstr2StagingEcomaInvoicesRepository getGstr2aStagingEcomaInvoicesRepository;
	
	@Autowired
	private GetGstr2EcomInvoicesRepository getGstr2aEcomInvoicesRepository;

	@Autowired
	private GetGstr2EcomaInvoicesRepository getGstr2aEcomaInvoicesRepository;


	public Pair<Long, Integer> processB2BWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bList = gstr2aProcessUtil
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
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingB2bInvoicesHeaderEntity> entityList = getGstr2aStagingB2bInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingB2bInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingB2bInvoicesRepository
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
		List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2bHeaders(b2bList);
		getGstr2aStagingB2bInvoicesRepository.saveAll(b2bFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(b2bFilterList)
				&& b2bFilterList.get(0).getB2bBatchIdGstr2a() != null)
						? b2bFilterList.get(0).getB2bBatchIdGstr2a() : 0L,
				b2bFilterList.size());
		// return b2bList.size();
	}

	public Pair<Long, Integer> processB2BAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baList = gstr2aProcessUtil
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
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingB2baInvoicesHeaderEntity> entityList = getGstr2aStagingB2baInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingB2baInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingB2baInvoicesRepository
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
		List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonB2baHeaders(b2baList);
		getGstr2aStagingB2baInvoicesRepository.saveAll(b2baFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(b2baFilterList)
				&& b2baFilterList.get(0).getB2bBatchIdGstr2a() != null)
						? b2baFilterList.get(0).getB2bBatchIdGstr2a() : 0L,
				b2baFilterList.size());
		// return b2baList.size();
	}

	public Pair<Long, Integer> processTCSWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processTDSAWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processTDSWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processISDWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingIsdInvoicesHeaderEntity> isdList = gstr2aProcessUtil
				.convertIsdWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("processISDWorkSheetData list size [{}]",
					isdList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdList)) {
			isdList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCgstin()
						+ "_" + data.getDocumentDate() + "_"
						+ data.getDocumentNumber() + "_"
						+ data.getIsdDocumentType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingIsdInvoicesHeaderEntity> entityList = getGstr2aStagingIsdInvoicesRepository
						.findByInvoiceKey(data.getGstin(), data.getCgstin(),
								data.getDocumentDate(),
								data.getDocumentNumber(),
								data.getIsdDocumentType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingIsdInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingIsdInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"processISDWorkSheetData ->Records ready for insert/update list size [{}]",
					isdList.size());
		}
		stopWatch.start("Insert/update Isd data");
		getGstr2aStagingIsdInvoicesRepository.saveAll(isdList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(isdList)
						&& isdList.get(0).getIsdBatchIdGstr2a() != null)
								? isdList.get(0).getIsdBatchIdGstr2a() : 0L,
				isdList.size());
		// return isdList.size();
	}

	public Pair<Long, Integer> processISDAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingIsdaInvoicesHeaderEntity> isdList = gstr2aProcessUtil
				.convertIsdaWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDAWorkSheetData list size [{}]",
					isdList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdList)) {
			isdList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCgstin()
						+ "_" + data.getDocumentDate() + "_"
						+ data.getDocumentNumber() + "_"
						+ data.getIsdDocumentType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingIsdaInvoicesHeaderEntity> entityList = getGstr2aStagingIsdaInvoicesRepository
						.findByInvoiceKey(data.getGstin(), data.getCgstin(),
								data.getDocumentDate(),
								data.getDocumentNumber(),
								data.getIsdDocumentType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingIsdaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingIsdaInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processISDAWorkSheetData ->Records ready for insert/update list size [{}]",
					isdList.size());
		}
		stopWatch.start("Insert/update Isd data");
		getGstr2aStagingIsdaInvoicesRepository.saveAll(isdList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(isdList)
						&& isdList.get(0).getIsdaBatchIdGstr2a() != null)
								? isdList.get(0).getIsdaBatchIdGstr2a() : 0L,
				isdList.size());
		// return isdList.size();
	}

	public Pair<Long, Integer> processCDNRAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnraList = gstr2aProcessUtil
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
						+ data.getCountergstin() + "_"
						+ data.getCredDebRefVoucherDate() + "_"
						+ data.getCredDebRefVoucherNum() + "_"
						+ data.getCredDebRefVoucher();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingCdnaInvoicesHeaderEntity> entityList = getGstr2aStagingCdnaInvoicesRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCountergstin(),
								data.getCredDebRefVoucherDate(),
								data.getCredDebRefVoucherNum(),
								data.getCredDebRefVoucher());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingCdnaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingCdnaInvoicesRepository
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
		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnraFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdnaHeaders(cdnraList);
		getGstr2aStagingCdnaInvoicesRepository.saveAll(cdnraFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnraFilterList)
				&& cdnraFilterList.get(0).getCdnBatchIdGstr2a() != null)
						? cdnraFilterList.get(0).getCdnBatchIdGstr2a() : 0L,
				cdnraFilterList.size());
		// return cdnraList.size();
	}

	public Pair<Long, Integer> processCDNRWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnList = gstr2aProcessUtil
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
						+ data.getCountergstin() + "_"
						+ data.getCredDebRefVoucherDate() + "_"
						+ data.getCredDebRefVoucherNum() + "_"
						+ data.getCredDebRefVoucher();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingCdnInvoicesHeaderEntity> entityList = getGstr2aStagingCdnInvoicesRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCountergstin(),
								data.getCredDebRefVoucherDate(),
								data.getCredDebRefVoucherNum(),
								data.getCredDebRefVoucher());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingCdnInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingCdnInvoicesRepository
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
		List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonCdnHeaders(cdnList);
		getGstr2aStagingCdnInvoicesRepository.saveAll(cdnFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnFilterList)
				&& cdnFilterList.get(0).getCdnBatchIdGstr2a() != null)
						? cdnFilterList.get(0).getCdnBatchIdGstr2a() : 0L,
				cdnFilterList.size());
		// return cdnList.size();
	}

	public Pair<Long, Integer> processIMPGWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingImpgHeaderEntity> impgList = gstr2aProcessUtil
				.convertImpgWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGWorkSheetData list size [{}]",
					impgList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(impgList)) {
			impgList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getPortCode()
						+ "_" + data.getBoeCreatedDate() + "_"
						+ data.getBoeNum();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processIMPGWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingImpgHeaderEntity> entityList = getGstr2aStagingImpgRepository
						.findByInvoiceKey(data.getGstin(), data.getPortCode(),
								data.getBoeCreatedDate(), data.getBoeNum());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingImpgHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingImpgRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processIMPGWorkSheetData ->Records ready for insert/update list size [{}]",
					impgList.size());
		}
		stopWatch.start("Insert/update impg data");
		getGstr2aStagingImpgRepository.saveAll(impgList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(impgList)
						&& impgList.get(0).getBatchId() != null)
								? impgList.get(0).getBatchId() : 0L,
				impgList.size());
		// return impgList.size();
	}

	public Pair<Long, Integer> processIMPGSEZWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGSEZWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingImpgSezHeaderEntity> impgSezList = gstr2aProcessUtil
				.convertImpgSezWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGSEZWorkSheetData list size [{}]",
					impgSezList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(impgSezList)) {
			impgSezList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getSgstin()
						+ "_" + data.getPortCode() + "_"
						+ data.getBoeCreatedDate() + "_" + data.getBoeNum();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processIMPGSEZWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingImpgSezHeaderEntity> entityList = getGstr2aStagingImpgSezRepository
						.findByInvoiceKey(data.getGstin(), data.getSgstin(),
								data.getPortCode(), data.getBoeCreatedDate(),
								data.getBoeNum());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingImpgSezHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingImpgSezRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processIMPGSEZWorkSheetData ->Records ready for insert/update list size [{}]",
					impgSezList.size());
		}
		stopWatch.start("Insert/update impg data");
		getGstr2aStagingImpgSezRepository.saveAll(impgSezList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(impgSezList)
						&& impgSezList.get(0).getBatchId() != null)
								? impgSezList.get(0).getBatchId() : 0L,
				impgSezList.size());
		// return impgSezList.size();
	}

	public Pair<Long, Integer> processUserB2BWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bList = gstr2aProcessUtil
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
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aB2bInvoicesHeaderEntity> entityList = getGstr2aB2bInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aB2bInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aB2bInvoicesRepository
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
		Type listType = new TypeToken<List<GetGstr2aB2bInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aB2bInvoicesHeaderEntity> postDtoList = modelmapper
				.map(b2bList, listType);
		List<GetGstr2aB2bInvoicesHeaderEntity> b2bFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonB2bHeader(postDtoList);
		getGstr2aB2bInvoicesRepository.saveAll(b2bFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(b2bFilterList)
				&& b2bFilterList.get(0).getB2bBatchIdGstr2a() != null)
						? b2bFilterList.get(0).getB2bBatchIdGstr2a() : 0L,
				b2bFilterList.size());
		// return b2bList.size();
	}

	public Pair<Long, Integer> processUserB2BAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processB2BAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baList = gstr2aProcessUtil
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
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processB2BAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aB2baInvoicesHeaderEntity> entityList = getGstr2aB2baInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aB2baInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aB2baInvoicesRepository
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
		Type listType = new TypeToken<List<GetGstr2aB2baInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aB2baInvoicesHeaderEntity> postDtoList = modelmapper
				.map(b2baList, listType);
		List<GetGstr2aB2baInvoicesHeaderEntity> b2baFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonB2baHeader(postDtoList);
		getGstr2aB2baInvoicesRepository.saveAll(b2baFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(b2baFilterList)
				&& b2baFilterList.get(0).getB2bBatchIdGstr2a() != null)
						? b2baFilterList.get(0).getB2bBatchIdGstr2a() : 0L,
				b2baFilterList.size());
		// return b2baList.size();
	}

	public Pair<Long, Integer> processUserTCSWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processUserTDSAWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processUserTDSWorkSheetData(Object[][] objList,
			int columnCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pair<Long, Integer> processUserISDWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingIsdInvoicesHeaderEntity> isdList = gstr2aProcessUtil
				.convertIsdWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("processISDWorkSheetData list size [{}]",
					isdList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdList)) {
			isdList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCgstin()
						+ "_" + data.getDocumentDate() + "_"
						+ data.getDocumentNumber() + "_"
						+ data.getIsdDocumentType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aIsdInvoicesHeaderEntity> entityList = getGstr2aIsdInvoicesRepository
						.findByInvoiceKey(data.getGstin(), data.getCgstin(),
								data.getDocumentDate(),
								data.getDocumentNumber(),
								data.getIsdDocumentType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aIsdInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aIsdInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug(
					"processISDWorkSheetData ->Records ready for insert/update list size [{}]",
					isdList.size());
		}
		stopWatch.start("Insert/update Isd data");
		Type listType = new TypeToken<List<GetGstr2aIsdInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aIsdInvoicesHeaderEntity> postDtoList = modelmapper
				.map(isdList, listType);
		getGstr2aIsdInvoicesRepository.saveAll(postDtoList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(isdList)
						&& isdList.get(0).getIsdBatchIdGstr2a() != null)
								? isdList.get(0).getIsdBatchIdGstr2a() : 0L,
				isdList.size());
		// return isdList.size();
	}

	public Pair<Long, Integer> processUserISDAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingIsdaInvoicesHeaderEntity> isdList = gstr2aProcessUtil
				.convertIsdaWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processISDAWorkSheetData list size [{}]",
					isdList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(isdList)) {
			isdList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getCgstin()
						+ "_" + data.getDocumentDate() + "_"
						+ data.getDocumentNumber() + "_"
						+ data.getIsdDocumentType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processISDAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aIsdaInvoicesHeaderEntity> entityList = getGstr2aIsdaInvoicesRepository
						.findByInvoiceKey(data.getGstin(), data.getCgstin(),
								data.getDocumentDate(),
								data.getDocumentNumber(),
								data.getIsdDocumentType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aIsdaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aIsdaInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processISDAWorkSheetData ->Records ready for insert/update list size [{}]",
					isdList.size());
		}
		stopWatch.start("Insert/update Isd data");
		Type listType = new TypeToken<List<GetGstr2aIsdaInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aIsdaInvoicesHeaderEntity> postDtoList = modelmapper
				.map(isdList, listType);
		getGstr2aIsdaInvoicesRepository.saveAll(postDtoList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(isdList)
						&& isdList.get(0).getIsdaBatchIdGstr2a() != null)
								? isdList.get(0).getIsdaBatchIdGstr2a() : 0L,
				isdList.size());
		// return isdList.size();
	}

	public Pair<Long, Integer> processUserCDNRAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnraList = gstr2aProcessUtil
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
						+ data.getCountergstin() + "_"
						+ data.getCredDebRefVoucherDate() + "_"
						+ data.getCredDebRefVoucherNum() + "_"
						+ data.getCredDebRefVoucher();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aCdnaInvoicesHeaderEntity> entityList = getGstr2aCdnaInvoicesRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCountergstin(),
								data.getCredDebRefVoucherDate(),
								data.getCredDebRefVoucherNum(),
								data.getCredDebRefVoucher());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aCdnaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aCdnaInvoicesRepository
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
		Type listType = new TypeToken<List<GetGstr2aCdnaInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aCdnaInvoicesHeaderEntity> postDtoList = modelmapper
				.map(cdnraList, listType);
		List<GetGstr2aCdnaInvoicesHeaderEntity> cdnaFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonCdnaHeader(postDtoList);
		getGstr2aCdnaInvoicesRepository.saveAll(cdnaFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnaFilterList)
				&& cdnaFilterList.get(0).getCdnBatchIdGstr2a() != null)
						? cdnaFilterList.get(0).getCdnBatchIdGstr2a() : 0L,
				cdnaFilterList.size());
		// return cdnraList.size();
	}

	public Pair<Long, Integer> processUserCDNRWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processCDNRWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnList = gstr2aProcessUtil
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
						+ data.getCountergstin() + "_"
						+ data.getCredDebRefVoucherDate() + "_"
						+ data.getCredDebRefVoucherNum() + "_"
						+ data.getCredDebRefVoucher();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processCDNRWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aCdnInvoicesHeaderEntity> entityList = getGstr2aCdnInvoicesRepository
						.findByInvoiceKey(data.getGstin(),
								data.getCountergstin(),
								data.getCredDebRefVoucherDate(),
								data.getCredDebRefVoucherNum(),
								data.getCredDebRefVoucher());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aCdnInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aCdnInvoicesRepository
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
		Type listType = new TypeToken<List<GetGstr2aCdnInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aCdnInvoicesHeaderEntity> postDtoList = modelmapper
				.map(cdnList, listType);
		List<GetGstr2aCdnInvoicesHeaderEntity> cdnFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonCdnHeader(postDtoList);
		getGstr2aCdnInvoicesRepository.saveAll(cdnFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(cdnFilterList)
				&& cdnFilterList.get(0).getCdnBatchIdGstr2a() != null)
						? cdnFilterList.get(0).getCdnBatchIdGstr2a() : 0L,
				cdnFilterList.size());
		// return cdnList.size();
	}

	public Pair<Long, Integer> processUserIMPGWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingImpgHeaderEntity> impgList = gstr2aProcessUtil
				.convertImpgWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGWorkSheetData list size [{}]",
					impgList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(impgList)) {
			impgList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getPortCode()
						+ "_" + data.getBoeCreatedDate() + "_"
						+ data.getBoeNum();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processIMPGWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aImpgHeaderEntity> entityList = getGstr2aImpgRepository
						.findByInvoiceKey(data.getGstin(), data.getPortCode(),
								data.getBoeCreatedDate(), data.getBoeNum());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aImpgHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aImpgRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processIMPGWorkSheetData ->Records ready for insert/update list size [{}]",
					impgList.size());
		}
		stopWatch.start("Insert/update impg data");
		Type listType = new TypeToken<List<GetGstr2aImpgHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aImpgHeaderEntity> postDtoList = modelmapper.map(impgList,
				listType);
		getGstr2aImpgRepository.saveAll(postDtoList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(impgList)
						&& impgList.get(0).getBatchId() != null)
								? impgList.get(0).getBatchId() : 0L,
				impgList.size());
		// return impgList.size();
	}

	public Pair<Long, Integer> processUserIMPGSEZWorkSheetData(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGSEZWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingImpgSezHeaderEntity> impgSezList = gstr2aProcessUtil
				.convertImpgSezWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processIMPGSEZWorkSheetData list size [{}]",
					impgSezList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(impgSezList)) {
			impgSezList.forEach(data -> {
				String invoiceKey = data.getGstin() + "_" + data.getSgstin()
						+ "_" + data.getPortCode() + "_"
						+ data.getBoeCreatedDate() + "_" + data.getBoeNum();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processIMPGSEZWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aImpgSezHeaderEntity> entityList = getGstr2aImpgSezRepository
						.findByInvoiceKey(data.getGstin(), data.getSgstin(),
								data.getPortCode(), data.getBoeCreatedDate(),
								data.getBoeNum());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aImpgSezHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aImpgSezRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processIMPGSEZWorkSheetData ->Records ready for insert/update list size [{}]",
					impgSezList.size());
		}
		stopWatch.start("Insert/update impg data");
		Type listType = new TypeToken<List<GetGstr2aImpgSezHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aImpgSezHeaderEntity> postDtoList = modelmapper
				.map(impgSezList, listType);
		getGstr2aImpgSezRepository.saveAll(postDtoList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>(
				(CollectionUtils.isNotEmpty(impgSezList)
						&& impgSezList.get(0).getBatchId() != null)
								? impgSezList.get(0).getBatchId() : 0L,
				impgSezList.size());
		// return impgSezList.size();
	}
	
	public Pair<Long, Integer> processUserEcomWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomList = gstr2aProcessUtil
				.convertEcomWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomWorkSheetData list size [{}]",
					ecomList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomList)) {
			ecomList.forEach(data -> {
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEcomWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aEcomInvoicesHeaderEntity> entityList = getGstr2aEcomInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aEcomInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aEcomInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processEcomWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomList.size());
		}
		stopWatch.start("Insert/update Ecom data");
		Type listType = new TypeToken<List<GetGstr2aEcomInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aEcomInvoicesHeaderEntity> postDtoList = modelmapper
				.map(ecomList, listType);
		List<GetGstr2aEcomInvoicesHeaderEntity> ecomFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonEcomHeader(postDtoList);
		getGstr2aEcomInvoicesRepository.saveAll(ecomFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(ecomFilterList)
				&& ecomFilterList.get(0).getEcomBatchIdGstr2a() != null)
						? ecomFilterList.get(0).getEcomBatchIdGstr2a() : 0L,
				ecomFilterList.size());
	}

	public Pair<Long, Integer> processUserEcomaWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaList = gstr2aProcessUtil
				.convertEcomaWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomAWorkSheetData list size [{}]",
					ecomaList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomaList)) {
			ecomaList.forEach(data -> {
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEcomAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aEcomaInvoicesHeaderEntity> entityList = getGstr2aEcomaInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aEcomaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aEcomaInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processEcomAWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomaList.size());
		}
		stopWatch.start("Insert/update Ecoma data");
		Type listType = new TypeToken<List<GetGstr2aEcomaInvoicesHeaderEntity>>() {
		}.getType();
		ModelMapper modelmapper = new ModelMapper();
		List<GetGstr2aEcomaInvoicesHeaderEntity> postDtoList = modelmapper
				.map(ecomaList, listType);
		List<GetGstr2aEcomaInvoicesHeaderEntity> ecomaFilterList = Gstr2aTaxRateDiffUtil
				.filterInvoiceKeyAndMergeRateonEcomaHeader(postDtoList);
		getGstr2aEcomaInvoicesRepository.saveAll(ecomaFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(ecomaFilterList)
				&& ecomaFilterList.get(0).getEcomBatchIdGstr2a() != null)
						? ecomaFilterList.get(0).getEcomBatchIdGstr2a() : 0L,
				ecomaFilterList.size());
		
	}
	
	public Pair<Long, Integer> processEcomWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomList = gstr2aProcessUtil
				.convertEcomWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomWorkSheetData list size [{}]",
					ecomList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomList)) {
			ecomList.forEach(data -> {
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEcomWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingEcomInvoicesHeaderEntity> entityList = getGstr2aStagingEcomInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingEcomInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingEcomInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processEcomWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomList.size());
		}
		stopWatch.start("Insert/update Ecom data");
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonEcomHeaders(ecomList);
		getGstr2aStagingEcomInvoicesRepository.saveAll(ecomFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(ecomFilterList)
				&& ecomFilterList.get(0).getEcomBatchIdGstr2a() != null)
						? ecomFilterList.get(0).getEcomBatchIdGstr2a() : 0L,
				ecomFilterList.size());
	}

	public Pair<Long, Integer> processEcomAWorkSheetData(Object[][] objList,
			int columnCount, String cgstin, String taxPeriod, Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomAWorkSheetData list size [{}] started",
					objList.length);
		}
		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaList = gstr2aProcessUtil
				.convertEcomaWorkSheetDataToList(objList, columnCount, cgstin,
						taxPeriod, batchId);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processEcomAWorkSheetData list size [{}]",
					ecomaList.size());
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start("Finding the data by invoice key");
		List<Long> totalIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(ecomaList)) {
			ecomaList.forEach(data -> {
				String invoiceKey = data.getSgstin() + "_" + data.getCgstin()
						+ "_" + data.getInvDate() + "_" + data.getInvNum() + "_"
						+ data.getInvType();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processEcomAWorkSheetData -> invoiceKey-->"
							+ invoiceKey);
				}

				List<GetGstr2aStagingEcomaInvoicesHeaderEntity> entityList = getGstr2aStagingEcomaInvoicesRepository
						.findByInvoiceKey(data.getSgstin(), data.getCgstin(),
								data.getInvDate(), data.getInvNum(),
								data.getInvType());
				if (CollectionUtils.isNotEmpty(entityList)) {
					totalIds.addAll(entityList.stream()
							.map(GetGstr2aStagingEcomaInvoicesHeaderEntity::getId)
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
			partions.forEach(partion -> getGstr2aStagingEcomaInvoicesRepository
					.updateSameRecords(partion, now));
		}
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"processB2BAWorkSheetData ->Records ready for insert/update list size [{}]",
					ecomaList.size());
		}
		stopWatch.start("Insert/update Ecoma data");
		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaFilterList = Gstr2aTaxRateDiffUtilStaging
				.filterInvoiceKeyAndMergeRateonEcomaHeaders(ecomaList);
		getGstr2aStagingEcomaInvoicesRepository.saveAll(ecomaFilterList);
		stopWatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(stopWatch.prettyPrint());
		}
		return new Pair<>((CollectionUtils.isNotEmpty(ecomaFilterList)
				&& ecomaFilterList.get(0).getEcomBatchIdGstr2a() != null)
						? ecomaFilterList.get(0).getEcomBatchIdGstr2a() : 0L,
				ecomaFilterList.size());
	}

}
