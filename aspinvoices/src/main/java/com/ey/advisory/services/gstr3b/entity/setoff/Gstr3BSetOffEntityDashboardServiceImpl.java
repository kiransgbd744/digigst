package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveLiabilitySetOffStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository;
import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffEntity;
import com.ey.advisory.app.gstr3b.Gstr3BSaveLiabilitySetOffStatusEntity;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */
@Slf4j
@Component("Gstr3BSetOffEntityDashboardServiceImpl")
public class Gstr3BSetOffEntityDashboardServiceImpl
		implements Gstr3BSetOffEntityDashboardService {

	@Autowired
	EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
	Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository balanceRepository;

	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	Gstr3BSaveChangesLiabilitySetOffRepository paidItcRepository;

	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	Gstr3BGstnSaveToAspRepository liabilityRevChargeRepo;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeDetailsRepository")
	Gstr3BSetOffEntityComputeDetailsRepository computeDetailsRepository;

	@Autowired
	@Qualifier("Gstr3BSetOffEntityComputeConfigRepository")
	Gstr3BSetOffEntityComputeConfigRepository computeStatusRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("Gstr3BSaveLiabilitySetOffStatusRepository")
	private Gstr3BSaveLiabilitySetOffStatusRepository liabilitySetOffStatus;

	@Override
	public List<Gstr3bSetOffEntityDashboardRespDto> getStatusData(
			List<String> gstinsList, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR3B Set Off Entity Dashboard,"
					+ "Fetching state names for gstins %s", gstinsList);
			LOGGER.debug(msg);
		}

		Map<String, String> stateNames = entityService
				.getStateNames(gstinsList);

		Map<String, String> statusMap = new HashMap<>();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"In GSTR3B Set Off Entity Dashboard,"
							+ "state names for gstins %s are %s",
					gstinsList, stateNames);
			LOGGER.debug(msg);
		}

		// Liability Status and time
		List<Gstr3BSaveLiabilitySetOffStatusEntity> liabilityStatusData = liabilitySetOffStatus
				.findByGstinInAndTaxPeriod(gstinsList, taxPeriod);

		List<Gstr3BSaveLiabilitySetOffStatusDto> resp6 = liabilityStatusData
				.stream().map(o -> convertToDto7(o))
				.collect(Collectors.toCollection(ArrayList::new));

		for (Gstr3BSaveLiabilitySetOffStatusDto dto : resp6) {

			statusMap.put(dto.getGstin(),
					dto.getLiabilitySetoffStatus() != null
							? dto.getLiabilitySetoffStatus()
							: "NOT_SAVED");

		}

		// Get Auth Token Status
		Map<String, String> authTokenStatus = authTokenService
				.getAuthTokenStatusForGstins(gstinsList);

		// Paid Through ITC
		List<Gstr3BSaveChangesLiabilitySetOffEntity> paidThroughItcData = paidItcRepository
				.findByGstinInAndTaxPeriodAndIsActive(gstinsList, taxPeriod,
						true);

		List<Gstr3bSetOffEntityPditcDto> resp2 = paidThroughItcData.stream()
				.map(o -> convertToDto2(o))
				.collect(Collectors.toCollection(ArrayList::new));

		// Liability Other than Reverse Charge
		List<Gstr3bGstnSaveToAspEntity> liabilityRevChargeData = liabilityRevChargeRepo
				.findByGstinInAndTaxPeriodAndIsActiveAndSectionName(gstinsList,
						taxPeriod);
		Gstr3bSetOffEntityLiabilityDto get3_1bData = get3_1bData(
				liabilityRevChargeData);

		Gstr3bSetOffEntityLiabilityDto get3_1_1aData = get3_1_1aData(
				liabilityRevChargeData);

		Gstr3bSetOffEntityLiabilityDto get4c_Data = get4c_Data(
				liabilityRevChargeData);

		List<Gstr3bSetOffEntityLiabilityDto> resp3 = liabilityRevChargeData
				.stream()
				.map(o -> convertToDto3(o, get3_1bData, get3_1_1aData,
						get4c_Data))
				.collect(Collectors.toCollection(ArrayList::new));

		String msg4 = String.format("Inside Entity dashboard 3B setoff  "
				+ "before calling ledgerCashBalData db resp %s gstin : ",
				gstinsList);
		LOGGER.debug(msg4);

		// Cash and Credit Ledger
		List<Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity> ledgerCashBalData = balanceRepository
				.findByGstinInAndTaxPeriodAndIsActive(gstinsList, taxPeriod,
						true);

		String msg = String.format(
				"Inside Entity dashboard 3B setoff  "
						+ "after calling ledgerCashBalData db resp %s : ",
				ledgerCashBalData);
		LOGGER.debug(msg);
		List<Gstr3bSetOffEntityLedgerDto> resp1 = new ArrayList<>();
		if (ledgerCashBalData == null || ledgerCashBalData.isEmpty()) {
			Gstr3bSetOffEntityLedgerDto respDto = new Gstr3bSetOffEntityLedgerDto();
			resp3.forEach(e -> {
				if (e.getSectionName().equals("4(c)")) {
					respDto.setCreditLedgerBalanceIgst(
							e.getLiabilityIgst().compareTo(BigDecimal.ZERO) > 0
									? e.getLiabilityIgst()
									: BigDecimal.ZERO);
					respDto.setCreditLedgerBalanceCgst(
							e.getLiabilityCgst().compareTo(BigDecimal.ZERO) > 0
									? e.getLiabilityCgst()
									: BigDecimal.ZERO);
					respDto.setCreditLedgerBalanceSgst(
							e.getLiabilitySgst().compareTo(BigDecimal.ZERO) > 0
									? e.getLiabilitySgst()
									: BigDecimal.ZERO);
					respDto.setCreditLedgerBalanceCess(
							e.getLiabilityCess().compareTo(BigDecimal.ZERO) > 0
									? e.getLiabilityCess()
									: BigDecimal.ZERO);
					respDto.setGstin(e.getGstin());
					respDto.setLedgerTimestamp(
							EYDateUtil.toISTDateTimeFromUTC(e.getCreateDate()));
				}
			});
			resp1.add(respDto);
		} else {
			resp1 = ledgerCashBalData.stream().map(o -> convertToDto1(o, resp3))
					.collect(Collectors.toCollection(ArrayList::new));
			String msg2 = String.format("Inside Entity dashboard 3B setoff  "
					+ "resp1  in else block %s : ", resp1);
			LOGGER.debug(msg2);
		}
		// Net Liability, Liability(Rev), Int & fee, Net GST Lia, Addnl Cash
		List<Gstr3bSetOffComputeDetailsRespDto> rsp = new ArrayList<>();

		for (String gstin : gstinsList) {

			List<Gstr3BSetOffEntityComputeDetailsEntity> liabilityData = computeDetailsRepository
					.findByGstinAndTaxPeriodAndIsDelete(gstin, taxPeriod,
							false);

			Gstr3bSetOffComputeDetailsRespDto libDataDto = liabilityData
					.stream().map(o -> convertToDto4(o, statusMap))
					.collect(Collectors.reducing(
							new Gstr3bSetOffComputeDetailsRespDto(),
							(acc, ele) -> acc.merge(ele, gstin)));
			rsp.add(libDataDto);

		}

		List<Gstr3bSetOffComputeDetailsRespDto> resp4 = rsp.stream()
				.map(o -> convertToDto6(o, resp3))
				.collect(Collectors.toCollection(ArrayList::new));

		// Compute Status and time
		List<Gstr3BSetOffEntityComputeConfigEntity> computeStatusData = computeStatusRepository
				.findByGstinInAndTaxPeriodAndIsActive(gstinsList, taxPeriod,
						true);

		List<Gstr3bSetOffEntityComputeStatusDto> resp5 = computeStatusData
				.stream().map(o -> convertToDto5(o))
				.collect(Collectors.toCollection(ArrayList::new));

		// Iterating responses and adding to map
		Map<String, Gstr3bSetOffEntityLedgerDto> map1 = new HashMap<>();
		for (int i = 0; i < resp1.size(); i++) {
			if (isIndexOutOfBounds3(resp1, i)) {
				String key = resp1.get(i).getGstin() + taxPeriod;
				map1.put(key, resp1.get(i));
			}
		}

		Map<String, Gstr3bSetOffEntityPditcDto> map2 = new HashMap<>();
		for (int i = 0; i < resp2.size(); i++) {
			if (isIndexOutOfBounds4(resp2, i)) {
				String key = resp2.get(i).getGstin() + taxPeriod;
				map2.put(key, resp2.get(i));
			}
		}

		Map<String, Gstr3bSetOffEntityLiabilityDto> map3 = new HashMap<>();
		for (int i = 0; i < resp3.size(); i++) {
			if (isIndexOutOfBounds2(resp3, i)
					&& resp3.get(i).getSectionName().equals("3.1(a)")) {
				String key = resp3.get(i).getGstin() + taxPeriod;
				map3.put(key, resp3.get(i));
			}
		}

		Map<String, Gstr3bSetOffComputeDetailsRespDto> map4 = new HashMap<>();
		for (int i = 0; i < resp4.size(); i++) {
			if (isIndexOutOfBounds1(resp4, i)) {
				String key = resp4.get(i).getGstin() + taxPeriod;
				map4.put(key, resp4.get(i));
			}
		}
		Map<String, Gstr3bSetOffEntityComputeStatusDto> map5 = new HashMap<>();
		for (int i = 0; i < resp5.size(); i++) {
			if (isIndexOutOfBounds5(resp5, i)) {
				String key = resp5.get(i).getGstin() + taxPeriod;
				map5.put(key, resp5.get(i));
			}
		}

		Map<String, Gstr3BSaveLiabilitySetOffStatusDto> map6 = new HashMap<>();
		for (int i = 0; i < resp6.size(); i++) {
			if (isIndexOutOfBounds6(resp6, i)) {
				String key = resp6.get(i).getGstin() + taxPeriod;
				map6.put(key, resp6.get(i));
			}
		}

		// Adding data to List
		List<Gstr3bSetOffEntityDashboardRespDto> respListDto = new ArrayList<>();
		gstinsList.forEach(gstin -> {
			Gstr3bSetOffEntityDashboardRespDto respDto = new Gstr3bSetOffEntityDashboardRespDto();
			String key = gstin + taxPeriod;
			respDto.setGstin(gstin);
			respDto.setAuthStatus(authTokenStatus.get(gstin));
			respDto.setStateName(stateNames.get(gstin));
			GSTNDetailEntity gstinInfo = gstinInfoRepository
					.findRegDates(gstin);
			String registrationType = gstinInfo != null
					? gstinInfo.getRegistrationType()
					: "";
			respDto.setRegtype(registrationType);
			if (map1.containsKey(key)) {
				String msg3 = String.format(
						"Inside Entity dashboard 3B setoff  "
								+ "respDto map1  %s , %s map1 : ",
						respDto, map1);
				LOGGER.debug(msg3);
				respDto.setLedgerTimestamp(map1.get(key).getLedgerTimestamp());
				respDto.setCashLedgerBalanceIgst(
						map1.get(key).getCashLedgerBalanceIgst());
				respDto.setCashLedgerBalanceCgst(
						map1.get(key).getCashLedgerBalanceCgst());
				respDto.setCashLedgerBalanceSgst(
						map1.get(key).getCashLedgerBalanceSgst());
				respDto.setCashLedgerBalanceCess(
						map1.get(key).getCashLedgerBalanceCess());
				respDto.setCashLedgerBalanceInterest(
						map1.get(key).getCashLedgerBalanceInterest());
				respDto.setCashLedgerBalanceLateFee(
						map1.get(key).getCashLedgerBalanceLateFee());
				respDto.setCreditLedgerBalanceIgst(
						map1.get(key).getCreditLedgerBalanceIgst());
				respDto.setCreditLedgerBalanceCgst(
						map1.get(key).getCreditLedgerBalanceCgst());
				respDto.setCreditLedgerBalanceSgst(
						map1.get(key).getCreditLedgerBalanceSgst());
				respDto.setCreditLedgerBalanceCess(
						map1.get(key).getCreditLedgerBalanceCess());
				String msg1 = String
						.format("Inside Entity dashboard 3B setoff  "
								+ "respDto map1  %s : ", respDto);
				LOGGER.debug(msg1);

			}
			if (map3.containsKey(key)) {
				respDto.setLiabilityIgst(map3.get(key).getLiabilityIgst());
				respDto.setLiabilityCgst(map3.get(key).getLiabilityCgst());
				respDto.setLiabilitySgst(map3.get(key).getLiabilitySgst());
				respDto.setLiabilityCess(map3.get(key).getLiabilityCess());

			}
			if (map2.containsKey(key)) {

				respDto.setPaidThroughItcIgst(
						map2.get(key).getPaidThroughItcIgst());
				respDto.setPaidThroughItcCgst(
						map2.get(key).getPaidThroughItcCgst());
				respDto.setPaidThroughItcSgst(
						map2.get(key).getPaidThroughItcSgst());
				respDto.setPaidThroughItcCess(
						map2.get(key).getPaidThroughItcCess());
			}

			if (map4.containsKey(key)) {
				respDto.setNetLiabilityIgst(
						map4.get(key).getNetLiabilityIgst());
				respDto.setNetLiabilityCgst(
						map4.get(key).getNetLiabilityCgst());
				respDto.setNetLiabilitySgst(
						map4.get(key).getNetLiabilitySgst());
				respDto.setNetLiabilityCess(
						map4.get(key).getNetLiabilityCess());
				respDto.setLiabilityReverseChargeIgst(
						map4.get(key).getLiabilityReverseChargeIgst());
				respDto.setLiabilityReverseChargeCgst(
						map4.get(key).getLiabilityReverseChargeCgst());
				respDto.setLiabilityReverseChargeSgst(
						map4.get(key).getLiabilityReverseChargeSgst());
				respDto.setLiabilityReverseChargeCess(
						map4.get(key).getLiabilityReverseChargeCess());
				respDto.setInterestAndLateFeeIgst(
						map4.get(key).getInterestAndLateFeeIgst());
				respDto.setInterestAndLateFeeCgst(
						map4.get(key).getInterestAndLateFeeCgst());
				respDto.setInterestAndLateFeeSgst(
						map4.get(key).getInterestAndLateFeeSgst());
				respDto.setInterestAndLateFeeCess(
						map4.get(key).getInterestAndLateFeeCess());
				respDto.setNetGstLiabilityIgst(
						map4.get(key).getNetGstLiabilityIgst());
				respDto.setNetGstLiabilityCgst(
						map4.get(key).getNetGstLiabilityCgst());
				respDto.setNetGstLiabilitySgst(
						map4.get(key).getNetGstLiabilitySgst());
				respDto.setNetGstLiabilityCess(
						map4.get(key).getNetGstLiabilityCess());
				respDto.setAddnlCashReqIgst(
						map4.get(key).getAddnlCashReqIgst());
				respDto.setAddnlCashReqCgst(
						map4.get(key).getAddnlCashReqCgst());
				respDto.setAddnlCashReqSgst(
						map4.get(key).getAddnlCashReqSgst());
				respDto.setAddnlCashReqCess(
						map4.get(key).getAddnlCashReqCess());
			}
			if (map5.containsKey(key)) {
				respDto.setComputeSetoffTimeStamp(
						map5.get(key).getComputeSetoffTimeStamp());
				respDto.setComputeSetoffStatus(
						map5.get(key).getComputeSetoffStatus());
			}

			if (map6.containsKey(key)) {
				respDto.setLiabilitySetoffTimeStamp(
						map6.get(key).getLiabilitySetoffTimeStamp());
				respDto.setLiabilitySetoffStatus(
						map6.get(key).getLiabilitySetoffStatus());
			} else {
				respDto.setLiabilitySetoffStatus("Not Initiated");
			}
			respListDto.add(respDto);
		});

		return respListDto;
	}

	// Converting to DTO
	//// Net Liability, Liability(Rev), Int & fee, Net GST Lia, Addnl Cash
	private Gstr3bSetOffComputeDetailsRespDto convertToDto6(
			Gstr3bSetOffComputeDetailsRespDto entity,
			List<Gstr3bSetOffEntityLiabilityDto> resp3) {

		Gstr3bSetOffComputeDetailsRespDto respDto = new Gstr3bSetOffComputeDetailsRespDto();
		respDto.setGstin(entity.getGstin());
		respDto.setNetLiabilityIgst(entity.getNetLiabilityIgst());
		respDto.setNetLiabilityCgst(entity.getNetLiabilityCgst());
		respDto.setNetLiabilitySgst(entity.getNetLiabilitySgst());
		respDto.setNetLiabilityCess(entity.getNetLiabilityCess());
		resp3.forEach(e -> {
			if (e.getGstin().equals(entity.getGstin())
					&& e.getSectionName().equals("3.1(d)")) {
				respDto.setLiabilityReverseChargeIgst(e.getLiabilityIgst());
				respDto.setLiabilityReverseChargeCgst(e.getLiabilityCgst());
				respDto.setLiabilityReverseChargeSgst(e.getLiabilitySgst());
				respDto.setLiabilityReverseChargeCess(e.getLiabilityCess());
			}
		});
		respDto.setInterestAndLateFeeIgst(entity.getInterestAndLateFeeIgst());
		respDto.setInterestAndLateFeeCgst(entity.getInterestAndLateFeeCgst());
		respDto.setInterestAndLateFeeSgst(entity.getInterestAndLateFeeSgst());
		respDto.setInterestAndLateFeeCess(entity.getInterestAndLateFeeCess());
		respDto.setAddnlCashReqIgst(entity.getAddnlCashReqIgst());
		respDto.setAddnlCashReqCgst(entity.getAddnlCashReqCgst());
		respDto.setAddnlCashReqSgst(entity.getAddnlCashReqSgst());
		respDto.setAddnlCashReqCess(entity.getAddnlCashReqCess());
		respDto.setNetGstLiabilityIgst(entity.getNetGstLiabilityIgst());
		respDto.setNetGstLiabilityCgst(entity.getNetGstLiabilityCgst());
		respDto.setNetGstLiabilitySgst(entity.getNetGstLiabilitySgst());
		respDto.setNetGstLiabilityCess(entity.getNetGstLiabilityCess());
		return respDto;
	}

	// Cash and Credit Ledger
	private Gstr3bSetOffEntityLedgerDto convertToDto1(
			Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity entity,
			List<Gstr3bSetOffEntityLiabilityDto> resp3) {

		Gstr3bSetOffEntityLedgerDto respDto = new Gstr3bSetOffEntityLedgerDto();
		if (entity != null) {
			respDto.setGstin(entity.getGstin());
			respDto.setLedgerTimestamp(
					EYDateUtil.toISTDateTimeFromUTC(entity.getUpdatedOn()));
			respDto.setCashLedgerBalanceIgst(entity.getTaxIgst());
			respDto.setCashLedgerBalanceCgst(entity.getTaxCgst());
			respDto.setCashLedgerBalanceSgst(entity.getTaxSgst());
			respDto.setCashLedgerBalanceCess(entity.getTaxCess());
			BigDecimal interest = entity.getInterestIgst()
					.add(entity.getInterestCgst()).add(entity.getInterestSgst())
					.add(entity.getInterestCess());
			respDto.setCashLedgerBalanceInterest(interest);
			BigDecimal fee = entity.getFeeIgst().add(entity.getFeeCgst())
					.add(entity.getFeeSgst().add(entity.getFeeCess()));
			respDto.setCashLedgerBalanceLateFee(fee);
			respDto.setCreditLedgerBalanceIgst(entity.getItcBalanceIgst());
			respDto.setCreditLedgerBalanceCgst(entity.getItcBalanceCgst());
			respDto.setCreditLedgerBalanceSgst(entity.getItcBalanceSgst());
			respDto.setCreditLedgerBalanceCess(entity.getItcBalanceCess());
			String msg = String.format(
					"Inside Entity dashboard 3B setoff  "
							+ "convertToDto1 before forech  respDto %s : ",
					respDto);
			LOGGER.debug(msg);
			resp3.forEach(e -> {
				if (e.getGstin().equals(entity.getGstin())
						&& e.getSectionName().equals("4(c)")) {
					respDto.setCreditLedgerBalanceIgst(entity
							.getItcBalanceIgst()
							.add(e.getLiabilityIgst() != null
									&& e.getLiabilityIgst()
											.compareTo(BigDecimal.ZERO) > 0
													? e.getLiabilityIgst()
													: BigDecimal.ZERO));
					respDto.setCreditLedgerBalanceCgst(entity
							.getItcBalanceCgst()
							.add(e.getLiabilityCgst() != null
									&& e.getLiabilityCgst()
											.compareTo(BigDecimal.ZERO) > 0
													? e.getLiabilityCgst()
													: BigDecimal.ZERO));
					respDto.setCreditLedgerBalanceSgst(entity
							.getItcBalanceSgst()
							.add(e.getLiabilitySgst() != null
									&& e.getLiabilitySgst()
											.compareTo(BigDecimal.ZERO) > 0
													? e.getLiabilitySgst()
													: BigDecimal.ZERO));
					respDto.setCreditLedgerBalanceCess(entity
							.getItcBalanceCess()
							.add(e.getLiabilityCess() != null
									&& e.getLiabilityCess()
											.compareTo(BigDecimal.ZERO) > 0
													? e.getLiabilityCess()
													: BigDecimal.ZERO));
				}
			});
		}
		String msg = String.format("Inside Entity dashboard 3B setoff  "
				+ "convertToDto1 after forech  respDto %s : ", respDto);
		LOGGER.error(msg);
		return respDto;
	}

	// Paid Through ITC
	private Gstr3bSetOffEntityPditcDto convertToDto2(
			Gstr3BSaveChangesLiabilitySetOffEntity entity) {
		Gstr3bSetOffEntityPditcDto respDto = new Gstr3bSetOffEntityPditcDto();
		respDto.setGstin(entity.getGstin());

		BigDecimal ipdIgst = entity.getIPDIgst() != null ? entity.getIPDIgst()
				: BigDecimal.ZERO;
		BigDecimal cpdIgst = entity.getCPDIgst() != null ? entity.getCPDIgst()
				: BigDecimal.ZERO;
		BigDecimal spdIgst = entity.getSPDIgst() != null ? entity.getSPDIgst()
				: BigDecimal.ZERO;
		BigDecimal igst = ipdIgst.add(cpdIgst).add(spdIgst);
		respDto.setPaidThroughItcIgst(igst);

		BigDecimal ipdCgst = entity.getIPDCgst() != null ? entity.getIPDCgst()
				: BigDecimal.ZERO;
		BigDecimal cpdCgst = entity.getCPDCgst() != null ? entity.getCPDCgst()
				: BigDecimal.ZERO;
		BigDecimal cgst = ipdCgst.add(cpdCgst);
		respDto.setPaidThroughItcCgst(cgst);

		BigDecimal ipdSgst = entity.getIPDSgst() != null ? entity.getIPDSgst()
				: BigDecimal.ZERO;
		BigDecimal spdSgst = entity.getSPDSgst() != null ? entity.getSPDSgst()
				: BigDecimal.ZERO;
		BigDecimal sgst = ipdSgst.add(spdSgst);
		respDto.setPaidThroughItcSgst(sgst);

		BigDecimal csPdCess = entity.getCsPdCess() != null
				? entity.getCsPdCess()
				: BigDecimal.ZERO;
		respDto.setPaidThroughItcCess(csPdCess);

		return respDto;
	}

	// Liability Other than reverse charge
	private Gstr3bSetOffEntityLiabilityDto convertToDto3(
			Gstr3bGstnSaveToAspEntity entity,
			Gstr3bSetOffEntityLiabilityDto get3_1bData,
			Gstr3bSetOffEntityLiabilityDto get3_1_1aData,
			Gstr3bSetOffEntityLiabilityDto get4c_Data) {

		BigDecimal igstB = get3_1bData.getLiabilityIgst();
		BigDecimal cgstB = get3_1bData.getLiabilityCgst();
		BigDecimal sgstB = get3_1bData.getLiabilitySgst();
		BigDecimal cessB = get3_1bData.getLiabilityCess();

		BigDecimal ecomIgst = get3_1_1aData.getLiabilityIgst();
		BigDecimal ecomCgst = get3_1_1aData.getLiabilityCgst();
		BigDecimal ecomSgst = get3_1_1aData.getLiabilitySgst();
		BigDecimal ecomCess = get3_1_1aData.getLiabilityCess();

		Gstr3bSetOffEntityLiabilityDto respDto = new Gstr3bSetOffEntityLiabilityDto();

		if (entity.getSectionName().equals("3.1(a)")) {
			respDto.setGstin(entity.getGstin());
			respDto.setLiabilityIgst(entity.getIgst().add(igstB)
					.add(get4c_Data.getLiabilityIgst()));
			respDto.setLiabilityCgst(entity.getCgst().add(cgstB)
					.add(get4c_Data.getLiabilityCgst()));
			respDto.setLiabilitySgst(entity.getSgst().add(sgstB)
					.add(get4c_Data.getLiabilitySgst()));
			respDto.setLiabilityCess(entity.getCess().add(cessB)
					.add(get4c_Data.getLiabilityCess()));
			respDto.setSectionName(entity.getSectionName());
		} else if (entity.getSectionName().equals("3.1(d)")) {
			respDto.setGstin(entity.getGstin());
			respDto.setLiabilityIgst(entity.getIgst().add(ecomIgst));
			respDto.setLiabilityCgst(entity.getCgst().add(ecomCgst));
			respDto.setLiabilitySgst(entity.getSgst().add(ecomSgst));
			respDto.setLiabilityCess(entity.getCess().add(ecomCess));
			respDto.setSectionName(entity.getSectionName());
		} else if (entity.getSectionName().equals("4(c)")) {
			respDto.setGstin(entity.getGstin());
			respDto.setLiabilityIgst(entity.getIgst());
			respDto.setLiabilityCgst(entity.getCgst());
			respDto.setLiabilitySgst(entity.getSgst());
			respDto.setLiabilityCess(entity.getCess());
			respDto.setSectionName(entity.getSectionName());
		} else if (entity.getSectionName().equals("3.1(b)")) {
			respDto.setGstin(entity.getGstin());
			respDto.setLiabilityIgst(entity.getIgst());
			respDto.setLiabilityCgst(entity.getCgst());
			respDto.setLiabilitySgst(entity.getSgst());
			respDto.setLiabilityCess(entity.getCess());
			respDto.setSectionName(entity.getSectionName());
		} else if (entity.getSectionName()
				.equals(Gstr3BConstants.Table3_1_1_A)) {
			respDto.setGstin(entity.getGstin());
			respDto.setLiabilityIgst(entity.getIgst());
			respDto.setLiabilityCgst(entity.getCgst());
			respDto.setLiabilitySgst(entity.getSgst());
			respDto.setLiabilityCess(entity.getCess());
			respDto.setSectionName(entity.getSectionName());
		}

		return respDto;
	}

	//// Net Liability, Liability(Rev), Int & fee, Net GST Lia, Addnl Cash
	private Gstr3bSetOffComputeDetailsRespDto convertToDto4(
			Gstr3BSetOffEntityComputeDetailsEntity entity,
			Map<String, String> statusMap) {

		Gstr3bSetOffComputeDetailsRespDto respDto = new Gstr3bSetOffComputeDetailsRespDto();
		respDto.setGstin(entity.getGstin());
		String saveStatus = statusMap.get(respDto.getGstin()) != null
				? statusMap.get(respDto.getGstin())
				: "NOT_SAVED";
		if (entity.getSection().equals("NET_LIABILITY")) {
			respDto.setNetLiabilityIgst(entity.getIgst());
			respDto.setNetLiabilityCgst(entity.getCgst());
			respDto.setNetLiabilitySgst(entity.getSgst());
			respDto.setNetLiabilityCess(entity.getCess());
		} else if (entity.getSection().equals("INTEREST_LATE_FEE")) {
			respDto.setInterestAndLateFeeIgst(entity.getIgst());
			respDto.setInterestAndLateFeeCgst(entity.getCgst());
			respDto.setInterestAndLateFeeSgst(entity.getSgst());
			respDto.setInterestAndLateFeeCess(entity.getCess());
		} else if (entity.getSection().equals("ADDNL_CASH_REQ")) {
			if (saveStatus.equalsIgnoreCase("SAVED")) {
				respDto.setAddnlCashReqIgst(BigDecimal.ZERO);
				respDto.setAddnlCashReqCgst(BigDecimal.ZERO);
				respDto.setAddnlCashReqSgst(BigDecimal.ZERO);
				respDto.setAddnlCashReqCess(BigDecimal.ZERO);
			} else {
				respDto.setAddnlCashReqIgst(entity.getIgst());
				respDto.setAddnlCashReqCgst(entity.getCgst());
				respDto.setAddnlCashReqSgst(entity.getSgst());
				respDto.setAddnlCashReqCess(entity.getCess());
			}
		} else if (entity.getSection().equals("NET_GST_LIABILITY")) {
			respDto.setNetGstLiabilityIgst(entity.getIgst());
			respDto.setNetGstLiabilityCgst(entity.getCgst());
			respDto.setNetGstLiabilitySgst(entity.getSgst());
			respDto.setNetGstLiabilityCess(entity.getCess());
		}
		return respDto;
	}

	// Compute Status
	private Gstr3bSetOffEntityComputeStatusDto convertToDto5(
			Gstr3BSetOffEntityComputeConfigEntity entity) {

		Gstr3bSetOffEntityComputeStatusDto respDto = new Gstr3bSetOffEntityComputeStatusDto();
		respDto.setGstin(entity.getGstin());
		respDto.setComputeSetoffTimeStamp(
				EYDateUtil.toISTDateTimeFromUTC(entity.getUpdatedOn()));
		respDto.setComputeSetoffStatus(entity.getComputeStatus());

		return respDto;
	}

	// Liability Status
	private Gstr3BSaveLiabilitySetOffStatusDto convertToDto7(
			Gstr3BSaveLiabilitySetOffStatusEntity entity) {

		Gstr3BSaveLiabilitySetOffStatusDto respDto = new Gstr3BSaveLiabilitySetOffStatusDto();
		respDto.setGstin(entity.getGstin());
		respDto.setLiabilitySetoffTimeStamp(
				EYDateUtil.toISTDateTimeFromUTC(entity.getUpdatedOn()));
		respDto.setLiabilitySetoffStatus(entity.getStatus());

		return respDto;
	}

	// Out of bound Exception Check
	private static boolean isIndexOutOfBounds1(
			final List<Gstr3bSetOffComputeDetailsRespDto> response, int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean isIndexOutOfBounds2(
			final List<Gstr3bSetOffEntityLiabilityDto> response, int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean isIndexOutOfBounds3(
			final List<Gstr3bSetOffEntityLedgerDto> response, int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean isIndexOutOfBounds4(
			final List<Gstr3bSetOffEntityPditcDto> response, int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	private static boolean isIndexOutOfBounds5(
			final List<Gstr3bSetOffEntityComputeStatusDto> response,
			int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	//
	private static boolean isIndexOutOfBounds6(
			final List<Gstr3BSaveLiabilitySetOffStatusDto> response,
			int index) {
		if (index < 0 || index > response.size()) {
			return false;
		} else {
			return true;
		}
	}

	private Gstr3bSetOffEntityLiabilityDto get3_1bData(
			List<Gstr3bGstnSaveToAspEntity> entityList) {

		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;

		Gstr3bSetOffEntityLiabilityDto dto = new Gstr3bSetOffEntityLiabilityDto();

		for (Gstr3bGstnSaveToAspEntity entity : entityList) {

			if (entity != null
					&& entity.getSectionName().equalsIgnoreCase("3.1(b)")) {

				igstB = entity.getIgst() != null ? entity.getIgst()
						: BigDecimal.ZERO;
				cgstB = entity.getCgst() != null ? entity.getCgst()
						: BigDecimal.ZERO;
				sgstB = entity.getSgst() != null ? entity.getSgst()
						: BigDecimal.ZERO;
				cessB = entity.getCess() != null ? entity.getCess()
						: BigDecimal.ZERO;
			}
		}
		dto.setLiabilityIgst(igstB);
		dto.setLiabilityCgst(cgstB);
		dto.setLiabilitySgst(sgstB);
		dto.setLiabilityCess(cessB);

		return dto;
	}

	private Gstr3bSetOffEntityLiabilityDto get3_1_1aData(
			List<Gstr3bGstnSaveToAspEntity> entityList) {

		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;

		Gstr3bSetOffEntityLiabilityDto dto = new Gstr3bSetOffEntityLiabilityDto();

		for (Gstr3bGstnSaveToAspEntity entity : entityList) {

			if (entity != null && entity.getSectionName()
					.equalsIgnoreCase(Gstr3BConstants.Table3_1_1_A)) {

				igstB = entity.getIgst() != null ? entity.getIgst()
						: BigDecimal.ZERO;
				cgstB = entity.getCgst() != null ? entity.getCgst()
						: BigDecimal.ZERO;
				sgstB = entity.getSgst() != null ? entity.getSgst()
						: BigDecimal.ZERO;
				cessB = entity.getCess() != null ? entity.getCess()
						: BigDecimal.ZERO;
				break;
			}
		}
		dto.setLiabilityIgst(igstB);
		dto.setLiabilityCgst(cgstB);
		dto.setLiabilitySgst(sgstB);
		dto.setLiabilityCess(cessB);

		return dto;
	}

	private Gstr3bSetOffEntityLiabilityDto get4c_Data(
			List<Gstr3bGstnSaveToAspEntity> entityList) {

		BigDecimal igst4c = BigDecimal.ZERO;
		BigDecimal cgst4c = BigDecimal.ZERO;
		BigDecimal sgst4c = BigDecimal.ZERO;
		BigDecimal cess4c = BigDecimal.ZERO;

		Gstr3bSetOffEntityLiabilityDto dto = new Gstr3bSetOffEntityLiabilityDto();

		for (Gstr3bGstnSaveToAspEntity entity : entityList) {

			if (entity != null
					&& entity.getSectionName().equalsIgnoreCase("4(c)")) {

				igst4c = entity.getIgst() != null
						&& entity.getIgst().compareTo(BigDecimal.ZERO) < 0
								? entity.getIgst().abs()
								: BigDecimal.ZERO;

				cgst4c = entity.getCgst() != null
						&& entity.getCgst().compareTo(BigDecimal.ZERO) < 0
								? entity.getCgst().abs()
								: BigDecimal.ZERO;

				sgst4c = entity.getSgst() != null
						&& entity.getSgst().compareTo(BigDecimal.ZERO) < 0
								? entity.getSgst().abs()
								: BigDecimal.ZERO;

				cess4c = entity.getCess() != null
						&& entity.getCess().compareTo(BigDecimal.ZERO) < 0
								? entity.getCess().abs()
								: BigDecimal.ZERO;
			}
		}
		dto.setLiabilityIgst(igst4c);
		dto.setLiabilityCgst(cgst4c);
		dto.setLiabilitySgst(sgst4c);
		dto.setLiabilityCess(cess4c);

		return dto;

	}
}
