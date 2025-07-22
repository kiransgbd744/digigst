/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.StateCache;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr3b32PopUpSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3b32popUpReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3b5PopUpSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3b5popUpReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bDiffLibpopUpReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bDifflibPopUpSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bProcessAndReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bProcessSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bProcessSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bReverseIntgReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bReviewSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bTabLibpopUpReviewSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Gstr3bTablibPopUpSummaryItemDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardDto;
import com.ey.advisory.app.gstr3b.Gstr3BEntityDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BExcemptNilNonGstnDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BInterStateSuppliesDto;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffDto;
import com.ey.advisory.app.gstr3b.Gstr3BLiabilitySetOffService;
import com.ey.advisory.app.gstr3b.Gstr3bDashboardDto;
import com.ey.advisory.app.gstr3b.Gstr3bUtil;
import com.ey.advisory.app.gstr3b.LedgerDetailsDto;
import com.ey.advisory.app.gstr3b.PaidThroughItcDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva
 *
 */
@Slf4j
@Service("Gstr3BReviewSummaryReqRevIntegrationHandler")
public class Gstr3BReviewSummaryReqRevIntegrationHandler {

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("DefaultStateCache")
	private StateCache statecodeCache;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("GstnSummaryErpSectionService")
	private GstnSummaryErpSectionService gstnSummaryErpSectionService;

	@Autowired
	private Gstr3BEntityDashboardService gstr3BEntityService;

	@Autowired
	private Gstr3BGstinDashboardService gstr3bGstinService;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository permRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	@Qualifier("Gstr3BLiabilitySetOffServiceImpl")
	private Gstr3BLiabilitySetOffService gstr3BLaibilityService;

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	private Gstr3BSaveStatusRepository gstr3BSaveStatusRepo;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	public Integer reviewSummaryRequestToErp(
			RevIntegrationScenarioTriggerDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"reviewSummaryRequestToErp method called with reqDto {}",
					dto);
		}
		String destinationName = dto.getDestinationName();
		Long scenarioId = dto.getScenarioId();
		String groupcode = dto.getGroupcode();
		String gstin = dto.getGstin();
		Long entityId = dto.getEntityId();
		String entityName = null;
		String entityPan = null;

		try {
			List<String> listOfPrevtaxPeriod = GenUtil.listOfPrevtaxPeriod(2);

			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();

			}

			List<Gstr3BEntityDashboardDto> entityData = new ArrayList<>();
			for (String returnPeriod : listOfPrevtaxPeriod) {
				List<String> gstins = new ArrayList<>(Arrays.asList(gstin));
				entityData.addAll(gstr3BEntityService
						.getEntityDashBoard(returnPeriod, gstins, entityId));

			}
			Gstr3bReviewHeaderSummaryRequestDto reviewSummeryreqDto = convertProcessDocsAsDtos(
					gstin, entityName, entityPan, entityData);
			long currentBatchSize = 0;
			if (reviewSummeryreqDto.getProcessAndReviewSummary() != null
					&& !reviewSummeryreqDto.getProcessAndReviewSummary()
							.isEmpty()) {
				currentBatchSize = currentBatchSize + reviewSummeryreqDto
						.getProcessAndReviewSummary().size();
			}

			Long gstinId = gstnDetailRepo.findIdByGstin(gstin);

			// Get erp id based on scenariodId
			/*
			 * List<ErpEventsScenarioPermissionEntity> permEntity =
			 * erpEventsScenPermiRep.getErpEventsScenarioPerms(scenarioId);
			 */
			ErpScenarioPermissionEntity permEntity = permRepository
					.findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenarioId,
							entityId);
			AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(groupcode,
					entityId, gstin, destinationName, scenarioId,
					currentBatchSize, null, ERPConstants.BACKGROUND_BASED_JOB,
					permEntity.getErpId(), null,
					APIConstants.SYSTEM.toUpperCase());

			// Erp Batch Id forming
			batch = batchRepo.save(batch);

			int respcode = destinationConn.pushToErp(reviewSummeryreqDto,
					"Gstr3bReviewHeaderSummaryRequestDto", batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, respcode);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Before Pushing to ERP 3B Rev Int Data {} :",
						reviewSummeryreqDto);
			}
			return respcode;
		} catch (Exception ex) {
			LOGGER.error("Unexpected Error", ex);
			throw new AppException("Unexpected Error", ex);
		}
	}

	private Gstr3b5popUpReviewSummaryDto popUpSummery5(String gstin,
			String returnPeriod) {

		Gstr3b5popUpReviewSummaryDto headerDto = new Gstr3b5popUpReviewSummaryDto();
		List<Gstr3BExcemptNilNonGstnDto> respList5 = gstr3bGstinService
				.getExcemptNilNonGstIS(returnPeriod, gstin);
		List<Gstr3b5PopUpSummaryItemDto> dtoList5 = new ArrayList<>();
		for (Gstr3BExcemptNilNonGstnDto res : respList5) {
			Gstr3b5PopUpSummaryItemDto dto = new Gstr3b5PopUpSummaryItemDto();
			ImmutableMap<String, String> getsupplyTypeDescByTableName = Gstr3bUtil
					.getsupplyTypeDescByTableName();
			dto.setNatureSup(
					getsupplyTypeDescByTableName.get(res.getSectionName()));
			dto.setDigiInterSup(res.getInterStateCompute());
			dto.setDigiIntraSup(res.getIntraStateCompute());
			dto.setUiInterSup(res.getInterState());
			dto.setUiIntraSup(res.getIntraState());
			dtoList5.add(dto);
		}
		headerDto.setItems(dtoList5);
		return headerDto;

	}

	private Gstr3b32popUpReviewSummaryDto popUpSummery32(String gstin,
			String returnPeriod) {

		Gstr3b32popUpReviewSummaryDto headerDto = new Gstr3b32popUpReviewSummaryDto();

		List<Gstr3BInterStateSuppliesDto> respList = gstr3bGstinService
				.getInterStateSupplies(returnPeriod, gstin);
		List<Gstr3b32PopUpSummaryItemDto> dtoList32 = new ArrayList<>();
		for (Gstr3BInterStateSuppliesDto res : respList) {
			Gstr3b32PopUpSummaryItemDto dto = new Gstr3b32PopUpSummaryItemDto();
			dto.setTypeOfSupp(res.getSectionName().replaceAll("\\P{L}+", ""));
			dto.setDigiCompPsup(res.getPosCompute());
			dto.setDigiCompTbVal(res.getTaxableValCompute());
			dto.setDigiCompIgst(res.getIgstCompute());
			dto.setUiCompPsup(res.getPos());
			dto.setUiCompTbVal(res.getTaxableVal());
			dto.setUiCompIgst(res.getIgst());
			dto.setGstnCompPsup(res.getPosAutoCal());
			dto.setGstnCompTbVal(res.getTaxableValAutoCal());
			dto.setGstnCompIgst(res.getIgstAutoCal());
			dtoList32.add(dto);
		}
		headerDto.setItems(dtoList32);
		return headerDto;

	}

	private Gstr3bTabLibpopUpReviewSummaryDto popUpSummeryTagLib(String gstin,
			String returnPeriod, String status, String msg) {
		Gstr3bTabLibpopUpReviewSummaryDto headerDto = new Gstr3bTabLibpopUpReviewSummaryDto();
		try {
			Gstr3BLiabilitySetOffDto respListtaglib = gstr3BLaibilityService
					.get3BLiabilitySetOff(gstin, returnPeriod, status, msg);
			List<LedgerDetailsDto> ledgerDetails = respListtaglib
					.getLedgerDetails();
			List<Gstr3bTablibPopUpSummaryItemDto> dtoList = new ArrayList<>();
			for (LedgerDetailsDto cashLedgerDto : ledgerDetails) {
				Gstr3bTablibPopUpSummaryItemDto dto = new Gstr3bTablibPopUpSummaryItemDto();
				ImmutableMap<String, String> liabilitySetDesc = LiabilitySetDesc();
				dto.setDescription(
						liabilitySetDesc.get(cashLedgerDto.getDesc()));
				dto.setCashIgst(cashLedgerDto.getI() != null
						? cashLedgerDto.getI() : BigDecimal.ZERO);
				dto.setCashCgst(cashLedgerDto.getC() != null
						? cashLedgerDto.getC() : BigDecimal.ZERO);
				dto.setCashState(cashLedgerDto.getS() != null
						? cashLedgerDto.getS() : BigDecimal.ZERO);
				dto.setCashCess(cashLedgerDto.getCs() != null
						? cashLedgerDto.getCs() : BigDecimal.ZERO);
				dto.setCashTotal(dto.getCashIgst().add(dto.getCashCgst())
						.add(dto.getCashState()).add(dto.getCashCess()));
				dto.setCrdtIgst(cashLedgerDto.getCri() != null
						? cashLedgerDto.getCri() : BigDecimal.ZERO);
				dto.setCrdtCgst(cashLedgerDto.getCrc() != null
						? cashLedgerDto.getCrc() : BigDecimal.ZERO);
				dto.setCrdtState(cashLedgerDto.getCrs() != null
						? cashLedgerDto.getCrs() : BigDecimal.ZERO);
				dto.setCrdtCess(cashLedgerDto.getCrcs() != null
						? cashLedgerDto.getCrcs() : BigDecimal.ZERO);
				dto.setCrdtTotal(dto.getCrdtIgst().add(dto.getCrdtCgst())
						.add(dto.getCrdtState()).add(dto.getCrdtCess()));
				dtoList.add(dto);
			}
			List<PaidThroughItcDto> gstr3bDetails = respListtaglib
					.getGstr3bDetails();

			for (PaidThroughItcDto gstr3dto : gstr3bDetails) {
				Gstr3bTablibPopUpSummaryItemDto dto = new Gstr3bTablibPopUpSummaryItemDto();
				// 3,4,5,6 we have to calculate
				dto.setDescription(gstr3dto.getDesc());// 1
				dto.setOthRevChgPbl(gstr3dto.getOtrci() != null
						? gstr3dto.getOtrci() : BigDecimal.ZERO);// 2
				dto.setPdThrItcIgst(gstr3dto.getPdi() != null
						? gstr3dto.getPdi() : BigDecimal.ZERO);// 3
				dto.setPdThrItcCgst(gstr3dto.getPdc() != null
						? gstr3dto.getPdc() : BigDecimal.ZERO);// 4
				dto.setPdThrItcSgst(gstr3dto.getPds() != null
						? gstr3dto.getPds() : BigDecimal.ZERO);// 5
				dto.setPdThrItcCess(gstr3dto.getPdcs() != null
						? gstr3dto.getPdcs() : BigDecimal.ZERO);// 6

				dto.setOrtaxTobePdcs(
						dto.getOthRevChgPbl().subtract(dto.getPdThrItcIgst())
								.subtract(dto.getPdThrItcSgst())
								.subtract(dto.getPdThrItcCgst())
								.subtract(dto.getPdThrItcCess()));// 7 we have
																	// to
																	// calculate
				dto.setRevChgTaxPbl(gstr3dto.getRci8() != null
						? gstr3dto.getRci8() : BigDecimal.ZERO);// 8
				dto.setRctobePdCas(gstr3dto.getRci8() != null
						? gstr3dto.getRci8() : BigDecimal.ZERO);// 9
				dto.setIntrestPayble(gstr3dto.getInti10() != null
						? gstr3dto.getInti10() : BigDecimal.ZERO);// 10
				dto.setIntTobePdCas(gstr3dto.getInti10() != null
						? gstr3dto.getInti10() : BigDecimal.ZERO);// 11
				dto.setLateFeePayble(gstr3dto.getLateFee12() != null
						? gstr3dto.getLateFee12() : BigDecimal.ZERO);// 12
				dto.setLtftobePdincas(gstr3dto.getLateFee12() != null
						? gstr3dto.getLateFee12() : BigDecimal.ZERO);// 13
				// Utilizable Cash balance (col 14) = min ( col 7+ col 9) ,
				// cash ledger total(tax)) + min( col 11 ,
				// cash ledger total(interest)) + min( col 13 , cash ledger
				// total(latefee))
				List<Gstr3bTablibPopUpSummaryItemDto> taxDoc = dtoList.stream()
						.filter(doc -> GSTConstants.TAX
								.equalsIgnoreCase(doc.getDescription()))
						.collect(Collectors.toList());
				List<Gstr3bTablibPopUpSummaryItemDto> interestDoc = dtoList
						.stream()
						.filter(doc -> GSTConstants.INTEREST
								.equalsIgnoreCase(doc.getDescription()))
						.collect(Collectors.toList());
				List<Gstr3bTablibPopUpSummaryItemDto> interestLatefee = dtoList
						.stream()
						.filter(doc -> "Late fee"
								.equalsIgnoreCase(doc.getDescription()))
						.collect(Collectors.toList());
				BigDecimal cahLedgerTotalInterest = interestDoc.get(0)
						.getCashTotal();
				BigDecimal cahLedgerTotalLateFee = interestLatefee.get(0)
						.getCashTotal();
				BigDecimal cul7_9Sum = dto.getOrtaxTobePdcs()
						.add(dto.getRevChgTaxPbl());
				BigDecimal cahLedgerTotalTax = taxDoc.get(0).getCashTotal();
				BigDecimal minOfCol11AndcahLedTotIntetrest = dto
						.getIntTobePdCas().min(cahLedgerTotalInterest);
				BigDecimal minOfColu3AndcahLedTotlateFee = dto
						.getLtftobePdincas().min(cahLedgerTotalLateFee);
				dto.setUtliCashBal(cul7_9Sum.min(
						cahLedgerTotalTax.add(minOfCol11AndcahLedTotIntetrest)
								.add(minOfColu3AndcahLedTotlateFee)));
				// we have to calculate(14)
				// Additional Cash required ( col 15) = A + B + C
				// A = (col 7+ col 9 - (min ( col 7+ col 9, cash ledger
				// total(tax)))
				// - if results in negative value consider value 0
				// B = col 11 - min( col 11 , cash ledger total(interest))
				// - if results in negative value consider value 0
				// C = col 13 - min( col 13 , cash ledger total(latefee))
				// - if results in negative value consider value 0
				BigDecimal a = cul7_9Sum
						.subtract(cul7_9Sum.min(cahLedgerTotalTax));
				BigDecimal b = dto.getIntTobePdCas()
						.subtract(dto.getIntTobePdCas()
								.min(minOfCol11AndcahLedTotIntetrest));
				BigDecimal c = dto.getLtftobePdincas()
						.subtract(dto.getLtftobePdincas()
								.min(minOfColu3AndcahLedTotlateFee));
				a = negativeValueConsiderAsZero(a);
				b = negativeValueConsiderAsZero(b);
				c = negativeValueConsiderAsZero(c);
				dto.setAddCashReqd(a.add(b).add(c));// we have to calculate(15)
				dtoList.add(dto);
			}
			headerDto.setItems(dtoList);
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}

		return headerDto;

	}

	private Gstr3bReverseIntgReviewSummaryDto reviewSummeryConvert(String gstin,
			String returnPeriod, String entityName) 
	{
		Long entityId = entityRepo.findIdEntityId(entityName);
		Gstr3bDashboardDto gstinData = gstr3bGstinService
				.getgstr3bgstnDashBoardList(returnPeriod, gstin, entityId);

		Gstr3bReverseIntgReviewSummaryDto reviewSummary = new Gstr3bReverseIntgReviewSummaryDto();
		List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard = gstinData
				.getGstr3bGstinDashboard();
		ImmutableMap<String, String> getsupplyTypeDescByTableName = Gstr3bUtil
				.getsupplyTypeDescByTableName();
		List<Gstr3bReviewSummaryItemDto> dtoList = new ArrayList<>();

		for (Gstr3BGstinDashboardDto reqdo : gstr3bGstinDashboard) {
			if (reqdo.getComputedTaxableVal() == null) {
				reqdo.setComputedTaxableVal(BigDecimal.ZERO);
			}
			if (reqdo.getComputdCess() == null) {
				reqdo.setComputdCess(BigDecimal.ZERO);
			}
			if (reqdo.getComputedCgst() == null) {
				reqdo.setComputedCgst(BigDecimal.ZERO);
			}
			if (reqdo.getComputedSgst() == null) {
				reqdo.setComputedSgst(BigDecimal.ZERO);
			}
			if (reqdo.getComputedIgst() == null) {
				reqdo.setComputedIgst(BigDecimal.ZERO);
			}
			if (reqdo.getAutoCalCess() == null) {
				reqdo.setAutoCalCess(BigDecimal.ZERO);
			}
			if (reqdo.getAutoCalCgst() == null) {
				reqdo.setAutoCalCgst(BigDecimal.ZERO);
			}
			if (reqdo.getAutoCalSgst() == null) {
				reqdo.setAutoCalSgst(BigDecimal.ZERO);
			}
			if (reqdo.getAutoCalIgst() == null) {
				reqdo.setAutoCalIgst(BigDecimal.ZERO);
			}
			if (reqdo.getGstinTaxableVal() == null) {
				reqdo.setGstinTaxableVal(BigDecimal.ZERO);
			}
			if (reqdo.getGstinCess() == null) {
				reqdo.setGstinCess(BigDecimal.ZERO);
			}
			if (reqdo.getGstinCgst() == null) {
				reqdo.setGstinCgst(BigDecimal.ZERO);
			}
			if (reqdo.getGstinSgst() == null) {
				reqdo.setGstinSgst(BigDecimal.ZERO);
			}
			if (reqdo.getGstinIgst() == null) {
				reqdo.setGstinIgst(BigDecimal.ZERO);
			}
			if (reqdo.getUserInputCess() == null) {
				reqdo.setUserInputCess(BigDecimal.ZERO);
			}
			if (reqdo.getUserInputCgst() == null) {
				reqdo.setUserInputCgst(BigDecimal.ZERO);
			}
			if (reqdo.getUserInputSgst() == null) {
				reqdo.setUserInputSgst(BigDecimal.ZERO);
			}
			if (reqdo.getUserInputIgst() == null) {
				reqdo.setUserInputIgst(BigDecimal.ZERO);
			}
			if (reqdo.getUserInputTaxableVal() == null) {
				reqdo.setUserInputTaxableVal(BigDecimal.ZERO);
			}
		}

		get31(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get311(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get32(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get4A(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get4B(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get4And4c(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList,
				Gstr3BConstants.Table4);
		get4And4c(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList,
				Gstr3BConstants.Table4C);
		get4D(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get5(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);
		get51(getsupplyTypeDescByTableName, gstr3bGstinDashboard, dtoList);

		for (Gstr3BGstinDashboardDto reqdo : gstr3bGstinDashboard) {

			if (Gstr3BConstants.Table3_1.equals(reqdo.getTable())
					|| Gstr3BConstants.Table3_2.equals(reqdo.getTable())
					|| Gstr3BConstants.Table4.equals(reqdo.getTable())
					|| Gstr3BConstants.Table4A.equals(reqdo.getTable())
					|| Gstr3BConstants.Table4B.equals(reqdo.getTable())
					|| Gstr3BConstants.Table4C.equals(reqdo.getTable())
					|| Gstr3BConstants.Table4D.equals(reqdo.getTable())
					|| Gstr3BConstants.Table5.equals(reqdo.getTable())
					|| Gstr3BConstants.Table5_1.equals(reqdo.getTable())
					|| Gstr3BConstants.Table3_1_1.equals(reqdo.getTable())) {
				continue;
			}

			Gstr3bReviewSummaryItemDto dto = new Gstr3bReviewSummaryItemDto();
			dto.setTableName(reqdo.getTable());
			dto.setTypeOfSupp(
					getsupplyTypeDescByTableName.get(reqdo.getTable()));
			// computed mapped to digital

			dto.setDigiTaxbleVal(reqdo.getComputedTaxableVal());
			dto.setDigiCess(reqdo.getComputdCess());
			dto.setDigiCgst(reqdo.getComputedCgst());
			dto.setDigiSgst(reqdo.getComputedSgst());
			dto.setDigiIgst(reqdo.getComputedIgst());
			// autoCalculated mapped to compuedgstin
			dto.setGstnCompCess(reqdo.getAutoCalCess());
			dto.setGstnCompCgst(reqdo.getAutoCalCgst());
			dto.setGstnCompIgst(reqdo.getAutoCalIgst());
			dto.setGstnCompSgst(reqdo.getAutoCalSgst());
			dto.setGstnCompTaxle(reqdo.getAutoCalTaxableVal());
			// gstin values mapped to gstin
			dto.setGstnTaxbleVal(reqdo.getGstinTaxableVal());
			dto.setGstnCess(reqdo.getGstinCess());
			dto.setGstnCgst(reqdo.getGstinCgst());
			dto.setGstnIgst(reqdo.getGstinIgst());
			dto.setGstnSgst(reqdo.getGstinSgst());
			// userInput mapped to user input
			dto.setDigiUiCess(reqdo.getUserInputCess());
			dto.setDigiUiCgst(reqdo.getUserInputCgst());
			dto.setDigiUiIgst(reqdo.getUserInputIgst());
			dto.setDigiUiSgst(reqdo.getUserInputSgst());
			dto.setDigiUiTbVal(reqdo.getUserInputTaxableVal());
			// difference=userInput-gstin

			dto.setDiffTaxbleVal(
					dto.getDigiUiTbVal().subtract(dto.getGstnTaxbleVal()));
			dto.setDiffCess(dto.getDigiUiCess().subtract(dto.getGstnCess()));
			dto.setDiffCgst(dto.getDigiUiCgst().subtract(dto.getGstnCgst()));
			dto.setDiffSgst(dto.getDigiUiSgst().subtract(dto.getGstnSgst()));
			dto.setDiffIgst(dto.getDigiUiIgst().subtract(dto.getGstnIgst()));
			dtoList.add(dto);
		}
		reviewSummary.setItems(dtoList);
		return reviewSummary;

	}

	private void get311(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {

		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table3_1_1_A.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_1_1_B.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table3_1_1);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table3_1_1));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);

	}

	private void get31(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table3_1_A.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_1_B.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_1_C.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_1_D.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_1_E.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table3_1);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table3_1));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get4And4c(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList, String type) {

		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;
		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;
		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;
		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;
		BigDecimal computedTaxableVal4A = BigDecimal.ZERO;
		BigDecimal computedSgst4A = BigDecimal.ZERO;
		BigDecimal computedCgst4A = BigDecimal.ZERO;
		BigDecimal computedIgst4A = BigDecimal.ZERO;
		BigDecimal computedCess4A = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal4A = BigDecimal.ZERO;
		BigDecimal userInputIgst4A = BigDecimal.ZERO;
		BigDecimal userInputCgst4A = BigDecimal.ZERO;
		BigDecimal userInputSgst4A = BigDecimal.ZERO;
		BigDecimal userInputCess4A = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal4A = BigDecimal.ZERO;
		BigDecimal gstinCgst4A = BigDecimal.ZERO;
		BigDecimal gstinIgst4A = BigDecimal.ZERO;
		BigDecimal gstinSgst4A = BigDecimal.ZERO;
		BigDecimal gstinCess4A = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal4A = BigDecimal.ZERO;
		BigDecimal autoCalIgst4A = BigDecimal.ZERO;
		BigDecimal autoCalCgst4A = BigDecimal.ZERO;
		BigDecimal autoCalSgst4A = BigDecimal.ZERO;
		BigDecimal autoCalCess4A = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table4A1.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A2.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A3.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A4.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A5.equals(reqDo.getTable())) {
				computedTaxableVal4A = computedTaxableVal4A
						.add(reqDo.getComputedTaxableVal());
				computedSgst4A = computedSgst4A.add(reqDo.getComputedSgst());
				computedCgst4A = computedCgst4A.add(reqDo.getComputedCgst());
				computedIgst4A = computedIgst4A.add(reqDo.getComputedIgst());
				computedCess4A = computedCess4A.add(reqDo.getComputdCess());

				if (reqDo.getUserInputTaxableVal() == null) {
					reqDo.setUserInputTaxableVal(BigDecimal.ZERO);
				}
				userInputTaxbleVal4A = userInputTaxbleVal4A
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst4A = userInputIgst4A.add(reqDo.getUserInputIgst());
				userInputCgst4A = userInputCgst4A.add(reqDo.getUserInputCgst());
				userInputSgst4A = userInputSgst4A.add(reqDo.getUserInputSgst());
				userInputCess4A = userInputCess4A.add(reqDo.getUserInputCess());

				gstinTaxableVal4A = gstinTaxableVal4A
						.add(reqDo.getGstinTaxableVal());
				gstinIgst4A = gstinIgst4A.add(reqDo.getGstinIgst());
				gstinCgst4A = gstinCgst4A.add(reqDo.getGstinCgst());
				gstinSgst4A = gstinSgst4A.add(reqDo.getGstinSgst());
				gstinCess4A = gstinCess4A.add(reqDo.getGstinCess());

				autoCalTaxableVal4A = autoCalTaxableVal4A
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst4A = autoCalIgst4A.add(reqDo.getAutoCalIgst());
				autoCalCgst4A = autoCalCgst4A.add(reqDo.getAutoCalCgst());
				autoCalSgst4A = autoCalSgst4A.add(reqDo.getAutoCalSgst());
				autoCalCess4A = autoCalCess4A.add(reqDo.getAutoCalCess());
			}
		}

		BigDecimal computedTaxableVal4B = BigDecimal.ZERO;
		BigDecimal computedSgst4B = BigDecimal.ZERO;
		BigDecimal computedCgst4B = BigDecimal.ZERO;
		BigDecimal computedIgst4B = BigDecimal.ZERO;
		BigDecimal computedCess4B = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal4B = BigDecimal.ZERO;
		BigDecimal userInputIgst4B = BigDecimal.ZERO;
		BigDecimal userInputCgst4B = BigDecimal.ZERO;
		BigDecimal userInputSgst4B = BigDecimal.ZERO;
		BigDecimal userInputCess4B = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal4B = BigDecimal.ZERO;
		BigDecimal gstinCgst4B = BigDecimal.ZERO;
		BigDecimal gstinIgst4B = BigDecimal.ZERO;
		BigDecimal gstinSgst4B = BigDecimal.ZERO;
		BigDecimal gstinCess4B = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal4B = BigDecimal.ZERO;
		BigDecimal autoCalIgst4B = BigDecimal.ZERO;
		BigDecimal autoCalCgst4B = BigDecimal.ZERO;
		BigDecimal autoCalSgst4B = BigDecimal.ZERO;
		BigDecimal autoCalCess4B = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table4B1.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4B2.equals(reqDo.getTable())) {
				computedTaxableVal4B = computedTaxableVal4B
						.add(reqDo.getComputedTaxableVal());
				computedSgst4B = computedSgst4B.add(reqDo.getComputedSgst());
				computedCgst4B = computedCgst4B.add(reqDo.getComputedCgst());
				computedIgst4B = computedIgst4B.add(reqDo.getComputedIgst());
				computedCess4B = computedCess4B.add(reqDo.getComputdCess());

				userInputTaxbleVal4B = userInputTaxbleVal4B
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst4B = userInputIgst4B.add(reqDo.getUserInputIgst());
				userInputCgst4B = userInputCgst4B.add(reqDo.getUserInputCgst());
				userInputSgst4B = userInputSgst4B.add(reqDo.getUserInputSgst());
				userInputCess4B = userInputCess4B.add(reqDo.getUserInputCess());

				gstinTaxableVal4B = gstinTaxableVal4B
						.add(reqDo.getGstinTaxableVal());
				gstinIgst4B = gstinIgst4B.add(reqDo.getGstinIgst());
				gstinCgst4B = gstinCgst4B.add(reqDo.getGstinCgst());
				gstinSgst4B = gstinSgst4B.add(reqDo.getGstinSgst());
				gstinCess4B = gstinCess4B.add(reqDo.getGstinCess());

				autoCalTaxableVal4B = autoCalTaxableVal4B
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst4B = autoCalIgst4B.add(reqDo.getAutoCalIgst());
				autoCalCgst4B = autoCalCgst4B.add(reqDo.getAutoCalCgst());
				autoCalSgst4B = autoCalSgst4B.add(reqDo.getAutoCalSgst());
				autoCalCess4B = autoCalCess4B.add(reqDo.getAutoCalCess());
			}
		}

		computedTaxableVal = computedTaxableVal4A
				.subtract(computedTaxableVal4B);
		computedSgst = computedSgst4A.subtract(computedSgst4B);
		computedCgst = computedCgst4A.subtract(computedCgst4B);
		computedIgst = computedIgst4A.subtract(computedIgst4B);
		computedCess = computedCess4A.subtract(computedCess4B);

		userInputTaxbleVal = userInputTaxbleVal4A
				.subtract(userInputTaxbleVal4B);
		userInputIgst = userInputIgst4A.subtract(userInputIgst4B);
		userInputCgst = userInputCgst4A.subtract(userInputCgst4B);
		userInputSgst = userInputSgst4A.subtract(userInputSgst4B);
		userInputCess = userInputCess4A.subtract(userInputCess4B);

		gstinTaxableVal = gstinTaxableVal4A.subtract(gstinTaxableVal4B);
		gstinIgst = gstinIgst4A.subtract(gstinIgst4B);
		gstinCgst = gstinCgst4A.subtract(gstinCgst4B);
		gstinSgst = gstinSgst4A.subtract(gstinSgst4B);
		gstinCess = gstinCess4A.subtract(gstinCess4B);

		autoCalTaxableVal = autoCalTaxableVal4A.subtract(autoCalTaxableVal4B);
		autoCalIgst = autoCalIgst4A.subtract(autoCalIgst4B);
		autoCalCgst = autoCalCgst4A.subtract(autoCalCgst4B);
		autoCalSgst = autoCalSgst4A.subtract(autoCalSgst4B);
		autoCalCess = autoCalCess4A.subtract(autoCalCess4B);

		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		if (Gstr3BConstants.Table4.equals(type)
				|| Gstr3BConstants.Table4C.equals(type)) {
			dto3B.setTableName(type);
			dto3B.setTypeOfSupp(getsupplyTypeDescByTableName.get(type));
		}
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get32(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table3_2_A.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_2_B.equals(reqDo.getTable())
					|| Gstr3BConstants.Table3_2_C.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table3_2);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table3_2));

		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));

		dtoList.add(dto3B);
	}

	private void get4A(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table4A1.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A2.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A3.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A4.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4A5.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table4A);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table4A));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get4B(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table4B1.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4B2.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table4B);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table4B));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get4D(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table4D1.equals(reqDo.getTable())
					|| Gstr3BConstants.Table4D2.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table4D);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table4D));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get5(ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table5A.equals(reqDo.getTable())
					|| Gstr3BConstants.Table5B.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table5);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table5));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	private void get51(
			ImmutableMap<String, String> getsupplyTypeDescByTableName,
			List<Gstr3BGstinDashboardDto> gstr3bGstinDashboard,
			List<Gstr3bReviewSummaryItemDto> dtoList) {
		BigDecimal computedTaxableVal = BigDecimal.ZERO;
		BigDecimal computedSgst = BigDecimal.ZERO;
		BigDecimal computedCgst = BigDecimal.ZERO;
		BigDecimal computedIgst = BigDecimal.ZERO;
		BigDecimal computedCess = BigDecimal.ZERO;

		BigDecimal userInputTaxbleVal = BigDecimal.ZERO;
		BigDecimal userInputIgst = BigDecimal.ZERO;
		BigDecimal userInputCgst = BigDecimal.ZERO;
		BigDecimal userInputSgst = BigDecimal.ZERO;
		BigDecimal userInputCess = BigDecimal.ZERO;

		BigDecimal gstinTaxableVal = BigDecimal.ZERO;
		BigDecimal gstinCgst = BigDecimal.ZERO;
		BigDecimal gstinIgst = BigDecimal.ZERO;
		BigDecimal gstinSgst = BigDecimal.ZERO;
		BigDecimal gstinCess = BigDecimal.ZERO;

		BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
		BigDecimal autoCalIgst = BigDecimal.ZERO;
		BigDecimal autoCalCgst = BigDecimal.ZERO;
		BigDecimal autoCalSgst = BigDecimal.ZERO;
		BigDecimal autoCalCess = BigDecimal.ZERO;

		for (Gstr3BGstinDashboardDto reqDo : gstr3bGstinDashboard) {
			if (Gstr3BConstants.Table5_1A.equals(reqDo.getTable())
					|| Gstr3BConstants.Table5_1B.equals(reqDo.getTable())) {
				computedTaxableVal = computedTaxableVal
						.add(reqDo.getComputedTaxableVal());
				computedSgst = computedSgst.add(reqDo.getComputedSgst());
				computedCgst = computedCgst.add(reqDo.getComputedCgst());
				computedIgst = computedIgst.add(reqDo.getComputedIgst());
				computedCess = computedCess.add(reqDo.getComputdCess());

				userInputTaxbleVal = userInputTaxbleVal
						.add(reqDo.getUserInputTaxableVal());
				userInputIgst = userInputIgst.add(reqDo.getUserInputIgst());
				userInputCgst = userInputCgst.add(reqDo.getUserInputCgst());
				userInputSgst = userInputSgst.add(reqDo.getUserInputSgst());
				userInputCess = userInputCess.add(reqDo.getUserInputCess());

				gstinTaxableVal = gstinTaxableVal
						.add(reqDo.getGstinTaxableVal());
				gstinIgst = gstinIgst.add(reqDo.getGstinIgst());
				gstinCgst = gstinCgst.add(reqDo.getGstinCgst());
				gstinSgst = gstinSgst.add(reqDo.getGstinSgst());
				gstinCess = gstinCess.add(reqDo.getGstinCess());

				autoCalTaxableVal = autoCalTaxableVal
						.add(reqDo.getAutoCalTaxableVal());
				autoCalIgst = autoCalIgst.add(reqDo.getAutoCalIgst());
				autoCalCgst = autoCalCgst.add(reqDo.getAutoCalCgst());
				autoCalSgst = autoCalSgst.add(reqDo.getAutoCalSgst());
				autoCalCess = autoCalCess.add(reqDo.getAutoCalCess());
			}
		}
		Gstr3bReviewSummaryItemDto dto3B = new Gstr3bReviewSummaryItemDto();
		dto3B.setTableName(Gstr3BConstants.Table5_1);
		dto3B.setTypeOfSupp(
				getsupplyTypeDescByTableName.get(Gstr3BConstants.Table5_1));
		dto3B.setDigiTaxbleVal(computedTaxableVal);
		dto3B.setDigiCess(computedCess);
		dto3B.setDigiCgst(computedCgst);
		dto3B.setDigiSgst(computedSgst);
		dto3B.setDigiIgst(computedIgst);

		dto3B.setGstnCompTaxle(autoCalTaxableVal);
		dto3B.setGstnCompCess(autoCalCess);
		dto3B.setGstnCompCgst(autoCalCgst);
		dto3B.setGstnCompSgst(autoCalSgst);
		dto3B.setGstnCompIgst(autoCalIgst);

		dto3B.setGstnTaxbleVal(gstinTaxableVal);
		dto3B.setGstnCess(gstinCess);
		dto3B.setGstnCgst(gstinCgst);
		dto3B.setGstnIgst(gstinIgst);
		dto3B.setGstnSgst(gstinSgst);

		dto3B.setDigiUiCess(userInputCess);
		dto3B.setDigiUiCgst(userInputCgst);
		dto3B.setDigiUiIgst(userInputIgst);
		dto3B.setDigiUiSgst(userInputSgst);
		dto3B.setDigiUiTbVal(userInputTaxbleVal);

		dto3B.setDiffTaxbleVal(userInputTaxbleVal.subtract(gstinTaxableVal));
		dto3B.setDiffCess(userInputCess.subtract(gstinCess));
		dto3B.setDiffCgst(userInputCgst.subtract(gstinCgst));
		dto3B.setDiffSgst(userInputSgst.subtract(gstinSgst));
		dto3B.setDiffIgst(userInputIgst.subtract(gstinIgst));
		dtoList.add(dto3B);
	}

	public Gstr3bReviewHeaderSummaryRequestDto convertProcessDocsAsDtos(
			String gstin, String entityName, String entityPan,
			List<Gstr3BEntityDashboardDto> processSumItemDtos) {
		Gstr3bReviewHeaderSummaryRequestDto requestDto = new Gstr3bReviewHeaderSummaryRequestDto();

		List<Gstr3bProcessAndReviewSummaryDto> processAndRevSumList = new ArrayList<>();
		if (processSumItemDtos != null && !processSumItemDtos.isEmpty()) {
			Pair<String, String> authTokenActive = authTokenActive(gstin);
			String status = authTokenActive.getValue0();
			String msg = authTokenActive.getValue1();
			processSumItemDtos.forEach(processSumItemDto -> {
				if (processSumItemDto.getGstin() != null
						&& processSumItemDto.getTaxPeriod() != null) {
					Gstr3bProcessAndReviewSummaryDto reqSumDto = new Gstr3bProcessAndReviewSummaryDto();
					String taxPeriod = processSumItemDto.getTaxPeriod();
					if (entityName != null) {
						reqSumDto.setEntity(StringUtils.upperCase(entityName));
					}
					reqSumDto.setEntityPan(entityPan);
					reqSumDto.setGstinNum(gstin);
					reqSumDto.setReturnPeriod(processSumItemDto.getTaxPeriod());

					reqSumDto.setProcessSummary(
							processSumConvert(processSumItemDto));

					reqSumDto.setReviewSummary(
							reviewSummeryConvert(gstin, taxPeriod, entityName));
					reqSumDto.setPopUp32(popUpSummery32(taxPeriod, taxPeriod));
					reqSumDto.setPopUp5(popUpSummery5(gstin, taxPeriod));
					reqSumDto.setTabLib(
							popUpSummeryTagLib(gstin, taxPeriod, status, msg));
					reqSumDto.setDiff(diff(reqSumDto.getReviewSummary()));
					processAndRevSumList.add(reqSumDto);
				}
			});
		}
		requestDto.setProcessAndReviewSummary(processAndRevSumList);
		return requestDto;
	}

	private static Gstr3bDiffLibpopUpReviewSummaryDto diff(
			Gstr3bReverseIntgReviewSummaryDto reviewSummary) {
		Gstr3bDiffLibpopUpReviewSummaryDto diff = new Gstr3bDiffLibpopUpReviewSummaryDto();
		List<Gstr3bReviewSummaryItemDto> items = reviewSummary.getItems();
		List<Gstr3bDifflibPopUpSummaryItemDto> dtoList = new ArrayList<>();
		for (Gstr3bReviewSummaryItemDto item : items) {
			Gstr3bDifflibPopUpSummaryItemDto dto = new Gstr3bDifflibPopUpSummaryItemDto();
			dto.setTableName(item.getTableName());
			dto.setTypeOfSupp(item.getTypeOfSupp());
			dto.setDiffTaxbleVal(item.getDiffTaxbleVal());
			dto.setDiffIgst(item.getDiffIgst());
			dto.setDiffCgst(item.getDiffCgst());
			dto.setDiffSgst(item.getDiffSgst());
			dto.setDiffCess(item.getDiffCess());
			dtoList.add(dto);
		}
		diff.setItems(dtoList);
		return diff;
	}

	public Gstr3bProcessSummaryDto processSumConvert(
			Gstr3BEntityDashboardDto reqdo) {
		Gstr3bProcessSummaryDto processDto = new Gstr3bProcessSummaryDto();
		List<Gstr3bProcessSummaryItemDto> dtoList = new ArrayList<>();
		Gstr3bProcessSummaryItemDto dto = new Gstr3bProcessSummaryItemDto();
		dto.setSaveStatus(reqdo.getSavedStatus());
		dto.setTimeStamp(reqdo.getLastUpdatedOn());
		dto.setTotalItc(reqdo.getTotalItc());
		dto.setTotalLiab(reqdo.getTotalLiability());
		dtoList.add(dto);
		processDto.setItems(dtoList);
		return processDto;

	}

	private static ImmutableMap<String, String> LiabilitySetDesc() {

		ImmutableMap<String, String> map = ImmutableMap
				.<String, String>builder().put("tx", "Tax")
				.put("intr", "Interest").put("fee", "Late fee")

				.build();
		return map;
	}

	private static BigDecimal negativeValueConsiderAsZero(BigDecimal value) {

		if (value.compareTo(BigDecimal.ZERO) < 1) {
			return BigDecimal.ZERO;
		}
		return value;
	}

	private Pair<String, String> authTokenActive(String gstin) {
		String authToken = "Active";
		String msg = null;
		if (!authTokenService.getAuthTokenStatusForGstin(gstin)
				.equalsIgnoreCase("A")) {
			authToken = "InActive";
			msg = String.format(
					"Warning - Auth token is inactive for GSTIN %s"
							+ ",  hence data displayed is as per "
							+ "DigiGST and not as per GSTN. To fetch data "
							+ "from GSTN, please activate auth token : ",
					gstin);
			LOGGER.error(msg);
		}

		return new Pair<>(authToken, msg);

	}
}
