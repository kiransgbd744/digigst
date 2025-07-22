/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsRecodsDataDaoImpl")
public class ImsRecodsDataDaoImpl implements ImsRecodsDataDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	private List<String> actionList = ImmutableList.of("P", "A", "R", "N");
	
	private static final List<String> tableTypeList = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CDN, APIConstants.IMS_TYPE_CDNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);
	
	private List<String> docTypeList = ImmutableList.of("INV", "CR", "DR", "RNV", "RCR", "RDR");

	private List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);

	@Override
	public Pair<List<ImsRecordDataResponseDto>, Integer> findImsRecodsScreenData(
			ImsRecordDataRequestDto reqDto, Pageable pageReq) {

		LOGGER.debug("Inside ImsRecodsDataDaoImap.findImsRecodsScreenData() "
				+ " methode reqDto {} :", reqDto.toString());

		List<ImsRecordDataResponseDto> respList = new ArrayList<>();
		Pair<List<ImsRecordDataResponseDto>, Integer> resp = null;
		Integer totalCount = 0;

		try {

			List<String> gstins = reqDto.getGstins();
			// if GSTINs are empty getting it from entity

			List<String> gstnsList = new ArrayList<>();

			if (CollectionUtils.isEmpty(gstins)) {
				List<Long> entityIds = new ArrayList<>();
				entityIds.add(reqDto.getEntityId());
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);

				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, regTypeList);

				reqDto.setGstins(gstnsList);

				LOGGER.debug("GSTINs were empty, fetched GSTINs based on "
						+ " entityId: {}", gstnsList);
			}

			List<String> actionDigiGsts = reqDto.getActionDigiGsts();
			if(actionDigiGsts == null || actionDigiGsts.isEmpty()) {
				actionDigiGsts = actionList;
			}
			
			List<String> actionGstns = reqDto.getActionGstns();
			if(actionGstns == null || actionGstns.isEmpty()) {
				actionGstns = actionList;
			}
			
			
			List<String> docNumbers = reqDto.getDocNumbers();
			if(docNumbers == null || docNumbers.isEmpty()) {
				docNumbers = Arrays.asList("");
			}
			
			List<String> docTypes = reqDto.getDocTypes();
			if(docTypes == null || docTypes.isEmpty()) {
				
				docTypes = docTypeList;
			}
			
			String fromDocDate = reqDto.getFromDocDate();
			String toDocDate = reqDto.getToDocDate();
			
			List<String> imsUniqueIds = reqDto.getImsUniqueIds();
			if(imsUniqueIds == null || imsUniqueIds.isEmpty()) {
				imsUniqueIds = Arrays.asList("");
			}
			
			List<String> tableTypes = reqDto.getTableTypes();
			if(tableTypes == null || tableTypes.isEmpty()) {
				
				tableTypes = tableTypeList;
			}
			
			List<String> vendorGstins = reqDto.getVendorGstins();
			if(vendorGstins == null || vendorGstins.isEmpty()) {
				vendorGstins = Arrays.asList("");
			}
			
			List<String> vendorPans = reqDto.getVendorPans();
			if(vendorPans == null || vendorPans.isEmpty()) {
				vendorPans = Arrays.asList("");
			}
			
			int pageNumber = pageReq.getPageNumber();
			int pageSize = pageReq.getPageSize();
			gstins = reqDto.getGstins();

			StoredProcedureQuery storedProc = procCall(actionDigiGsts,
					actionGstns, docNumbers, docTypes, fromDocDate, toDocDate,
					gstins, imsUniqueIds, tableTypes, vendorGstins, vendorPans,
					pageNumber, pageSize);

			long dbLoadStartTime = System.currentTimeMillis();

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();

			long dbLoadEndTime = System.currentTimeMillis();

			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStartTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to load the Data"
								+ " from DB is '%d' millisecs,"
								+ " proc Name and Data count:" + " '%s' - '%s'",
						dbLoadTimeDiff, "USP_IMS_RECORDS_SCREEN_UI",
						records.size());
				LOGGER.debug(msg);
			}
			if (records != null && !records.isEmpty()) {

				respList = records.stream().map(o -> convert(o))
						.collect(Collectors.toCollection(ArrayList::new));

				totalCount = respList.get(0).getTotalCount();
			}

			resp = new Pair<>(respList, totalCount);

		} catch (Exception e) {
			LOGGER.error("Error Occured inside ImsRecodsDataDaoImap {}", e);
			throw new AppException();
		}
		return resp;
	}

	private ImsRecordDataResponseDto convert(Object[] arr) {
		
		ImsRecordDataResponseDto dto = new ImsRecordDataResponseDto();
		
		try {
			
			dto.setActionGstn((arr[0] != null) ? arr[0].toString() : null);
			dto.setActionDigiGst((arr[1] != null) ? arr[1].toString() : null);
			dto.setGstinRecipient((arr[2] != null) ? arr[2].toString() : null);
			dto.setGstinSupplier((arr[3] != null) ? arr[3].toString() : null);
			dto.setDocType((arr[4] != null) ? arr[4].toString() : null);
			dto.setDocNumber((arr[5] != null) ? arr[5].toString() : null);
			dto.setDocDate((arr[6] != null) ? arr[6].toString() : null);
			dto.setImsUniqueId((arr[7] != null) ? arr[7].toString() : null);
			dto.setTableType((arr[8] != null) ? arr[8].toString() : null);
			dto.setTaxableValue(
					(arr[9] != null) ? new BigDecimal(arr[9].toString())
							: BigDecimal.ZERO);
			dto.setIgst((arr[10] != null) ? new BigDecimal(arr[10].toString())
					: BigDecimal.ZERO);
			dto.setCgst((arr[11] != null) ? new BigDecimal(arr[11].toString())
					: BigDecimal.ZERO);
			dto.setSgst((arr[12] != null) ? new BigDecimal(arr[12].toString())
					: BigDecimal.ZERO);
			dto.setCess((arr[13] != null) ? new BigDecimal(arr[13].toString())
					: BigDecimal.ZERO);
			dto.setTotalTax(
					(arr[14] != null) ? new BigDecimal(arr[14].toString())
							: BigDecimal.ZERO);
			dto.setInvoiceValue(
					(arr[15] != null) ? new BigDecimal(arr[15].toString())
							: BigDecimal.ZERO);
			dto.setPos((arr[16] != null) ? arr[16].toString() : null);
			dto.setSupplierLegalName(
					(arr[17] != null) ? arr[17].toString() : null);
			dto.setSupplierTradeName(
					(arr[18] != null) ? arr[18].toString() : null);
			dto.setFormType((arr[19] != null) ? arr[19].toString() : null);
			dto.setGstr1FillingStatus(
					(arr[20] != null) ? arr[20].toString() : null);
			dto.setGstr1FillingPeriod(
					(arr[21] != null) ? arr[21].toString() : null);
			dto.setOrgDocNumber((arr[22] != null) ? arr[22].toString() : null);
			dto.setOrgDocDate((arr[23] != null) ? arr[23].toString() : null);
			dto.setPendingActionBlocked((arr[24] != null) ? arr[24].toString() : null);
			dto.setCheckSum((arr[25] != null) ? arr[25].toString() : null);
			dto.setGetCallDateTime(
					(arr[26] != null) ? arr[26].toString() : null);
			dto.setTotalCount(
					(arr[29] != null) ? Integer.parseInt(arr[29].toString())
							: 0);
			
			dto.setActionDigiGstTimestamp((arr[27] != null) ? arr[27].toString() : null);
			dto.setDockey((arr[28] != null) ? arr[28].toString() : null);

			dto.setActionGstnTimestamp(dto.getGetCallDateTime());
			//Ims newly added columns
			dto.setItcReductionRequired(
					(arr[30] != null) ? arr[30].toString() : null);
			dto.setIgstDeclToReduceItc(
					(arr[31] != null) ? new BigDecimal(arr[31].toString())
							: BigDecimal.ZERO);
			dto.setCgstDeclToReduceItc(
					(arr[32] != null) ? new BigDecimal(arr[32].toString())
							: BigDecimal.ZERO);
			dto.setSgstDeclToReduceItc(
					(arr[33] != null) ? new BigDecimal(arr[33].toString())
							: BigDecimal.ZERO);
			dto.setCessDeclToReduceItc(
					(arr[34] != null) ? new BigDecimal(arr[34].toString())
							: BigDecimal.ZERO);
			dto.setImsResponseRemarks(
					(arr[35] != null) ? arr[35].toString() : null);
			
		} catch (Exception e) {
			LOGGER.error(
					"Error Occurred in convert method ImsRecodsDataDaoImpl {} :",
					e);
			throw new AppException(e);
		}
		return dto;
	}

	private StoredProcedureQuery procCall(List<String> actionDigiGsts,
			List<String> actionGstns, List<String> docNumbers,
			List<String> docTypes, String fromDocDate, String toDocDate,
			List<String> gstins, List<String> imsUniqueIds,
			List<String> tableTypes, List<String> vendorGstins,
			List<String> vendorPans, int pageNumber, int pageSize) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_IMS_RECORDS_SCREEN_UI");

		if (LOGGER.isDebugEnabled()) {
			String msg = ("About to execute USP_IMS_RECORDS_SCREEN_UI proc");
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("IN_RECIPIENT_GSTIN",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("FROM_DOC_DATE",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("TO_DOC_DATE", String.class,
				ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_ACTION_GSTIN",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_ACTION_DIGI",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_TABLE_TYPE",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_DOC_TYPE", String.class,
				ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_DOC_NUM", String.class,
				ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_VENDORPANS",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_VENDORGSTINS",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_IMS_UNIQUE_ID",
				String.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_PAGESIZE",
				Integer.class, ParameterMode.IN);
		storedProc.registerStoredProcedureParameter("IN_PAGENO", Integer.class,
				ParameterMode.IN);

		storedProc.setParameter("IN_RECIPIENT_GSTIN", String.join(",", gstins));
		storedProc.setParameter("FROM_DOC_DATE", fromDocDate);
		storedProc.setParameter("TO_DOC_DATE", toDocDate);
		storedProc.setParameter("IN_ACTION_GSTIN",
				String.join(",", actionGstns));
		storedProc.setParameter("IN_ACTION_DIGI",
				String.join(",", actionDigiGsts));
		storedProc.setParameter("IN_TABLE_TYPE", String.join(",", tableTypes));
		storedProc.setParameter("IN_DOC_TYPE", String.join(",", docTypes));
		storedProc.setParameter("IN_DOC_NUM", String.join(",", docNumbers));
		storedProc.setParameter("IN_VENDORPANS", String.join(",", vendorPans));
		storedProc.setParameter("IN_VENDORGSTINS",
				String.join(",", vendorGstins));
		storedProc.setParameter("IN_IMS_UNIQUE_ID",
				String.join(",", imsUniqueIds));
		storedProc.setParameter("IN_PAGESIZE", pageSize);
		storedProc.setParameter("IN_PAGENO", pageNumber);

		return storedProc;

	}

}
