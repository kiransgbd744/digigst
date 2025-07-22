package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
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
import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnaGstnRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionFinalDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionItemDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionLineItemDto;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionTotalDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("Get6AConsoForSectionServiceImpl")
@Slf4j
public class Get6AConsoForSectionServiceImpl
		implements Get6AConsoForSectionService {

	@Autowired
	@Qualifier("Get6AConsoForSectionDaoImpl")
	private Get6AConsoForSectionDaoImpl daoImpl;

	@Autowired
	@Qualifier("Gstr6aGetB2bGstnRepository")
	private Gstr6aGetB2bGstnRepository b2bRepository;

	@Autowired
	@Qualifier("Gstr6aGetB2baGstnRepository")
	private Gstr6aGetB2baGstnRepository b2baRepository;

	@Autowired
	@Qualifier("Gstr6aGetCdnGstnRepository")
	private Gstr6aGetCdnGstnRepository cdnRepository;

	@Autowired
	@Qualifier("Gstr6aGetCdnaGstnRepository")
	private Gstr6aGetCdnaGstnRepository cdnaRepository;

	@Autowired
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	// GetGstr2aCdnaInvoicesHeaderRepository

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";
	private static final String BLANK = null;

	public List<Long> findChunkIds(final String gstin, final String retPeriod,
			final String section, Long batchId) {
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
		}
		return erpBatchIds;

	}

	/*
	 * Get a Get GSTR 2A Information Based on Tin,tax period, Batch Id, ERP
	 * Batch Id for each section of different call.
	 * 
	 */
	public Get2AConsoForSectionFinalDto findERPGet6AConsoForSection(
			final String gstin, final String retPeriod, final String section,
			Long batchId, final Long erpBatchId) {

		Get2AConsoForSectionFinalDto finalDto = new Get2AConsoForSectionFinalDto();
		List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos = new ArrayList<>();
		List<Object[]> objs = daoImpl.findGet6AConsoForSection(gstin, retPeriod,
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
		}
		finalDto.setGet2AConsSecDtos(get2AConsoForSecDtos);
		return finalDto;
	}

	private void mapObjToB2BSection(
			List<Get2AConsoForSectionHeaderDto> get2AConsoForSecDtos,
			List<Object[]> objs, String entityName, String entityPan) {
		LOGGER.debug("Obj Size{} ", objs.size());
		List<Get2AConsoForSectionTotalDto> get2AConsoSecTotalDtos = new ArrayList<>();
		Set<Long> get2AIds = new HashSet<>();
		objs.forEach(obj -> {
			Get2AConsoForSectionTotalDto get2AConsoForSecDto = new Get2AConsoForSectionTotalDto();
			get2AConsoForSecDto.setSectionName(B2B);
			get2AConsoForSecDto
					.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : BLANK);
			get2AConsoForSecDto
					.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setCtin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : BLANK);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("CFS {} ",
					obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("File Period {} ",
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);
			get2AConsoForSecDto.setFilePeriod(
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);

			LOGGER.debug("Dto for CFS and FilePeriod  {} ",
					get2AConsoForSecDto);

			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : BLANK);
			get2AConsoForSecDto.setSuppInvNum(
					obj[5] != null ? String.valueOf(obj[5]) : BLANK);

			get2AConsoForSecDto.setSuppInvDate(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);
			get2AConsoForSecDto.setSuppInvVal(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			get2AConsoForSecDto
					.setPos(obj[8] != null ? String.valueOf(obj[8]) : BLANK);
			get2AConsoForSecDto
					.setRchrg(obj[9] != null ? String.valueOf(obj[9]) : BLANK);
			get2AConsoForSecDto.setInvType(
					obj[10] != null ? String.valueOf(obj[10]) : BLANK);

			get2AConsoForSecDto.setGetBatchId(
					obj[11] != null ? String.valueOf(obj[11]) : BLANK);

			get2AConsoForSecDto.setIgstAmt(
					obj[12] != null ? new BigDecimal(String.valueOf(obj[12]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCessAmt(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setIrnNum(
					obj[18] != null ? String.valueOf(obj[18]) : BLANK);
			get2AConsoForSecDto.setIrnGenDate(
					obj[19] != null ? String.valueOf(obj[19]) : BLANK);
			get2AConsoForSecDto.setIrnSourceType(
					obj[20] != null ? String.valueOf(obj[20]) : BLANK);
			get2AConsoForSecDto.setInvStatus(
					obj[21] != null ? String.valueOf(obj[21]) : BLANK);
			// get2AConsoForSecDto.setInvStatus(
			// obj[32] != null ? String.valueOf(obj[32]) : BLANK);
			String invKey = obj[22] != null ? String.valueOf(obj[22]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setItemNumber(
					obj[23] != null ? String.valueOf(obj[23]) : BLANK);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[24] != null ? new BigDecimal(String.valueOf(obj[24]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCgstAmt(
					obj[25] != null ? new BigDecimal(String.valueOf(obj[25]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemSgstAmt(
					obj[26] != null ? new BigDecimal(String.valueOf(obj[26]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCessAmt(
					obj[27] != null ? new BigDecimal(String.valueOf(obj[27]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[28] != null ? new BigDecimal(String.valueOf(obj[28]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[29] != null ? new BigDecimal(String.valueOf(obj[29]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[30] != null ? new Long(String.valueOf(obj[30])) : null);
			Long id = obj[30] != null ? new Long(String.valueOf(obj[30]))
					: null;
			get2AIds.add(id);

			get2AConsoForSecDto.setSuppTdLgName(
					obj[31] != null ? String.valueOf(obj[31]) : BLANK);
			get2AConsoForSecDto.setTradeName(
					obj[32] != null ? String.valueOf(obj[32]) : BLANK);
			get2AConsoForSecDto.setModifiedTime(
					obj[33] != null ? String.valueOf(obj[33]) : BLANK);
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
						LOGGER.debug("CFS {} ", totalB2bDto.getCfs());
						LOGGER.debug("File Period {} ",
								totalB2bDto.getFilePeriod());
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
								.setSuppTdLgName(totalB2bDto.getSuppTdLgName());
						get2AConsoForSecDto
								.setFilePeriod(totalB2bDto.getFilePeriod());
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

						get2AConsoForSecDto
								.setFilePeriod(totalB2bDto.getFilePeriod());
						if (!Strings
								.isNullOrEmpty(totalB2bDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalB2bDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}

						get2AConsoForSecDto.setRetType("6A");
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
					.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : BLANK);
			get2AConsoForSecDto
					.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setCtin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : BLANK);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("CFS {} ",
					obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("Dto for CFS and FilePeriod  {} ",
					get2AConsoForSecDto);
			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : BLANK);
			get2AConsoForSecDto.setSuppInvNum(
					obj[5] != null ? String.valueOf(obj[5]) : BLANK);

			get2AConsoForSecDto.setSuppInvDate(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);
			get2AConsoForSecDto.setSuppInvVal(obj[7] != null
					? new BigDecimal(String.valueOf(obj[7])) : BigDecimal.ZERO);
			get2AConsoForSecDto
					.setPos(obj[8] != null ? String.valueOf(obj[8]) : BLANK);
			get2AConsoForSecDto
					.setRchrg(obj[9] != null ? String.valueOf(obj[9]) : BLANK);
			get2AConsoForSecDto.setInvType(
					obj[10] != null ? String.valueOf(obj[10]) : BLANK);

			get2AConsoForSecDto.setGetBatchId(
					obj[11] != null ? String.valueOf(obj[11]) : BLANK);

			get2AConsoForSecDto.setIgstAmt(
					obj[12] != null ? new BigDecimal(String.valueOf(obj[12]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCessAmt(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setTaxableValue(
					obj[16] != null ? new BigDecimal(String.valueOf(obj[16]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setInvStatus(
					obj[18] != null ? String.valueOf(obj[18]) : BLANK);

			String invKey = obj[19] != null ? String.valueOf(obj[19]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}

			get2AConsoForSecDto.setOrgInvNum(
					obj[20] != null ? String.valueOf(obj[20]) : BLANK);

			get2AConsoForSecDto.setOrgInvDate(
					obj[21] != null ? String.valueOf(obj[21]) : BLANK);

			get2AConsoForSecDto.setItemNumber(
					obj[22] != null ? String.valueOf(obj[22]) : BLANK);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[23] != null ? new BigDecimal(String.valueOf(obj[23]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCgstAmt(
					obj[24] != null ? new BigDecimal(String.valueOf(obj[24]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemSgstAmt(
					obj[25] != null ? new BigDecimal(String.valueOf(obj[25]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCessAmt(
					obj[26] != null ? new BigDecimal(String.valueOf(obj[26]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[27] != null ? new BigDecimal(String.valueOf(obj[27]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[28] != null ? new BigDecimal(String.valueOf(obj[28]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[30] != null ? new Long(String.valueOf(obj[30])) : null);
			Long id = obj[30] != null ? new Long(String.valueOf(obj[30]))
					: null;
			get2AIds.add(id);

			get2AConsoForSecDto.setSuppTdLgName(
					obj[31] != null ? String.valueOf(obj[31]) : BLANK);
			get2AConsoForSecDto.setTradeName(
					obj[32] != null ? String.valueOf(obj[32]) : BLANK);
			get2AConsoForSecDto.setModifiedTime(
					obj[33] != null ? String.valueOf(obj[33]) : BLANK);

			LOGGER.debug("File Period {} ",
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);
			get2AConsoForSecDto.setFilePeriod(
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);

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

						get2AConsoForSecDto.setSuppTdLgName(
								totalB2baDto.getSuppTdLgName());
						get2AConsoForSecDto
								.setInvStatus(totalB2baDto.getInvStatus());

						get2AConsoForSecDto.setInvKey(totalB2baDto.getInvKey());
						get2AConsoForSecDto
								.setOrgInvNum(totalB2baDto.getOrgInvNum());
						get2AConsoForSecDto
								.setOrgInvDate(totalB2baDto.getOrgInvDate());
						get2AConsoForSecDto
								.setTradeName(totalB2baDto.getTradeName());
						get2AConsoForSecDto
								.setFilePeriod(totalB2baDto.getFilePeriod());
						if (!Strings.isNullOrEmpty(
								totalB2baDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalB2baDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}

						get2AConsoForSecDto.setRetType("6A");
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
					.setCtin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : BLANK);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : BLANK);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("CFS {} ",
					obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			LOGGER.debug("File Period {} ",
					obj[35] != null ? String.valueOf(obj[35]) : BLANK);
			get2AConsoForSecDto.setFilePeriod(
					obj[35] != null ? String.valueOf(obj[35]) : BLANK);

			LOGGER.debug("Dto for CFS and FilePeriod  {} ",
					get2AConsoForSecDto);

			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : BLANK);

			get2AConsoForSecDto.setNoteType(
					obj[5] != null ? String.valueOf(obj[5]) : BLANK);

			get2AConsoForSecDto.setNoteNumber(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);

			get2AConsoForSecDto.setNoteDate(
					obj[7] != null ? String.valueOf(obj[7]) : BLANK);
			get2AConsoForSecDto
					.setPgst(obj[8] != null ? String.valueOf(obj[8]) : BLANK);

			get2AConsoForSecDto.setInvNumber(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);

			get2AConsoForSecDto.setInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : BLANK);

			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);

			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : BLANK);

			get2AConsoForSecDto.setGetBatchId(
					obj[10] != null ? String.valueOf(obj[10]) : BLANK);
			get2AConsoForSecDto.setIgstAmt(
					obj[11] != null ? new BigDecimal(String.valueOf(obj[11]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setCgstAmt(
					obj[12] != null ? new BigDecimal(String.valueOf(obj[12]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSgstAmt(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setCessAmt(
					obj[14] != null ? new BigDecimal(String.valueOf(obj[14]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxableValue(
					obj[15] != null ? new BigDecimal(String.valueOf(obj[15]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setSuppInvVal(
					obj[17] != null ? new BigDecimal(String.valueOf(obj[17]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto
					.setPos(obj[18] != null ? String.valueOf(obj[18]) : BLANK);

			get2AConsoForSecDto.setIrnNum(
					obj[19] != null ? String.valueOf(obj[19]) : BLANK);

			get2AConsoForSecDto.setIrnGenDate(
					obj[20] != null ? String.valueOf(obj[20]) : BLANK);

			get2AConsoForSecDto.setInvStatus(
					obj[21] != null ? String.valueOf(obj[21]) : BLANK);

			String invKey = obj[22] != null ? String.valueOf(obj[22]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}

			get2AConsoForSecDto.setItemNumber(
					obj[23] != null ? String.valueOf(obj[23]) : BLANK);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[24] != null ? new BigDecimal(String.valueOf(obj[24]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCgstAmt(
					obj[25] != null ? new BigDecimal(String.valueOf(obj[25]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemSgstAmt(
					obj[26] != null ? new BigDecimal(String.valueOf(obj[26]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[27] != null ? new BigDecimal(String.valueOf(obj[27]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemTaxableValue(
					obj[28] != null ? new BigDecimal(String.valueOf(obj[28]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[29] != null ? new BigDecimal(String.valueOf(obj[29]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[30] != null ? new Long(String.valueOf(obj[30])) : null);
			Long id = obj[30] != null ? new Long(String.valueOf(obj[30]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[31] != null ? String.valueOf(obj[31]) : BLANK);
			get2AConsoForSecDto.setTradeName(
					obj[32] != null ? String.valueOf(obj[32]) : BLANK);
			get2AConsoForSecDto.setDflag(
					obj[33] != null ? String.valueOf(obj[33]) : BLANK);
			get2AConsoForSecDto.setInvType(
					obj[5] != null ? String.valueOf(obj[5]) : BLANK);
			get2AConsoForSecDto.setModifiedTime(
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);
			get2AConsoForSecDto.setFilePeriod(
					obj[35] != null ? String.valueOf(obj[35]) : BLANK);

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
						get2AConsoForSecDto.setChksum(totalCdnDto.getChksum());
						get2AConsoForSecDto
								.setInvType(totalCdnDto.getInvType());
						get2AConsoForSecDto
								.setSuppInvNum(totalCdnDto.getSuppInvNum());
						get2AConsoForSecDto
								.setSuppInvDate(totalCdnDto.getSuppInvDate());
						get2AConsoForSecDto
								.setSuppInvVal(totalCdnDto.getSuppInvVal());
						get2AConsoForSecDto.setPgst(totalCdnDto.getPgst());
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
						get2AConsoForSecDto
								.setTaxableValue(totalCdnDto.getTaxableValue());
						get2AConsoForSecDto
								.setSuppTdLgName(totalCdnDto.getSuppTdLgName());
						get2AConsoForSecDto.setPos(totalCdnDto.getPos());
						get2AConsoForSecDto.setIrnNum(totalCdnDto.getIrnNum());
						get2AConsoForSecDto
								.setIrnGenDate(totalCdnDto.getIrnGenDate());
						get2AConsoForSecDto.setIrnSourceType(
								totalCdnDto.getIrnSourceType());
						get2AConsoForSecDto
								.setInvStatus(totalCdnDto.getInvStatus());
						get2AConsoForSecDto
								.setTradeName(totalCdnDto.getTradeName());
						get2AConsoForSecDto
								.setFilePeriod(totalCdnDto.getFilePeriod());
						get2AConsoForSecDto.setCfs(totalCdnDto.getCfs());
						if (!Strings
								.isNullOrEmpty(totalCdnDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalCdnDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}
						get2AConsoForSecDto.setRetType("6A");
						get2AConsoForSecDto.setEntityName(entityName);
						get2AConsoForSecDto.setEntityPan(entityPan);
						get2AConsoForSecDto.setInvKey(totalCdnDto.getInvKey());
						get2AConsoForSecDto
								.setNoteType(totalCdnDto.getNoteType());
						get2AConsoForSecDto
								.setNoteNumber(totalCdnDto.getNoteNumber());
						get2AConsoForSecDto
								.setNoteDate(totalCdnDto.getNoteDate());
						get2AConsoForSecDto.setDflag(totalCdnDto.getDflag());
						get2AConsoForSecDto
								.setInvType(totalCdnDto.getInvType());
						get2AConsoForSecDto
								.setInvNumber(totalCdnDto.getInvNumber());
						get2AConsoForSecDto
								.setInvDate(totalCdnDto.getInvDate());
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
					.setCtin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : BLANK);
			get2AConsoForSecDto
					.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : BLANK);
			get2AConsoForSecDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : BLANK);
			get2AConsoForSecDto
					.setCfs(obj[3] != null ? String.valueOf(obj[3]) : BLANK);

			get2AConsoForSecDto
					.setChksum(obj[4] != null ? String.valueOf(obj[4]) : BLANK);

			get2AConsoForSecDto.setNoteType(
					obj[5] != null ? String.valueOf(obj[5]) : BLANK);
			get2AConsoForSecDto.setNoteNumber(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);

			get2AConsoForSecDto.setNoteDate(
					obj[7] != null ? String.valueOf(obj[7]) : BLANK);

			get2AConsoForSecDto.setSuppInvNum(
					obj[6] != null ? String.valueOf(obj[6]) : BLANK);

			get2AConsoForSecDto.setSuppInvDate(
					obj[7] != null ? String.valueOf(obj[7]) : BLANK);

			get2AConsoForSecDto.setOrgNoteNumber(
					obj[8] != null ? String.valueOf(obj[8]) : BLANK);

			get2AConsoForSecDto.setOrgNoteDate(
					obj[9] != null ? String.valueOf(obj[9]) : BLANK);

			get2AConsoForSecDto.setInvNumber(
					obj[10] != null ? String.valueOf(obj[10]) : BLANK);
			get2AConsoForSecDto
					.setPgst(obj[11] != null ? String.valueOf(obj[11]) : BLANK);

			get2AConsoForSecDto.setInvDate(
					obj[12] != null ? String.valueOf(obj[12]) : BLANK);
			get2AConsoForSecDto.setGetBatchId(
					obj[13] != null ? String.valueOf(obj[13]) : BLANK);

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

			get2AConsoForSecDto.setTaxableValue(
					obj[18] != null ? new BigDecimal(String.valueOf(obj[18]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto
					.setPos(obj[20] != null ? String.valueOf(obj[20]) : BLANK);

			get2AConsoForSecDto.setInvStatus(
					obj[21] != null ? String.valueOf(obj[21]) : BLANK);

			String invKey = obj[22] != null ? String.valueOf(obj[22]) : null;
			if (invKey != null && !invKey.isEmpty()) {
				get2AConsoForSecDto.setInvKey(invKey.toUpperCase());
			}
			get2AConsoForSecDto.setItemNumber(
					obj[23] != null ? String.valueOf(obj[23]) : BLANK);
			get2AConsoForSecDto.setItemIgstAmt(
					obj[24] != null ? new BigDecimal(String.valueOf(obj[24]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemCgstAmt(
					obj[25] != null ? new BigDecimal(String.valueOf(obj[25]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setItemSgstAmt(
					obj[26] != null ? new BigDecimal(String.valueOf(obj[26]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemCessAmt(
					obj[27] != null ? new BigDecimal(String.valueOf(obj[27]))
							: BigDecimal.ZERO);

			get2AConsoForSecDto.setItemTaxableValue(
					obj[28] != null ? new BigDecimal(String.valueOf(obj[28]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setTaxRate(
					obj[29] != null ? new BigDecimal(String.valueOf(obj[29]))
							: BigDecimal.ZERO);
			get2AConsoForSecDto.setId(
					obj[30] != null ? new Long(String.valueOf(obj[30])) : null);
			Long id = obj[30] != null ? new Long(String.valueOf(obj[30]))
					: null;
			if (id != null) {
				get2AIds.add(id);
			}
			get2AConsoForSecDto.setSuppTdLgName(
					obj[31] != null ? String.valueOf(obj[31]) : BLANK);
			get2AConsoForSecDto.setTradeName(
					obj[32] != null ? String.valueOf(obj[32]) : BLANK);
			get2AConsoForSecDto.setDflag(
					obj[33] != null ? String.valueOf(obj[33]) : BLANK);

			get2AConsoForSecDto.setSuppInvNum(
					obj[34] != null ? String.valueOf(obj[34]) : BLANK);

			get2AConsoForSecDto.setModifiedTime(
					obj[35] != null ? String.valueOf(obj[35]) : BLANK);
			get2AConsoForSecDto.setFilePeriod(
					obj[36] != null ? String.valueOf(obj[36]) : BLANK);

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
						get2AConsoForSecDto
								.setFilePeriod(totalCdnaDto.getFilePeriod());
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
								.setSupplyType(totalCdnaDto.getSupplyType());
						get2AConsoForSecDto
								.setInvStatus(totalCdnaDto.getInvStatus());
						get2AConsoForSecDto.setInvKey(totalCdnaDto.getInvKey());
						get2AConsoForSecDto
								.setTradeName(totalCdnaDto.getTradeName());
						get2AConsoForSecDto.setDflag(totalCdnaDto.getDflag());

						if (!Strings.isNullOrEmpty(
								totalCdnaDto.getModifiedTime())) {
							Pair<String, String> modfData = setModfDtTm(
									totalCdnaDto.getModifiedTime());
							get2AConsoForSecDto
									.setModfOnDate(modfData.getValue0());
							get2AConsoForSecDto
									.setModfOnTime(modfData.getValue1());
						}

						get2AConsoForSecDto.setRetType("6A");
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

	private static Pair<String, String> setModfDtTm(String modOrigStr) {

		LOGGER.debug("Date {} ", modOrigStr);

		LocalDateTime dateTime = null;

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		DateTimeFormatter formatter1 = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

		try {
			dateTime = LocalDateTime.parse(modOrigStr, formatter);
		} catch (Exception e) {
			LOGGER.error(
					"Error occured while deserializing the date, going to next formatter",
					e);
		}
		try {
			dateTime = LocalDateTime.parse(modOrigStr, formatter1);
		} catch (Exception e) {
			LOGGER.error("Error occured while deserializing the date", e);
		}

		LocalDateTime istDateTime = EYDateUtil.toISTDateTimeFromUTC(dateTime);
		String modfDate = EYDateUtil.fmtDateOnly(istDateTime);
		String modfTime = EYDateUtil.fmtTimeOnly(istDateTime);

		return new Pair<String, String>(modfDate, modfTime);
	}

}
