package com.ey.advisory.app.services.jobs.erp;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.Gstr1ProcessedRecordsFetchDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.AdvBasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicDocGstnSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicDocIssuedSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummaryScreenHsnSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicEcomSupScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Gstr1InvSeriesSectionDao;
import com.ey.advisory.app.data.daos.client.simplified.SezBasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1NilExmpNonGstStautsService;
import com.ey.advisory.app.docs.dto.Gstr1AdvancedVerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.Gstr1HsnSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.erp.AdvanceAmadmentPopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.AdvanceAmantPopupVerticalItemDto;
import com.ey.advisory.app.docs.dto.erp.AdvancePopupGstnViewDto;
import com.ey.advisory.app.docs.dto.erp.AdvancePopupGstnViewItemDto;
import com.ey.advisory.app.docs.dto.erp.AdvancePopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.AdvancePopupVerticalItemDto;
import com.ey.advisory.app.docs.dto.erp.AdvncePopupSummaryDto;
import com.ey.advisory.app.docs.dto.erp.AdvncePopupSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Advp111aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp111bPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211bPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupGstnViewDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupGstnViewItemDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupSummaryDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.B2csaPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csaPopupSummaryDto;
import com.ey.advisory.app.docs.dto.erp.B2csaPopupSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupSummaryDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupVerticalItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.HsnPopupDto;
import com.ey.advisory.app.docs.dto.erp.HsnPopupItemDto;
import com.ey.advisory.app.docs.dto.erp.NilCompoutedDto;
import com.ey.advisory.app.docs.dto.erp.NilCompoutedItemDto;
import com.ey.advisory.app.docs.dto.erp.NilDto;
import com.ey.advisory.app.docs.dto.erp.NilItemDto;
import com.ey.advisory.app.docs.dto.erp.NilPopupDto;
import com.ey.advisory.app.docs.dto.erp.NilPopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.NilPopupVerticalItemDto;
import com.ey.advisory.app.docs.dto.erp.ReviwB2banPopupVertItemDto;
import com.ey.advisory.app.docs.dto.erp.ReviwSumB2bAPopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.ReviwSumCommonPopupVerticalDto;
import com.ey.advisory.app.docs.dto.erp.ReviwSumCommonPopupVerticalItemDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSummaryStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstVerticalStatusRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.processors.handler.GstnReturnStatusUtil;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataAtGstn;
import com.ey.advisory.app.services.search.docsummarysearch.GstnSummarySectionService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1AdvancedSummaryService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1AspDocVerticalSummaryService;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1AspVerticalSummaryService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service("Gstr1ReviewSummaryRequestDocsImpl")
public class Gstr1ReviewSummaryRequestDocsImpl
		implements Gstr1ReviewSummaryRequestDocs {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1ReviewSummaryRequestDocsImpl.class);

	private static final String GSTIN = "sgstin";

	private static final String HYPHEN = " - ";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("GstnSummaryErpSectionService")
	private GstnSummaryErpSectionService summaryErpSecService;

	@Autowired
	@Qualifier("gstr1SummaryDataAtGstnImpl")
	Gstr1SummaryDataAtGstn gstr1SummaryDataAtGstn;

	@Autowired
	@Qualifier("GstnSummarySectionService")
	private GstnSummarySectionService gstnSummarySectionService;

	@Autowired
	@Qualifier("BasicDocSummaryScreenSectionDaoImpl")
	private BasicDocSummaryScreenSectionDaoImpl basicDocSummaryScreenSectionDao;

	@Autowired
	@Qualifier("SezBasicDocSummaryScreenSectionDaoImpl")
	private SezBasicDocSummaryScreenSectionDaoImpl sezBasicDocSummaryDao;

	@Autowired
	@Qualifier("BasicDocSummaryScreenHsnSectionDaoImpl")
	private BasicDocSummaryScreenHsnSectionDaoImpl basicHsnSummaryDao;

	@Autowired
	@Qualifier("BasicDocIssuedSummaryScreenSectionDaoImpl")
	private BasicDocIssuedSummaryScreenSectionDaoImpl basicDocIssueSummaryDao;

	@Autowired
	@Qualifier("AdvBasicDocSummaryScreenSectionDaoImpl")
	private AdvBasicDocSummaryScreenSectionDaoImpl advBasicDocSummaryDao;

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchDaoImpl")
	private Gstr1ProcessedRecordsFetchDaoImpl gstr1ProcRecordsFetchDaoImpl;

	@Autowired
	@Qualifier("BasicDocGstnSummaryScreenSectionDaoImpl")
	private BasicDocGstnSummaryScreenSectionDaoImpl basicDocGstnSumScrSecDao;

	@Autowired
	@Qualifier("Gstr1AspVerticalSummaryService")
	private Gstr1AspVerticalSummaryService aspVertical;

	@Autowired
	@Qualifier("Gstr1AspDocVerticalSummaryService")
	private Gstr1AspDocVerticalSummaryService gstr1AspDocVertSumSer;

	@Autowired
	@Qualifier("Gstr1NilExmpNonGstStautsService")
	private Gstr1NilExmpNonGstStautsService gstr1NilExmpNonGstStasSer;

	@Autowired
	@Qualifier("Gstr1AdvancedSummaryService")
	private Gstr1AdvancedSummaryService service;

	@Autowired
	@Qualifier("Gstr1InvSeriesSectionDaoImpl")
	private Gstr1InvSeriesSectionDao loadData;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	@Autowired
	private GstnReturnStatusUtil returnStatusUtil;

	@Autowired
	@Qualifier("BasicEcomSupScreenSectionDaoImpl")
	BasicEcomSupScreenSectionDaoImpl basicDocSummaryDao;

	public List<Gstr1ReviewSummaryRequestItemDto> processSummary(String gstin,
			int derivedRetPeriod, String entityPan, String stateName,
			String entityName, String companyCode) {
		List<Gstr1ReviewSummaryRequestItemDto> itemDto = new ArrayList<>();

		List<String> gstnList = new ArrayList<>();
		gstnList.add(gstin);

		List<Object[]> objs = getProcessSumryData(gstnList, derivedRetPeriod);
		// Convert All Item Dto
		itemDto = convertProcessToDocs(objs, entityName, entityPan, companyCode,
				stateName);
		Map<String, List<Gstr1ReviewSummaryRequestItemDto>> returnSectionMap = createMapByReturnSection(
				itemDto);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryRequestDocsImpl "
					+ "returnSectionMap : {}", returnSectionMap.size());
		}

		itemDto = calculateDataPeriodByDocType(returnSectionMap);

		return itemDto;
	}

	private List<Object[]> getProcessSumryData(List<String> gstin,
			int derivedRetPeriod) {

		List<Object[]> obj = null;
		String sql = getProcessSummaryQuery(gstin, derivedRetPeriod);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryRequestDocsImpl " + "sql : {}",
					sql);
		}
		Query q = entityManager.createNativeQuery(sql);
		q.setParameter(GSTIN, gstin);
		if (derivedRetPeriod > 0) {
			q.setParameter("taxPeriod", derivedRetPeriod);
		}
		obj = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryRequestDocsImpl " + "obj : {}",
					obj.size());
		}

		return obj;
	}

	private Map<String, List<Gstr1ReviewSummaryRequestItemDto>> createMapByReturnSection(
			List<Gstr1ReviewSummaryRequestItemDto> itemDto) {

		Map<String, List<Gstr1ReviewSummaryRequestItemDto>> returnSectionMap = new LinkedHashMap<>();

		itemDto.forEach(dto -> {
			StringBuilder key = new StringBuilder();
			key.append(dto.getGstinNum());
			key.append("_");
			key.append(dto.getRetPer());
			String docKey = key.toString();
			if (returnSectionMap.containsKey(docKey)) {
				List<Gstr1ReviewSummaryRequestItemDto> dtos = returnSectionMap
						.get(docKey);
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			} else {
				List<Gstr1ReviewSummaryRequestItemDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			}
		});
		return returnSectionMap;
	}

	private List<Gstr1ReviewSummaryRequestItemDto> calculateDataPeriodByDocType(
			Map<String, List<Gstr1ReviewSummaryRequestItemDto>> returnSectionMap) {

		List<Gstr1ReviewSummaryRequestItemDto> finalItemsDto = new LinkedList<>();
		if (returnSectionMap != null) {
			returnSectionMap.entrySet().forEach(entries -> {
				BigInteger count = BigInteger.ZERO;
				BigDecimal eyOutsupp = BigDecimal.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;

				int savedCount = 0;
				int errorCount = 0;
				int notSentCount = 0;
				int notSavedCount = 0;
				int totalCount = 0;

				List<Gstr1ReviewSummaryRequestItemDto> dtos = entries
						.getValue();
				if (dtos != null && !dtos.isEmpty()) {
					Gstr1ReviewSummaryRequestItemDto itemDto = new Gstr1ReviewSummaryRequestItemDto();
					for (Gstr1ReviewSummaryRequestItemDto dto : dtos) {
						itemDto.setEntityName(dto.getEntityName());
						itemDto.setEntityPan(dto.getEntityPan());
						itemDto.setCompanyCode(dto.getCompanyCode());
						itemDto.setState(dto.getState());
						itemDto.setRetPer(dto.getRetPer());
						itemDto.setGstinNum(dto.getGstinNum());
						count = count.add(dto.getEyTotDoc());
						savedCount = savedCount + dto.getSavedCount();
						errorCount = errorCount + dto.getErrorCount();
						notSentCount = notSentCount + dto.getNotSentCount();
						notSavedCount = notSavedCount + dto.getNotSavedCount();
						totalCount = totalCount + dto.getTotalCount();
						if (dto.getTaxDoctype() != null && dto.getTaxDoctype()
								.equalsIgnoreCase("ADV ADJ")) {
							eyOutsupp = eyOutsupp.subtract(dto.getEyOutsupp());
							igst = igst.subtract(dto.getEyIgstval());
							cgst = cgst.subtract(dto.getEyCgstval());
							sgst = sgst.subtract(dto.getEySgstval());
							cess = cess.subtract(dto.getEyCessval());
						} else {
							eyOutsupp = eyOutsupp.add(dto.getEyOutsupp());
							igst = igst.add(dto.getEyIgstval());
							cgst = cgst.add(dto.getEyCgstval());
							sgst = sgst.add(dto.getEySgstval());
							cess = cess.add(dto.getEyCessval());
						}
						itemDto.setRtnPerdStatus(dto.getRtnPerdStatus());
						itemDto.setEyDate(dto.getEyDate());
						itemDto.setEyTime(dto.getEyTime());
					}
					itemDto.setEyTotDoc(count);
					itemDto.setEyOutsupp(eyOutsupp);
					itemDto.setEyIgstval(igst);
					itemDto.setEyCgstval(cgst);
					itemDto.setEySgstval(sgst);
					itemDto.setEyCessval(cess);
					if (itemDto.getEyStatus() == null
							|| itemDto.getEyStatus().isEmpty()) {
						String[] key = entries.getKey().split("_");
						String gstin = key[0];
						String retPeriod = key[1];

						Pair<String, String> lastTransactionReturnStatus = returnStatusUtil
								.getLastTransactionReturnStatus(gstin,
										retPeriod,
										APIConstants.GSTR1.toUpperCase());
						itemDto.setEyStatus(
								lastTransactionReturnStatus.getValue0());
					}
					finalItemsDto.add(itemDto);
				}
			});
		}
		return finalItemsDto;
	}

	private List<Gstr1ReviewSummaryRequestItemDto> convertProcessToDocs(
			List<Object[]> objs, String entityName, String entityPan,
			String companyCode, String stateName) {
		List<Gstr1ReviewSummaryRequestItemDto> itemDto = new ArrayList<>();
		for (Object[] obj : objs) {
			Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
			if (entityName != null) {
				child.setEntityName(StringUtils.upperCase(entityName));
			}
			String gstin = obj[0] != null ? String.valueOf(obj[0]) : null;
			String retPeriod = obj[1] != null ? String.valueOf(obj[1]) : null;
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setState(stateName);
			child.setGstinNum(obj[0] != null ? String.valueOf(obj[0]) : null);
			child.setRetPer(obj[1] != null ? String.valueOf(obj[1]) : null);
			child.setTaxDoctype(obj[2] != null ? String.valueOf(obj[2]) : null);
			child.setEyOutsupp(obj[3] != null
					? new BigDecimal(String.valueOf(obj[3])) : BigDecimal.ZERO);
			child.setEyIgstval(obj[4] != null
					? new BigDecimal(String.valueOf(obj[4])) : BigDecimal.ZERO);
			child.setEyCgstval(obj[5] != null
					? new BigDecimal(String.valueOf(obj[5])) : BigDecimal.ZERO);
			child.setEySgstval(obj[6] != null
					? new BigDecimal(String.valueOf(obj[6])) : BigDecimal.ZERO);
			child.setEyCessval(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			int notSentCount = 0;
			if (obj[8] != null) {
				BigInteger notSentCountBig = obj[8] != null
						? new BigInteger(String.valueOf(obj[8]))
						: BigInteger.ZERO;
				notSentCount = notSentCountBig.intValue();
			}
			child.setNotSentCount(notSentCount);

			int savedCount = 0;
			if (obj[9] != null) {
				BigInteger savedCountBig = obj[9] != null
						? new BigInteger(String.valueOf(obj[9]))
						: BigInteger.ZERO;
				savedCount = savedCountBig.intValue();
			}
			child.setSavedCount(savedCount);

			int notSavedCount = 0;
			if (obj[10] != null) {
				BigInteger notSavedCountBig = obj[10] != null
						? new BigInteger(String.valueOf(obj[10]))
						: BigInteger.ZERO;
				notSavedCount = notSavedCountBig.intValue();
			}
			child.setNotSavedCount(notSavedCount);

			int errorCount = 0;
			if (obj[11] != null) {
				BigInteger errorCountBig = obj[11] != null
						? new BigInteger(String.valueOf(obj[11]))
						: BigInteger.ZERO;
				errorCount = errorCountBig.intValue();
			}
			child.setErrorCount(errorCount);

			int totalCount = 0;
			if (obj[12] != null) {
				BigInteger totalCountBig = obj[12] != null
						? new BigInteger(String.valueOf(obj[12]))
						: BigInteger.ZERO;
				totalCount = totalCountBig.intValue();
			}
			child.setTotalCount(totalCount);
			child.setEyTotDoc(
					obj[13] != null ? new BigInteger(String.valueOf(obj[13]))
							: BigInteger.ZERO);
			List<String> findStatusP = gstnSubmitRepository
					.findStatusPByGstinAndRetPeriodAndRetrunType(gstin,
							retPeriod, APIConstants.GSTR1.toUpperCase());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1ReviewSummaryRequestDocsImpl "
						+ "findStatusP : {}", findStatusP);
			}

			if (!findStatusP.isEmpty()) {
				child.setEyStatus("SUBMITTED");
			} else {
				child.setEyStatus("");
			}

			String currentDate = "";
			String currentTime = "";

			Pair<String, String> lastTransactionReturnStatus = returnStatusUtil
					.getLastTransactionReturnStatus(gstin, retPeriod,
							APIConstants.GSTR1.toUpperCase());

			if (lastTransactionReturnStatus != null) {
				if (!Strings.isNullOrEmpty(
						lastTransactionReturnStatus.getValue1())) {
					currentDate = lastTransactionReturnStatus.getValue1()
							.substring(0, 10);
					currentTime = lastTransactionReturnStatus.getValue1()
							.substring(13, 21);
				}
			}
			String date = fmtDate(currentDate);
			child.setEyDate(date);
			child.setEyTime(currentTime);
			itemDto.add(child);
		}

		return itemDto;
	}

	private static String deriveStatusByTotSavedErrorCount(int totalCount,
			int savedCount, int errorCount, int notSentCount) {
		if (totalCount > 0) {
			if (totalCount == notSentCount) {
				return "NOT INITIATED";
			} else if (totalCount == savedCount) {
				return "SAVED";
			} else if (totalCount == errorCount) {
				return "FAILED";
			} else {
				return "PARTIALLY SAVED";
			}
		} else {
			return "NOT INITIATED";
		}
	}

	public List<Gstr1ReviewSummaryRequestItemDto> reviewSummary(String gstin,
			String returnPeriod, String entityName, String entityPan,
			Long entityId, String companyCode) {
		List<Gstr1ReviewSummaryRequestItemDto> finalItemsDto = new ArrayList<>();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);

		LOGGER.debug("entity ID {} ", entityId);
		List<Long> entityIds = new ArrayList<>();
		entityIds.add(entityId);
		request.setEntityId(entityIds);
		List<Gstr1SummarySectionDto> outwardDocSectionDtos = basicDocGstnSumScrSecDao
				.loadBasicSummarySection(request);

		Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForOutwardSections = mapGstnDataForAllOutwardSections(
				outwardDocSectionDtos);
		List<Gstr1SummaryNilSectionDto> gstr1SumNilSecDtos = basicDocGstnSumScrSecDao
				.loadBasicSummarySectionNil(request);

		List<Gstr1SummaryDocSectionDto> basicDocGstnSumDaos = basicDocGstnSumScrSecDao
				.loadBasicSummaryDocSection(request);

		convertOutwardRSDocsAsDtos(finalItemsDto, gstin, returnPeriod,
				entityName, entityPan, companyCode,
				mapGstnDataForOutwardSections);

		convertNilExtNonAsDtos(finalItemsDto, gstin, returnPeriod, entityName,
				entityPan, companyCode, gstr1SumNilSecDtos);
		converthHsnSummaryAsDtos(finalItemsDto, gstin, returnPeriod, entityName,
				entityPan, companyCode, mapGstnDataForOutwardSections,
				request.getEntityId());
		convertAdvanceSummaryAsDtos(finalItemsDto, gstin, returnPeriod,
				entityName, entityPan, companyCode,
				mapGstnDataForOutwardSections);
		convertSezRSDocsAsDtos(finalItemsDto, gstin, returnPeriod, entityName,
				entityPan, companyCode, mapGstnDataForOutwardSections);
		convertDocIssueRSDocsAsDtos(finalItemsDto, gstin, returnPeriod,
				entityName, entityPan, companyCode, basicDocGstnSumDaos);
		// -------107050-----table 14,15 rev integ reveiw summerary----//
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(request);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Size of result list: {}", result.size());
		}

		Map<String, List<Gstr1SummarySectionDto>> eySummaries = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("eySummaries size: {}", eySummaries.size());
		}

		if (eySummaries != null
				&& eySummaries.containsKey(GSTConstants.GSTR1_14)) {
			List<Gstr1SummarySectionDto> tbl14SectionList = eySummaries
					.get(GSTConstants.GSTR1_14);
			if (tbl14SectionList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl14SectionList");
				}
				table14And15ValuesMappingToDto(finalItemsDto, tbl14SectionList);
				
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl14SectionList is null");
					
				}
			}
		}

		List<Gstr1SummarySectionDto> tbl14ofOneList = eySummaries
				.get(GSTConstants.GSTR1_14I);
		if (tbl14ofOneList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14ofOneList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl14ofOneList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14ofOneList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl14ofTwoList = eySummaries
				.get(GSTConstants.GSTR1_14II);
		if (tbl14ofTwoList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14ofTwoList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl14ofTwoList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14ofTwoList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl14AmdSectionList = eySummaries
				.get(GSTConstants.GSTR1_14A);
		if (tbl14AmdSectionList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdSectionList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl14AmdSectionList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdSectionList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl14AmdofOneList = eySummaries
				.get(GSTConstants.GSTR1_14AI);
		if (tbl14AmdofOneList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdofOneList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl14AmdofOneList);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdofOneList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl14AmdofTwoList = eySummaries
				.get(GSTConstants.GSTR1_14AII);
		if (tbl14AmdofTwoList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdofTwoList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl14AmdofTwoList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl14AmdofTwoList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl15SectionList = eySummaries
				.get(GSTConstants.GSTR1_15);
		if (tbl15SectionList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15SectionList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl15SectionList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15SectionList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl15ofOneList = eySummaries
				.get(GSTConstants.GSTR1_15I);
		if (tbl15ofOneList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofOneList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl15ofOneList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofOneList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl15ofTwoList = eySummaries
				.get(GSTConstants.GSTR1_15II);
		if (tbl15ofTwoList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofTwoList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl15ofTwoList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofTwoList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl15ofThreeList = eySummaries
				.get(GSTConstants.GSTR1_15III);
		if (tbl15ofThreeList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofThreeList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl15ofThreeList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofThreeList is null");
			}
		}

		List<Gstr1SummarySectionDto> tbl15ofFourList = eySummaries
				.get(GSTConstants.GSTR1_15IV);
		if (tbl15ofFourList != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofFourList");
			}
			table14And15ValuesMappingToDto(finalItemsDto, tbl15ofFourList);
			
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("reveiw summary values for table tbl15ofFourList is  null");
			}
		}

		
			List<Gstr1SummarySectionDto> tbl15AmdofOneSecList = eySummaries
					.get(GSTConstants.GSTR1_15AI);
			if (tbl15AmdofOneSecList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdofOneSecList");
				}
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15AmdofOneSecList);
				
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdofOneSecList is null");
				}
			}
		

		
			List<Gstr1SummarySectionDto> tbl15AmdofOneList = eySummaries
					.get(GSTConstants.GSTR1_15AIA);
			if (tbl15AmdofOneList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table 15A.1.a");
				}
				
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15AmdofOneList);
				
				}
			 else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table 15A.1.a is null");
				}
			}
		

	
			List<Gstr1SummarySectionDto> tbl15AmdofTwoList = eySummaries
					.get(GSTConstants.GSTR1_15AIB);
			if (tbl15AmdofTwoList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table 15A.1.b");
				}
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15AmdofTwoList);
				
				}
			 else {
				 if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("reveiw summary values for table 15A.1.b is null ");
					}
			}
		

		
			List<Gstr1SummarySectionDto> tbl15AmdoftwoSecList = eySummaries
					.get(GSTConstants.GSTR1_15AII);
			if (tbl15AmdoftwoSecList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdoftwoSecList");
				}
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15AmdoftwoSecList);
				
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdofOneSecList is null");
				}
			}
		

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIIA)) {
			List<Gstr1SummarySectionDto> tbl15AmdofThreeList = eySummaries
					.get(GSTConstants.GSTR1_15AIIA);
			if (tbl15AmdofThreeList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdofThreeList");
				}
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15AmdofThreeList);
				
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table tbl15AmdofThreeList is null");
				}
			}
		}

		if (eySummaries.containsKey(GSTConstants.GSTR1_15AIIB)) {
			List<Gstr1SummarySectionDto> tbl15FourofThreeList = eySummaries
					.get(GSTConstants.GSTR1_15AIIB);
			if (tbl15FourofThreeList != null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table 15A.2.a");
				}
				table14And15ValuesMappingToDto(finalItemsDto,
						tbl15FourofThreeList);
				
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("reveiw summary values for table 15A.2.a is null");
				}
			}
		}
		return finalItemsDto;
	}

	private Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForAllOutwardSections(
			List<Gstr1SummarySectionDto> docSecDtos) {

		Map<String, List<Gstr1SummarySectionDto>> mapGstr1SummaryDocSecDtos = new HashMap<>();
		docSecDtos.forEach(docSecDto -> {
			StringBuilder key = new StringBuilder();
			key.append(docSecDto.getTaxDocType());
			String docKey = key.toString();
			if (mapGstr1SummaryDocSecDtos.containsKey(docKey)) {
				List<Gstr1SummarySectionDto> docSectionDtos = mapGstr1SummaryDocSecDtos
						.get(docKey);
				docSectionDtos.add(docSecDto);
				mapGstr1SummaryDocSecDtos.put(docKey, docSectionDtos);
			} else {
				List<Gstr1SummarySectionDto> docSectionDtos = new ArrayList<>();
				docSectionDtos.add(docSecDto);
				mapGstr1SummaryDocSecDtos.put(docKey, docSectionDtos);
			}
		});
		return mapGstr1SummaryDocSecDtos;
	}

	private void convertOutwardRSDocsAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> finalItemsDto, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForOutwardSections) {

		List<Gstr1ReviewSummaryRequestItemDto> itemDtos = convertOutwardRSDoc(
				entityName, entityPan, companyCode, gstin, returnPeriod);

		if (!itemDtos.isEmpty()) {
			Map<String, List<Gstr1ReviewSummaryRequestItemDto>> mapOutwardReviewSummary = createMapByOutwardReturnTaxCat(
					itemDtos);

			mapOutwardReviewSummary.keySet().forEach(key -> {
				Gstr1ReviewSummaryRequestItemDto itemDto = new Gstr1ReviewSummaryRequestItemDto();
				List<Gstr1ReviewSummaryRequestItemDto> itemsDto = mapOutwardReviewSummary
						.get(key);
				for (Gstr1ReviewSummaryRequestItemDto dto : itemsDto) {
					itemDto.setEntityName(dto.getEntityName());
					itemDto.setEntityPan(dto.getEntityPan());
					itemDto.setCompanyCode(dto.getCompanyCode());
					itemDto.setRetPer(dto.getRetPer());
					itemDto.setDataType(dto.getDataType());
					itemDto.setTaxCate(dto.getTaxCate());
					itemDto.setGstinNum(dto.getGstinNum());
					itemDto.setAspCount(dto.getAspCount());
					itemDto.setAspInval(dto.getAspInval());
					itemDto.setAspTbval(dto.getAspTbval());
					itemDto.setAspTxval(dto.getAspTxval());
					itemDto.setAspIgstval(dto.getAspIgstval());
					itemDto.setAspCgstval(dto.getAspCgstval());
					itemDto.setAspSgstval(dto.getAspSgstval());
					itemDto.setAspCessval(dto.getAspCessval());

					// Live Gstin Data
					List<Gstr1SummarySectionDto> sumSecDtos = new ArrayList<>();
					if ("EXPORTS".equalsIgnoreCase(dto.getTaxCate())) {
						sumSecDtos = mapGstnDataForOutwardSections.get("EXP");
					} else if ("EXPORTS-A".equalsIgnoreCase(dto.getTaxCate())) {
						sumSecDtos = mapGstnDataForOutwardSections.get("EXPA");
					} else {
						sumSecDtos = mapGstnDataForOutwardSections
								.get(dto.getTaxCate());
					}
					if (sumSecDtos != null && !sumSecDtos.isEmpty()) {
						sumSecDtos.forEach(sumSecDto -> {
							itemDto.setGstnCount(sumSecDto.getRecords() > 0
									? BigInteger.valueOf(sumSecDto.getRecords())
									: BigInteger.ZERO);
							itemDto.setGstnIgstval(sumSecDto.getIgst() != null
									? sumSecDto.getIgst() : BigDecimal.ZERO);
							itemDto.setGstnCgstval(sumSecDto.getCgst() != null
									? sumSecDto.getCgst() : BigDecimal.ZERO);
							itemDto.setGstnSgstval(sumSecDto.getSgst() != null
									? sumSecDto.getSgst() : BigDecimal.ZERO);
							itemDto.setGstnCessval(sumSecDto.getCess() != null
									? sumSecDto.getCess() : BigDecimal.ZERO);
							itemDto.setGstnTbval(
									sumSecDto.getTaxableValue() != null
											? sumSecDto.getTaxableValue()
											: BigDecimal.ZERO);
							itemDto.setGstnTxval(
									sumSecDto.getTaxPayable() != null
											? sumSecDto.getTaxPayable()
											: BigDecimal.ZERO);
							itemDto.setGstnInval(sumSecDto.getInvValue() != null
									? sumSecDto.getInvValue()
									: BigDecimal.ZERO);
						});
					}
					itemDto.setDiffCount(
							itemDto.getAspCount() != null
									? itemDto.getAspCount()
											.subtract(itemDto.getGstnCount())
									: BigInteger.ZERO);
					itemDto.setDiffTbval(
							itemDto.getAspTbval() != null
									? itemDto.getAspTbval()
											.subtract(itemDto.getGstnTbval())
									: BigDecimal.ZERO);
					itemDto.setDiffTxval(
							itemDto.getAspTxval() != null
									? itemDto.getAspTxval()
											.subtract(itemDto.getGstnTxval())
									: BigDecimal.ZERO);
					itemDto.setDiffInval(
							itemDto.getAspInval() != null
									? itemDto.getAspInval()
											.subtract(itemDto.getGstnInval())
									: BigDecimal.ZERO);
					itemDto.setDiffIgstval(itemDto.getAspIgstval() != null
							? itemDto.getAspIgstval()
									.subtract(itemDto.getGstnIgstval())
							: BigDecimal.ZERO);
					itemDto.setDiffCgstval(itemDto.getAspCgstval() != null
							? itemDto.getAspCgstval()
									.subtract(itemDto.getGstnCgstval())
							: BigDecimal.ZERO);
					itemDto.setDiffSgstval(itemDto.getAspSgstval() != null
							? itemDto.getAspSgstval()
									.subtract(itemDto.getGstnSgstval())
							: BigDecimal.ZERO);
					itemDto.setDiffCessval(itemDto.getAspCessval() != null
							? itemDto.getAspCessval()
									.subtract(itemDto.getGstnCessval())
							: BigDecimal.ZERO);
					itemDto.setProfitCenter(dto.getProfitCenter());
					itemDto.setPlantCode(dto.getPlantCode());

					itemDto.setLocation(dto.getLocation());
					itemDto.setSalesOrganization(dto.getSalesOrganization());
					itemDto.setDistChannel(dto.getDistChannel());
					itemDto.setDivision(dto.getDivision());
					itemDto.setUseraccess1(dto.getUseraccess1());
					itemDto.setUseraccess2(dto.getUseraccess2());
					itemDto.setUseraccess3(dto.getUseraccess3());
					itemDto.setUseraccess4(dto.getUseraccess4());
					itemDto.setUseraccess5(dto.getUseraccess5());
					itemDto.setUseraccess6(dto.getUseraccess6());
				}
				finalItemsDto.add(itemDto);
			});
		}
	}

	private Map<String, List<Gstr1ReviewSummaryRequestItemDto>> createMapByOutwardReturnTaxCat(
			List<Gstr1ReviewSummaryRequestItemDto> itemDto) {

		Map<String, List<Gstr1ReviewSummaryRequestItemDto>> returnSectionMap = new LinkedHashMap<>();

		itemDto.forEach(dto -> {
			StringBuilder key = new StringBuilder();
			key.append(dto.getGstinNum());
			key.append("_");
			key.append(dto.getRetPer());
			key.append("_");
			key.append(dto.getTaxCate());
			String docKey = key.toString();
			if (returnSectionMap.containsKey(docKey)) {
				List<Gstr1ReviewSummaryRequestItemDto> dtos = returnSectionMap
						.get(docKey);
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			} else {
				List<Gstr1ReviewSummaryRequestItemDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(docKey, dtos);
			}
		});
		return returnSectionMap;
	}

	private List<Gstr1ReviewSummaryRequestItemDto> convertOutwardRSDoc(
			String entityName, String entityPan, String companyCode,
			String gstin, String returnPeriod) {
		List<Gstr1ReviewSummaryRequestItemDto> childArry = new ArrayList<>();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummarySectionDto> outwardSumB2bSections = basicDocSummaryScreenSectionDao
				.loadBasicSummarySection(request);
		Gstr1ReviewSummaryRequestItemDto child2 = new Gstr1ReviewSummaryRequestItemDto();
		BigInteger aspCount = BigInteger.ZERO;
		BigDecimal aspInvVal = BigDecimal.ZERO;
		BigDecimal aspTbVal = BigDecimal.ZERO;
		BigDecimal aspTxVal = BigDecimal.ZERO;
		BigDecimal aspIgstVal = BigDecimal.ZERO;
		BigDecimal aspCgstVal = BigDecimal.ZERO;
		BigDecimal aspSgstVal = BigDecimal.ZERO;
		BigDecimal aspCessVal = BigDecimal.ZERO;
		for (Gstr1SummarySectionDto outwardSumB2bSection : outwardSumB2bSections) {
			if ("CDNUR".equalsIgnoreCase(outwardSumB2bSection.getTaxDocType())
					|| "CDNUR-EXPORTS".equalsIgnoreCase(
							outwardSumB2bSection.getTaxDocType())
					|| "CDNUR-B2CL".equalsIgnoreCase(
							outwardSumB2bSection.getTaxDocType())) {
				aspCount = aspCount
						.add(outwardSumB2bSection.getRecords() != null
								? new BigInteger(String.valueOf(
										outwardSumB2bSection.getRecords()))
								: BigInteger.ZERO);
				aspInvVal = aspInvVal
						.add(outwardSumB2bSection.getInvValue() != null
								? outwardSumB2bSection.getInvValue()
								: BigDecimal.ZERO);
				aspTbVal = aspTbVal
						.add(outwardSumB2bSection.getTaxableValue() != null
								? outwardSumB2bSection.getTaxableValue()
								: BigDecimal.ZERO);

				aspTxVal = aspTxVal
						.add(outwardSumB2bSection.getTaxPayable() != null
								? outwardSumB2bSection.getTaxPayable()
								: BigDecimal.ZERO);
				aspIgstVal = aspIgstVal
						.add(outwardSumB2bSection.getIgst() != null
								? outwardSumB2bSection.getIgst()
								: BigDecimal.ZERO);
				aspCgstVal = aspCgstVal
						.add(outwardSumB2bSection.getCgst() != null
								? outwardSumB2bSection.getCgst()
								: BigDecimal.ZERO);
				aspSgstVal = aspSgstVal
						.add(outwardSumB2bSection.getSgst() != null
								? outwardSumB2bSection.getSgst()
								: BigDecimal.ZERO);
				aspCessVal = aspCessVal
						.add(outwardSumB2bSection.getCess() != null
								? outwardSumB2bSection.getCess()
								: BigDecimal.ZERO);

			}
		}
		child2.setEntityPan(entityPan);
		child2.setCompanyCode(companyCode);
		if (entityName != null) {
			child2.setEntityName(StringUtils.upperCase(entityName));
		}
		child2.setRetPer(returnPeriod);
		child2.setGstinNum(gstin);
		child2.setDataType("OUTWARD");

		child2.setTaxCate("CDNUR");
		child2.setAspCount(aspCount);
		child2.setAspInval(aspInvVal);
		child2.setAspTbval(aspTbVal);
		child2.setAspTxval(aspTxVal);

		child2.setAspIgstval(aspIgstVal);
		child2.setAspCgstval(aspCgstVal);
		child2.setAspSgstval(aspSgstVal);
		child2.setAspCessval(aspCessVal);
		childArry.add(child2);

		outwardSumB2bSections.forEach(outwardSumB2bSection -> {
			if (!"CDNUR".equalsIgnoreCase(outwardSumB2bSection.getTaxDocType())
					&& !"CDNUR-EXPORTS".equalsIgnoreCase(
							outwardSumB2bSection.getTaxDocType())
					&& !"CDNUR-B2CL".equalsIgnoreCase(
							outwardSumB2bSection.getTaxDocType())) {
				Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
				childArry.add(child);
				child.setEntityPan(entityPan);
				child.setCompanyCode(companyCode);
				if (entityName != null) {
					child.setEntityName(StringUtils.upperCase(entityName));
				}
				child.setRetPer(returnPeriod);
				child.setGstinNum(gstin);
				child.setDataType("OUTWARD");

				child.setTaxCate(outwardSumB2bSection.getTaxDocType());
				child.setDocType(outwardSumB2bSection.getTaxDocType());
				child.setAspCount(outwardSumB2bSection.getRecords() != null
						? new BigInteger(String
								.valueOf(outwardSumB2bSection.getRecords()))
						: BigInteger.ZERO);
				child.setAspInval(outwardSumB2bSection.getInvValue());
				child.setAspTbval(outwardSumB2bSection.getTaxableValue());
				child.setAspTxval(outwardSumB2bSection.getTaxPayable());

				child.setAspIgstval(outwardSumB2bSection.getIgst());
				child.setAspCgstval(outwardSumB2bSection.getCgst());
				child.setAspSgstval(outwardSumB2bSection.getSgst());
				child.setAspCessval(outwardSumB2bSection.getCess());
			}
		});
		return childArry;
	}

	private void convertSezRSDocsAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> childArry, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForOutwardSections) {

		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummarySectionDto> sumSecDtos = sezBasicDocSummaryDao
				.loadBasicSummarySEZTSection(request);

		sumSecDtos.forEach(sumSecDto -> {
			Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
			childArry.add(child);
			if (entityName != null) {
				child.setEntityName(StringUtils.upperCase(entityName));
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setRetPer(returnPeriod);
			child.setGstinNum(gstin);

			child.setTaxCate(sumSecDto.getTaxDocType());
			child.setDataType("SEZ");
			child.setAspCount(sumSecDto.getRecords() != null
					? new BigInteger(String.valueOf(sumSecDto.getRecords()))
					: BigInteger.ZERO);
			child.setAspInval(sumSecDto.getInvValue() != null
					? new BigDecimal(String.valueOf(sumSecDto.getInvValue()))
					: BigDecimal.ZERO);
			child.setAspTbval(sumSecDto.getTaxableValue() != null
					? new BigDecimal(
							String.valueOf(sumSecDto.getTaxableValue()))
					: BigDecimal.ZERO);
			child.setAspTxval(sumSecDto.getTaxPayable() != null
					? new BigDecimal(String.valueOf(sumSecDto.getTaxPayable()))
					: BigDecimal.ZERO);

			child.setAspIgstval(sumSecDto.getIgst() != null
					? new BigDecimal(String.valueOf(sumSecDto.getIgst()))
					: BigDecimal.ZERO);
			child.setAspCgstval(sumSecDto.getCgst() != null
					? new BigDecimal(String.valueOf(sumSecDto.getCgst()))
					: BigDecimal.ZERO);
			child.setAspSgstval(sumSecDto.getSgst() != null
					? new BigDecimal(String.valueOf(sumSecDto.getSgst()))
					: BigDecimal.ZERO);

			child.setAspCessval(sumSecDto.getCess() != null
					? new BigDecimal(String.valueOf(sumSecDto.getCess()))
					: BigDecimal.ZERO);

			// Live Gstin Data
			List<Gstr1SummarySectionDto> summarySecDtos = mapGstnDataForOutwardSections
					.get(sumSecDto.getTaxDocType());
			if (summarySecDtos != null && !summarySecDtos.isEmpty()) {
				summarySecDtos.forEach(summarySecDto -> {
					child.setGstnCount(summarySecDto.getRecords() > 0
							? BigInteger.valueOf(summarySecDto.getRecords())
							: BigInteger.ZERO);
					child.setGstnIgstval(summarySecDto.getIgst() != null
							? summarySecDto.getIgst() : BigDecimal.ZERO);
					child.setGstnCgstval(summarySecDto.getCgst() != null
							? summarySecDto.getCgst() : BigDecimal.ZERO);
					child.setGstnSgstval(summarySecDto.getSgst() != null
							? summarySecDto.getSgst() : BigDecimal.ZERO);
					child.setGstnCessval(summarySecDto.getCess() != null
							? summarySecDto.getCess() : BigDecimal.ZERO);
					child.setGstnTbval(summarySecDto.getTaxableValue() != null
							? summarySecDto.getTaxableValue()
							: BigDecimal.ZERO);
					child.setGstnTxval(summarySecDto.getTaxPayable() != null
							? summarySecDto.getTaxPayable() : BigDecimal.ZERO);
					child.setGstnInval(summarySecDto.getInvValue() != null
							? summarySecDto.getInvValue() : BigDecimal.ZERO);
				});
			}
			child.setDiffCount(child.getAspCount() != null
					? child.getAspCount().subtract(child.getGstnCount())
					: BigInteger.ZERO);
			child.setDiffTbval(child.getAspTbval() != null
					? child.getAspTbval().subtract(child.getGstnTbval())
					: BigDecimal.ZERO);
			child.setDiffTxval(child.getAspTxval() != null
					? child.getAspTxval().subtract(child.getGstnTxval())
					: BigDecimal.ZERO);
			child.setDiffInval(child.getAspInval() != null
					? child.getAspInval().subtract(child.getGstnInval())
					: BigDecimal.ZERO);
			child.setDiffIgstval(child.getAspIgstval() != null
					? child.getAspIgstval().subtract(child.getGstnIgstval())
					: BigDecimal.ZERO);
			child.setDiffCgstval(child.getAspCgstval() != null
					? child.getAspCgstval().subtract(child.getGstnCgstval())
					: BigDecimal.ZERO);
			child.setDiffSgstval(child.getAspSgstval() != null
					? child.getAspSgstval().subtract(child.getGstnSgstval())
					: BigDecimal.ZERO);
			child.setDiffCessval(child.getAspCessval() != null
					? child.getAspCessval().subtract(child.getGstnCessval())
					: BigDecimal.ZERO);

		});
	}

	private void convertDocIssueRSDocsAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> childArry, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			List<Gstr1SummaryDocSectionDto> basicDocGstnSumDaos) {
		Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummaryDocSectionDto> docSecDtos = basicDocIssueSummaryDao
				.loadBasicSummarySection(request);

		for (Gstr1SummaryDocSectionDto docSecDto : docSecDtos) {
			if (entityName != null) {
				child.setEntityName(StringUtils.upperCase(entityName));
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setTaxCate("DOC");
			child.setDataType("DOC");
			child.setRetPer(returnPeriod);
			child.setGstinNum(gstin);
			child.setAspCount(docSecDto.getTotal() != null
					? new BigInteger(String.valueOf(docSecDto.getTotal()))
					: BigInteger.ZERO);
			child.setAspCancel(docSecDto.getDocCancelled() != null
					? new BigDecimal((docSecDto.getDocCancelled())) : null);
			child.setAspNetissue(docSecDto.getNetIssued() != null
					? new BigDecimal((String.valueOf(docSecDto.getNetIssued())))
					: null);

			// Live Gstin Data
			if (basicDocGstnSumDaos != null) {
				basicDocGstnSumDaos.forEach(basicDocGstnSumDao -> {
					child.setGstnCount(new BigInteger(
							String.valueOf(basicDocGstnSumDao.getTotal())));
					child.setGstCancel(basicDocGstnSumDao.getDocCancelled() > 0
							? BigDecimal.valueOf(
									basicDocGstnSumDao.getDocCancelled())
							: BigDecimal.ZERO);
					child.setGstNetissue(basicDocGstnSumDao.getNetIssued() > 0
							? BigDecimal
									.valueOf(basicDocGstnSumDao.getNetIssued())
							: BigDecimal.ZERO);
				});
			}
			child.setDiffCount(child.getAspCount() != null
					? child.getAspCount().subtract(child.getGstnCount())
					: BigInteger.ZERO);
			child.setDiffCancel(child.getAspCancel() != null
					? child.getAspCancel().subtract(child.getGstCancel())
					: BigDecimal.ZERO);
			child.setDiffNetissue(child.getAspNetissue() != null
					? child.getAspNetissue().subtract(child.getGstNetissue())
					: BigDecimal.ZERO);
		}
		childArry.add(child);
	}

	private void convertNilExtNonAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> childArry, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			List<Gstr1SummaryNilSectionDto> gstr1SumNilSecDtos) {

		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummaryNilSectionDto> nilSectionDtos = basicDocIssueSummaryDao
				.loadBasicSummarySectionNil(request);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ReviewSummaryRequestDocsImpl "
					+ "nilSectionDtos : {}", nilSectionDtos.size());
		}
		Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
		BigDecimal gstNilRsup = BigDecimal.ZERO;
		BigDecimal gstExpSup = BigDecimal.ZERO;
		BigDecimal gstNonGsup = BigDecimal.ZERO;

		for (Gstr1SummaryNilSectionDto nilSectionDto : nilSectionDtos) {
			if ("CR".equalsIgnoreCase(nilSectionDto.getDocType())
					|| "RCR".equalsIgnoreCase(nilSectionDto.getDocType())) {
				gstNilRsup = gstNilRsup
						.subtract(nilSectionDto.getAspNitRated() != null
								? nilSectionDto.getAspNitRated()
								: BigDecimal.ZERO);
				gstExpSup = gstExpSup
						.subtract(nilSectionDto.getAspExempted() != null
								? nilSectionDto.getAspExempted()
								: BigDecimal.ZERO);
				gstNonGsup = gstNonGsup
						.subtract(nilSectionDto.getAspNonGst() != null
								? nilSectionDto.getAspNonGst()
								: BigDecimal.ZERO);
			} else {
				gstNilRsup = gstNilRsup
						.add(nilSectionDto.getAspNitRated() != null
								? nilSectionDto.getAspNitRated()
								: BigDecimal.ZERO);
				gstExpSup = gstExpSup.add(nilSectionDto.getAspExempted() != null
						? nilSectionDto.getAspExempted() : BigDecimal.ZERO);
				gstNonGsup = gstNonGsup.add(nilSectionDto.getAspNonGst() != null
						? nilSectionDto.getAspNonGst() : BigDecimal.ZERO);
			}
		}
		child.setEntityPan(entityPan);
		child.setCompanyCode(companyCode);
		if (entityName != null) {
			child.setEntityName(StringUtils.upperCase(entityName));
		}
		child.setRetPer(returnPeriod);
		child.setGstinNum(gstin);
		child.setDataType("NIL");
		child.setTaxCate("NIL");
		child.setAspNilRsup(gstNilRsup);
		child.setAspExpSup(gstExpSup);
		child.setAspNonGsup(gstNonGsup);

		// Live Gstin Data
		if (gstr1SumNilSecDtos != null && !gstr1SumNilSecDtos.isEmpty()) {
			gstr1SumNilSecDtos.forEach(gstr1SumNilSecDto -> {
				child.setGstNilRsup(gstr1SumNilSecDto.getAspNitRated() != null
						? gstr1SumNilSecDto.getAspNitRated() : BigDecimal.ZERO);
				child.setGstExpSup(gstr1SumNilSecDto.getAspExempted() != null
						? gstr1SumNilSecDto.getAspExempted() : BigDecimal.ZERO);
				child.setGstNonGsup(gstr1SumNilSecDto.getAspNonGst() != null
						? gstr1SumNilSecDto.getAspNonGst() : BigDecimal.ZERO);
			});
		}
		child.setDiffNilRsup(child.getAspNilRsup() != null
				? child.getAspNilRsup().subtract(child.getGstNilRsup())
				: BigDecimal.ZERO);
		child.setDiffExpSup(child.getAspExpSup() != null
				? child.getAspExpSup().subtract(child.getGstExpSup())
				: BigDecimal.ZERO);
		child.setDiffNonGsup(child.getAspNonGsup() != null
				? child.getAspNonGsup().subtract(child.getGstNonGsup())
				: BigDecimal.ZERO);
		childArry.add(child);
	}

	private void converthHsnSummaryAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> childArry, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForOutwardSections,
			List<Long> entityIds) {
		Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		request.setEntityId(entityIds);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummaryCDSectionDto> hsnDetailsSecDtos = basicHsnSummaryDao
				.loadBasicSummarySection(request);

		LOGGER.debug("dtos {} ", hsnDetailsSecDtos);
		for (Gstr1SummaryCDSectionDto hsnDetailsSecDto : hsnDetailsSecDtos) {
			if (entityName != null) {
				child.setEntityName(StringUtils.upperCase(entityName));
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setRetPer(returnPeriod);
			child.setGstinNum(gstin);
			child.setTaxCate("HSN");
			child.setDataType("HSN");
			child.setAspCount(hsnDetailsSecDto.getRecords() != null
					? new BigInteger(
							String.valueOf(hsnDetailsSecDto.getRecords()))
					: BigInteger.ZERO);
			child.setAspInval(hsnDetailsSecDto.getInvValue());
			child.setAspTbval(hsnDetailsSecDto.getTaxableValue());
			child.setAspTxval(hsnDetailsSecDto.getTaxPayable());
			child.setAspIgstval(hsnDetailsSecDto.getIgst());
			child.setAspCgstval(hsnDetailsSecDto.getCgst());
			child.setAspSgstval(hsnDetailsSecDto.getSgst());
			child.setAspCessval(hsnDetailsSecDto.getCess());
			hsnDetailsSecDto.setTaxDocType("HSN");

			// Live Gstin Data
			List<Gstr1SummarySectionDto> summarySecDtos = mapGstnDataForOutwardSections
					.get(hsnDetailsSecDto.getTaxDocType());
			if (summarySecDtos != null && !summarySecDtos.isEmpty()) {
				summarySecDtos.forEach(summarySecDto -> {
					child.setGstnCount(summarySecDto.getRecords() > 0
							? BigInteger.valueOf(summarySecDto.getRecords())
							: BigInteger.ZERO);
					child.setGstnIgstval(summarySecDto.getIgst() != null
							? summarySecDto.getIgst() : BigDecimal.ZERO);
					child.setGstnCgstval(summarySecDto.getCgst() != null
							? summarySecDto.getCgst() : BigDecimal.ZERO);
					child.setGstnSgstval(summarySecDto.getSgst() != null
							? summarySecDto.getSgst() : BigDecimal.ZERO);
					child.setGstnCessval(summarySecDto.getCess() != null
							? summarySecDto.getCess() : BigDecimal.ZERO);
					child.setGstnTbval(summarySecDto.getTaxableValue() != null
							? summarySecDto.getTaxableValue()
							: BigDecimal.ZERO);
					child.setGstnTxval(summarySecDto.getTaxPayable() != null
							? summarySecDto.getTaxPayable() : BigDecimal.ZERO);
					child.setGstnInval(summarySecDto.getInvValue() != null
							? summarySecDto.getInvValue() : BigDecimal.ZERO);
				});
			}
			child.setDiffCount(child.getAspCount() != null
					? child.getAspCount().subtract(child.getGstnCount())
					: BigInteger.ZERO);
			child.setDiffTbval(child.getAspTbval() != null
					? child.getAspTbval().subtract(child.getGstnTbval())
					: BigDecimal.ZERO);
			child.setDiffTxval(child.getAspTxval() != null
					? child.getAspTxval().subtract(child.getGstnTxval())
					: BigDecimal.ZERO);
			child.setDiffInval(child.getAspInval() != null
					? child.getAspInval().subtract(child.getGstnInval())
					: BigDecimal.ZERO);
			child.setDiffIgstval(child.getAspIgstval() != null
					? child.getAspIgstval().subtract(child.getGstnIgstval())
					: BigDecimal.ZERO);
			child.setDiffCgstval(child.getAspCgstval() != null
					? child.getAspCgstval().subtract(child.getGstnCgstval())
					: BigDecimal.ZERO);
			child.setDiffSgstval(child.getAspSgstval() != null
					? child.getAspSgstval().subtract(child.getGstnSgstval())
					: BigDecimal.ZERO);
			child.setDiffCessval(child.getAspCessval() != null
					? child.getAspCessval().subtract(child.getGstnCessval())
					: BigDecimal.ZERO);
		}
		childArry.add(child);

	}

	private void convertAdvanceSummaryAsDtos(
			List<Gstr1ReviewSummaryRequestItemDto> childArry, String gstin,
			String returnPeriod, String entityName, String entityPan,
			String companyCode,
			Map<String, List<Gstr1SummarySectionDto>> mapGstnDataForOutwardSections) {

		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		List<Gstr1SummarySectionDto> advAdjusts = advBasicDocSummaryDao
				.loadBasicSummaryATSection(request);
		advAdjusts.forEach(advAdjust -> {
			Gstr1ReviewSummaryRequestItemDto child = new Gstr1ReviewSummaryRequestItemDto();
			childArry.add(child);
			if (entityName != null) {
				child.setEntityName(StringUtils.upperCase(entityName));
			}
			child.setEntityPan(entityPan);
			child.setCompanyCode(companyCode);
			child.setRetPer(returnPeriod);
			child.setGstinNum(gstin);
			child.setTaxCate(advAdjust.getTaxDocType());

			child.setDataType("ADV");

			child.setAspCount(advAdjust.getRecords() != null
					? new BigInteger(String.valueOf(advAdjust.getRecords()))
					: BigInteger.ZERO);
			child.setAspInval(advAdjust.getInvValue() != null
					? advAdjust.getInvValue() : BigDecimal.ZERO);
			child.setAspTbval(advAdjust.getTaxableValue() != null
					? new BigDecimal(
							String.valueOf(advAdjust.getTaxableValue()))
					: BigDecimal.ZERO);
			child.setAspTxval(advAdjust.getTaxPayable() != null
					? advAdjust.getTaxPayable() : BigDecimal.ZERO);

			child.setAspIgstval(advAdjust.getIgst() != null
					? advAdjust.getIgst() : BigDecimal.ZERO);
			child.setAspCgstval(advAdjust.getCgst() != null
					? advAdjust.getCgst() : BigDecimal.ZERO);
			child.setAspSgstval(advAdjust.getSgst() != null
					? advAdjust.getSgst() : BigDecimal.ZERO);

			child.setAspCessval(advAdjust.getCess() != null
					? advAdjust.getCess() : BigDecimal.ZERO);

			// Live Gstin Data
			List<Gstr1SummarySectionDto> gstr1SummaryAdvAdjs = new ArrayList<>();
			if ("ADV REC".equalsIgnoreCase(advAdjust.getTaxDocType())) {
				gstr1SummaryAdvAdjs = mapGstnDataForOutwardSections.get("AT");
			} else if ("ADV REC-A"
					.equalsIgnoreCase(advAdjust.getTaxDocType())) {
				gstr1SummaryAdvAdjs = mapGstnDataForOutwardSections.get("ATA");
			} else if ("ADV ADJ".equalsIgnoreCase(advAdjust.getTaxDocType())) {
				gstr1SummaryAdvAdjs = mapGstnDataForOutwardSections.get("TXPD");
			} else {
				gstr1SummaryAdvAdjs = mapGstnDataForOutwardSections
						.get("TXPDA");
			}
			if (gstr1SummaryAdvAdjs != null && !gstr1SummaryAdvAdjs.isEmpty()) {
				gstr1SummaryAdvAdjs.forEach(gstr1SummaryAdvAdj -> {
					child.setGstnCount(gstr1SummaryAdvAdj.getRecords() > 0
							? new BigInteger(String
									.valueOf(gstr1SummaryAdvAdj.getRecords()))
							: BigInteger.ZERO);
					child.setGstnInval(gstr1SummaryAdvAdj.getInvValue() != null
							? gstr1SummaryAdvAdj.getInvValue()
							: BigDecimal.ZERO);
					child.setGstnTbval(
							gstr1SummaryAdvAdj.getTaxableValue() != null
									? gstr1SummaryAdvAdj.getTaxableValue()
									: BigDecimal.ZERO);
					child.setGstnTxval(
							gstr1SummaryAdvAdj.getTaxPayable() != null
									? gstr1SummaryAdvAdj.getTaxPayable()
									: BigDecimal.ZERO);
					child.setGstnIgstval(gstr1SummaryAdvAdj.getIgst() != null
							? gstr1SummaryAdvAdj.getIgst() : BigDecimal.ZERO);
					child.setGstnCgstval(gstr1SummaryAdvAdj.getCgst() != null
							? gstr1SummaryAdvAdj.getCgst() : BigDecimal.ZERO);
					child.setGstnSgstval(gstr1SummaryAdvAdj.getSgst() != null
							? gstr1SummaryAdvAdj.getSgst() : BigDecimal.ZERO);
					child.setGstnCessval(gstr1SummaryAdvAdj.getCess() != null
							? gstr1SummaryAdvAdj.getCess() : BigDecimal.ZERO);

				});
			}
			child.setDiffCount(child.getAspCount() != null
					? child.getAspCount().subtract(child.getGstnCount())
					: BigInteger.ZERO);
			child.setDiffTbval(child.getAspTbval() != null
					? child.getAspTbval().subtract(child.getGstnTbval())
					: BigDecimal.ZERO);
			child.setDiffTxval(child.getAspTxval() != null
					? child.getAspTxval().subtract(child.getGstnTxval())
					: BigDecimal.ZERO);
			child.setDiffInval(child.getAspInval() != null
					? child.getAspInval().subtract(child.getGstnInval())
					: BigDecimal.ZERO);
			child.setDiffIgstval(child.getAspIgstval() != null
					? child.getAspIgstval().subtract(child.getGstnIgstval())
					: BigDecimal.ZERO);
			child.setDiffCgstval(child.getAspCgstval() != null
					? child.getAspCgstval().subtract(child.getGstnCgstval())
					: BigDecimal.ZERO);
			child.setDiffSgstval(child.getAspSgstval() != null
					? child.getAspSgstval().subtract(child.getGstnSgstval())
					: BigDecimal.ZERO);
			child.setDiffCessval(child.getAspCessval() != null
					? child.getAspCessval().subtract(child.getGstnCessval())
					: BigDecimal.ZERO);
		});
	}

	private String getProcessSummaryQuery(List<String> gstinList,
			int derivedRetPeriod) {

		Integer tabStatus = getTabStatus(gstinList, derivedRetPeriod);

		String sql = gstr1ProcRecordsFetchDaoImpl.createQueryString(null,
				gstinList, derivedRetPeriod, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, tabStatus);
		return sql;
	}

	@Override
	public B2csPopupDto getB2csPopupDto(String gstin, String returnPeriod,
			Long entityId) {
		B2csPopupDto b2csPopupDto = new B2csPopupDto();
		try {

			Annexure1SummaryReqDto sumReqDto = new Annexure1SummaryReqDto();
			sumReqDto.setDocType("B2CS");
			Map<String, List<String>> dataSecAttrs = new HashMap<>();
			List<String> gstinList = new ArrayList<>();
			gstinList.add(gstin);
			dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
			sumReqDto.setDataSecAttrs(dataSecAttrs);
			sumReqDto.setTaxPeriod(returnPeriod);
			sumReqDto.setEntityId(Arrays.asList(entityId));
			// summary popup
			B2csPopupSummaryDto sumDto = new B2csPopupSummaryDto();
			List<B2csPopupSummaryItemDto> sumItemDtos = new ArrayList<>();
			List<Ret1AspVerticalSummaryDto> summaryDtos = aspVertical
					.find(sumReqDto);
			if (summaryDtos != null && !summaryDtos.isEmpty()) {
				summaryDtos.forEach(summaryDto -> {
					B2csPopupSummaryItemDto itemDto = new B2csPopupSummaryItemDto();
					itemDto.setB2cs(summaryDto.getTransType());
					itemDto.setAssessableAmt(summaryDto.getTaxableValue());
					itemDto.setInvoiceVal(summaryDto.getInvoiceValue());
					itemDto.setIgst(summaryDto.getIgst());
					itemDto.setCgst(summaryDto.getCgst());
					itemDto.setSgst(summaryDto.getSgst());
					itemDto.setCess(summaryDto.getCess());
					itemDto.setCont(summaryDto.getCount() != null
							? new BigInteger(
									String.valueOf(summaryDto.getCount()))
							: BigInteger.ZERO);
					sumItemDtos.add(itemDto);
				});
			}
			sumDto.setSumItemDtos(sumItemDtos);
			b2csPopupDto.setSumDto(sumDto);

			// Gstn Data Popup
			B2csPopupGstnViewDto b2csPopupGstnViewDto = new B2csPopupGstnViewDto();
			List<B2csPopupGstnViewItemDto> gstinItemDtos = new ArrayList<>();
			List<Ret1AspVerticalSummaryDto> vertGstinDetDtos = aspVertical
					.findgstnDetails(sumReqDto);
			if (vertGstinDetDtos != null && !vertGstinDetDtos.isEmpty()) {
				vertGstinDetDtos.forEach(vertGstinDetDto -> {
					B2csPopupGstnViewItemDto itemDto = new B2csPopupGstnViewItemDto();
					itemDto.setAssessableAmt(vertGstinDetDto.getTaxableValue());
					itemDto.setTranType(vertGstinDetDto.getTransType());
					itemDto.setRate(vertGstinDetDto.getRate());
					itemDto.setBillingPos(vertGstinDetDto.getPos());
					itemDto.setIgst(vertGstinDetDto.getIgst());
					itemDto.setSgst(vertGstinDetDto.getSgst());
					itemDto.setCgst(vertGstinDetDto.getCgst());
					itemDto.setCess(vertGstinDetDto.getCess());
					gstinItemDtos.add(itemDto);
				});
			}
			b2csPopupGstnViewDto.setItemDtos(gstinItemDtos);
			b2csPopupDto.setB2csPopupGstnViewDto(b2csPopupGstnViewDto);

			ReviwSumCommonPopupVerticalDto verticalDto = new ReviwSumCommonPopupVerticalDto();
			List<ReviwSumCommonPopupVerticalItemDto> vertItemDtos = new ArrayList<>();
			List<Gstr1VerticalSummaryRespDto> vertSumRespDtos = aspVertical
					.findVerticalDetails(sumReqDto);
			vertSumRespDtos.forEach(vertSumRespDto -> {
				ReviwSumCommonPopupVerticalItemDto vertItemDto = new ReviwSumCommonPopupVerticalItemDto();
				vertItemDto.setTranType(vertSumRespDto.getTransType());
				vertItemDto.setNdBillingPos(vertSumRespDto.getNewPos() + HYPHEN
						+ vertSumRespDto.getNewStateName());
				vertItemDto.setNdEcomGstin(vertSumRespDto.getNewEcomGstin());
				vertItemDto
						.setNdEcomSupVal(vertSumRespDto.getNewEcomSupplValue());
				vertItemDto.setNdHsnSac(vertSumRespDto.getNewHsnOrSac());
				vertItemDto.setNdQuantity(vertSumRespDto.getNewQunty());
				vertItemDto.setNdRate(vertSumRespDto.getNewRate());
				vertItemDto
						.setNdTaxableVal(vertSumRespDto.getNewTaxableValue());
				vertItemDto.setNdUom(vertSumRespDto.getNewUom());

				vertItemDto.setAmtTotalVal(vertSumRespDto.getTotalValue());
				vertItemDto.setAmtIgst(vertSumRespDto.getIgst());
				vertItemDto.setAmtSgst(vertSumRespDto.getCess());
				vertItemDto.setAmtCgst(vertSumRespDto.getCgst());
				vertItemDto.setAmtCess(vertSumRespDto.getCess());

				vertItemDto.setOhDistChanl(vertSumRespDto.getDistrChannel());
				vertItemDto.setOhDivision(vertSumRespDto.getDivision());
				vertItemDto.setOhLocation(vertSumRespDto.getLocation());
				vertItemDto.setOhPlantCode(vertSumRespDto.getPlant());
				vertItemDto.setOhPrftCntr1(vertSumRespDto.getProfitCntr());
				vertItemDto.setOhPrftCntr3(vertSumRespDto.getUsrAccess1());
				vertItemDto.setOhPrftCntr4(vertSumRespDto.getUsrAccess2());
				vertItemDto.setOhPrftCntr5(vertSumRespDto.getUsrAccess3());
				vertItemDto.setOhPrftCntr6(vertSumRespDto.getUsrAccess4());
				vertItemDto.setOhPrftCntr7(vertSumRespDto.getUsrAccess5());
				vertItemDto.setOhPrftCntr8(vertSumRespDto.getUsrAccess6());
				vertItemDto.setOhUdf1(vertSumRespDto.getUsrDefined1());
				vertItemDto.setOhUdf2(vertSumRespDto.getUsrDefined2());
				vertItemDto.setOhUdf3(vertSumRespDto.getUsrDefined3());
				vertItemDtos.add(vertItemDto);
			});
			verticalDto.setItem(vertItemDtos);
			b2csPopupDto.setVerticalDto(verticalDto);

		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		return b2csPopupDto;
	}

	@Override
	public B2csaPopupDto getB2csaPopupDto(String gstin, String returnPeriod,
			Long entityId) {
		B2csaPopupDto b2csaPopupDto = new B2csaPopupDto();
		try {

			Annexure1SummaryReqDto sumReqDto = new Annexure1SummaryReqDto();
			sumReqDto.setDocType("B2CSA");
			Map<String, List<String>> dataSecAttrs = new HashMap<>();
			List<String> gstinList = new ArrayList<>();
			gstinList.add(gstin);
			dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
			sumReqDto.setDataSecAttrs(dataSecAttrs);
			sumReqDto.setTaxPeriod(returnPeriod);
			sumReqDto.setEntityId(Arrays.asList(entityId));
			// summary popup
			B2csaPopupSummaryDto sumDto = new B2csaPopupSummaryDto();
			List<B2csaPopupSummaryItemDto> sumItemDtos = new ArrayList<>();
			List<Ret1AspVerticalSummaryDto> summaryDtos = aspVertical
					.find(sumReqDto);
			if (summaryDtos != null && !summaryDtos.isEmpty()) {
				summaryDtos.forEach(summaryDto -> {
					B2csaPopupSummaryItemDto itemDto = new B2csaPopupSummaryItemDto();
					itemDto.setB2csa(summaryDto.getTransType());
					itemDto.setAssessableAmt(summaryDto.getTaxableValue());
					itemDto.setInvoiceVal(summaryDto.getInvoiceValue());
					itemDto.setIgst(summaryDto.getIgst());
					itemDto.setCgst(summaryDto.getCgst());
					itemDto.setSgst(summaryDto.getSgst());
					itemDto.setCess(summaryDto.getCess());
					itemDto.setCont(summaryDto.getCount() != null
							? new BigInteger(
									String.valueOf(summaryDto.getCount()))
							: BigInteger.ZERO);
					sumItemDtos.add(itemDto);
				});
			}
			sumDto.setSumItemDtos(sumItemDtos);
			b2csaPopupDto.setSumDto(sumDto);

			// Gstn Data Popup
			B2csPopupGstnViewDto b2csPopupGstnViewDto = new B2csPopupGstnViewDto();
			List<B2csPopupGstnViewItemDto> gstinItemDtos = new ArrayList<>();
			List<Ret1AspVerticalSummaryDto> vertGstinDetDtos = aspVertical
					.findgstnDetails(sumReqDto);
			if (vertGstinDetDtos != null && !vertGstinDetDtos.isEmpty()) {
				vertGstinDetDtos.forEach(vertGstinDetDto -> {
					B2csPopupGstnViewItemDto itemDto = new B2csPopupGstnViewItemDto();
					itemDto.setAssessableAmt(vertGstinDetDto.getTaxableValue());
					itemDto.setTranType(vertGstinDetDto.getTransType());
					itemDto.setRate(vertGstinDetDto.getRate());
					itemDto.setBillingPos(vertGstinDetDto.getPos());
					itemDto.setIgst(vertGstinDetDto.getIgst());
					itemDto.setSgst(vertGstinDetDto.getSgst());
					itemDto.setCgst(vertGstinDetDto.getCgst());
					itemDto.setCess(vertGstinDetDto.getCess());
					gstinItemDtos.add(itemDto);
				});
			}
			b2csPopupGstnViewDto.setItemDtos(gstinItemDtos);
			b2csaPopupDto.setB2csPopupGstnViewDto(b2csPopupGstnViewDto);

			ReviwSumB2bAPopupVerticalDto verticalDto = new ReviwSumB2bAPopupVerticalDto();
			List<ReviwB2banPopupVertItemDto> vertItemDtos = new ArrayList<>();
			List<Gstr1VerticalSummaryRespDto> vertSumRespDtos = aspVertical
					.findVerticalDetails(sumReqDto);
			vertSumRespDtos.forEach(vertSumRespDto -> {
				ReviwB2banPopupVertItemDto vertItemDto = new ReviwB2banPopupVertItemDto();
				vertItemDto.setTranType(vertSumRespDto.getTransType());
				vertItemDto.setOdMonth(vertSumRespDto.getMonth());
				vertItemDto.setOdBillingPos(vertSumRespDto.getOrgPos() + HYPHEN
						+ vertSumRespDto.getOrgStateName());
				vertItemDto.setOdQuantity(vertSumRespDto.getOrgQunty());
				vertItemDto.setOdEcomGstin(vertSumRespDto.getOrgEcomGstin());
				vertItemDto
						.setOdEcomSupVal(vertSumRespDto.getOrgEcomSupplValue());
				vertItemDto.setOdRate(vertSumRespDto.getOrgRate());
				vertItemDto
						.setOdTaxableVal(vertSumRespDto.getOrgTaxableValue());
				vertItemDto.setOdUom(vertSumRespDto.getOrgUom());
				vertItemDto.setOdHsnSac(vertSumRespDto.getOrgHsnOrSac());

				vertItemDto.setNdBillingPos(vertSumRespDto.getNewPos() + HYPHEN
						+ vertSumRespDto.getNewStateName());
				vertItemDto.setNdEcomGstin(vertSumRespDto.getNewEcomGstin());
				vertItemDto
						.setNdEcomSupVal(vertSumRespDto.getNewEcomSupplValue());
				vertItemDto.setNdHsnSac(vertSumRespDto.getNewHsnOrSac());
				vertItemDto.setNdQuantity(vertSumRespDto.getNewQunty());
				vertItemDto.setNdRate(vertSumRespDto.getNewRate());
				vertItemDto
						.setNdTaxableVal(vertSumRespDto.getNewTaxableValue());
				vertItemDto.setNdUom(vertSumRespDto.getNewUom());

				vertItemDto.setAmtTotalVal(vertSumRespDto.getTotalValue());
				vertItemDto.setAmtIgst(vertSumRespDto.getIgst());
				vertItemDto.setAmtSgst(vertSumRespDto.getCess());
				vertItemDto.setAmtCgst(vertSumRespDto.getCgst());
				vertItemDto.setAmtCess(vertSumRespDto.getCess());

				vertItemDto.setOhDistChanl(vertSumRespDto.getDistrChannel());
				vertItemDto.setOhDivision(vertSumRespDto.getDivision());
				vertItemDto.setOhLocation(vertSumRespDto.getLocation());
				vertItemDto.setOhPlantCode(vertSumRespDto.getPlant());
				vertItemDto.setOhPrftCntr1(vertSumRespDto.getProfitCntr());
				vertItemDto.setOhPrftCntr3(vertSumRespDto.getUsrAccess1());
				vertItemDto.setOhPrftCntr4(vertSumRespDto.getUsrAccess2());
				vertItemDto.setOhPrftCntr5(vertSumRespDto.getUsrAccess3());
				vertItemDto.setOhPrftCntr6(vertSumRespDto.getUsrAccess4());
				vertItemDto.setOhPrftCntr7(vertSumRespDto.getUsrAccess5());
				vertItemDto.setOhPrftCntr8(vertSumRespDto.getUsrAccess6());
				vertItemDto.setOhUdf1(vertSumRespDto.getUsrDefined1());
				vertItemDto.setOhUdf2(vertSumRespDto.getUsrDefined2());
				vertItemDto.setOhUdf3(vertSumRespDto.getUsrDefined3());
				vertItemDtos.add(vertItemDto);
			});
			verticalDto.setItem(vertItemDtos);
			b2csaPopupDto.setVericalDto(verticalDto);

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return b2csaPopupDto;
	}

	@Override
	public HsnPopupDto getHsnPopup(String gstin, String returnPeriod,
			Long entityId) {
		HsnPopupDto hsnPopupDto = new HsnPopupDto();
		Annexure1SummaryReqDto req = new Annexure1SummaryReqDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		req.setDataSecAttrs(dataSecAttrs);
		req.setTaxPeriod(returnPeriod);
		req.setEntityId(Arrays.asList(entityId));
		List<Gstr1HsnSummarySectionDto> aspResultList = gstr1AspDocVertSumSer
				.findHsn(req);
		List<HsnPopupItemDto> hsnPopItemDtos = new ArrayList<>();
		int srNo = 0;
		for (Gstr1HsnSummarySectionDto aspResult : aspResultList) {
			HsnPopupItemDto hsnPopItemDto = new HsnPopupItemDto();
			srNo = 1 + srNo;
			hsnPopItemDto.setSlno(srNo);
			hsnPopItemDto.setUqc(aspResult.getUqc());
			hsnPopItemDto.setHsn(aspResult.getHsn() != null
					? new BigInteger(String.valueOf(aspResult.getHsn()))
					: null);
			hsnPopItemDto.setAspQunty(aspResult.getAspQunty());
			hsnPopItemDto.setAspTotalTax(aspResult.getAspTaxableValue());
			hsnPopItemDto.setAspTotalVal(aspResult.getAspTotalValue());
			hsnPopItemDto.setAspIgst(aspResult.getAspIgst());
			hsnPopItemDto.setAspCgst(aspResult.getAspCgst());
			hsnPopItemDto.setAspSgst(aspResult.getAspSgst());
			hsnPopItemDto.setAspCess(aspResult.getAspCess());

			hsnPopItemDto.setEditQunty(aspResult.getUsrQunty());
			hsnPopItemDto.setEdtTotalTax(aspResult.getUsrTaxableValue());
			hsnPopItemDto.setEditTotalVal(aspResult.getUsrTotalValue());
			hsnPopItemDto.setEditIgst(aspResult.getUsrIgst());
			hsnPopItemDto.setEditCgst(aspResult.getUsrCgst());
			hsnPopItemDto.setEditSgst(aspResult.getUsrSgst());
			hsnPopItemDto.setEditCess(aspResult.getUsrCess());
			hsnPopItemDtos.add(hsnPopItemDto);
		}
		hsnPopupDto.setHsnPopItemDtos(hsnPopItemDtos);
		return hsnPopupDto;
	}

	@Override
	public DocPopupDto getDocPopup(String gstin, String returnPeriod,
			Long entityId) {
		DocPopupDto docPopupDto = new DocPopupDto();
		Annexure1SummaryReqDto req = new Annexure1SummaryReqDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		req.setDataSecAttrs(dataSecAttrs);
		req.setTaxPeriod(returnPeriod);
		req.setEntityId(Arrays.asList(entityId));
		DocPopupSummaryDto summaryDto = new DocPopupSummaryDto();
		List<DocPopupSummaryItemDto> sumItemDtos = new ArrayList<>();

		List<Gstr1VerticalDocSeriesRespDto> summaryRespDtos = loadData
				.gstinViewSection(req);
		List<Long> natureIds = new ArrayList<>();
		if (summaryRespDtos != null && !summaryRespDtos.isEmpty()) {
			for (Gstr1VerticalDocSeriesRespDto docSectionDto : summaryRespDtos) {
				DocPopupSummaryItemDto summItemDto = new DocPopupSummaryItemDto();
				summItemDto.setSlno(docSectionDto.getDocNatureId());
				natureIds.add(docSectionDto.getDocNatureId());
				summItemDto.setNmDoc(docSectionDto.getDocNature());
				summItemDto.setTotalNumber(docSectionDto.getTotal() != null
						? new BigInteger(
								String.valueOf(docSectionDto.getTotal()))
						: BigInteger.ZERO);
				summItemDto.setCancelled(docSectionDto.getCancelled() != null
						? new BigInteger(docSectionDto.getCancelled())
						: BigInteger.ZERO);
				summItemDto.setNetIssued(docSectionDto.getNetIssued() != null
						? new BigInteger(
								String.valueOf(docSectionDto.getNetIssued()))
						: BigInteger.ZERO);
				sumItemDtos.add(summItemDto);
			}
		}

		summaryDto.setSumItemDtos(sumItemDtos);
		docPopupDto.setDocPopupSummaryDto(summaryDto);

		DocPopupVerticalDto docPopVertDto = new DocPopupVerticalDto();

		List<DocPopupVerticalItemDto> verticalItemDtos = new ArrayList<>();
		if (!natureIds.isEmpty()) {
			natureIds.forEach(natureId -> {
				req.setDocNatureId(natureId.intValue());
				List<Gstr1VerticalDocSeriesRespDto> verticalRespDtos = loadData
						.gstinViewSection(req);
				if (verticalRespDtos != null && !verticalRespDtos.isEmpty()) {
					verticalRespDtos.forEach(verticalRespDto -> {
						DocPopupVerticalItemDto verticalItemDto = new DocPopupVerticalItemDto();
						verticalItemDto
								.setSeriesFor(verticalRespDto.getDocNatureId());
						verticalItemDto.setTotalNumber(
								verticalRespDto.getTotal() != null
										? new BigInteger(
												verticalRespDto.getTotal())
										: BigInteger.ZERO);
						verticalItemDto
								.setSeriesFrom(verticalRespDto.getSeriesFrom());
						verticalItemDto
								.setSeriesTo(verticalRespDto.getSeriesTo());
						verticalItemDto.setTotalNumber(
								verticalRespDto.getTotal() != null
										? new BigInteger(String.valueOf(
												verticalRespDto.getTotal()))
										: BigInteger.ZERO);
						verticalItemDto.setNetIssued(
								verticalRespDto.getNetIssued() != null
										? new BigInteger(String.valueOf(
												verticalRespDto.getNetIssued()))
										: BigInteger.ZERO);
						verticalItemDto.setCancelled(
								verticalRespDto.getCancelled() != null
										? new BigInteger(String.valueOf(
												verticalRespDto.getCancelled()))
										: BigInteger.ZERO);

						verticalItemDtos.add(verticalItemDto);
					});
				}
			});
		}
		Map<Long, List<DocPopupVerticalItemDto>> mapDocPopupVertical = mapDocPopupVertical(
				verticalItemDtos);
		List<DocPopupVerticalItemDto> vertiNewItemDtos = new ArrayList<>();
		List<DocPopupVerticalItemDto> docPopupOneVertItemDtos = mapDocPopupVertical
				.get(new Long("1"));
		if (docPopupOneVertItemDtos != null
				&& !docPopupOneVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupOneVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("1"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupTowVertItemDtos = mapDocPopupVertical
				.get(new Long("2"));
		if (docPopupTowVertItemDtos != null
				&& !docPopupTowVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupTowVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("2"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupThreeVertItemDtos = mapDocPopupVertical
				.get(new Long("3"));
		if (docPopupThreeVertItemDtos != null
				&& !docPopupThreeVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupThreeVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("3"));
			vertiNewItemDtos.add(docPopupVertItem);
		}
		List<DocPopupVerticalItemDto> docPopupFourVertItemDtos = mapDocPopupVertical
				.get(new Long("4"));
		if (docPopupFourVertItemDtos != null
				&& !docPopupFourVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupFourVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("4"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupFiveVertItemDtos = mapDocPopupVertical
				.get(new Long("5"));
		if (docPopupFiveVertItemDtos != null
				&& !docPopupFiveVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupFiveVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("5"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupSixVertItemDtos = mapDocPopupVertical
				.get(new Long("6"));
		if (docPopupSixVertItemDtos != null
				&& !docPopupSixVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupSixVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("6"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupSevenVertItemDtos = mapDocPopupVertical
				.get(new Long("7"));
		if (docPopupSevenVertItemDtos != null
				&& !docPopupSevenVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupSevenVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("7"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupEghtVertItemDtos = mapDocPopupVertical
				.get(new Long("8"));
		if (docPopupEghtVertItemDtos != null
				&& !docPopupEghtVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupEghtVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("8"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupNineVertItemDtos = mapDocPopupVertical
				.get(new Long("9"));
		if (docPopupNineVertItemDtos != null
				&& !docPopupNineVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupNineVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("9"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupTenVertItemDtos = mapDocPopupVertical
				.get(new Long("10"));
		if (docPopupTenVertItemDtos != null
				&& !docPopupTenVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupTenVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("10"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupElevenVertItemDtos = mapDocPopupVertical
				.get(new Long("11"));
		if (docPopupElevenVertItemDtos != null
				&& !docPopupElevenVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupElevenVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("11"));
			vertiNewItemDtos.add(docPopupVertItem);
		}

		List<DocPopupVerticalItemDto> docPopupTwelveVertItemDtos = mapDocPopupVertical
				.get(new Long("12"));
		if (docPopupTwelveVertItemDtos != null
				&& !docPopupTwelveVertItemDtos.isEmpty()) {
			vertiNewItemDtos.addAll(docPopupTwelveVertItemDtos);
		} else {
			DocPopupVerticalItemDto docPopupVertItem = new DocPopupVerticalItemDto();
			docPopupVertItem.setSeriesFor(new Long("12"));
			vertiNewItemDtos.add(docPopupVertItem);
		}
		docPopVertDto.setItemDtos(vertiNewItemDtos);
		docPopupDto.setDocPopupVerticalDto(docPopVertDto);
		return docPopupDto;
	}

	private Map<Long, List<DocPopupVerticalItemDto>> mapDocPopupVertical(
			List<DocPopupVerticalItemDto> docPopupVertItemDtos) {
		Map<Long, List<DocPopupVerticalItemDto>> mapDocPopupVerticleDto = new HashMap<>();
		docPopupVertItemDtos.forEach(docPopupVertItemDto -> {
			if (mapDocPopupVerticleDto
					.containsKey(docPopupVertItemDto.getSeriesFor())) {
				List<DocPopupVerticalItemDto> docPopupVertiItemDtos = mapDocPopupVerticleDto
						.get(docPopupVertItemDto.getSeriesFor());
				docPopupVertiItemDtos.add(docPopupVertItemDto);
				mapDocPopupVerticleDto.put(docPopupVertItemDto.getSeriesFor(),
						docPopupVertiItemDtos);
			} else {
				List<DocPopupVerticalItemDto> docPopupVertiItemDtos = new ArrayList<>();
				docPopupVertiItemDtos.add(docPopupVertItemDto);
				mapDocPopupVerticleDto.put(docPopupVertItemDto.getSeriesFor(),
						docPopupVertiItemDtos);
			}
		});
		return mapDocPopupVerticleDto;
	}

	public NilPopupDto getNilNonPopup(String gstin, String returnPeriod,
			Long entityId) {
		NilPopupDto nilPopupDto = new NilPopupDto();
		Gstr1ProcessedRecordsReqDto reqDto = new Gstr1ProcessedRecordsReqDto();

		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstinList = new ArrayList<>();
		gstinList.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
		reqDto.setDataSecAttrs(dataSecAttrs);
		reqDto.setRetunPeriod(returnPeriod);
		reqDto.setEntityId(Arrays.asList(entityId));
		NilDto nilDto = new NilDto();
		List<NilItemDto> nilItemDtos = new ArrayList<>();

		List<Gstr1NilExmpNonGstSummaryStatusRespDto> summaryDetails = gstr1NilExmpNonGstStasSer
				.findNilExmpNonGstSummaryStauts(reqDto);
		summaryDetails.forEach(summaryDeta -> {
			NilItemDto itemDto = new NilItemDto();
			itemDto.setCont(
					new BigInteger(String.valueOf(summaryDeta.getCount())));
			itemDto.setExemAmt(summaryDeta.getExtAmount());
			itemDto.setNilAmt(summaryDeta.getNilAmount());
			itemDto.setNonGstAmt(summaryDeta.getNonAmount());
			itemDto.setNiNonGstSup(summaryDeta.getType());
			nilItemDtos.add(itemDto);
		});

		nilDto.setNilItemDtos(nilItemDtos);
		nilPopupDto.setNilDto(nilDto);

		NilCompoutedDto nilComputedDto = new NilCompoutedDto();
		List<NilCompoutedItemDto> nilCompItemDtos = new ArrayList<>();
		List<Gstr1NilExmpNonGstStatusRespDto> userInputDetails = gstr1NilExmpNonGstStasSer
				.findNilExmpNonGstStauts(reqDto);
		if (userInputDetails != null && !userInputDetails.isEmpty()) {
			userInputDetails.forEach(userInput -> {
				NilCompoutedItemDto nilCompItemDto = new NilCompoutedItemDto();
				nilCompItemDto.setDescription(userInput.getDesc());
				nilCompItemDto.setAspExemSup(userInput.getAspExempted());
				nilCompItemDto.setAspNiRSup(userInput.getAspNilRated());
				nilCompItemDto.setAspNoGstSup(userInput.getAspNonGst());
				nilCompItemDto.setEditExemSup(userInput.getUsrExempted());
				nilCompItemDto.setEditNiRSup(userInput.getUsrNilRated());
				nilCompItemDto.setEditNoGstSup(userInput.getUsrNonGst());
				nilCompItemDtos.add(nilCompItemDto);
			});
		}
		nilComputedDto.setNilCompItemDtos(nilCompItemDtos);
		nilPopupDto.setNilComputedDto(nilComputedDto);

		List<Gstr1NilExmpNonGstVerticalStatusRespDto> nilExampNonVertDtos = gstr1NilExmpNonGstStasSer
				.findNilExmpNonGstVerticalStauts(reqDto);

		NilPopupVerticalDto verticalDto = new NilPopupVerticalDto();
		List<NilPopupVerticalItemDto> itemDtos = new ArrayList<>();

		if (nilExampNonVertDtos != null && !nilExampNonVertDtos.isEmpty()) {
			nilExampNonVertDtos.forEach(nilExampNonVertDto -> {
				NilPopupVerticalItemDto itemDto = new NilPopupVerticalItemDto();
				itemDto.setDescription(nilExampNonVertDto.getDesc());
				itemDto.setExInterRegd(nilExampNonVertDto.getExtInterReg());
				itemDto.setExInterUnregd(nilExampNonVertDto.getExtInterUnreg());
				itemDto.setExIntraRegd(nilExampNonVertDto.getExtIntraReg());
				itemDto.setExIntraUnregd(nilExampNonVertDto.getExtIntraUnreg());
				itemDto.setHsn(nilExampNonVertDto.getHsn());
				itemDto.setNiInterRegd(nilExampNonVertDto.getNilInterReg());
				itemDto.setNiInterUnregd(nilExampNonVertDto.getNilInterUnreg());
				itemDto.setNiIntraRegd(nilExampNonVertDto.getNilIntraReg());
				itemDto.setNiIntraUnregd(nilExampNonVertDto.getNilIntraUnreg());
				itemDto.setNoInterRegd(nilExampNonVertDto.getNonInterReg());
				itemDto.setNoInterUnregd(nilExampNonVertDto.getNonInterUnreg());
				itemDto.setNoIntraRegd(nilExampNonVertDto.getNonIntraReg());
				itemDto.setNoIntraUnregd(nilExampNonVertDto.getNonIntraUnreg());
				itemDto.setQuantity(nilExampNonVertDto.getQunty());
				itemDto.setUqc(nilExampNonVertDto.getUqc());
				itemDtos.add(itemDto);
			});
		}
		verticalDto.setItemDtos(itemDtos);
		nilPopupDto.setVerticleDto(verticalDto);
		return nilPopupDto;
	}

	@Override
	public Advp111aPopupDto getAdvp111aPopupDto(String gstin,
			String returnPeriod, Long entityId) {
		Advp111aPopupDto advp111aPopupDto = new Advp111aPopupDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		request.setDocType("AT");
		request.setEntityId(Arrays.asList(entityId));
		List<Ret1AspVerticalSummaryDto> summaryDetails = service
				.findSummaryDetails(request);

		AdvncePopupSummaryDto sumDto = new AdvncePopupSummaryDto();

		List<AdvncePopupSummaryItemDto> sumItemDtos = new ArrayList<>();
		if (summaryDetails != null && !summaryDetails.isEmpty()) {
			summaryDetails.forEach(summary -> {
				AdvncePopupSummaryItemDto sumItemDto = new AdvncePopupSummaryItemDto();
				sumItemDto.setCont(summary.getCount() != null
						? new BigInteger(String.valueOf(summary.getCount()))
						: BigInteger.ZERO);
				sumItemDto.setInvValue(summary.getInvoiceValue());
				sumItemDto.setAssessableAmt(summary.getTaxableValue());
				sumItemDto.setIgst(summary.getIgst());
				sumItemDto.setCgst(summary.getCgst());
				sumItemDto.setSgst(summary.getSgst());
				sumItemDto.setCess(summary.getCess());
				sumItemDtos.add(sumItemDto);
			});
		}
		sumDto.setSumItemDtos(sumItemDtos);
		advp111aPopupDto.setSumDto(sumDto);

		AdvancePopupGstnViewDto advancePopupGstnViewDto = new AdvancePopupGstnViewDto();

		List<AdvancePopupGstnViewItemDto> itemDtos = new ArrayList<>();

		List<Ret1AspVerticalSummaryDto> gstnDetails = service
				.findgstnDetails(request);

		if (gstnDetails != null && !gstnDetails.isEmpty()) {
			gstnDetails.forEach(gstnDetail -> {
				AdvancePopupGstnViewItemDto itemDto = new AdvancePopupGstnViewItemDto();
				itemDto.setTranType(gstnDetail.getTransType());
				itemDto.setAssessableAmt(gstnDetail.getDocAmount());
				itemDto.setBillingPos(gstnDetail.getPos());
				itemDto.setIgst(gstnDetail.getIgst());
				itemDto.setCgst(gstnDetail.getCgst());
				itemDto.setSgst(gstnDetail.getSgst());
				itemDto.setCess(gstnDetail.getCess());
				itemDtos.add(itemDto);
			});
		}
		advancePopupGstnViewDto.setItemDtos(itemDtos);
		advp111aPopupDto.setAdvancePopupGstnViewDto(advancePopupGstnViewDto);

		AdvancePopupVerticalDto vericalDto = new AdvancePopupVerticalDto();
		List<AdvancePopupVerticalItemDto> verticalItemDtos = new ArrayList<>();
		List<Gstr1AdvancedVerticalSummaryRespDto> verticalDetailsDtos = service
				.findVerticalDetails(request);

		if (verticalDetailsDtos != null && !verticalDetailsDtos.isEmpty()) {
			verticalDetailsDtos.forEach(vertical -> {
				AdvancePopupVerticalItemDto verticalItemDto = new AdvancePopupVerticalItemDto();
				verticalItemDto.setTranType(vertical.getTransType());
				verticalItemDto.setBillingPos(vertical.getNewPos() + HYPHEN
						+ vertical.getNewStateName());
				verticalItemDto.setIgst(vertical.getIgst());
				verticalItemDto.setCgst(vertical.getCgst());
				verticalItemDto.setSgst(vertical.getSgst());
				verticalItemDto.setCess(vertical.getCess());
				verticalItemDto.setRate(vertical.getNewRate());
				verticalItemDto.setNgar(vertical.getNewTaxableValue());
				verticalItemDto.setDistChanl(vertical.getDistrChannel());
				verticalItemDto.setDivision(vertical.getDivision());
				verticalItemDto.setSalesOrg(vertical.getSalesOrg());
				verticalItemDto.setLocation(vertical.getLocation());
				verticalItemDto.setPlantCode(vertical.getPlant());
				verticalItemDto.setPrftCntr1(vertical.getProfitCntr());
				verticalItemDto.setPrftCntr3(vertical.getUsrAccess1());
				verticalItemDto.setPrftCntr4(vertical.getUsrAccess2());
				verticalItemDto.setPrftCntr5(vertical.getUsrAccess3());
				verticalItemDto.setPrftCntr6(vertical.getUsrAccess4());
				verticalItemDto.setPrftCntr7(vertical.getUsrAccess5());
				verticalItemDto.setPrftCntr8(vertical.getUsrAccess6());
				verticalItemDto.setUdf1(vertical.getUsrDefined1());
				verticalItemDto.setUdf2(vertical.getUsrDefined2());
				verticalItemDto.setUdf3(vertical.getUsrDefined3());
				verticalItemDtos.add(verticalItemDto);
			});
		}
		vericalDto.setItem(verticalItemDtos);
		advp111aPopupDto.setVericalDto(vericalDto);
		return advp111aPopupDto;
	}

	@Override
	public Advp111bPopupDto getAdvp111bPopupDto(String gstin,
			String returnPeriod, Long entityId) {
		Advp111bPopupDto advp111bPopupDto = new Advp111bPopupDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		request.setDocType("TXPD");
		request.setEntityId(Arrays.asList(entityId));
		List<Ret1AspVerticalSummaryDto> summaryDetails = service
				.findSummaryDetails(request);

		AdvncePopupSummaryDto sumDto = new AdvncePopupSummaryDto();

		List<AdvncePopupSummaryItemDto> sumItemDtos = new ArrayList<>();
		if (summaryDetails != null && !summaryDetails.isEmpty()) {
			summaryDetails.forEach(summary -> {
				AdvncePopupSummaryItemDto sumItemDto = new AdvncePopupSummaryItemDto();
				sumItemDto.setCont(summary.getCount() != null
						? new BigInteger(String.valueOf(summary.getCount()))
						: BigInteger.ZERO);
				sumItemDto.setInvValue(summary.getInvoiceValue());
				sumItemDto.setAssessableAmt(summary.getTaxableValue());
				sumItemDto.setIgst(summary.getIgst());
				sumItemDto.setCgst(summary.getCgst());
				sumItemDto.setSgst(summary.getSgst());
				sumItemDto.setCess(summary.getCess());
				sumItemDtos.add(sumItemDto);
			});
		}
		sumDto.setSumItemDtos(sumItemDtos);
		advp111bPopupDto.setSumDto(sumDto);

		AdvancePopupGstnViewDto advancePopupGstnViewDto = new AdvancePopupGstnViewDto();

		List<AdvancePopupGstnViewItemDto> itemDtos = new ArrayList<>();

		List<Ret1AspVerticalSummaryDto> gstnDetails = service
				.findgstnDetails(request);

		if (gstnDetails != null && !gstnDetails.isEmpty()) {
			gstnDetails.forEach(gstnDetail -> {
				AdvancePopupGstnViewItemDto itemDto = new AdvancePopupGstnViewItemDto();
				itemDto.setTranType(gstnDetail.getTransType());
				itemDto.setAssessableAmt(gstnDetail.getTaxableValue());
				itemDto.setBillingPos(gstnDetail.getPos());
				itemDto.setIgst(gstnDetail.getIgst());
				itemDto.setCgst(gstnDetail.getCgst());
				itemDto.setSgst(gstnDetail.getSgst());
				itemDto.setCess(gstnDetail.getCess());
				itemDtos.add(itemDto);
			});
		}
		advancePopupGstnViewDto.setItemDtos(itemDtos);
		advp111bPopupDto.setAdvancePopupGstnViewDto(advancePopupGstnViewDto);

		AdvancePopupVerticalDto vericalDto = new AdvancePopupVerticalDto();
		List<AdvancePopupVerticalItemDto> verticalItemDtos = new ArrayList<>();
		List<Gstr1AdvancedVerticalSummaryRespDto> verticalDetailsDtos = service
				.findVerticalDetails(request);

		if (verticalDetailsDtos != null && !verticalDetailsDtos.isEmpty()) {
			verticalDetailsDtos.forEach(vertical -> {
				AdvancePopupVerticalItemDto verticalItemDto = new AdvancePopupVerticalItemDto();
				verticalItemDto.setTranType(vertical.getTransType());

				verticalItemDto.setBillingPos(vertical.getNewPos() + HYPHEN
						+ vertical.getNewStateName());
				verticalItemDto.setIgst(vertical.getIgst());
				verticalItemDto.setCgst(vertical.getCgst());
				verticalItemDto.setSgst(vertical.getSgst());
				verticalItemDto.setCess(vertical.getCess());
				verticalItemDto.setRate(vertical.getNewRate());
				verticalItemDto.setNgar(vertical.getNewTaxableValue());
				verticalItemDto.setDistChanl(vertical.getDistrChannel());
				verticalItemDto.setDivision(vertical.getDivision());
				verticalItemDto.setSalesOrg(vertical.getSalesOrg());
				verticalItemDto.setLocation(vertical.getLocation());
				verticalItemDto.setPlantCode(vertical.getPlant());
				verticalItemDto.setPrftCntr1(vertical.getProfitCntr());
				verticalItemDto.setPrftCntr3(vertical.getUsrAccess1());
				verticalItemDto.setPrftCntr4(vertical.getUsrAccess2());
				verticalItemDto.setPrftCntr5(vertical.getUsrAccess3());
				verticalItemDto.setPrftCntr6(vertical.getUsrAccess4());
				verticalItemDto.setPrftCntr7(vertical.getUsrAccess5());
				verticalItemDto.setPrftCntr8(vertical.getUsrAccess6());
				verticalItemDto.setUdf1(vertical.getUsrDefined1());
				verticalItemDto.setUdf2(vertical.getUsrDefined2());
				verticalItemDto.setUdf3(vertical.getUsrDefined3());
				verticalItemDtos.add(verticalItemDto);
			});
		}
		vericalDto.setItem(verticalItemDtos);
		advp111bPopupDto.setVericalDto(vericalDto);
		return advp111bPopupDto;
	}

	@Override
	public Advp211aPopupDto getAdvp211aPopupDto(String gstin,
			String returnPeriod, Long entityId) {
		Advp211aPopupDto advp211aPopupDto = new Advp211aPopupDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		request.setDocType("ATA");
		request.setEntityId(Arrays.asList(entityId));
		List<Ret1AspVerticalSummaryDto> summaryDetails = service
				.findSummaryDetails(request);

		AdvncePopupSummaryDto sumDto = new AdvncePopupSummaryDto();

		List<AdvncePopupSummaryItemDto> sumItemDtos = new ArrayList<>();
		if (summaryDetails != null && !summaryDetails.isEmpty()) {
			summaryDetails.forEach(summary -> {
				AdvncePopupSummaryItemDto sumItemDto = new AdvncePopupSummaryItemDto();
				sumItemDto.setCont(summary.getCount() != null
						? new BigInteger(String.valueOf(summary.getCount()))
						: BigInteger.ZERO);
				sumItemDto.setInvValue(summary.getInvoiceValue());
				sumItemDto.setAssessableAmt(summary.getTaxableValue());
				sumItemDto.setIgst(summary.getIgst());
				sumItemDto.setCgst(summary.getCgst());
				sumItemDto.setSgst(summary.getSgst());
				sumItemDto.setCess(summary.getCess());
				sumItemDtos.add(sumItemDto);
			});
		}
		sumDto.setSumItemDtos(sumItemDtos);
		advp211aPopupDto.setSumDto(sumDto);

		AdvancePopupGstnViewDto advancePopupGstnViewDto = new AdvancePopupGstnViewDto();

		List<AdvancePopupGstnViewItemDto> itemDtos = new ArrayList<>();

		List<Ret1AspVerticalSummaryDto> gstnDetails = service
				.findgstnDetails(request);

		if (gstnDetails != null && !gstnDetails.isEmpty()) {
			gstnDetails.forEach(gstnDetail -> {
				AdvancePopupGstnViewItemDto itemDto = new AdvancePopupGstnViewItemDto();
				itemDto.setTranType(gstnDetail.getTransType());
				itemDto.setAssessableAmt(gstnDetail.getTaxableValue());
				itemDto.setBillingPos(gstnDetail.getPos());
				itemDto.setIgst(gstnDetail.getIgst());
				itemDto.setCgst(gstnDetail.getCgst());
				itemDto.setSgst(gstnDetail.getSgst());
				itemDto.setCess(gstnDetail.getCess());
				itemDtos.add(itemDto);
			});
		}
		advancePopupGstnViewDto.setItemDtos(itemDtos);
		advp211aPopupDto.setAdvancePopupGstnViewDto(advancePopupGstnViewDto);

		AdvanceAmadmentPopupVerticalDto vericalDto = new AdvanceAmadmentPopupVerticalDto();
		List<AdvanceAmantPopupVerticalItemDto> verticalItemDtos = new ArrayList<>();
		List<Gstr1AdvancedVerticalSummaryRespDto> verticalDetailsDtos = service
				.findVerticalDetails(request);

		if (verticalDetailsDtos != null && !verticalDetailsDtos.isEmpty()) {
			verticalDetailsDtos.forEach(vertical -> {
				AdvanceAmantPopupVerticalItemDto verticalItemDto = new AdvanceAmantPopupVerticalItemDto();
				verticalItemDto.setTranType(vertical.getTransType());
				verticalItemDto.setMon(vertical.getMonth());
				verticalItemDto.setObillingPos(vertical.getOrgPos() + HYPHEN
						+ vertical.getOrgStateName());
				verticalItemDto.setOrate(vertical.getOrgRate());
				verticalItemDto.setOgar(vertical.getOrgTaxableValue());
				verticalItemDto.setNbillingPos(vertical.getNewPos() + HYPHEN
						+ vertical.getNewStateName());
				verticalItemDto.setNrate(vertical.getNewRate());
				verticalItemDto.setNgar(vertical.getNewTaxableValue());

				verticalItemDto.setIgst(vertical.getIgst());
				verticalItemDto.setCgst(vertical.getCgst());
				verticalItemDto.setSgst(vertical.getSgst());
				verticalItemDto.setCess(vertical.getCess());

				verticalItemDto.setDistChanl(vertical.getDistrChannel());
				verticalItemDto.setDivision(vertical.getDivision());
				verticalItemDto.setSalesOrg(vertical.getSalesOrg());
				verticalItemDto.setLocation(vertical.getLocation());
				verticalItemDto.setPlantCode(vertical.getPlant());
				verticalItemDto.setPrftCntr1(vertical.getProfitCntr());
				verticalItemDto.setPrftCntr3(vertical.getUsrAccess1());
				verticalItemDto.setPrftCntr4(vertical.getUsrAccess2());
				verticalItemDto.setPrftCntr5(vertical.getUsrAccess3());
				verticalItemDto.setPrftCntr6(vertical.getUsrAccess4());
				verticalItemDto.setPrftCntr7(vertical.getUsrAccess5());
				verticalItemDto.setPrftCntr8(vertical.getUsrAccess6());

				verticalItemDto.setUdf1(vertical.getUsrDefined1());
				verticalItemDto.setUdf2(vertical.getUsrDefined2());
				verticalItemDto.setUdf3(vertical.getUsrDefined3());
				verticalItemDtos.add(verticalItemDto);
			});
		}
		vericalDto.setItem(verticalItemDtos);
		advp211aPopupDto.setVericalDto(vericalDto);
		return advp211aPopupDto;
	}

	@Override
	public Advp211bPopupDto getAdvp211bPopupDto(String gstin,
			String returnPeriod, Long entityId) {
		Advp211bPopupDto advp211bPopupDto = new Advp211bPopupDto();
		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(returnPeriod);
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		request.setDataSecAttrs(dataSecAttrs);
		request.setDocType("TXPDA");
		request.setEntityId(Arrays.asList(entityId));
		List<Ret1AspVerticalSummaryDto> summaryDetails = service
				.findSummaryDetails(request);

		AdvncePopupSummaryDto sumDto = new AdvncePopupSummaryDto();

		List<AdvncePopupSummaryItemDto> sumItemDtos = new ArrayList<>();
		if (summaryDetails != null && !summaryDetails.isEmpty()) {
			summaryDetails.forEach(summary -> {
				AdvncePopupSummaryItemDto sumItemDto = new AdvncePopupSummaryItemDto();
				sumItemDto.setCont(summary.getCount() != null
						? new BigInteger(String.valueOf(summary.getCount()))
						: BigInteger.ZERO);
				sumItemDto.setInvValue(summary.getInvoiceValue());
				sumItemDto.setAssessableAmt(summary.getTaxableValue());
				sumItemDto.setIgst(summary.getIgst());
				sumItemDto.setCgst(summary.getCgst());
				sumItemDto.setSgst(summary.getSgst());
				sumItemDto.setCess(summary.getCess());
				sumItemDtos.add(sumItemDto);
			});
		}
		sumDto.setSumItemDtos(sumItemDtos);
		advp211bPopupDto.setSumDto(sumDto);

		AdvancePopupGstnViewDto advancePopupGstnViewDto = new AdvancePopupGstnViewDto();

		List<AdvancePopupGstnViewItemDto> itemDtos = new ArrayList<>();

		List<Ret1AspVerticalSummaryDto> gstnDetails = service
				.findgstnDetails(request);

		if (gstnDetails != null && !gstnDetails.isEmpty()) {
			gstnDetails.forEach(gstnDetail -> {
				AdvancePopupGstnViewItemDto itemDto = new AdvancePopupGstnViewItemDto();
				itemDto.setTranType(gstnDetail.getTransType());
				itemDto.setAssessableAmt(gstnDetail.getTaxableValue());
				itemDto.setBillingPos(gstnDetail.getPos());
				itemDto.setIgst(gstnDetail.getIgst());
				itemDto.setCgst(gstnDetail.getCgst());
				itemDto.setCess(gstnDetail.getCess());
				itemDto.setSgst(gstnDetail.getSgst());
				itemDtos.add(itemDto);
			});
		}
		advancePopupGstnViewDto.setItemDtos(itemDtos);
		advp211bPopupDto.setAdvancePopupGstnViewDto(advancePopupGstnViewDto);

		AdvanceAmadmentPopupVerticalDto vericalDto = new AdvanceAmadmentPopupVerticalDto();
		List<AdvanceAmantPopupVerticalItemDto> verticalItemDtos = new ArrayList<>();
		List<Gstr1AdvancedVerticalSummaryRespDto> verticalDetailsDtos = service
				.findVerticalDetails(request);

		if (verticalDetailsDtos != null && !verticalDetailsDtos.isEmpty()) {
			verticalDetailsDtos.forEach(vertical -> {
				AdvanceAmantPopupVerticalItemDto verticalItemDto = new AdvanceAmantPopupVerticalItemDto();
				verticalItemDto.setTranType(vertical.getTransType());
				verticalItemDto.setMon(vertical.getMonth());
				verticalItemDto.setObillingPos(vertical.getOrgPos() + HYPHEN
						+ vertical.getOrgStateName());
				verticalItemDto.setOrate(vertical.getOrgRate());
				verticalItemDto.setOgar(vertical.getOrgTaxableValue());
				verticalItemDto.setNbillingPos(vertical.getNewPos() + HYPHEN
						+ vertical.getNewStateName());
				verticalItemDto.setNrate(vertical.getNewRate());
				verticalItemDto.setNgar(vertical.getNewTaxableValue());

				verticalItemDto.setIgst(vertical.getIgst());
				verticalItemDto.setCgst(vertical.getCgst());
				verticalItemDto.setSgst(vertical.getSgst());
				verticalItemDto.setCess(vertical.getCess());

				verticalItemDto.setDistChanl(vertical.getDistrChannel());
				verticalItemDto.setDivision(vertical.getDivision());
				verticalItemDto.setSalesOrg(vertical.getSalesOrg());
				verticalItemDto.setLocation(vertical.getLocation());
				verticalItemDto.setPlantCode(vertical.getPlant());
				verticalItemDto.setPrftCntr1(vertical.getProfitCntr());
				verticalItemDto.setPrftCntr3(vertical.getUsrAccess1());
				verticalItemDto.setPrftCntr4(vertical.getUsrAccess2());
				verticalItemDto.setPrftCntr5(vertical.getUsrAccess3());
				verticalItemDto.setPrftCntr6(vertical.getUsrAccess4());
				verticalItemDto.setPrftCntr7(vertical.getUsrAccess5());
				verticalItemDto.setPrftCntr8(vertical.getUsrAccess6());

				verticalItemDto.setUdf1(vertical.getUsrDefined1());
				verticalItemDto.setUdf2(vertical.getUsrDefined2());
				verticalItemDto.setUdf3(vertical.getUsrDefined3());
				verticalItemDtos.add(verticalItemDto);
			});
		}
		vericalDto.setItem(verticalItemDtos);
		advp211bPopupDto.setVericalDto(vericalDto);
		return advp211bPopupDto;
	}

	public Integer getTabStatus(List<String> gstinList, int taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN IN (:gstinList) ");
		}

		String buildQuery = queryBuilder.toString();

		String queryStr = "SELECT (CASE WHEN IS_NIL_UI = FALSE THEN 1 ELSE 2 END) "
				+ "FROM GSTN_USER_REQUEST WHERE RETURN_TYPE='GSTR1' "
				+ "AND REQUEST_TYPE='SAVE' " + buildQuery
				+ " ORDER BY ID DESC LIMIT 1";

		Query Q = entityManager.createNativeQuery(queryStr);
		if (taxPeriod != 0) {
			Q.setParameter("taxPeriod", taxPeriod);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}

		@SuppressWarnings("unchecked")
		List<Object> resultSet = Q.getResultList();

		Integer status = 1;
		if (resultSet != null && !resultSet.isEmpty()) {
			status = (Integer) resultSet.get(0);
		}

		return status;

	}

	public static String fmtDate(String ldt) {
		if (ldt != null) {
			String[] s = ((String) ldt).split("-|/");
			if (s.length == 3) {
				if (s[0].length() == 4)
					return s[0] + "-" + s[1] + "-" + s[2];
				else if (s[2].length() == 4)
					return s[2] + "-" + s[1] + "-" + s[0];
			} else {
				return null;
			}
		}
		return null;
	}

	public void table14And15ValuesMappingToDto(
			List<Gstr1ReviewSummaryRequestItemDto> finalItemsDto,
			List<Gstr1SummarySectionDto> tbl14SectionList) {

		for (Gstr1SummarySectionDto dto : tbl14SectionList) {
			LOGGER.debug("dtos {} ", dto);
			Gstr1ReviewSummaryRequestItemDto itemDto = new Gstr1ReviewSummaryRequestItemDto();
			itemDto.setDataType("Category");
			itemDto.setTaxCate(dto.getTaxDocType());
			itemDto.setAspCount(dto.getRecords() != null
					? BigInteger.valueOf(dto.getRecords()) : null);
			
			itemDto.setAspIgstval(
					dto.getIgst() != null ? dto.getIgst() : BigDecimal.ZERO);
			itemDto.setAspCgstval(
					dto.getCgst() != null ? dto.getCgst() : BigDecimal.ZERO);
			itemDto.setAspSgstval(
					dto.getSgst() != null ? dto.getSgst() : BigDecimal.ZERO);
			itemDto.setAspCessval(
					dto.getCess() != null ? dto.getCess() : BigDecimal.ZERO);

			itemDto.setAspTbval(dto.getTaxableValue() != null
					? dto.getTaxableValue() : BigDecimal.ZERO);
			
			if (dto.getTaxDocType() != null && 
				    (dto.getTaxDocType().equals("15(i)") || 
				     dto.getTaxDocType().equals("15(ii)") || 
				     dto.getTaxDocType().equals("15(iii)") || 
				     dto.getTaxDocType().equals("15(iv)") ||
				     dto.getTaxDocType().equals("15A.1.a") || 
				     dto.getTaxDocType().equals("15A.1.b") || 
				     dto.getTaxDocType().equals("15A.2.a") || 
				     dto.getTaxDocType().equals("15A.2.b"))) {

				// Set all values to zero
				itemDto.setGstnCount(BigInteger.ZERO);
				itemDto.setGstnIgstval(BigDecimal.ZERO);
				itemDto.setGstnCgstval(BigDecimal.ZERO);
				itemDto.setGstnSgstval(BigDecimal.ZERO);
				itemDto.setGstnCessval(BigDecimal.ZERO);
				itemDto.setGstnTbval(BigDecimal.ZERO);

				itemDto.setDiffCount(BigInteger.ZERO);
				itemDto.setDiffIgstval(BigDecimal.ZERO);
				itemDto.setDiffCgstval(BigDecimal.ZERO);
				itemDto.setDiffSgstval(BigDecimal.ZERO);
				itemDto.setDiffCessval(BigDecimal.ZERO);
				itemDto.setDiffTbval(BigDecimal.ZERO);

			} else {
				// Set values from DTO with null checks
				itemDto.setGstnCount(dto.getGstnCount() != null
						? BigInteger.valueOf(dto.getGstnCount())
						: BigInteger.ZERO);
				itemDto.setGstnIgstval(
						dto.getGstnIgst() != null ? dto.getGstnIgst()
								: BigDecimal.ZERO);
				itemDto.setGstnCgstval(
						dto.getGstnCgst() != null ? dto.getGstnCgst()
								: BigDecimal.ZERO);
				itemDto.setGstnSgstval(
						dto.getGstnSgst() != null ? dto.getGstnSgst()
								: BigDecimal.ZERO);
				itemDto.setGstnCessval(
						dto.getGstnCess() != null ? dto.getGstnCess()
								: BigDecimal.ZERO);
				itemDto.setGstnTbval(dto.getGstnTaxableValue() != null
						? dto.getGstnTaxableValue()
						: BigDecimal.ZERO);

				itemDto.setDiffCount(
						dto.getRecords() != null
								? BigInteger.valueOf(dto.getRecords())
										.subtract(itemDto.getGstnCount())
								: BigInteger.ZERO);
				itemDto.setDiffIgstval(itemDto.getAspIgstval() != null
						? itemDto.getAspIgstval()
								.subtract(itemDto.getGstnIgstval())
						: BigDecimal.ZERO);
				itemDto.setDiffCgstval(itemDto.getAspCgstval() != null
						? itemDto.getAspCgstval()
								.subtract(itemDto.getGstnCgstval())
						: BigDecimal.ZERO);
				itemDto.setDiffSgstval(itemDto.getAspSgstval() != null
						? itemDto.getAspSgstval()
								.subtract(itemDto.getGstnSgstval())
						: BigDecimal.ZERO);
				itemDto.setDiffCessval(itemDto.getAspCessval() != null
						? itemDto.getAspCessval()
								.subtract(itemDto.getGstnCessval())
						: BigDecimal.ZERO);
				itemDto.setDiffTbval(dto.getTaxableValue() != null
						? dto.getTaxableValue().subtract(itemDto.getGstnTbval())
						: BigDecimal.ZERO);
			}

			/*itemDto.setGstnCount(dto.getGstnCount() != null
					? BigInteger.valueOf(dto.getGstnCount()) : BigInteger.ZERO);
			itemDto.setGstnIgstval(dto.getGstnIgst() != null ? dto.getGstnIgst()
					: BigDecimal.ZERO);
			itemDto.setGstnCgstval(dto.getGstnCgst() != null ? dto.getGstnCgst()
					: BigDecimal.ZERO);
			itemDto.setGstnSgstval(dto.getGstnSgst() != null ? dto.getGstnSgst()
					: BigDecimal.ZERO);
			itemDto.setGstnCessval(dto.getGstnCess() != null ? dto.getGstnCess()
					: BigDecimal.ZERO);
			itemDto.setGstnTbval(dto.getGstnTaxableValue() != null
					? dto.getGstnTaxableValue() : BigDecimal.ZERO);

			
			itemDto.setDiffCount(dto.getRecords() != null ? BigInteger
					.valueOf(dto.getRecords()).subtract(itemDto.getGstnCount())
					: BigInteger.ZERO);
			
			itemDto.setDiffIgstval(itemDto.getAspIgstval() != null
					? itemDto.getAspIgstval().subtract(itemDto.getGstnIgstval())
					: BigDecimal.ZERO);
			itemDto.setDiffCgstval(itemDto.getAspCgstval() != null
					? itemDto.getAspCgstval().subtract(itemDto.getGstnCgstval())
					: BigDecimal.ZERO);
			itemDto.setDiffSgstval(itemDto.getAspSgstval() != null
					? itemDto.getAspSgstval().subtract(itemDto.getGstnSgstval())
					: BigDecimal.ZERO);
			itemDto.setDiffCessval(itemDto.getAspCessval() != null
					? itemDto.getAspCessval().subtract(itemDto.getGstnCessval())
					: BigDecimal.ZERO);

			itemDto.setDiffTbval(
					dto.getTaxableValue() != null
							? dto.getTaxableValue()
									.subtract(itemDto.getGstnTaxableValue())
							: BigDecimal.ZERO);*/

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Printing itemDto for taxDocType: {}",
						dto.getTaxDocType());
				LOGGER.debug("itemDto: {}", itemDto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Printing itemDto for taxDocType: {}",
							dto.getTaxDocType());
					LOGGER.debug("itemDto: {}", itemDto);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Printing itemDto for taxDocType: {}",
							dto.getTaxDocType());
					Field[] fields = itemDto.getClass().getDeclaredFields();
					for (Field field : fields) {
						try {
							field.setAccessible(true);
							Object value = field.get(itemDto);
							LOGGER.debug("{}: {}", field.getName(), value);
						} catch (IllegalAccessException e) {
							LOGGER.error("Error accessing field {}",
									field.getName(), e);
						}
					}
				}
			}
			finalItemsDto.add(itemDto);
		}
	}
}
