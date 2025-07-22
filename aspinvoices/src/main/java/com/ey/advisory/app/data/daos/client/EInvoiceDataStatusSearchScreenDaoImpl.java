package com.ey.advisory.app.data.daos.client;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Anx1NewDataStatusEntity;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("EInvoiceDataStatusSearchScreenDaoImpl")
public class EInvoiceDataStatusSearchScreenDaoImpl
		implements EInvoiceDataStatusSearchScreenDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public static final String DATATYPE = "dataType";
	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";

	public static final String ITC_04 = "ITC-04";
	
	public static final String GSTR7_TXN = "gstr7txn";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EInvoiceDataStatusSearchScreenDaoImpl.class);

	@Override
	public List<Anx1NewDataStatusEntity> dataAnx1NewStatusSection(
			String sectionType, DataStatusSearchReqDto req, String buildQuery,
			String dataType, Object dataRecvFrom, Object dataRecvTo) {

		LocalDate documentDateFrom = req.getDocumentDateFrom();
		LocalDate documentDateTo = req.getDocumentDateTo();
		LocalDate accVoucherDateFrom = req.getAccVoucherDateFrom();
		LocalDate accVoucherDateTo = req.getAccVoucherDateTo();
		
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String profitCenter = null;
		String gstin = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;

		List<String> pcList = null;
		List<String> gstinList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					if (dataSecAttrs.get(OnboardingConstant.PC) != null
							&& !dataSecAttrs.get(OnboardingConstant.PC)
									.isEmpty()) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (dataSecAttrs.get(OnboardingConstant.PLANT) != null
							&& !dataSecAttrs.get(OnboardingConstant.PLANT)
									.isEmpty()) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (dataSecAttrs.get(OnboardingConstant.DIVISION) != null
							&& !dataSecAttrs.get(OnboardingConstant.DIVISION)
									.isEmpty()) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (dataSecAttrs.get(OnboardingConstant.LOCATION) != null
							&& !dataSecAttrs.get(OnboardingConstant.LOCATION)
									.isEmpty()) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (dataSecAttrs.get(OnboardingConstant.SO) != null
							&& !dataSecAttrs.get(OnboardingConstant.SO)
									.isEmpty()) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& !dataSecAttrs.get(OnboardingConstant.PO)
									.isEmpty()) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& !dataSecAttrs.get(OnboardingConstant.DC)
									.isEmpty()) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD1)
									.isEmpty()) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD2)
									.isEmpty()) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD3)
									.isEmpty()) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD4)
									.isEmpty()) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD5)
									.isEmpty()) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& !dataSecAttrs.get(OnboardingConstant.UD6)
									.isEmpty()) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}

		}

		String queryStr = createNewQueryString(dataType, buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Query: {}", queryStr);
		}
		Query q = entityManager.createNativeQuery(queryStr);
		
		if ("vendor_payment".equalsIgnoreCase(dataType)
				|| GSTR7_TXN.equalsIgnoreCase(dataType)) {
			
			if (gstin != null && !gstin.isEmpty() && gstinList != null
					&& !gstinList.isEmpty()) {
				q.setParameter("gstinList", gstinList);
			}
		}else {

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& !pcList.isEmpty()) {
			q.setParameter("pcList", pcList);

		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& !plantList.isEmpty()) {
			q.setParameter("plantList", plantList);

		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& !salesList.isEmpty()) {
			q.setParameter("salesList", salesList);

		}
		if (gstin != null && !gstin.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);

		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& !divisionList.isEmpty()) {
			q.setParameter("divisionList", divisionList);
		}

		if (location != null && !location.isEmpty() && locationList != null
				&& !locationList.isEmpty()) {
			q.setParameter("locationList", locationList);

		}
		if (purchase != null && !purchase.isEmpty() && purcList != null
				&& !purcList.isEmpty()) {
			q.setParameter("purcList", purcList);
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& !distList.isEmpty()) {
			q.setParameter("distList", distList);

		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& !ud1List.isEmpty()) {
			q.setParameter("ud1List", ud1List);
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& !ud2List.isEmpty()) {
			q.setParameter("ud2List", ud2List);
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& !ud3List.isEmpty()) {
			q.setParameter("ud3List", ud3List);
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& !ud4List.isEmpty()) {
			q.setParameter("ud4List", ud4List);
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& !ud5List.isEmpty()) {
			q.setParameter("ud5List", ud5List);
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& !ud6List.isEmpty()) {
			q.setParameter("ud6List", ud6List);
		}
		}
		if (sectionType != null) {
			if ("DATA_RECEIVED".equals(sectionType)) {
				q.setParameter("recivedFromDate", dataRecvFrom);
				q.setParameter("recivedToDate", dataRecvTo);
			} else if ("DOCUMENT_DATE".equals(sectionType)
					&& ("inward".equalsIgnoreCase(dataType)
							|| "outward".equalsIgnoreCase(dataType))) {

				q.setParameter("documentFromDate", documentDateFrom);
				q.setParameter("documentToDate", documentDateTo);
			} else if ("ACCOUNTING_VOUCHER_DATE".equals(sectionType)
					&& ("inward".equalsIgnoreCase(dataType)
							|| "outward".equalsIgnoreCase(dataType))) {
				q.setParameter("accVoucherFromDate", accVoucherDateFrom);
				q.setParameter("accVoucherToDate", accVoucherDateTo);
			} else {
				int firstDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dataRecvFrom.toString());
				int secondDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dataRecvTo.toString());
				q.setParameter("retunPeriodFrom", firstDerRetPeriod);
				q.setParameter("retunPeriodTo", secondDerRetPeriod);
			}
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<Anx1NewDataStatusEntity> retList = new ArrayList<>();

		/*
		 * List<Anx1NewDataStatusEntity> outwardEwbIdAndEInvIds =
		 * outwardEwbIdAndEInvId( sectionType, buildQuery, dataRecvFrom,
		 * dataRecvTo, profitCenter, gstin, plant, sales, division, location,
		 * purchase, distChannel, ud1, ud2, ud3, ud4, ud5, ud6, pcList,
		 * gstinList, plantList, divisionList, locationList, salesList,
		 * purcList, distList, ud1List, ud2List, ud3List, ud4List, ud5List,
		 * ud6List);
		 * 
		 * Map<LocalDate, List<Anx1NewDataStatusEntity>>
		 * mapmapOutwardEwbIdAndEInvId = mapOutwardEwbIdAndEInvId(
		 * outwardEwbIdAndEInvIds);
		 */
		if (OUTWARD.equalsIgnoreCase(dataType)) {
			retList = list.stream().map(o -> convertOutward(o, null))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (INWARD.equalsIgnoreCase(dataType)) {
			retList = list.stream()
					.map(o -> convertInward(o, sectionType))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if (ITC_04.equalsIgnoreCase(dataType)) {
			retList = list.stream()
					.map(o -> convertItc04(o, sectionType))
					.collect(Collectors.toCollection(ArrayList::new));
		} else if ("vendor_payment".equalsIgnoreCase(dataType)
				|| GSTR7_TXN.equalsIgnoreCase(dataType)) {
			retList = list.stream()
					.map(o -> convertVendorPayment(o, sectionType))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Datastatus --> After Converting ResultSet to "
					+ "Dto Data in list -----> " + retList);
		}
		return retList;
	}

	private Map<LocalDate, List<Anx1NewDataStatusEntity>> mapOutwardEwbIdAndEInvId(
			List<Anx1NewDataStatusEntity> anx1DataStatusEntities) {
		Map<LocalDate, List<Anx1NewDataStatusEntity>> mapRecieveDateStatus = new HashMap<>();
		anx1DataStatusEntities.forEach(anx1DataStatusEntity -> {
			LocalDate recieveDate = anx1DataStatusEntity.getReceivedDate();
			if (mapRecieveDateStatus.containsKey(recieveDate)) {
				List<Anx1NewDataStatusEntity> statusEntities = mapRecieveDateStatus
						.get(recieveDate);
				statusEntities.add(anx1DataStatusEntity);
				mapRecieveDateStatus.put(recieveDate, statusEntities);
			} else {
				List<Anx1NewDataStatusEntity> statusEntities = new ArrayList<>();
				statusEntities.add(anx1DataStatusEntity);
				mapRecieveDateStatus.put(recieveDate, statusEntities);
			}
		});
		return mapRecieveDateStatus;
	}

	private List<Anx1NewDataStatusEntity> outwardEwbIdAndEInvId(
			String sectionType, String buildQuery, Object dataRecvFrom,
			Object dataRecvTo, String profitCenter, String gstin, String plant,
			String sales, String division, String location, String purchase,
			String distChannel, String ud1, String ud2, String ud3, String ud4,
			String ud5, String ud6, List<String> pcList, List<String> gstinList,
			List<String> plantList, List<String> divisionList,
			List<String> locationList, List<String> salesList,
			List<String> purcList, List<String> distList, List<String> ud1List,
			List<String> ud2List, List<String> ud3List, List<String> ud4List,
			List<String> ud5List, List<String> ud6List, Object documentDateFrom,
			Object documentDateTo, Object accVoucherDateFrom,
			Object accVoucherDateTo) {

		List<Anx1NewDataStatusEntity> statusEntities = new ArrayList<>();
		String outwardQueryPartA = outwarPartybQuery(buildQuery);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Outward Query outwardQueryPartA: {}",
					outwardQueryPartA);
		}
		Query q2 = entityManager.createNativeQuery(outwardQueryPartA);

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& !pcList.isEmpty()) {
			q2.setParameter("pcList", pcList);

		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& !plantList.isEmpty()) {
			q2.setParameter("plantList", plantList);

		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& !salesList.isEmpty()) {
			q2.setParameter("salesList", salesList);

		}
		if (gstin != null && !gstin.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q2.setParameter("gstinList", gstinList);

		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& !divisionList.isEmpty()) {
			q2.setParameter("divisionList", divisionList);
		}

		if (location != null && !location.isEmpty() && locationList != null
				&& !locationList.isEmpty()) {
			q2.setParameter("locationList", locationList);

		}
		if (purchase != null && !purchase.isEmpty() && purcList != null
				&& !purcList.isEmpty()) {
			q2.setParameter("purcList", purcList);
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& !distList.isEmpty()) {
			q2.setParameter("distList", distList);

		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& !ud1List.isEmpty()) {
			q2.setParameter("ud1List", ud1List);
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& !ud2List.isEmpty()) {
			q2.setParameter("ud2List", ud2List);
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& !ud3List.isEmpty()) {
			q2.setParameter("ud3List", ud3List);
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& !ud4List.isEmpty()) {
			q2.setParameter("ud4List", ud4List);
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& !ud5List.isEmpty()) {
			q2.setParameter("ud5List", ud5List);
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& !ud6List.isEmpty()) {
			q2.setParameter("ud6List", ud6List);
		}
		if (sectionType != null) {
			if ("DATA_RECEIVED".equals(sectionType)) {
				q2.setParameter("recivedFromDate", dataRecvFrom);
				q2.setParameter("recivedToDate", dataRecvTo);
			} else if ("DOCUMENT_DATE".equals(sectionType)) {
				q2.setParameter("documentFromDate", documentDateFrom);
				q2.setParameter("documentToDate", documentDateTo);
			} else if ("ACCOUNTING_VOUCHER_DATE".equals(sectionType)) {
				q2.setParameter("accVoucherFromDate", accVoucherDateFrom);
				q2.setParameter("accVoucherToDate", accVoucherDateTo);
			} else {

				int firstDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dataRecvFrom.toString());
				int secondDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dataRecvTo.toString());
				q2.setParameter("retunPeriodFrom", firstDerRetPeriod);
				q2.setParameter("retunPeriodTo", secondDerRetPeriod);
			}
		}
		@SuppressWarnings("unchecked")
		List<Object[]> outwardPartBObjs = q2.getResultList();

		outwardPartBObjs.forEach(outwardPartBObj -> {
			Anx1NewDataStatusEntity statusEntity = new Anx1NewDataStatusEntity();
			if (outwardPartBObj[0] != null
					&& !outwardPartBObj[0].toString().isEmpty()) {
				statusEntity.setEwbId(
						new BigInteger(String.valueOf(outwardPartBObj[0])));
			}

			if (outwardPartBObj[1] != null
					&& !outwardPartBObj[1].toString().isEmpty()) {
				statusEntity.setEinvId(
						new BigInteger(String.valueOf(outwardPartBObj[1])));
			}
			if (outwardPartBObj[2] != null
					&& !outwardPartBObj[2].toString().isEmpty()) {
				java.sql.Date recieveDate = (java.sql.Date) outwardPartBObj[2];
				statusEntity.setReceivedDate(recieveDate.toLocalDate());
			}
			statusEntities.add(statusEntity);
		});
		return statusEntities;
	}

	private Anx1NewDataStatusEntity convertOutward(Object[] arr,
			Map<LocalDate, List<Anx1NewDataStatusEntity>> mapOutwardEwbIdAndEInvId) {

		Anx1NewDataStatusEntity obj = new Anx1NewDataStatusEntity();

		java.sql.Date sqlRecieveDate = (java.sql.Date) arr[0];
		LocalDate recieveDate = null;
		if (sqlRecieveDate != null) {
			obj.setReceivedDate(sqlRecieveDate.toLocalDate());
			recieveDate = sqlRecieveDate.toLocalDate();
		}

		BigInteger b = GenUtil.getBigInteger(arr[2]);
		if (b != null) {
			obj.setTotalRecords(b);
		}
		if ((Integer) arr[3] != null) {
			obj.setSapTotal((Integer) arr[3]);
		}

		BigInteger actProc = arr[4] != null
				? new BigInteger(String.valueOf(arr[4])) : BigInteger.ZERO;
		obj.setProcessedActive(actProc);

		BigInteger inActProc = arr[5] != null
				? new BigInteger(String.valueOf(arr[5])) : BigInteger.ZERO;
		obj.setProcessedInactive(inActProc);

		BigInteger aErr = arr[6] != null
				? new BigInteger(String.valueOf(arr[6])) : BigInteger.ZERO;
		obj.setErrorActive(aErr);

		BigInteger inaErr = arr[7] != null
				? new BigInteger(String.valueOf(arr[7])) : BigInteger.ZERO;
		obj.setErrorInactive(inaErr);

		BigInteger actInfo = arr[8] != null
				? new BigInteger(String.valueOf(arr[8])) : BigInteger.ZERO;
		obj.setInfoActive(actInfo);
		BigInteger inActInfo = arr[9] != null
				? new BigInteger(String.valueOf(arr[9])) : BigInteger.ZERO;
		obj.setInfoInactive(inActInfo);

		BigInteger invNotApplicable = arr[10] != null
				? new BigInteger(String.valueOf(arr[10])) : BigInteger.ZERO;
		obj.setInvNotApplicable(invNotApplicable);
		BigInteger invAspError = arr[11] != null
				? new BigInteger(String.valueOf(arr[11])) : BigInteger.ZERO;
		obj.setInvAspError(invAspError);
		BigInteger invAspProcessed = arr[12] != null
				? new BigInteger(String.valueOf(arr[12])) : BigInteger.ZERO;
		obj.setInvAspProcessed(invAspProcessed);
		BigInteger invIrnInProgress = arr[13] != null
				? new BigInteger(String.valueOf(arr[13])) : BigInteger.ZERO;
		obj.setInvIrnInProgress(invIrnInProgress);

		BigInteger invIrnProcessed = arr[14] != null
				? new BigInteger(String.valueOf(arr[14])) : BigInteger.ZERO;
		obj.setInvIrnProcessed(invIrnProcessed);

		BigInteger invIrnInError = arr[15] != null
				? new BigInteger(String.valueOf(arr[15])) : BigInteger.ZERO;
		obj.setInvIrnInError(invIrnInError);

		BigInteger invIrnCancelled = arr[16] != null
				? new BigInteger(String.valueOf(arr[16])) : BigInteger.ZERO;
		obj.setInvIrnCancelled(invIrnCancelled);

		BigInteger einvInfoError = arr[17] != null
				? new BigInteger(String.valueOf(arr[17])) : BigInteger.ZERO;
		obj.setEinvVInfoError(einvInfoError);

		BigInteger einvNotOpted = arr[18] != null
				? new BigInteger(String.valueOf(arr[18])) : BigInteger.ZERO;
		obj.setEinvNotOpted(einvNotOpted);

		BigInteger ewbNotApplicable = arr[19] != null
				? new BigInteger(String.valueOf(arr[19])) : BigInteger.ZERO;
		obj.setEwbNotApplicable(ewbNotApplicable);

		BigInteger ewbAspError = arr[20] != null
				? new BigInteger(String.valueOf(arr[20])) : BigInteger.ZERO;
		obj.setEwbAspError(ewbAspError);
		BigInteger ewbAspProcessed = arr[21] != null
				? new BigInteger(String.valueOf(arr[21])) : BigInteger.ZERO;
		obj.setEwbAspProcessed(ewbAspProcessed);
		BigInteger ewbGenInProgress = arr[22] != null
				? new BigInteger(String.valueOf(arr[22])) : BigInteger.ZERO;
		obj.setEwbGenInProgress(ewbGenInProgress);

		BigInteger ewbNicError = arr[23] != null
				? new BigInteger(String.valueOf(arr[23])) : BigInteger.ZERO;
		obj.setEwbNicError(ewbNicError);
		BigInteger ewbPartAGenerated = arr[24] != null
				? new BigInteger(String.valueOf(arr[24])) : BigInteger.ZERO;
		obj.setEwbPartAGenerated(ewbPartAGenerated);
		BigInteger ewbCancelled = arr[25] != null
				? new BigInteger(String.valueOf(arr[25])) : BigInteger.ZERO;
		obj.setEwbCancelled(ewbCancelled);
		BigInteger ewbGeneratedOnErp = arr[26] != null
				? new BigInteger(String.valueOf(arr[26])) : BigInteger.ZERO;
		obj.setEwbGeneratedOnErp(ewbGeneratedOnErp);

		BigInteger ewbNotGeneratedOnErp = arr[27] != null
				? new BigInteger(String.valueOf(arr[27])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(ewbNotGeneratedOnErp);

		BigInteger aspNA = arr[28] != null
				? new BigInteger(String.valueOf(arr[28])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspNA);

		BigInteger aspError = arr[29] != null
				? new BigInteger(String.valueOf(arr[29])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspError);

		BigInteger aspProcess = arr[30] != null
				? new BigInteger(String.valueOf(arr[30])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspProcess);

		BigInteger aspSaveInitiated = arr[31] != null
				? new BigInteger(String.valueOf(arr[31])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspSaveInitiated);

		BigInteger aspSavedGstin = arr[32] != null
				? new BigInteger(String.valueOf(arr[32])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspSavedGstin);

		BigInteger aspErrorsGstin = arr[33] != null
				? new BigInteger(String.valueOf(arr[33])) : BigInteger.ZERO;
		obj.setEwbNotGeneratedOnErp(aspErrorsGstin);

		BigInteger ewbNotOpted = arr[34] != null
				? new BigInteger(String.valueOf(arr[34])) : BigInteger.ZERO;
		obj.setEwbNotOpted(ewbNotOpted);

		if (mapOutwardEwbIdAndEInvId != null) {
			List<Anx1NewDataStatusEntity> outwardEwbIdAndEInvIds = mapOutwardEwbIdAndEInvId
					.get(recieveDate);
			if (outwardEwbIdAndEInvIds != null
					&& !outwardEwbIdAndEInvIds.isEmpty()) {
				outwardEwbIdAndEInvIds.forEach(outwardEwbIdAndEInvId -> {
					obj.setEwbId(outwardEwbIdAndEInvId.getEwbId());
					obj.setEinvId(outwardEwbIdAndEInvId.getEinvId());
				});
			}
		}
		return obj;
	}

	private Anx1NewDataStatusEntity convertInward(Object[] arr,
			String sectionType) {

		Anx1NewDataStatusEntity obj = new Anx1NewDataStatusEntity();

		java.sql.Date sqlRecieveDate = (java.sql.Date) arr[0];

		if (sqlRecieveDate != null) {
			obj.setReceivedDate(sqlRecieveDate.toLocalDate());
		}
		BigInteger b = arr[2] != null ? new BigInteger(String.valueOf(arr[2]))
				: BigInteger.ZERO;
		if (b != null) {
			obj.setTotalRecords(b);
		}
		if ((Integer) arr[3] != null) {
			obj.setSapTotal((Integer) arr[3]);
		}

		BigInteger actProc = arr[4] != null
				? new BigInteger(String.valueOf(arr[4])) : BigInteger.ZERO;
		obj.setProcessedActive(actProc);

		BigInteger inActProc = arr[5] != null
				? new BigInteger(String.valueOf(arr[5])) : BigInteger.ZERO;
		obj.setProcessedInactive(inActProc);

		BigInteger actErrors = arr[6] != null
				? new BigInteger(String.valueOf(arr[6])) : BigInteger.ZERO;
		obj.setErrorActive(actErrors);

		BigInteger inactErrors = arr[7] != null
				? new BigInteger(String.valueOf(arr[7])) : BigInteger.ZERO;
		obj.setErrorInactive(inactErrors);

		BigInteger actInformation = arr[8] != null
				? new BigInteger(String.valueOf(arr[8])) : BigInteger.ZERO;
		obj.setInfoActive(actInformation);
		BigInteger inActInfo = arr[9] != null
				? new BigInteger(String.valueOf(arr[9])) : BigInteger.ZERO;
		obj.setInfoInactive(inActInfo);
		return obj;
	}

	private Anx1NewDataStatusEntity convertItc04(Object[] arr,
			String sectionType) {

		Anx1NewDataStatusEntity obj = new Anx1NewDataStatusEntity();

		java.sql.Date sqlRecieveDate = (java.sql.Date) arr[0];

		if (sqlRecieveDate != null) {
			obj.setReceivedDate(sqlRecieveDate.toLocalDate());
		}
		BigInteger b = arr[1] != null ? new BigInteger(String.valueOf(arr[1]))
				: BigInteger.ZERO;
		if (b != null) {
			obj.setTotalRecords(b);
		}

		BigInteger actProc = arr[2] != null
				? new BigInteger(String.valueOf(arr[2])) : BigInteger.ZERO;
		obj.setProcessedActive(actProc);

		BigInteger inActProc = arr[3] != null
				? new BigInteger(String.valueOf(arr[3])) : BigInteger.ZERO;
		obj.setProcessedInactive(inActProc);

		BigInteger actErrors = arr[4] != null
				? new BigInteger(String.valueOf(arr[4])) : BigInteger.ZERO;
		obj.setErrorActive(actErrors);

		BigInteger inactErrors = arr[5] != null
				? new BigInteger(String.valueOf(arr[5])) : BigInteger.ZERO;
		obj.setErrorInactive(inactErrors);

		/*
		 * BigInteger actInformation = arr[8] != null ? new
		 * BigInteger(String.valueOf(arr[])) : BigInteger.ZERO;
		 * obj.setInfoActive(actInformation); BigInteger inActInfo = arr[9] !=
		 * null ? new BigInteger(String.valueOf(arr[9])) : BigInteger.ZERO;
		 * obj.setInfoInactive(inActInfo);
		 */

		return obj;
	}
	
	private Anx1NewDataStatusEntity convertVendorPayment(Object[] arr,
			String sectionType) {

		Anx1NewDataStatusEntity obj = new Anx1NewDataStatusEntity();

		String sqlRecieveDate = (String) arr[0];

		if (sqlRecieveDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(sqlRecieveDate, formatter);
			obj.setReceivedDate(localDate);
		}

		BigInteger actProc = arr[1] != null
				? new BigInteger(String.valueOf(arr[1]))
				: BigInteger.ZERO;
		obj.setProcessedActive(actProc);
		
		BigInteger inActProc = arr[3] != null
				? new BigInteger(String.valueOf(arr[3]))
				: BigInteger.ZERO;

		obj.setProcessedInactive(inActProc);

		BigInteger actErrors = arr[2] != null
				? new BigInteger(String.valueOf(arr[2]))
				: BigInteger.ZERO;

		
		obj.setErrorActive(actErrors);
		
		BigInteger inActErrors = arr[4] != null
				? new BigInteger(String.valueOf(arr[4]))
				: BigInteger.ZERO;

		obj.setErrorInactive(inActErrors);
		obj.setTotalRecords(actProc.add(actErrors).add(inActErrors).add(inActProc));

		

		return obj;
	}


	public String createNewQueryString(String dataType, String buildQuery) {
		String queryStr = null;

		if (dataType.equalsIgnoreCase(OUTWARD)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Executing  Outward Query BEGIN");
			}
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT HDR.RECEIVED_DATE AS RECEIVED_DATE, ");
			sql.append("0 as SAPTOTAL,count(HDR.id) as TOTALRECORDS, ");
			sql.append("0 as DIFFERENCE_INCOUNT, ");
			sql.append("SUM(case when ASP_INVOICE_STATUS = 2 AND ");
			sql.append(" HDR.IS_DELETE = FALSE then 1 else NULL END) ");
			sql.append("AS ACT_PROCESSED,SUM(case when ");
			sql.append("ASP_INVOICE_STATUS = 2 AND HDR.IS_DELETE = TRUE then ");
			sql.append("1 else NULL END) AS INACT_PROCESSED,");
			sql.append("SUM(case when ASP_INVOICE_STATUS = 1 ");
			sql.append(
					"AND  HDR.IS_DELETE = FALSE then 1 else 0 END) AS ACT_ERRORS,");
			sql.append("SUM(case when ASP_INVOICE_STATUS = 1 ");
			sql.append(
					"AND  HDR.IS_DELETE = TRUE then 1 else 0 END) AS INACT_ERRORS,");
			sql.append(
					"SUM(case when IS_INFORMATION = TRUE AND ASP_INVOICE_STATUS = 2 ");
			sql.append(" AND  HDR.IS_DELETE = FALSE then 1 else NULL END) AS ");
			sql.append("ACT_INFORMATION,SUM(case when ");
			sql.append("IS_INFORMATION = TRUE AND ASP_INVOICE_STATUS = 2 AND ");
			sql.append(" HDR.IS_DELETE = TRUE then 1 else NULL END) ");
			sql.append("as INACT_INFORMATION,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS = 2 THEN 1 ELSE 0 END ) INV_NOT_APPLICABLE,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS = 6 THEN 1 ELSE 0 END ) INV_ASP_ERROR,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS = 7 THEN 1 ELSE 0 END ) INV_ASP_PROCESSED,");
			sql.append("0 as INV_IRN_INTIATED,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS IN (10,11) THEN 1 ELSE 0 END ) INV_IRN_GENERATED,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS IN (8,13) THEN 1 ELSE 0 END ) INV_ERRORS,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS = 9 THEN 1 ELSE 0 END ) INV_CANCELLED,");
			sql.append(
					"SUM(CASE WHEN INFO_ERROR_CODE IS NOT NULL THEN 1 ELSE 0 END ) EINV_INFO_ERROR,");
			sql.append(
					"SUM(CASE WHEN EINV_STATUS = 1 THEN 1 ELSE 0 END ) EINV_NOT_OPTED,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 1 THEN 1 ELSE 0 END ) EWB_NOT_APPLICABLE,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 2 THEN 1 ELSE 0 END ) EWB_ASP_ERROR,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 3 THEN 1 ELSE 0 END ) EWB_ASP_PROCESSED,");
			sql.append("0 AS EWB_GEN_IN_PROGRESS,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 5 THEN 1 ELSE 0 END ) EWB_NIC_ERROR,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 6 THEN 1 ELSE 0 END  ) EWB_PARTA_GENERATED,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 10 THEN 1 ELSE 0 END ) EWB_CANCELLED,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 23 THEN 1 ELSE 0 END ) EWB_GENERATED_ON_ERP,");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 24 THEN 1 ELSE 0 END ) EWB_NOT_GENERATED_ON_ERP,");
			sql.append(
					"SUM(CASE WHEN COMPLIANCE_APPLICABLE = FALSE THEN 1 ELSE 0 END) ASP_NOT_APPLICABLE,");
			sql.append(
					"SUM(CASE WHEN IS_ERROR = TRUE THEN 1 ELSE 0 END ) ASP_ERROR,");
			sql.append(
					"SUM(CASE WHEN IS_PROCESSED = TRUE THEN 1 ELSE 0 END ) ASP_PROCESS, ");
			sql.append(
					"SUM(CASE WHEN IS_SENT_TO_GSTN = TRUE AND BATCH_ID IS NOT NULL  ");
			sql.append(
					"AND  IS_SAVED_TO_GSTN = FALSE AND GSTN_ERROR = FALSE THEN 1 ELSE 0 END  ) ASP_SAVE_INITIATED, ");
			sql.append(
					"SUM(CASE WHEN IS_SAVED_TO_GSTN = TRUE THEN 1 ELSE 0 END  ) ASP_SAVED_GSTIN, ");
			sql.append(
					"SUM(CASE WHEN GSTN_ERROR = TRUE   THEN 1 ELSE 0 END  )  ASP_ERRORS_GSTN, ");
			sql.append(
					"SUM(CASE WHEN EWB_PROCESSING_STATUS = 25 THEN 1 ELSE 0 END  ) EWB_NOT_OPTED ");
			sql.append("FROM ANX_OUTWARD_DOC_HEADER hdr  ");
			sql.append("WHERE HDR.DATAORIGINTYPECODE IN ('A','AI','B','BI')  ");
			sql.append(buildQuery);
			sql.append(" GROUP BY HDR.RECEIVED_DATE ORDER BY  ");
			sql.append(" HDR.RECEIVED_DATE DESC ");

			queryStr = sql.toString();
		} else if (INWARD.equalsIgnoreCase(dataType)) {
			queryStr = "SELECT HDR.RECEIVED_DATE AS RECEIVED_DATE,"
					+ "0 as SAPTOTAL,count(id) as TOTALRECORDS,"
					+ "0 as DIFFERENCE_INCOUNT,"
					+ "SUM(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_PROCESSED,"
					+ "SUM(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_PROCESSED,"
					+ "SUM(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_ERRORS,"
					+ "SUM(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_ERRORS,"
					+ "sum(case when IS_INFORMATION = TRUE AND IS_PROCESSED ="
					+ " TRUE AND IS_DELETE = FALSE then 1 else NULL END) "
					+ "AS ACT_INFORMATION,sum(case when IS_INFORMATION = "
					+ "TRUE AND IS_PROCESSED = TRUE AND IS_DELETE = TRUE then "
					+ "1 else NULL END) as INACT_INFORMATION "
					+ "FROM ANX_INWARD_DOC_HEADER hdr "
					+ "WHERE HDR.DATAORIGINTYPECODE IN ('A','AI','B','BI') "
					+ buildQuery + " GROUP BY HDR.RECEIVED_DATE ORDER BY "
					+ "HDR.RECEIVED_DATE DESC";
		} else if (ITC_04.equalsIgnoreCase(dataType)) {
			queryStr = "SELECT HDR.RECEIVED_DATE AS RECEIVED_DATE,"
					+ "COUNT(*) AS TOTALRECORDS, "
					+ "COUNT(CASE WHEN IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
					+ "THEN 1 ELSE NULL END) AS ACT_PROCESSED,"
					+ "COUNT(CASE WHEN IS_PROCESSED = TRUE AND IS_DELETE = TRUE "
					+ "THEN 1 ELSE NULL END) AS INACT_PROCESSED,"
					+ " COUNT(CASE WHEN IS_ERROR = TRUE AND IS_DELETE = FALSE "
					+ "THEN 1 ELSE NULL END) AS ACT_ERRORS,"
					+ "COUNT(CASE WHEN IS_ERROR = TRUE AND IS_DELETE = TRUE "
					+ "THEN 1 ELSE NULL END) AS INACT_ERRORS "
					+ "FROM ITC04_HEADER HDR "
					+ "WHERE HDR.DATAORIGINTYPECODE IN ('A','AI','B','BI') "
					+ buildQuery + " GROUP BY HDR.RECEIVED_DATE "
					+ " ORDER BY HDR.RECEIVED_DATE DESC ";

		} else if ("vendor_payment".equalsIgnoreCase(dataType)) {//TO_VARCHAR(CREATED_ON, 'YYYY-MM-DD')
			
			queryStr = "SELECT  RECEIVED_DATE,"
					+ " SUM(ACTIVE_PSD_RECORDS) AS ACTIVE_PSD_RECORDS, "
					+ " SUM(ACTIVE_ERROR_RECORDS) AS ACTIVE_ERROR_RECORDS, "
					+ " SUM(INACTIVE_PSD_RECORDS) AS INACTIVE_PSD_RECORDS, "
					+ " SUM(INACTIVE_ERROR_RECORDS) AS INACTIVE_ERROR_RECORDS "
					+ " FROM ( SELECT "
					+ " TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') AS RECEIVED_DATE, "
					+ " COUNT(CASE WHEN HDR.IS_PROCESSED = TRUE AND HDR.IS_ACTIVE = TRUE  THEN 1 ELSE NULL END) AS ACTIVE_PSD_RECORDS, "
					+ " COUNT(CASE WHEN HDR.IS_PROCESSED = FALSE AND  HDR.IS_ACTIVE = TRUE THEN 1 ELSE NULL END)  AS ACTIVE_ERROR_RECORDS, "
					+ " COUNT(CASE WHEN HDR.IS_PROCESSED = TRUE AND HDR.IS_ACTIVE = FALSE  THEN 1 ELSE NULL END) AS INACTIVE_PSD_RECORDS, "
					+ " COUNT(CASE WHEN HDR.IS_PROCESSED = FALSE AND  HDR.IS_ACTIVE = FALSE THEN 1 ELSE NULL END)  AS INACTIVE_ERROR_RECORDS "
					+ " FROM TBL_180_DAYS_REVERSAL_PSD HDR "
					+ " WHERE  HDR.SOURCE = 'A' "
					+ buildQuery + " GROUP BY TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') "
					+ " UNION ALL "
					+ " SELECT  TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') AS RECEIVED_DATE,"
					+ " 0 AS ACTIVE_PSD_RECORDS, "
					+ " COUNT(CASE WHEN HDR.IS_ACTIVE = TRUE THEN 1 ELSE NULL END)  AS ACTIVE_ERROR_RECORDS, "
					+ " 0 AS INACTIVE_PSD_RECORDS, "
					+ " COUNT(CASE WHEN HDR.IS_ACTIVE = FALSE THEN 1 ELSE NULL END)  AS INACTIVE_ERROR_RECORDS "
					+ " FROM TBL_180_DAYS_REVERSAL_ERR HDR "
					+ " WHERE SOURCE='A' "
					+ 	buildQuery
					+ " GROUP BY TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') "
					+ " ) GROUP BY RECEIVED_DATE"
					+ " ORDER BY RECEIVED_DATE DESC ";
			
		}else if("gstr7txn".equalsIgnoreCase(dataType)) {
			queryStr =    " SELECT  RECEIVED_DATE,"
					+ "		SUM(ACTIVE_PSD_RECORDS) AS ACTIVE_PSD_RECORDS, "
					+ "		SUM(ACTIVE_ERROR_RECORDS) AS ACTIVE_ERROR_RECORDS, "
					+ "		SUM(INACTIVE_PSD_RECORDS) AS INACTIVE_PSD_RECORDS, "
					+ "		SUM(INACTIVE_ERROR_RECORDS) AS INACTIVE_ERROR_RECORDS "
					+ "		FROM ( SELECT "
					+ "		TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') AS RECEIVED_DATE, "
					+ "		COUNT(CASE WHEN HDR.IS_DELETE = FALSE AND HDR.IS_ERROR = FALSE THEN 1 ELSE NULL END) AS ACTIVE_PSD_RECORDS, "
					+ "		COUNT(CASE WHEN HDR.IS_DELETE = FALSE AND HDR.IS_ERROR = TRUE THEN 1 ELSE NULL END) AS ACTIVE_ERROR_RECORDS, "
					+ "		COUNT(CASE WHEN HDR.IS_DELETE = TRUE AND HDR.IS_ERROR = FALSE THEN 1 ELSE NULL END) AS INACTIVE_PSD_RECORDS, "
                    + "		COUNT(CASE WHEN HDR.IS_DELETE = TRUE AND HDR.IS_ERROR = TRUE THEN 1 ELSE NULL END) AS INACTIVE_ERROR_RECORDS "
					+ "		FROM GSTR7_TRANS_DOC_HEADER HDR "
					+ "		INNER JOIN GSTR7_TRANS_DOC_ITEM ITM "
                    + "		ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "		WHERE  HDR.DATAORIGINTYPECODE = 'A' " 
					+		buildQuery
					+ "	    GROUP BY TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') ) "
					+ "		GROUP BY RECEIVED_DATE "
					+ "		ORDER BY RECEIVED_DATE DESC ";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Datastatus Executed Query  -----> {}", queryStr);

		}
		return queryStr;
	}

	private String outwarPartybQuery(String buildQuery) {
		String query = "SELECT IFNULL(SUM(EWB_ID),0),IFNULL(SUM(EINV_ID),0),"
				+ "RECEIVED_DATE FROM ( select count(ID) AS EWB_ID, "
				+ "NULL AS EINV_ID,RECEIVED_DATE FROM ANX_OUTWARD_DOC_HEADER  HDR "
				+ "WHERE SUPPLIER_GSTIN IN ( SELECT GSTIN FROM "
				+ "ENTITY_SOURCE_INFO WHERE EWB_JOB = 1 OR EWB_JOB = 2) "
				+ "AND  EWB_PROCESSING_STATUS = 4 AND DATAORIGINTYPECODE "
				+ "IN ('A','AI') " + buildQuery
				+ " GROUP BY RECEIVED_DATE UNION "
				+ "select NULL AS EWB_ID,count(ID) AS EINV_ID,"
				+ "RECEIVED_DATE FROM ANX_OUTWARD_DOC_HEADER HDR WHERE "
				+ "SUPPLIER_GSTIN IN ( SELECT GSTIN FROM "
				+ "ENTITY_SOURCE_INFO WHERE E_INVOICE_JOB = 5 OR "
				+ "E_INVOICE_JOB = 7) AND  EINV_STATUS = 4 AND "
				+ "DATAORIGINTYPECODE IN ('A','AI','B','BI') " + buildQuery
				+ " GROUP BY RECEIVED_DATE ) "
				+ " GROUP BY RECEIVED_DATE ORDER BY RECEIVED_DATE DESC ";
		return query;
	}
	
   public static void main(String[] args) {
		
		String dataType="gstr7Txn";
		
		StringBuilder build = new StringBuilder();
		build.append(" AND HDR.DEDUCTOR_GSTIN IN (:gstinList) ");
		build.append(" AND TO_VARCHAR(HDR.CREATED_ON, 'YYYY-MM-DD') "
						+ " BETWEEN :recivedFromDate AND :recivedToDate");
		
		String buildQuery= build.toString();
		
		String queryStr = new EInvoiceDataStatusSearchScreenDaoImpl().createNewQueryString(dataType, buildQuery);
		
		String msg= String.format("Query: %s", queryStr);
		
		System.out.println(msg);
		
	}
}
