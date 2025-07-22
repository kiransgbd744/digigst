package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aB2bInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aB2baInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aCdnInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aCdnaInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aISDInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aImpgInvoicesHeaderRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aImpgSezInvoicesHeaderRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionFinalDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionItemDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionLineItemDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionTotalDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("Get2AConsoForSectionServiceImpl")
@Slf4j
public class Get2AConsoForSectionServiceImpl
		implements Get2AConsoForSectionService {

	@Autowired
	@Qualifier("Get2AConsoForSectionDaoImpl")
	private Get2AConsoForSectionDaoImpl daoImpl;

	@Autowired
	@Qualifier("GetGstr2aB2bInvoicesHeaderRepository")
	private GetGstr2aB2bInvoicesHeaderRepository b2bRepository;

	@Autowired
	@Qualifier("GetGstr2aB2baInvoicesHeaderRepository")
	private GetGstr2aB2baInvoicesHeaderRepository b2baRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnInvoicesHeaderRepository")
	private GetGstr2aCdnInvoicesHeaderRepository cdnRepository;

	@Autowired
	@Qualifier("GetGstr2aCdnaInvoicesHeaderRepository")
	private GetGstr2aCdnaInvoicesHeaderRepository cdnaRepository;

	@Autowired
	@Qualifier("GetGstr2aISDInvoicesHeaderRepository")
	private GetGstr2aISDInvoicesHeaderRepository isdRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgInvoicesHeaderRepository")
	private GetGstr2aImpgInvoicesHeaderRepository impgRepository;

	@Autowired
	@Qualifier("GetGstr2aImpgSezInvoicesHeaderRepository")
	private GetGstr2aImpgSezInvoicesHeaderRepository impgSezRepository;

	@Autowired
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";
	private static final String ISD = "ISD";
	private static final String IMPG = "IMPG";
	private static final String IMPGSEZ = "IMPGSEZ";

	public List<Long> findErpBatchIds(final String gstin,
			final String retPeriod, final String section, Long batchId) {
		List<Long> erpBatchIds = new ArrayList<>();
		if (B2B.equalsIgnoreCase(section)) {
			erpBatchIds = b2bRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (B2BA.equalsIgnoreCase(section)) {
			erpBatchIds = b2baRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (CDN.equalsIgnoreCase(section)) {
			erpBatchIds = cdnRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (CDNA.equalsIgnoreCase(section)) {
			erpBatchIds = cdnaRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (ISD.equalsIgnoreCase(section)) {
			erpBatchIds = isdRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (IMPG.equalsIgnoreCase(section)) {
			erpBatchIds = impgRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		} else if (IMPGSEZ.equalsIgnoreCase(section)) {
			erpBatchIds = impgSezRepository.getErpBatchIds(gstin, retPeriod,
					batchId);
		}
		return erpBatchIds;

	}

	/*
	 * Get a Get GSTR 2A Information Based on Tin,tax period, Batch Id, ERP
	 * Batch Id for each section of different call.
	 * 
	 */
	public Get2AConsoForSectionFinalDto findERPGet2AConsoForSection(
			final String gstin, final String retPeriod, final String section,
			Long batchId, final Long erpBatchId) {

		Get2AConsoForSectionFinalDto finalDto = new Get2AConsoForSectionFinalDto();
		List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos = new ArrayList<>();
		List<Object[]> objs = daoImpl.findGet2AConsoForSection(gstin, retPeriod,
				section, batchId, erpBatchId);
		GSTNDetailEntity gstnDetailEntity = gSTNDetailRepository
				.findByGstinAndIsDeleteFalse(gstin);
		EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
				.findEntityByEntityId(gstnDetailEntity.getEntityId());
		String entityName = entityInfoEntity.getEntityName();
		String entityPan = entityInfoEntity.getPan();

		if (B2B.equalsIgnoreCase(section) && objs != null && !objs.isEmpty()) {
			mapObjToB2BSection(get2AConsoForSecDtos, objs, entityName,
					entityPan);

		} else if (B2BA.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToB2BASection(get2AConsoForSecDtos, objs, entityName,
					entityPan);

		} else if (CDN.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToCDNSection(get2AConsoForSecDtos, objs, entityName,
					entityPan);
		} else if (CDNA.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToCDNASection(get2AConsoForSecDtos, objs, entityName,
					entityPan);
		} else if (ISD.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToISDSection(get2AConsoForSecDtos, objs, entityName,
					entityPan);
		} else if (IMPG.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToIMPGSection(get2AConsoForSecDtos, objs, entityName,
					entityPan);
		} else if (IMPGSEZ.equalsIgnoreCase(section) && objs != null
				&& !objs.isEmpty()) {
			mapObjToIMPGSEZSection(get2AConsoForSecDtos, objs, entityName,
					entityPan);
		}
		finalDto.setGet2AConsSecDtos(get2AConsoForSecDtos);
		return finalDto;
	}

	private void mapObjToB2BSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(B2B);
			get2AConsoForSecDto
					.setSgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : null);
			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : null);
			get2AConsoForSecDto.setSuppInvNum(
					obj[5] != null ? String.valueOf(obj[5]) : null);

			get2AConsoForSecDto.setSuppInvDate(
					obj[6] != null ? String.valueOf(obj[6]) : null);
			get2AConsoForSecDto.setSuppInvVal(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			get2AConsoForSecDto
					.setPos(obj[8] != null ? String.valueOf(obj[8]) : null);
			get2AConsoForSecDto
					.setRchrg(obj[9] != null ? String.valueOf(obj[9]) : null);
			get2AConsoForSecDto.setInvType(
					obj[10] != null ? String.valueOf(obj[10]) : null);

			get2AConsoForSecDto.setDiffPercent(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setGetBatchId(
					obj[12] != null ? String.valueOf(obj[12]) : null);

			get2AConsoForSecDto.setIgstAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCessAmt(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setActionTaken(
					obj[18] != null ? String.valueOf(obj[18]) : null);
			get2AConsoForSecDto.setApiSection(
					obj[19] != null ? String.valueOf(obj[19]) : null);
			get2AConsoForSecDto.setDataCategory(
					obj[20] != null ? String.valueOf(obj[20]) : null);

			get2AConsoForSecDto.setCfsGstr3b(
					obj[22] != null ? String.valueOf(obj[22]) : null);
			get2AConsoForSecDto.setCancelDate(
					obj[23] != null ? String.valueOf(obj[23]) : null);

			get2AConsoForSecDto.setFileDate(
					obj[24] != null ? String.valueOf(obj[24]) : null);
			get2AConsoForSecDto.setFilePeriod(
					obj[25] != null ? String.valueOf(obj[25]) : null);

			get2AConsoForSecDto.setOrgInvAmdPer(
					obj[26] != null ? String.valueOf(obj[26]) : null);

			get2AConsoForSecDto.setOrgInvAmdTyp(
					obj[27] != null ? String.valueOf(obj[27]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[28] != null ? String.valueOf(obj[28]) : null);
			get2AConsoForSecDto.setIrnNum(
					obj[29] != null ? String.valueOf(obj[29]) : null);
			get2AConsoForSecDto.setIrnGenDate(
					obj[30] != null ? String.valueOf(obj[30]) : null);
			get2AConsoForSecDto.setIrnSourceType(
					obj[31] != null ? String.valueOf(obj[31]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[32] != null ? String.valueOf(obj[32]) : null);
			String invKey = obj[33] != null ? String.valueOf(obj[33]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setItemNumber(
					obj[34] != null ? String.valueOf(obj[34]) : null);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[35] != null ? new BigDecimal(String.valueOf(obj[35]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCgstAmt(
					obj[36] != null ? new BigDecimal(String.valueOf(obj[36]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemSgstAmt(
					obj[37] != null ? new BigDecimal(String.valueOf(obj[37]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCessAmt(
					obj[38] != null ? new BigDecimal(String.valueOf(obj[38]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[39] != null ? new BigDecimal(String.valueOf(obj[39]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[40] != null ? new BigDecimal(String.valueOf(obj[40]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[42] != null ? new Long(String.valueOf(obj[42])) : null);
			Long id = obj[42] != null ? new Long(String.valueOf(obj[42]))
					: null;
			get2AIds.add(id);

			get2AConsoForSecDto.setSuppTdLgName(
					obj[43] != null ? String.valueOf(obj[43]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[44] != null ? String.valueOf(obj[44]) : null);
			get2AConsoForSecDto.setModifiedTime(
					obj[45] != null ? String.valueOf(obj[45]) : null);

			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});

		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalB2bDtos = mapGet2AConsol
						.get(get2AId);
				if (totalB2bDtos != null && !totalB2bDtos.isEmpty()) {
					totalB2bDtos.forEach(totalB2bDto -> {
						get2AConsoForSecDto
								.setSectionName(totalB2bDto.getSectionName());
						get2AConsoForSecDto.setSgstin(totalB2bDto.getSgstin());
						get2AConsoForSecDto.setCgstin(totalB2bDto.getCgstin());
						get2AConsoForSecDto.setCtin(totalB2bDto.getCtin());
						get2AConsoForSecDto
								.setTaxPeriod(totalB2bDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalB2bDto.getTaxPeriod());
						get2AConsoForSecDto.setCfs(totalB2bDto.getCfs());
						get2AConsoForSecDto.setChksum(totalB2bDto.getChksum());
						get2AConsoForSecDto
								.setSuppInvNum(totalB2bDto.getSuppInvNum());

						get2AConsoForSecDto
								.setSuppInvDate(totalB2bDto.getSuppInvDate());
						get2AConsoForSecDto
								.setSuppInvVal(totalB2bDto.getSuppInvVal());
						get2AConsoForSecDto.setPos(totalB2bDto.getPos());
						get2AConsoForSecDto.setRchrg(totalB2bDto.getRchrg());
						get2AConsoForSecDto.setInvType("R");
						get2AConsoForSecDto
								.setSupplyType(totalB2bDto.getInvType());
						get2AConsoForSecDto
								.setDiffPercent(totalB2bDto.getDiffPercent());
						get2AConsoForSecDto
								.setGetBatchId(totalB2bDto.getGetBatchId());

						get2AConsoForSecDto
								.setIgstAmt(totalB2bDto.getIgstAmt());
						get2AConsoForSecDto
								.setCgstAmt(totalB2bDto.getCgstAmt());
						get2AConsoForSecDto
								.setSgstAmt(totalB2bDto.getSgstAmt());
						get2AConsoForSecDto
								.setCessAmt(totalB2bDto.getCessAmt());

						get2AConsoForSecDto
								.setTaxableValue(totalB2bDto.getTaxableValue());

						get2AConsoForSecDto
								.setActionTaken(totalB2bDto.getActionTaken());
						get2AConsoForSecDto
								.setApiSection(totalB2bDto.getApiSection());
						get2AConsoForSecDto
								.setDataCategory(totalB2bDto.getDataCategory());

						get2AConsoForSecDto
								.setSuppTdLgName(totalB2bDto.getSuppTdLgName());
						get2AConsoForSecDto
								.setCfsGstr3b(totalB2bDto.getCfsGstr3b());
						get2AConsoForSecDto
								.setCancelDate(totalB2bDto.getCancelDate());

						get2AConsoForSecDto
								.setFileDate(totalB2bDto.getFileDate());
						get2AConsoForSecDto
								.setFilePeriod(totalB2bDto.getFilePeriod());
						get2AConsoForSecDto
								.setOrgInvAmdPer(totalB2bDto.getOrgInvAmdPer());
						get2AConsoForSecDto
								.setOrgInvAmdTyp(totalB2bDto.getOrgInvAmdTyp());
						get2AConsoForSecDto.setIrnNum(totalB2bDto.getIrnNum());
						get2AConsoForSecDto
								.setIrnGenDate(totalB2bDto.getIrnGenDate());
						get2AConsoForSecDto.setIrnSourceType(
								totalB2bDto.getIrnSourceType());
						get2AConsoForSecDto
								.setInvStatus(totalB2bDto.getInvStatus());
						get2AConsoForSecDto.setInvKey(totalB2bDto.getInvKey());
						get2AConsoForSecDto
								.setTradeName(totalB2bDto.getTradeName());
						if (!Strings
								.isNullOrEmpty(totalB2bDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalB2bDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);

						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setItemNumber(totalB2bDto.getItemNumber());
						itemDto.setIgstAmt(totalB2bDto.getItemIgstAmt());
						itemDto.setCgstAmt(totalB2bDto.getItemCgstAmt());
						itemDto.setSgstAmt(totalB2bDto.getItemSgstAmt());
						itemDto.setCessAmt(totalB2bDto.getItemCessAmt());
						itemDto.setTaxableValue(
								totalB2bDto.getItemTaxableValue());
						itemDto.setTaxRate(totalB2bDto.getTaxRate());
						itemDtos.add(itemDto);
					});
				}
				if (itemDtos != null && !itemDtos.isEmpty()) {
					lineItemDto.setItems(itemDtos);
				}
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private Map<Long, List<Get2AConsoForSectionTotalDto>> mapHeaderIdForGet2A(
			List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos) {
		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsoForSectionTotalDto = new HashMap<>();
		if (get2AConsoSecTotalDtos != null
				&& !get2AConsoSecTotalDtos.isEmpty()) {
			get2AConsoSecTotalDtos.forEach(get2AConsoSecTotalDto -> {
				if (mapGet2AConsoForSectionTotalDto
						.containsKey(get2AConsoSecTotalDto.getId())) {
					List<Get2AConsoForSectionTotalDto> totalSection = mapGet2AConsoForSectionTotalDto
							.get(get2AConsoSecTotalDto.getId());
					totalSection.add(get2AConsoSecTotalDto);

					mapGet2AConsoForSectionTotalDto
							.put(get2AConsoSecTotalDto.getId(), totalSection);
				} else {
					List<Get2AConsoForSectionTotalDto> totalSection = new ArrayList<>();
					totalSection.add(get2AConsoSecTotalDto);
					mapGet2AConsoForSectionTotalDto
							.put(get2AConsoSecTotalDto.getId(), totalSection);
				}
			});
		}
		return mapGet2AConsoForSectionTotalDto;
	}

	private void mapObjToB2BASection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();

		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(B2BA);
			get2AConsoForSecDto
					.setSgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : null);
			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : null);
			get2AConsoForSecDto.setSuppInvNum(
					obj[5] != null ? String.valueOf(obj[5]) : null);

			get2AConsoForSecDto.setSuppInvDate(
					obj[6] != null ? String.valueOf(obj[6]) : null);

			get2AConsoForSecDto.setSuppInvVal(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			get2AConsoForSecDto
					.setPos(obj[8] != null ? String.valueOf(obj[8]) : null);
			get2AConsoForSecDto
					.setRchrg(obj[9] != null ? String.valueOf(obj[9]) : null);
			get2AConsoForSecDto.setInvType(
					obj[10] != null ? String.valueOf(obj[10]) : null);

			get2AConsoForSecDto.setDiffPercent(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setGetBatchId(
					obj[12] != null ? String.valueOf(obj[12]) : null);

			get2AConsoForSecDto.setIgstAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCessAmt(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxableValue(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setActionTaken(
					obj[18] != null ? String.valueOf(obj[18]) : null);
			get2AConsoForSecDto.setApiSection(
					obj[19] != null ? String.valueOf(obj[19]) : null);

			get2AConsoForSecDto.setDataCategory(
					obj[20] != null ? String.valueOf(obj[20]) : null);

			get2AConsoForSecDto.setCfsGstr3b(
					obj[22] != null ? String.valueOf(obj[22]) : null);
			get2AConsoForSecDto.setCancelDate(
					obj[23] != null ? String.valueOf(obj[23]) : null);

			get2AConsoForSecDto.setFileDate(
					obj[24] != null ? String.valueOf(obj[24]) : null);
			get2AConsoForSecDto.setFilePeriod(
					obj[25] != null ? String.valueOf(obj[25]) : null);

			get2AConsoForSecDto.setOrgInvAmdPer(
					obj[26] != null ? String.valueOf(obj[26]) : null);

			get2AConsoForSecDto.setOrgInvAmdTyp(
					obj[27] != null ? String.valueOf(obj[27]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[28] != null ? String.valueOf(obj[28]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[29] != null ? String.valueOf(obj[29]) : null);
			String invKey = obj[30] != null ? String.valueOf(obj[30]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setOrgInvNum(
					obj[31] != null ? String.valueOf(obj[31]) : null);
			get2AConsoForSecDto.setOrgInvDate(
					obj[32] != null ? String.valueOf(obj[32]) : null);

			get2AConsoForSecDto.setItemNumber(
					obj[33] != null ? String.valueOf(obj[33]) : null);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[34] != null ? new BigDecimal(String.valueOf(obj[34]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCgstAmt(
					obj[35] != null ? new BigDecimal(String.valueOf(obj[35]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemSgstAmt(
					obj[36] != null ? new BigDecimal(String.valueOf(obj[36]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[37] != null ? new BigDecimal(String.valueOf(obj[37]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[38] != null ? new BigDecimal(String.valueOf(obj[38]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[39] != null ? new BigDecimal(String.valueOf(obj[39]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[41] != null ? new Long(String.valueOf(obj[41])) : null);
			Long id = obj[41] != null ? new Long(String.valueOf(obj[41]))
					: null;

			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[42] != null ? String.valueOf(obj[42]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[43] != null ? String.valueOf(obj[43]) : null);

			get2AConsoForSecDto.setModifiedTime(
					obj[44] != null ? String.valueOf(obj[44]) : null);

			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});

		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalB2baDtos = mapGet2AConsol
						.get(get2AId);
				if (totalB2baDtos != null && !totalB2baDtos.isEmpty()) {
					totalB2baDtos.forEach(totalB2baDto -> {
						get2AConsoForSecDto
								.setSectionName(totalB2baDto.getSectionName());
						get2AConsoForSecDto.setSgstin(totalB2baDto.getSgstin());
						get2AConsoForSecDto.setCgstin(totalB2baDto.getCgstin());
						get2AConsoForSecDto.setCtin(totalB2baDto.getCtin());
						get2AConsoForSecDto
								.setTaxPeriod(totalB2baDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalB2baDto.getTaxPeriod());
						get2AConsoForSecDto.setCfs(totalB2baDto.getCfs());
						get2AConsoForSecDto.setChksum(totalB2baDto.getChksum());
						get2AConsoForSecDto
								.setSuppInvNum(totalB2baDto.getSuppInvNum());
						get2AConsoForSecDto
								.setSuppInvDate(totalB2baDto.getSuppInvDate());
						get2AConsoForSecDto
								.setSuppInvVal(totalB2baDto.getSuppInvVal());
						get2AConsoForSecDto.setPos(totalB2baDto.getPos());
						get2AConsoForSecDto.setRchrg(totalB2baDto.getRchrg());
						get2AConsoForSecDto.setInvType("R");
						get2AConsoForSecDto
								.setSupplyType(totalB2baDto.getInvType());
						get2AConsoForSecDto
								.setDiffPercent(totalB2baDto.getDiffPercent());
						get2AConsoForSecDto
								.setGetBatchId(totalB2baDto.getGetBatchId());
						get2AConsoForSecDto
								.setIgstAmt(totalB2baDto.getIgstAmt());
						get2AConsoForSecDto
								.setCgstAmt(totalB2baDto.getCgstAmt());
						get2AConsoForSecDto
								.setSgstAmt(totalB2baDto.getSgstAmt());
						get2AConsoForSecDto
								.setCessAmt(totalB2baDto.getCessAmt());
						get2AConsoForSecDto.setTaxableValue(
								totalB2baDto.getTaxableValue());
						get2AConsoForSecDto
								.setActionTaken(totalB2baDto.getActionTaken());
						get2AConsoForSecDto
								.setApiSection(totalB2baDto.getApiSection());
						get2AConsoForSecDto.setDataCategory(
								totalB2baDto.getDataCategory());
						get2AConsoForSecDto.setSuppTdLgName(
								totalB2baDto.getSuppTdLgName());
						get2AConsoForSecDto
								.setCfsGstr3b(totalB2baDto.getCfsGstr3b());
						get2AConsoForSecDto
								.setCancelDate(totalB2baDto.getCancelDate());

						get2AConsoForSecDto
								.setFileDate(totalB2baDto.getFileDate());
						get2AConsoForSecDto
								.setFilePeriod(totalB2baDto.getFilePeriod());

						get2AConsoForSecDto.setOrgInvAmdPer(
								totalB2baDto.getOrgInvAmdPer());

						get2AConsoForSecDto.setOrgInvAmdTyp(
								totalB2baDto.getOrgInvAmdTyp());
						get2AConsoForSecDto
								.setInvStatus(totalB2baDto.getInvStatus());
						get2AConsoForSecDto.setInvKey(totalB2baDto.getInvKey());
						get2AConsoForSecDto
								.setOrgInvNum(totalB2baDto.getOrgInvNum());
						get2AConsoForSecDto
								.setOrgInvDate(totalB2baDto.getOrgInvDate());
						get2AConsoForSecDto
								.setTradeName(totalB2baDto.getTradeName());

						if (!Strings.isNullOrEmpty(
								totalB2baDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalB2baDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);

						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setItemNumber(totalB2baDto.getItemNumber());
						itemDto.setIgstAmt(totalB2baDto.getItemIgstAmt());

						itemDto.setCgstAmt(totalB2baDto.getItemCgstAmt());

						itemDto.setSgstAmt(totalB2baDto.getItemSgstAmt());

						itemDto.setCessAmt(totalB2baDto.getItemCessAmt());
						itemDto.setTaxableValue(totalB2baDto.getTaxableValue());
						itemDto.setTaxRate(totalB2baDto.getTaxRate());
						itemDtos.add(itemDto);
					});
				}
				if (itemDtos != null && !itemDtos.isEmpty()) {
					lineItemDto.setItems(itemDtos);
				}
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private void mapObjToCDNSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(CDN);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setSgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : null);

			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : null);

			get2AConsoForSecDto.setNoteType(
					obj[5] != null ? String.valueOf(obj[5]) : null);
			get2AConsoForSecDto.setNoteNumber(
					obj[6] != null ? String.valueOf(obj[6]) : null);
			get2AConsoForSecDto.setNoteDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : null);

			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto.setSuppInvVal(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);

			get2AConsoForSecDto.setOrgInvNum(
					obj[9] != null ? String.valueOf(obj[9]) : null);
			get2AConsoForSecDto
					.setPgst(obj[10] != null ? String.valueOf(obj[10]) : null);
			get2AConsoForSecDto.setDiffPercent(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setOrgInvDate(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			get2AConsoForSecDto.setGetBatchId(
					obj[13] != null ? String.valueOf(obj[13]) : null);
			get2AConsoForSecDto.setIgstAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setCessAmt(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto
					.setCflag(obj[18] != null ? String.valueOf(obj[18]) : null);
			get2AConsoForSecDto.setFromTime(
					obj[19] != null ? String.valueOf(obj[19]) : null);
			get2AConsoForSecDto.setTaxableValue(
					obj[20] != null ? new BigDecimal(String.valueOf(obj[20]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setDataCategory(
					obj[21] != null ? String.valueOf(obj[21]) : null);

			get2AConsoForSecDto
					.setPos(obj[23] != null ? String.valueOf(obj[23]) : null);

			get2AConsoForSecDto
					.setRchrg(obj[24] != null ? String.valueOf(obj[24]) : null);
			get2AConsoForSecDto.setInvType(
					obj[25] != null ? String.valueOf(obj[25]) : null);
			get2AConsoForSecDto
					.setDflag(obj[26] != null ? String.valueOf(obj[26]) : null);
			get2AConsoForSecDto.setCfsGstr3b(
					obj[27] != null ? String.valueOf(obj[27]) : null);

			get2AConsoForSecDto.setCancelDate(
					obj[28] != null ? String.valueOf(obj[28]) : null);

			get2AConsoForSecDto.setFileDate(
					obj[29] != null ? String.valueOf(obj[29]) : null);

			get2AConsoForSecDto.setFilePeriod(
					obj[30] != null ? String.valueOf(obj[30]) : null);

			get2AConsoForSecDto.setOrgInvAmdPer(
					obj[31] != null ? String.valueOf(obj[31]) : null);

			get2AConsoForSecDto.setOrgInvAmdTyp(
					obj[32] != null ? String.valueOf(obj[32]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[33] != null ? String.valueOf(obj[33]) : null);

			get2AConsoForSecDto.setIrnNum(
					obj[34] != null ? String.valueOf(obj[34]) : null);

			get2AConsoForSecDto.setIrnGenDate(
					obj[35] != null ? String.valueOf(obj[35]) : null);
			get2AConsoForSecDto.setIrnSourceType(
					obj[36] != null ? String.valueOf(obj[36]) : null);

			get2AConsoForSecDto.setInvStatus(
					obj[37] != null ? String.valueOf(obj[37]) : null);

			String invKey = obj[38] != null ? String.valueOf(obj[38]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}

			get2AConsoForSecDto.setItemNumber(
					obj[39] != null ? String.valueOf(obj[39]) : null);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[40] != null ? new BigDecimal(String.valueOf(obj[40]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCgstAmt(
					obj[41] != null ? new BigDecimal(String.valueOf(obj[41]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemSgstAmt(
					obj[42] != null ? new BigDecimal(String.valueOf(obj[42]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[43] != null ? new BigDecimal(String.valueOf(obj[43]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[44] != null ? new BigDecimal(String.valueOf(obj[44]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[45] != null ? new BigDecimal(String.valueOf(obj[45]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[47] != null ? new Long(String.valueOf(obj[47])) : null);
			Long id = obj[47] != null ? new Long(String.valueOf(obj[47]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[48] != null ? String.valueOf(obj[48]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[49] != null ? String.valueOf(obj[49]) : null);
			get2AConsoForSecDto.setModifiedTime(
					obj[50] != null ? String.valueOf(obj[50]) : null);

			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});
		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalCdnDtos = mapGet2AConsol
						.get(get2AId);
				if (totalCdnDtos != null && !totalCdnDtos.isEmpty()) {
					totalCdnDtos.forEach(totalCdnDto -> {
						get2AConsoForSecDto
								.setSectionName(totalCdnDto.getSectionName());
						get2AConsoForSecDto.setCtin(totalCdnDto.getCtin());
						get2AConsoForSecDto.setCgstin(totalCdnDto.getCgstin());
						get2AConsoForSecDto.setSgstin(totalCdnDto.getSgstin());
						get2AConsoForSecDto
								.setTaxPeriod(totalCdnDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalCdnDto.getTaxPeriod());
						get2AConsoForSecDto.setCfs(totalCdnDto.getCfs());
						get2AConsoForSecDto.setChksum(totalCdnDto.getChksum());
						get2AConsoForSecDto
								.setInvType(totalCdnDto.getInvType());
						get2AConsoForSecDto
								.setSuppInvNum(totalCdnDto.getSuppInvNum());
						get2AConsoForSecDto
								.setSuppInvDate(totalCdnDto.getSuppInvDate());
						get2AConsoForSecDto
								.setSuppInvVal(totalCdnDto.getSuppInvVal());
						get2AConsoForSecDto
								.setNoteType(totalCdnDto.getNoteType());
						get2AConsoForSecDto
								.setNoteNumber(totalCdnDto.getNoteNumber());
						get2AConsoForSecDto
								.setNoteDate(totalCdnDto.getNoteDate());
						get2AConsoForSecDto
								.setOrgInvNum(totalCdnDto.getOrgInvNum());
						get2AConsoForSecDto.setPgst(totalCdnDto.getPgst());
						get2AConsoForSecDto
								.setDiffPercent(totalCdnDto.getDiffPercent());
						get2AConsoForSecDto
								.setOrgInvDate(totalCdnDto.getInvDate());
						get2AConsoForSecDto
								.setGetBatchId(totalCdnDto.getGetBatchId());
						get2AConsoForSecDto
								.setIgstAmt(totalCdnDto.getIgstAmt());
						get2AConsoForSecDto
								.setCgstAmt(totalCdnDto.getCgstAmt());
						get2AConsoForSecDto
								.setSgstAmt(totalCdnDto.getSgstAmt());
						get2AConsoForSecDto
								.setCessAmt(totalCdnDto.getCessAmt());
						get2AConsoForSecDto.setCflag(totalCdnDto.getCflag());
						get2AConsoForSecDto
								.setFromTime(totalCdnDto.getFromTime());
						get2AConsoForSecDto
								.setTaxableValue(totalCdnDto.getTaxableValue());
						get2AConsoForSecDto
								.setDataCategory(totalCdnDto.getDataCategory());
						get2AConsoForSecDto
								.setSuppTdLgName(totalCdnDto.getSuppTdLgName());
						get2AConsoForSecDto.setPos(totalCdnDto.getPos());
						get2AConsoForSecDto.setRchrg(totalCdnDto.getRchrg());
						get2AConsoForSecDto
								.setInvType(totalCdnDto.getInvType());
						get2AConsoForSecDto.setDflag(totalCdnDto.getDflag());
						get2AConsoForSecDto
								.setCfsGstr3b(totalCdnDto.getCfsGstr3b());
						get2AConsoForSecDto
								.setCancelDate(totalCdnDto.getCancelDate());
						get2AConsoForSecDto
								.setFileDate(totalCdnDto.getFileDate());
						get2AConsoForSecDto
								.setFilePeriod(totalCdnDto.getFilePeriod());
						get2AConsoForSecDto
								.setOrgInvAmdPer(totalCdnDto.getOrgInvAmdPer());
						get2AConsoForSecDto
								.setOrgInvAmdTyp(totalCdnDto.getOrgInvAmdTyp());
						get2AConsoForSecDto
								.setSupplyType(totalCdnDto.getInvType());
						get2AConsoForSecDto.setIrnNum(totalCdnDto.getIrnNum());
						get2AConsoForSecDto
								.setIrnGenDate(totalCdnDto.getIrnGenDate());
						get2AConsoForSecDto.setIrnSourceType(
								totalCdnDto.getIrnSourceType());
						get2AConsoForSecDto
								.setInvStatus(totalCdnDto.getInvStatus());
						get2AConsoForSecDto
								.setTradeName(totalCdnDto.getTradeName());
						if (!Strings
								.isNullOrEmpty(totalCdnDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalCdnDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);
						get2AConsoForSecDto.setInvKey(totalCdnDto.getInvKey());
						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setItemNumber(totalCdnDto.getItemNumber());
						itemDto.setIgstAmt(totalCdnDto.getItemIgstAmt());
						itemDto.setCgstAmt(totalCdnDto.getItemCgstAmt());
						itemDto.setSgstAmt(totalCdnDto.getItemSgstAmt());
						itemDto.setCessAmt(totalCdnDto.getItemCessAmt());
						itemDto.setTaxableValue(
								totalCdnDto.getItemTaxableValue());
						itemDto.setTaxRate(totalCdnDto.getTaxRate());
						itemDtos.add(itemDto);
					});
				}
				if (itemDtos != null && !itemDtos.isEmpty()) {
					lineItemDto.setItems(itemDtos);
				}
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private void mapObjToCDNASection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();

		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(CDNA);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setSgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : null);

			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : null);

			get2AConsoForSecDto.setNoteType(
					obj[5] != null ? String.valueOf(obj[5]) : null);
			get2AConsoForSecDto.setNoteNumber(
					obj[6] != null ? String.valueOf(obj[6]) : null);

			get2AConsoForSecDto.setNoteDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : null);

			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : null);

			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto.setSuppInvVal(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);
			get2AConsoForSecDto.setOrgNoteType(
					obj[9] != null ? String.valueOf(obj[9]) : null);
			get2AConsoForSecDto.setOrgNoteNumber(
					obj[10] != null ? String.valueOf(obj[10]) : null);
			get2AConsoForSecDto.setOrgNoteDate(
					obj[11] != null ? String.valueOf(obj[11]) : null);

			get2AConsoForSecDto.setOrgInvNum(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			get2AConsoForSecDto
					.setPgst(obj[13] != null ? String.valueOf(obj[13]) : null);
			get2AConsoForSecDto.setDiffPercent(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setOrgInvDate(
					obj[15] != null ? String.valueOf(obj[15]) : null);
			get2AConsoForSecDto.setGetBatchId(
					obj[16] != null ? String.valueOf(obj[16]) : null);

			get2AConsoForSecDto.setIgstAmt(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[18] != null ? new BigDecimal(String.valueOf(obj[18]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[19] != null ? new BigDecimal(String.valueOf(obj[19]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCessAmt(
					obj[20] != null ? new BigDecimal(String.valueOf(obj[20]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(
					obj[21] != null ? new BigDecimal(String.valueOf(obj[21]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setDataCategory(
					obj[22] != null ? String.valueOf(obj[22]) : null);

			get2AConsoForSecDto
					.setPos(obj[24] != null ? String.valueOf(obj[24]) : null);

			get2AConsoForSecDto
					.setRchrg(obj[25] != null ? String.valueOf(obj[25]) : null);
			get2AConsoForSecDto.setInvType(
					obj[26] != null ? String.valueOf(obj[26]) : null);
			get2AConsoForSecDto
					.setDflag(obj[27] != null ? String.valueOf(obj[27]) : null);
			get2AConsoForSecDto.setCfsGstr3b(
					obj[28] != null ? String.valueOf(obj[28]) : null);

			get2AConsoForSecDto.setCancelDate(
					obj[29] != null ? String.valueOf(obj[29]) : null);

			get2AConsoForSecDto.setFileDate(
					obj[30] != null ? String.valueOf(obj[30]) : null);

			get2AConsoForSecDto.setFilePeriod(
					obj[31] != null ? String.valueOf(obj[31]) : null);

			get2AConsoForSecDto.setOrgInvAmdPer(
					obj[32] != null ? String.valueOf(obj[32]) : null);

			get2AConsoForSecDto.setOrgInvAmdTyp(
					obj[33] != null ? String.valueOf(obj[33]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[34] != null ? String.valueOf(obj[34]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[35] != null ? String.valueOf(obj[35]) : null);

			String invKey = obj[36] != null ? String.valueOf(obj[36]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setItemNumber(
					obj[37] != null ? String.valueOf(obj[37]) : null);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[38] != null ? new BigDecimal(String.valueOf(obj[38]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCgstAmt(
					obj[39] != null ? new BigDecimal(String.valueOf(obj[39]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemSgstAmt(
					obj[40] != null ? new BigDecimal(String.valueOf(obj[40]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[41] != null ? new BigDecimal(String.valueOf(obj[41]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemTaxableValue(
					obj[42] != null ? new BigDecimal(String.valueOf(obj[42]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[43] != null ? new BigDecimal(String.valueOf(obj[43]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[45] != null ? new Long(String.valueOf(obj[45])) : null);
			Long id = obj[45] != null ? new Long(String.valueOf(obj[45]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[46] != null ? String.valueOf(obj[46]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[47] != null ? String.valueOf(obj[47]) : null);
			get2AConsoForSecDto.setModifiedTime(
					obj[48] != null ? String.valueOf(obj[48]) : null);
			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});

		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalCdnaDtos = mapGet2AConsol
						.get(get2AId);
				if (totalCdnaDtos != null && !totalCdnaDtos.isEmpty()) {
					totalCdnaDtos.forEach(totalCdnaDto -> {
						get2AConsoForSecDto
								.setSectionName(totalCdnaDto.getSectionName());
						get2AConsoForSecDto.setCtin(totalCdnaDto.getCtin());
						get2AConsoForSecDto.setCgstin(totalCdnaDto.getCgstin());
						get2AConsoForSecDto.setSgstin(totalCdnaDto.getSgstin());
						get2AConsoForSecDto
								.setTaxPeriod(totalCdnaDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalCdnaDto.getTaxPeriod());
						get2AConsoForSecDto.setCfs(totalCdnaDto.getCfs());
						get2AConsoForSecDto.setChksum(totalCdnaDto.getChksum());
						get2AConsoForSecDto
								.setSuppInvNum(totalCdnaDto.getSuppInvNum());
						get2AConsoForSecDto
								.setSuppInvDate(totalCdnaDto.getSuppInvDate());
						get2AConsoForSecDto
								.setSuppInvVal(totalCdnaDto.getSuppInvVal());
						get2AConsoForSecDto
								.setOrgNoteType(totalCdnaDto.getOrgNoteType());
						get2AConsoForSecDto.setOrgNoteNumber(
								totalCdnaDto.getOrgNoteNumber());
						get2AConsoForSecDto
								.setOrgNoteDate(totalCdnaDto.getOrgNoteDate());

						get2AConsoForSecDto
								.setOrgInvNum(totalCdnaDto.getOrgInvNum());
						get2AConsoForSecDto.setPgst(totalCdnaDto.getPgst());
						get2AConsoForSecDto
								.setDiffPercent(totalCdnaDto.getDiffPercent());
						get2AConsoForSecDto
								.setOrgInvDate(totalCdnaDto.getOrgInvDate());
						get2AConsoForSecDto
								.setGetBatchId(totalCdnaDto.getGetBatchId());

						get2AConsoForSecDto
								.setIgstAmt(totalCdnaDto.getIgstAmt());
						get2AConsoForSecDto
								.setCgstAmt(totalCdnaDto.getCgstAmt());
						get2AConsoForSecDto
								.setSgstAmt(totalCdnaDto.getSgstAmt());

						get2AConsoForSecDto
								.setCessAmt(totalCdnaDto.getCessAmt());

						get2AConsoForSecDto.setTaxableValue(
								totalCdnaDto.getTaxableValue());
						get2AConsoForSecDto.setDataCategory(
								totalCdnaDto.getDataCategory());
						get2AConsoForSecDto.setSuppTdLgName(
								totalCdnaDto.getSuppTdLgName());

						get2AConsoForSecDto.setPos(totalCdnaDto.getPos());

						get2AConsoForSecDto.setRchrg(totalCdnaDto.getRchrg());
						get2AConsoForSecDto
								.setInvType(totalCdnaDto.getInvType());

						get2AConsoForSecDto.setDflag(totalCdnaDto.getDflag());
						get2AConsoForSecDto
								.setCfsGstr3b(totalCdnaDto.getCfsGstr3b());

						get2AConsoForSecDto
								.setCancelDate(totalCdnaDto.getCancelDate());

						get2AConsoForSecDto
								.setFileDate(totalCdnaDto.getFileDate());

						get2AConsoForSecDto
								.setFilePeriod(totalCdnaDto.getFilePeriod());

						get2AConsoForSecDto.setOrgInvAmdPer(
								totalCdnaDto.getOrgInvAmdPer());

						get2AConsoForSecDto.setOrgInvAmdTyp(
								totalCdnaDto.getOrgInvAmdTyp());
						get2AConsoForSecDto
								.setSupplyType(totalCdnaDto.getInvType());
						get2AConsoForSecDto
								.setInvStatus(totalCdnaDto.getInvStatus());
						get2AConsoForSecDto.setInvKey(totalCdnaDto.getInvKey());
						get2AConsoForSecDto
								.setTradeName(totalCdnaDto.getTradeName());
						if (!Strings.isNullOrEmpty(
								totalCdnaDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalCdnaDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);
						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();

						itemDto.setItemNumber(totalCdnaDto.getItemNumber());
						itemDto.setIgstAmt(totalCdnaDto.getItemIgstAmt());
						itemDto.setCgstAmt(totalCdnaDto.getItemCgstAmt());
						itemDto.setSgstAmt(totalCdnaDto.getSgstAmt());

						itemDto.setCessAmt(totalCdnaDto.getCessAmt());

						itemDto.setTaxableValue(
								totalCdnaDto.getItemTaxableValue());
						itemDto.setTaxRate(totalCdnaDto.getTaxRate());
						itemDtos.add(itemDto);
					});
				}
				if (itemDtos != null && !itemDtos.isEmpty()) {
					lineItemDto.setItems(itemDtos);
				}
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private void mapObjToISDSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(ISD);
			get2AConsoForSecDto
					.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto
					.setCtin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);

			get2AConsoForSecDto.setGetBatchId(
					obj[3] != null ? String.valueOf(obj[3]) : null);
			get2AConsoForSecDto
					.setCfs(obj[4] != null ? String.valueOf(obj[4]) : null);
			get2AConsoForSecDto
					.setChksum(obj[5] != null ? String.valueOf(obj[5]) : null);
			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : null);
			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : null);

			get2AConsoForSecDto
					.setInvType(obj[8] != null ? String.valueOf(obj[8]) : null);

			get2AConsoForSecDto
					.setItcElg(obj[9] != null ? String.valueOf(obj[9]) : null);

			get2AConsoForSecDto.setIgstAmt(
					obj[10] != null ? new BigDecimal(String.valueOf(obj[10]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[12] != null ? new BigDecimal(String.valueOf(obj[12]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setCessAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setDataCategory(
					obj[14] != null ? String.valueOf(obj[14]) : null);
			get2AConsoForSecDto.setSuppTdLgName(
					obj[15] != null ? String.valueOf(obj[15]) : null);

			get2AConsoForSecDto.setSupplyType(
					obj[16] != null ? String.valueOf(obj[16]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[17] != null ? String.valueOf(obj[17]) : null);
			String invKey = obj[18] != null ? String.valueOf(obj[18]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}

			get2AConsoForSecDto.setItemIgstAmt(
					obj[19] != null ? new BigDecimal(String.valueOf(obj[19]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCgstAmt(
					obj[20] != null ? new BigDecimal(String.valueOf(obj[20]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemSgstAmt(
					obj[21] != null ? new BigDecimal(String.valueOf(obj[21]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[22] != null ? new BigDecimal(String.valueOf(obj[22]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[24] != null ? new Long(String.valueOf(obj[24])) : null);
			Long id = obj[24] != null ? new Long(String.valueOf(obj[24]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}

			get2AConsoForSecDto.setSuppTdLgName(
					obj[25] != null ? String.valueOf(obj[25]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[26] != null ? String.valueOf(obj[26]) : null);
			get2AConsoForSecDto.setModifiedTime(
					obj[27] != null ? String.valueOf(obj[27]) : null);
			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);

		});
		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalIsdDtos = mapGet2AConsol
						.get(get2AId);
				if (totalIsdDtos != null && !totalIsdDtos.isEmpty()) {
					totalIsdDtos.forEach(totalIsdDto -> {
						get2AConsoForSecDto
								.setSectionName(totalIsdDto.getSectionName());
						get2AConsoForSecDto.setCtin(totalIsdDto.getCtin());
						get2AConsoForSecDto.setCgstin(totalIsdDto.getCgstin());
						get2AConsoForSecDto.setSgstin(totalIsdDto.getSgstin());
						get2AConsoForSecDto
								.setTaxPeriod(totalIsdDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalIsdDto.getTaxPeriod());
						get2AConsoForSecDto
								.setGetBatchId(totalIsdDto.getGetBatchId());
						get2AConsoForSecDto.setCfs(totalIsdDto.getCfs());
						get2AConsoForSecDto.setChksum(totalIsdDto.getChksum());

						get2AConsoForSecDto
								.setSuppInvNum(totalIsdDto.getSuppInvNum());
						get2AConsoForSecDto
								.setSuppInvDate(totalIsdDto.getSuppInvDate());

						String invType = null;
						if (totalIsdDto.getInvType() != null
								&& !totalIsdDto.getInvType().trim().isEmpty()) {
							if (totalIsdDto.getInvType().length() > 4) {
								invType = totalIsdDto.getInvType().substring(0,
										4);
							} else {
								invType = totalIsdDto.getInvType();
							}
						}
						get2AConsoForSecDto.setInvType(invType);

						get2AConsoForSecDto.setItcElg(totalIsdDto.getItcElg());

						get2AConsoForSecDto
								.setIgstAmt(totalIsdDto.getIgstAmt());
						get2AConsoForSecDto
								.setCgstAmt(totalIsdDto.getCgstAmt());
						get2AConsoForSecDto
								.setSgstAmt(totalIsdDto.getSgstAmt());

						get2AConsoForSecDto
								.setCessAmt(totalIsdDto.getCessAmt());
						get2AConsoForSecDto
								.setDataCategory(totalIsdDto.getDataCategory());
						get2AConsoForSecDto
								.setSuppTdLgName(totalIsdDto.getSuppTdLgName());

						get2AConsoForSecDto
								.setSupplyType(totalIsdDto.getSupplyType());
						get2AConsoForSecDto
								.setInvStatus(totalIsdDto.getInvStatus());

						get2AConsoForSecDto
								.setTradeName(totalIsdDto.getTradeName());
						if (!Strings
								.isNullOrEmpty(totalIsdDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalIsdDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}

						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);

						get2AConsoForSecDto.setInvKey(totalIsdDto.getInvKey());

						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setIgstAmt(totalIsdDto.getIgstAmt());

						itemDto.setCgstAmt(totalIsdDto.getItemCgstAmt());

						itemDto.setSgstAmt(totalIsdDto.getItemSgstAmt());

						itemDto.setCessAmt(totalIsdDto.getItemCessAmt());
						itemDtos.add(itemDto);
					});
				}
				if (itemDtos != null && !itemDtos.isEmpty()) {
					lineItemDto.setItems(itemDtos);
				}
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private void mapObjToIMPGSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(IMPG);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto.setFromTime(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			String boeRefDate = obj[3] != null ? String.valueOf(obj[3]) : null;
			if (boeRefDate != null && !boeRefDate.isEmpty()) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate boeRefLocalDate = LocalDate.parse(boeRefDate,
						dateTimeFormatter);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Boe Referred Date:{}", boeRefLocalDate);
				}
				get2AConsoForSecDto
						.setBoeRefDate(String.valueOf(boeRefLocalDate));
			}

			get2AConsoForSecDto.setPortCode(
					obj[4] != null ? String.valueOf(obj[4]) : null);
			get2AConsoForSecDto
					.setBoeNum(obj[5] != null ? String.valueOf(obj[5]) : null);

			String boeCreatedDate = obj[6] != null ? String.valueOf(obj[6])
					: null;
			if (boeCreatedDate != null && !boeCreatedDate.isEmpty()) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate boeCreatedLocalDate = LocalDate.parse(boeCreatedDate,
						dateTimeFormatter);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Boe Created Date:", boeCreatedLocalDate);
				}
				get2AConsoForSecDto
						.setBoeCreatedDt(String.valueOf(boeCreatedLocalDate));
			}
			get2AConsoForSecDto.setIgstAmt(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);

			get2AConsoForSecDto.setCessAmt(obj[8] != null
					? new BigDecimal(String.valueOf(obj[8])) : BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(obj[9] != null
					? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);
			get2AConsoForSecDto.setGetBatchId(
					obj[10] != null ? String.valueOf(obj[10]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[11] != null ? String.valueOf(obj[11]) : null);
			get2AConsoForSecDto.setAmdhistKey(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			get2AConsoForSecDto.setIsAmendtBoe(
					obj[13] != null ? String.valueOf(obj[13]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[14] != null ? String.valueOf(obj[14]) : null);
			String invKey = obj[15] != null ? String.valueOf(obj[15]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setItemIgstAmt(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[19] != null ? new Long(String.valueOf(obj[19])) : null);
			Long id = obj[19] != null ? new Long(String.valueOf(obj[19]))
					: null;
			get2AConsoForSecDto.setModifiedTime(
					obj[20] != null ? String.valueOf(obj[20]) : null);
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});
		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalImpgDtos = mapGet2AConsol
						.get(get2AId);
				if (totalImpgDtos != null && !totalImpgDtos.isEmpty()) {
					totalImpgDtos.forEach(totalImpgDto -> {
						get2AConsoForSecDto
								.setSectionName(totalImpgDto.getSectionName());
						get2AConsoForSecDto.setCtin(totalImpgDto.getCtin());
						get2AConsoForSecDto.setCgstin(totalImpgDto.getCgstin());
						get2AConsoForSecDto
								.setTaxPeriod(totalImpgDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalImpgDto.getTaxPeriod());
						get2AConsoForSecDto
								.setFromTime(totalImpgDto.getFromTime());

						get2AConsoForSecDto
								.setBoeRefDate(totalImpgDto.getBoeRefDate());

						get2AConsoForSecDto
								.setPortCode(totalImpgDto.getPortCode());
						get2AConsoForSecDto.setBoeNum(totalImpgDto.getBoeNum());
						get2AConsoForSecDto.setBoeCreatedDt(
								totalImpgDto.getBoeCreatedDt());
						get2AConsoForSecDto
								.setIgstAmt(totalImpgDto.getIgstAmt());

						get2AConsoForSecDto
								.setCessAmt(totalImpgDto.getCessAmt());

						get2AConsoForSecDto.setTaxableValue(
								totalImpgDto.getTaxableValue());
						get2AConsoForSecDto
								.setGetBatchId(totalImpgDto.getGetBatchId());
						get2AConsoForSecDto
								.setSupplyType(totalImpgDto.getSupplyType());
						get2AConsoForSecDto
								.setAmdhistKey(totalImpgDto.getAmdhistKey());
						get2AConsoForSecDto
								.setIsAmendtBoe(totalImpgDto.getIsAmendtBoe());
						get2AConsoForSecDto
								.setInvStatus(totalImpgDto.getInvStatus());
						get2AConsoForSecDto.setInvKey(totalImpgDto.getInvKey());
						if (!Strings.isNullOrEmpty(
								totalImpgDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalImpgDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);
						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setIgstAmt(totalImpgDto.getItemIgstAmt());
						itemDto.setCessAmt(totalImpgDto.getItemCessAmt());
						itemDtos.add(itemDto);
					});
				}
				lineItemDto.setItems(itemDtos);
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private void mapObjToIMPGSEZSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(IMPGSEZ);
			get2AConsoForSecDto
					.setCgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setCtin(obj[0] != null ? String.valueOf(obj[0]) : null);
			get2AConsoForSecDto
					.setSgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			get2AConsoForSecDto.setFromTime(
					obj[3] != null ? String.valueOf(obj[3]) : null);
			get2AConsoForSecDto.setSuppTdLgName(
					obj[4] != null ? String.valueOf(obj[4]) : null);

			// 10-08-2020
			String boeRefDate = obj[5] != null ? String.valueOf(obj[5]) : null;
			if (boeRefDate != null && !boeRefDate.isEmpty()) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate boeRefLocalDate = LocalDate.parse(boeRefDate,
						dateTimeFormatter);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Boe Referred Date:{}", boeRefLocalDate);
				}
				get2AConsoForSecDto
						.setBoeRefDate(String.valueOf(boeRefLocalDate));
			}
			get2AConsoForSecDto.setPortCode(
					obj[6] != null ? String.valueOf(obj[6]) : null);
			get2AConsoForSecDto
					.setBoeNum(obj[7] != null ? String.valueOf(obj[7]) : null);

			String boeCreatedDate = obj[8] != null ? String.valueOf(obj[8])
					: null;
			if (boeCreatedDate != null && !boeCreatedDate.isEmpty()) {
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate boeCreatedLocalDate = LocalDate.parse(boeCreatedDate,
						dateTimeFormatter);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Boe Inv Date:{}", boeCreatedLocalDate);
				}
				get2AConsoForSecDto
						.setBoeCreatedDt(String.valueOf(boeCreatedLocalDate));
			}
			get2AConsoForSecDto.setIgstAmt(obj[9] != null
					? new BigDecimal(String.valueOf(obj[9])) : BigDecimal.ZERO);

			get2AConsoForSecDto.setCessAmt(
					obj[10] != null ? new BigDecimal(String.valueOf(obj[10]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setGetBatchId(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			get2AConsoForSecDto.setSupplyType(
					obj[13] != null ? String.valueOf(obj[13]) : null);
			get2AConsoForSecDto.setAmdhistKey(
					obj[14] != null ? String.valueOf(obj[14]) : null);
			get2AConsoForSecDto.setIsAmendtBoe(
					obj[15] != null ? String.valueOf(obj[15]) : null);
			get2AConsoForSecDto.setInvStatus(
					obj[16] != null ? String.valueOf(obj[16]) : null);
			String invKey = obj[17] != null ? String.valueOf(obj[17]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}

			get2AConsoForSecDto.setItemIgstAmt(
					obj[18] != null ? new BigDecimal(String.valueOf(obj[18]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[19] != null ? new BigDecimal(String.valueOf(obj[19]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[22] != null ? new Long(String.valueOf(obj[22])) : null);
			Long id = obj[22] != null ? new Long(String.valueOf(obj[22]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[23] != null ? String.valueOf(obj[23]) : null);
			get2AConsoForSecDto.setTradeName(
					obj[24] != null ? String.valueOf(obj[24]) : null);
			get2AConsoForSecDto.setModifiedTime(
					obj[25] != null ? String.valueOf(obj[25]) : null);

			get2AConsoSecTotalDtos.add(get2AConsoForSecDto);
		});
		Map<Long, List<Get2AConsoForSectionTotalDto>> mapGet2AConsol = mapHeaderIdForGet2A(
				get2AConsoSecTotalDtos);
		if (!get2AIds.isEmpty()) {
			get2AIds.forEach(get2AId -> {
				Get2AConsoForSectionHeaderDto get2AConsoForSecDto = new Get2AConsoForSectionHeaderDto();
				Get2AConsoForSectionLineItemDto lineItemDto = new Get2AConsoForSectionLineItemDto();
				List<Get2AConsoForSectionItemDto> itemDtos = new ArrayList<>();
				List<Get2AConsoForSectionTotalDto> totalImpgSezDtos = mapGet2AConsol
						.get(get2AId);
				if (totalImpgSezDtos != null && !totalImpgSezDtos.isEmpty()) {
					totalImpgSezDtos.forEach(totalImpgSezDto -> {
						get2AConsoForSecDto.setSectionName(
								totalImpgSezDto.getSectionName());
						get2AConsoForSecDto
								.setCgstin(totalImpgSezDto.getCgstin());
						get2AConsoForSecDto.setCtin(totalImpgSezDto.getCtin());
						get2AConsoForSecDto
								.setSgstin(totalImpgSezDto.getSgstin());
						get2AConsoForSecDto
								.setTaxPeriod(totalImpgSezDto.getTaxPeriod());
						get2AConsoForSecDto
								.setRetPeriod(totalImpgSezDto.getTaxPeriod());
						get2AConsoForSecDto
								.setFromTime(totalImpgSezDto.getFromTime());
						get2AConsoForSecDto.setSuppTdLgName(
								totalImpgSezDto.getSuppTdLgName());

						get2AConsoForSecDto
								.setBoeRefDate(totalImpgSezDto.getBoeRefDate());
						get2AConsoForSecDto
								.setPortCode(totalImpgSezDto.getPortCode());
						get2AConsoForSecDto
								.setBoeNum(totalImpgSezDto.getBoeNum());

						get2AConsoForSecDto.setBoeCreatedDt(
								totalImpgSezDto.getBoeCreatedDt());

						get2AConsoForSecDto
								.setIgstAmt(totalImpgSezDto.getIgstAmt());

						get2AConsoForSecDto
								.setCessAmt(totalImpgSezDto.getCessAmt());

						get2AConsoForSecDto.setTaxableValue(
								totalImpgSezDto.getTaxableValue());
						get2AConsoForSecDto
								.setGetBatchId(totalImpgSezDto.getGetBatchId());
						get2AConsoForSecDto
								.setSupplyType(totalImpgSezDto.getSupplyType());
						get2AConsoForSecDto
								.setAmdhistKey(totalImpgSezDto.getAmdhistKey());
						get2AConsoForSecDto.setIsAmendtBoe(
								totalImpgSezDto.getIsAmendtBoe());
						get2AConsoForSecDto
								.setInvStatus(totalImpgSezDto.getInvStatus());
						get2AConsoForSecDto
								.setInvKey(totalImpgSezDto.getInvKey());
						get2AConsoForSecDto.setRetType("2A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);
						if (!Strings.isNullOrEmpty(
								totalImpgSezDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalImpgSezDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto
								.setTradeName(totalImpgSezDto.getTradeName());
						Get2AConsoForSectionItemDto itemDto = new Get2AConsoForSectionItemDto();
						itemDto.setIgstAmt(totalImpgSezDto.getItemIgstAmt());
						itemDto.setCessAmt(totalImpgSezDto.getItemCessAmt());
						itemDtos.add(itemDto);
					});
				}
				lineItemDto.setItems(itemDtos);
				get2AConsoForSecDto.setLineItemDtos(lineItemDto);
				get2AConsoForSecDtos.add(get2AConsoForSecDto);
			});
		}
	}

	private static Pair<String, String> setModfDtTm(String modOrigStr) {

		LocalDateTime dateTime = null;

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

		try {
			dateTime = LocalDateTime.parse(modOrigStr, formatter);
		} catch (Exception e) {
			LOGGER.error("Error occured while deserializing the date");
		}
		try {
			dateTime = LocalDateTime.parse(modOrigStr, formatter1);
		} catch (Exception e) {
			LOGGER.error("Error occured while deserializing the date");
		}

		LocalDateTime istDateTime = EYDateUtil.toISTDateTimeFromUTC(dateTime);
		String modfDate = EYDateUtil.fmtDateOnly(istDateTime);
		String modfTime = EYDateUtil.fmtTimeOnly(istDateTime);

		return new Pair<String, String>(modfDate, modfTime);
	}

}
